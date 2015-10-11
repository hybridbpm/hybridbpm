/*
 * Copyright (c) 2011-2015 Marat Gubaidullin. 
 *
 * This file is part of HYBRIDBPM.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package com.hybridbpm.core.api;

import com.hybridbpm.core.HazelcastServer;
import com.hybridbpm.core.connector.BpmConnector;
import com.hybridbpm.core.data.access.User;
import com.hybridbpm.core.data.bpm.Case;
import com.hybridbpm.core.data.bpm.File;
import com.hybridbpm.core.data.bpm.Task;
import com.hybridbpm.core.data.bpm.Variable;
import com.hybridbpm.core.data.development.Module;
import com.hybridbpm.core.event.BpmEvent;
import com.hybridbpm.core.event.DashboardNotificationEvent;
import com.hybridbpm.core.util.DashboardConstant;
import com.hybridbpm.core.util.HybridbpmCoreUtil;
import com.hybridbpm.model.ConnectorModel;
import com.hybridbpm.model.FieldModel;
import com.hybridbpm.model.ProcessModel;
import com.hybridbpm.model.TaskModel;
import com.hybridbpm.model.TransitionModel;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.record.impl.ORecordBytes;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.sql.*;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import groovy.lang.GroovyClassLoader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 *
 * @author Marat Gubaidullin
 */
public class InternalAPI extends AbstractAPI {

    private static final Logger logger = Logger.getLogger(InternalAPI.class.getSimpleName());

    private InternalAPI(User user, String sessionId) {
        super(user, sessionId);
    }

    public static InternalAPI get() {
        return new InternalAPI(User.getSystemUser(), null);
    }

    public void finishTask(Task task, Map<String, Object> variables, Map<String, List<File>> files, List<String> fileIdsToDelete) {
        Object caseId = getOrientGraph().getVertex(task.getId()).getEdges(Direction.IN, "ProcessTaskList").iterator().next().getVertex(Direction.OUT).getId();
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            task.setStatus(TaskModel.STATUS.FINISHED);
            task.setUpdateDate(new Date());
            task.setFinishDate(new Date());
            task = database.save(task);
            task = detach(task);
            if (variables != null) {
                saveVariables(caseId.toString(), variables);
            }
            if (files != null) {
                saveFiles(caseId.toString(), files);
            }
            if (fileIdsToDelete != null) {
                deleteFiles(fileIdsToDelete);
            }
            database.commit();
        }
        publishNextExecutor(caseId.toString(), task, null);
    }

    public void saveTask(Task task, Map<String, Object> variables, Map<String, List<File>> files, List<String> fileIdsToDelete) {
        Object caseId = getOrientGraph().getVertex(task.getId()).getEdges(Direction.IN, "ProcessTaskList").iterator().next().getVertex(Direction.OUT).getId();
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            task.setUpdateDate(new Date());
            task = database.save(task);
            task = detach(task);
            if (variables != null) {
                saveVariables(caseId.toString(), variables);
            }
            if (files != null) {
                saveFiles(caseId.toString(), files);
            }
            if (fileIdsToDelete != null) {
                deleteFiles(fileIdsToDelete);
            }
        }
    }

    public Case startCase(String processName, User initiator, String taskName, Date startDate, Map<String, Object> variables, Map<String, List<File>> files) {
        try {
            Module md = DevelopmentAPI.get(user, sessionId).getModuleByName(processName);
            ProcessModel processModel = HybridbpmCoreUtil.jsonToObject(md.getModel(), ProcessModel.class);
            TaskModel taskModel = processModel.getTaskModelByName(taskName);
            Case case1 = null;
            Task task = null;
            try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
                case1 = saveProcess(processModel, md.getModel(), initiator);
                task = saveFirstTask(case1, taskModel, initiator, startDate);
                saveVariables(case1.getId().toString(), variables);
//                database.commit();
//                database.activateOnCurrentThread();
                case1.setCode(case1.getId().toString());
                case1 = database.save(case1);
                database.commit();
                case1 = detach(case1);
                task = detach(task);
                saveFiles(case1, files);
            }
            publishNextExecutor(case1.getId().toString(), task, null);
            return case1;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    private Case saveProcess(ProcessModel processModel, String template, User initiator) throws RuntimeException {
        Case hCase = new Case();
        hCase.setInitiator(initiator.getUsername());
        hCase.setTemplate(template);
        hCase.setModelName(processModel.getName());
        hCase.setTitle(processModel.getName());
        hCase.setStartDate(new Date());
        hCase.setStatus(Case.STATUS.STARTED);
        hCase.setType(Case.TYPE.PROCESS);
        hCase = getOObjectDatabaseTx().save(hCase);
        return hCase;
    }

    private Task saveFirstTask(Case case1, TaskModel taskModel, User initiator, Date startDate) {
        Task task = new Task();
        task.setInitial(true);
        task.setTaskTitle(taskModel.getTitle());
        task.setIteration(1);
        task.setTaskName(taskModel.getName());
        task.setProcessModelName(case1.getModelName());
        task.setCaseCode(case1.getCode());
        task.setStatus(TaskModel.STATUS.FINISHED);
        task.setExecutor(initiator.getUsername());
        task.setInitiator(initiator.getUsername());
        task.setAssigned(Boolean.TRUE);
        task.setStartDate(startDate);
        task.setFinishDate(new Date());
        task.setUpdateDate(new Date());
        task.setTaskType(taskModel.getTaskType());
        task.setTaskPriority(taskModel.getTaskPriority());
        task = getOObjectDatabaseTx().save(task);
        getODatabaseDocumentTx().command(new OCommandSQL("CREATE EDGE ProcessTaskList FROM " + case1.getId() + " TO " + task.getId())).execute();
        return task;
    }

    private Task saveTask(Case case1, TaskModel taskModel, TaskModel.STATUS status, User taskUser, int iteration) {
        Task task = new Task();
        task.setInitial(false);
        task.setAssigned(Boolean.FALSE);
        task.setTaskTitle(taskModel.getTitle());
        task.setIteration(iteration);
        task.setTaskName(taskModel.getName());
        task.setProcessModelName(case1.getModelName());
        task.setCaseCode(case1.getCode());
        task.setStatus(status);
        task.setInitiator(case1.getInitiator());
        task.setUpdateDate(new Date());
        task.setTaskType(taskModel.getTaskType());
        if (Objects.equals(taskModel.getTaskType(), TaskModel.TASK_TYPE.AUTOMATIC)) {
            task.setExecutor(User.getSystemUser().getUsername());
        } else {
            task.setExecutor(taskUser != null ? taskUser.getUsername() : null);
        }
        task.setTaskPriority(taskModel.getTaskPriority());
        task = getOObjectDatabaseTx().save(task);
        getODatabaseDocumentTx().command(new OCommandSQL("CREATE EDGE ProcessTaskList FROM " + case1.getId() + " TO " + task.getId())).execute();
        return task;
    }

    public void executeActorResolver(String caseId, String taskId) throws RuntimeException {
        logger.log(Level.INFO, "InternalAPI.executeActorResolver {0}", new Object[]{caseId, taskId});
        Task task = null;
        TaskModel taskModel = null;
        List<String> actors = new ArrayList<>();
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            Case case1 = database.load(new ORecordId(caseId));
            if (Case.STATUS.STARTED.equals(case1.getStatus())) {

                task = database.load(new ORecordId(taskId));

                ProcessModel processModel = HybridbpmCoreUtil.jsonToObject(case1.getTemplate(), ProcessModel.class);
                taskModel = processModel.getTaskModelByName(task.getTaskName());

                if (taskModel.getRole() != null) {
                    // TODO: TESTME
                    List<ODocument> usernames = getODatabaseDocumentTx().query(new OSQLSynchQuery<ODocument>("SELECT  FROM User WHERE @rid IN (SELECT out('RoleGroup').in('UserGroup') FROM Role WHERE name = '" + taskModel.getRole() + "')"));
                    for (ODocument username : usernames) {
                        actors.add(username.getIdentity().toString());
                    }
                } else if (taskModel.getActorScript() != null && !taskModel.getActorScript().isEmpty()) {

                    ScriptEngine scriptEngine = prepareScriptEngine(case1);
                    Object rawActors = scriptEngine.eval(taskModel.getActorScript());
                    if (rawActors != null && rawActors instanceof List) {
                        actors.addAll((List) rawActors);
                    } else if (rawActors != null) {
                        actors.add(rawActors.toString());
                    }
                }
                task.setStatus(TaskModel.STATUS.TODO);
                task.setUpdateDate(new Date());
                task = database.save(task);
                task = detach(task);
                for (String actor : actors) {
                    getODatabaseDocumentTx().command(new OCommandSQL("CREATE EDGE UserTaskList FROM " + taskId + " TO " + actor)).execute();
                }
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
        if (!actors.isEmpty()) {
            notifyUsers(actors);
        }
        if (taskModel != null && taskModel.getMobileForm() != null && !taskModel.getMobileForm().isEmpty()) {
            // TODO: After actor resolver connector, ex. to notify by email???? publishNextExecutor(process, task, null); 
//            createMobileTask(task, taskModel, actors);
        }
    }

    public void executeTransition(String caseId, String taskId) throws RuntimeException {
        logger.log(Level.INFO, "InternalAPI.executeTransition {0}", caseId);
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            Case case1 = database.load(new ORecordId(caseId));
            if (Case.STATUS.STARTED.equals(case1.getStatus())) {
                Task task = database.load(new ORecordId(taskId));

                ProcessModel processModel = HybridbpmCoreUtil.jsonToObject(case1.getTemplate(), ProcessModel.class);
                TaskModel taskModel = processModel.getTaskModelByName(task.getTaskName());

                if (taskModel.getOutgoingTransitionModel().isEmpty()) {
                    case1.setStatus(Case.STATUS.FINISHED);
                    case1.setUpdateDate(new Date());
                    case1.setFinishDate(new Date());
                    database.save(case1);
                } else {
                    List<TaskModel> newTaskModels = new ArrayList<>();
                    if (taskModel.getSplitType().equals(TaskModel.GATE_TYPE.EXLUSIVE)) {
                        newTaskModels.addAll(executeExclusive(case1, processModel, taskModel));
                    } else if (taskModel.getSplitType().equals(TaskModel.GATE_TYPE.PARALLEL)) {
//                    newTaskModels.addAll(executeParallel(process, taskModel));
                    }
                    List<Task> tasks = new ArrayList<>();
                    List<String> joins = new ArrayList<>();
                    for (TaskModel tm : newTaskModels) {
                        if (tm.getJoinType().equals(TaskModel.GATE_TYPE.EXLUSIVE)) {
                            Task ti = saveTask(case1, tm, TaskModel.STATUS.CREATED, null, 0);
                            tasks.add(ti);
                        } else if (tm.getJoinType().equals(TaskModel.GATE_TYPE.PARALLEL)) {
                            String joinId = saveTaskJoin(caseId, tm.getName(), database);
                            joins.add(joinId);
                        }
                    }
                    getOObjectDatabaseTx().commit();
                    for (TaskModel tm : newTaskModels) {
                        if (tm.getJoinType().equals(TaskModel.GATE_TYPE.EXLUSIVE)) {
                            for (Task ti : tasks) {
                                publishNextExecutor(case1.getId().toString(), ti, null);
                            }
                        } else if (tm.getJoinType().equals(TaskModel.GATE_TYPE.PARALLEL)) {
                            for (String joinId : joins) {
                                publishNextExecutor(case1.getId().toString(), null, joinId);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

    public void executeJoin(String caseId, String taskId, String joinInstanceId) throws RuntimeException {
        logger.log(Level.INFO, "InternalAPI.executeJoin {0}", caseId);
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            Case hCase = database.load(new ORecordId(caseId));
            if (Case.STATUS.STARTED.equals(hCase.getStatus())) {
                Task task = database.load(new ORecordId(taskId));

                ProcessModel processModel = HybridbpmCoreUtil.jsonToObject(hCase.getTemplate(), ProcessModel.class);
                TaskModel taskModel = processModel.getTaskModelByName(task.getTaskName());

                List<ODocument> currentTaskIteration = database.query(new OSQLSynchQuery<ODocument>("SELECT MAX(iteration) FROM Task WHERE taskName ='" + task.getTaskName() + "' AND caseId = '" + caseId + "'", 1));
                Integer nextIteration = currentTaskIteration.size() > 0 ? Integer.parseInt(currentTaskIteration.get(0).field("MAX").toString()) + 1 : 1;

                List<ODocument> joinInstances = database.query(new OSQLSynchQuery<ODocument>("SELECT COUNT(*) FROM TaskJoin WHERE caseId ='" + caseId + "' AND taskName = '" + task.getTaskName() + "' AND iteration = '" + nextIteration + "' "));
                Integer transitionsCome = joinInstances.size() > 0 ? Integer.parseInt(joinInstances.get(0).field("COUNT").toString()) + 1 : 1;

                int transitionsToWait = taskModel.getIncomingTransitionModel().size();
                if (transitionsToWait == transitionsCome) {
                    Task ti = saveTask(hCase, taskModel, TaskModel.STATUS.CREATED, null, nextIteration);
                    getOObjectDatabaseTx().commit();
                    // publish next activity
                    publishNextExecutor(hCase.getId().toString(), ti, null);
                }
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

    private String saveTaskJoin(String caseId, String taskName, OObjectDatabaseTx database) {
        List<ODocument> currentIteration = database.query(new OSQLSynchQuery<ODocument>("SELECT MAX(in.iteration) FROM ProcessTaskList WHERE out.@rid = " + caseId + " AND in.taskName = '" + taskName + "'", 1));
        Integer iteration = currentIteration.size() > 0 ? Integer.parseInt(currentIteration.get(0).field("MAX").toString()) + 1 : 1;
        ODocument join = new ODocument("TaskJoin");
        join.field("iteration", iteration);
        join.field("taskName", taskName);
        join.field("caseId", caseId);
        join.save();
        return join.getIdentity().toString();
    }

    private List<TaskModel> executeExclusive(Case case1, ProcessModel processModel, TaskModel taskModel) throws RuntimeException {
        logger.info("InternalAPI.executeExclusive");
        List<TaskModel> newTasks = new ArrayList<>();
        try {
            ScriptEngine scriptEngine = prepareScriptEngine(case1);
            Boolean result = false;
            // if only one transition
            if (!taskModel.getOutgoingTransitionModel().isEmpty() && taskModel.getOutgoingTransitionModel().size() == 1) {
                result = true;
                TaskModel newTaskModel = processModel.getTaskModelById(taskModel.getOutgoingTransitionModel().get(0).getEndTaskModel());
                newTasks.add(newTaskModel);
            } else {
                // first check expressions
                for (TransitionModel transitionModel : taskModel.getOutgoingTransitionModel()) {
                    transitionModel = processModel.getTransitionModelById(transitionModel.getId());
                    if (!transitionModel.getDefaultTransition() && transitionModel.getExpression() != null && !transitionModel.getExpression().isEmpty()) {
                        result = (Boolean) scriptEngine.eval(transitionModel.getExpression());
                        if (result) {
                            newTasks.add(processModel.getTaskModelById(transitionModel.getEndTaskModel()));
                            break;
                        }
                    }
                }
                // go to default
                if (!result) {
                    for (TransitionModel transitionModel : taskModel.getOutgoingTransitionModel()) {
                        transitionModel = processModel.getTransitionModelByName(transitionModel.getName());
                        if (transitionModel.getDefaultTransition()) {
                            newTasks.add(processModel.getTaskModelById(transitionModel.getEndTaskModel()));
                            break;
                        }
                    }
                }
            }
        } catch (ScriptException scriptException) {
            logger.log(Level.SEVERE, scriptException.getMessage(), scriptException);
        }
        return newTasks;
    }

    private ScriptEngine prepareScriptEngine(Case case1) {
        logger.log(Level.INFO, "InternalAPI.prepareScriptEngine for case {0}", new Object[]{case1.getId()});
        Map<String, Object> variables = getVariableValues(case1.getId().toString());
        return prepareScriptEngine(case1, variables);
    }

    private ScriptEngine prepareScriptEngine(Case case1, Map<String, Object> variables) {
        ScriptEngine scriptEngine = DevelopmentAPI.getScriptEngine();
        Bindings bindings = scriptEngine.createBindings();
        if (case1 != null) {
            bindings.put("currentCase", case1);
        }
        for (String name : variables.keySet()) {
            bindings.put(name, variables.get(name));
        }
        scriptEngine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
        return scriptEngine;
    }

    public List<Variable> getProcessVariables(String caseId) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTxReloadClasses()) {
            List<Variable> variableInstances = database.query(new OSQLSynchQuery<Variable>("SELECT * FROM Variable WHERE out ='" + caseId + "'"));
            return detachList(variableInstances);
        }
    }

    protected Map<String, Object> getVariableValues(String caseId) {
        logger.log(Level.INFO, "InternalAPI.getVariableValues {0}", new Object[]{caseId});
        Map<String, Object> result = new HashMap<>();
        OObjectDatabaseTx database = getOObjectDatabaseTxReloadClasses();
        List<ODocument> variables = database.query(new OSQLSynchQuery<ODocument>("SELECT name, in.asString() FROM Variable WHERE out = " + caseId));
        for (ODocument variable : variables) {
            Object value = database.load(new ORecordId(variable.field("in").toString()));
            result.put(variable.field("name").toString(), value);
        }
        return detachMap(result);
    }

    private HashMap<String, Variable> getVariables(String caseId) {
        HashMap<String, Variable> result = new HashMap<>();
        List<Variable> vars = getOObjectDatabaseTxReloadClasses().query(new OSQLSynchQuery<Variable>("SELECT * FROM Variable WHERE caseId ='" + caseId + "'"));
        for (Variable variableInstance : vars) {
            result.put(variableInstance.getName(), variableInstance);
        }
        return result;
    }

    private void saveVariables(String caseId, Map<String, Object> variableValues) {
        variableValues = variableValues != null ? variableValues : new HashMap<String, Object>();
        for (String name : variableValues.keySet()) {
            Object object = getOObjectDatabaseTxReloadClasses().save(variableValues.get(name));
            ODocument doc = getOObjectDatabaseTx().getRecordByUserObject(object, false);
            List<ODocument> variableExists = getODatabaseDocumentTx().query(new OSQLSynchQuery<ODocument>("SELECT FROM Variable WHERE out = ? AND in = ? AND name = ?"), caseId, doc.getIdentity().toString(), name);
            System.out.println("variableExists " + variableExists);
            System.out.println("variableExists " + variableExists.isEmpty());
            System.out.println("variableExists " + variableExists.size());
            if (variableExists.isEmpty()) {
                OCommandSQL cmd = new OCommandSQL("CREATE EDGE Variable FROM " + caseId + " TO " + doc.getIdentity().toString() + " SET name = '" + name + "'");
                getODatabaseDocumentTx().command(cmd).execute();
            }
        }
    }

    private void publishNextExecutor(String caseId, Task task, String joinId) {
        logger.log(Level.INFO, "InternalAPI.publishNextExecutor start {0} {1}", new Object[]{caseId, (task != null ? task.getId() : null)});
        BpmEvent.EXECUTOR executor = null;
        if (task != null) {
            if (task.getStatus().equals(TaskModel.STATUS.CREATED)) {
                if (task.getTaskType().equals(TaskModel.TASK_TYPE.AUTOMATIC)) {
                    executor = BpmEvent.EXECUTOR.CONNECTOR;
                } else if (task.getTaskType().equals(TaskModel.TASK_TYPE.HUMAN)) {
                    executor = BpmEvent.EXECUTOR.ACTOR_RESOLVER;
                }
            } else if (task.getStatus().equals(TaskModel.STATUS.FINISHED)) {
                executor = BpmEvent.EXECUTOR.TRANSITION;
            }
        } else if (joinId != null) {
            executor = BpmEvent.EXECUTOR.JOIN;
        }
        if (executor != null) {
            String taskId = task != null ? task.getId().toString() : null;
            publishBpmEvent(caseId, taskId, joinId, executor);
            logger.log(Level.INFO, "InternalAPI.publishNextExecutor finish {0}", new Object[]{executor});
        } else {
            logger.log(Level.INFO, "InternalAPI.publishNextExecutor done {0}", new Object[]{caseId});
        }
    }

    private void publishBpmEventTransition(String caseId, String taskId) {
        publishBpmEvent(caseId, taskId, null, BpmEvent.EXECUTOR.TRANSITION);
    }

    private void publishBpmEventJoin(String caseId, String taskId, String joinInstanceId) {
        publishBpmEvent(caseId, taskId, null, BpmEvent.EXECUTOR.JOIN);
    }

    private void publishBpmEvent(String caseId, String taskId, String joinId, BpmEvent.EXECUTOR executor) {
        BpmEvent bpmEvent = new BpmEvent();
        bpmEvent.setCaseId(caseId);
        bpmEvent.setTaskId(taskId);
        bpmEvent.setJoinId(joinId);
        bpmEvent.setExecutor(executor);
        HazelcastServer.publishBpmEvent(bpmEvent);
    }

    public void executeConnectorOut(String caseId, String taskId) throws RuntimeException {
        logger.log(Level.INFO, "InternalAPI.executeConnectorOut");
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            Case case1 = database.load(new ORecordId(caseId));
            if (Case.STATUS.STARTED.equals(case1.getStatus())) {

                Task task = database.load(new ORecordId(taskId));

                ProcessModel processModel = HybridbpmCoreUtil.jsonToObject(case1.getTemplate(), ProcessModel.class);
                TaskModel taskModel = processModel.getTaskModelByName(task.getTaskName());

                if (taskModel.getConnector() != null && !taskModel.getConnector().isEmpty()) {

                    Map<String, Object> variables = getVariableValues(caseId);
                    ScriptEngine scriptEngine = prepareScriptEngine(case1, variables);

                    BpmConnector bpmConnector = createConnectorInstance(taskModel.getConnector(), taskModel.getInParameters(), scriptEngine);
                    if (bpmConnector != null) {
                        bpmConnector.execute();

                        task.setStatus(TaskModel.STATUS.FINISHED);
                        task = database.save(task);
                        if (!taskModel.getOutParameters().isEmpty()) {
                            variables = getBpmConnectorOutParameters(taskModel.getOutParameters(), scriptEngine, bpmConnector, variables);
                            saveVariables(case1.getId().toString(), variables);
                        }
                    }
                } else {
                    task.setStatus(TaskModel.STATUS.FINISHED);
                    task = database.save(task);
                }
                publishNextExecutor(case1.getId().toString(), task, null);
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private BpmConnector createConnectorInstance(String connectorClassName, Map<String, String> inParameters, ScriptEngine scriptEngine) {
        try {
            GroovyClassLoader groovyClassLoader = DevelopmentAPI.getGroovyClassLoader();
            Class clazz = groovyClassLoader.loadClass(connectorClassName);
            BpmConnector connector = (BpmConnector) clazz.newInstance();

            for (String inParam : inParameters.keySet()) {
                Object paramValue = scriptEngine.eval(inParameters.get(inParam));
                connector.setInParameter(inParam, paramValue);
            }
            return connector;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | ScriptException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            return null;
        }
    }

    private Map<String, Object> getBpmConnectorOutParameters(Map<String, String> outParameters, ScriptEngine scriptEngine, BpmConnector connector, Map<String, Object> variables) {
        Map<String, Object> result = new HashMap<>();
        try {
            Bindings bindings = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("bpmConnector", connector);
            for (String name : outParameters.keySet()) {
                StringBuilder script = new StringBuilder();
                script.append(outParameters.get(name)).append("=").append("bpmConnector.getOutParameter('").append(name).append("')");
                scriptEngine.eval(script.toString());
            }

            for (String name : variables.keySet()) {
                bindings = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
                result.put(name, bindings.get(name));
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return result;
    }

    protected Map<String, Object> executeConnector(ConnectorModel connectorModel, Map<String, String> variableScripts) throws RuntimeException {
        logger.log(Level.INFO, "InternalAPI.executeConnector");
        Map<String, Object> result = new HashMap<>();
        try {
            ScriptEngine scriptEngine = prepareScriptEngine(null, new HashMap<String, Object>(0));
            BpmConnector bpmConnector = createConnectorInstance(connectorModel.getName(), variableScripts, scriptEngine);
            if (bpmConnector != null) {
                bpmConnector.execute();
            }

            if (!connectorModel.getOutParameters().isEmpty()) {
                Map<String, String> outParameters = new HashMap<>(connectorModel.getOutParameters().size());
                Map<String, Object> variables = new HashMap<>(connectorModel.getOutParameters().size());
                for (FieldModel f : connectorModel.getOutParameters()) {
                    outParameters.put(f.getName(), f.getName());
                    variables.put(f.getName(), null);
                }
                result = getBpmConnectorOutParameters(outParameters, scriptEngine, bpmConnector, variables);
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return result;
    }

    private BpmConnector createConnectorInstance(ConnectorModel connectorModel, Map<String, String> variableScripts) {
        try {
            GroovyClassLoader groovyClassLoader = DevelopmentAPI.getGroovyClassLoader();
            Class clazz = groovyClassLoader.loadClass(connectorModel.getName());

            ScriptEngine scriptEngine = DevelopmentAPI.getScriptEngine();
            Map<String, Object> variableValues = new HashMap<>(variableScripts.size());

            for (FieldModel fieldModel : connectorModel.getInParameters()) {
                String script = variableScripts.get(fieldModel.getName());
                if (script != null) {
                    Object value = scriptEngine.eval(script);
                    variableValues.put(fieldModel.getName(), value);
                }
            }

            BpmConnector connector = (BpmConnector) clazz.newInstance();
            for (FieldModel fieldModel : connectorModel.getInParameters()) {
                Object processVarValue = variableValues.get(fieldModel.getName());
                try {
                    Field f = connector.getClass().getDeclaredField(fieldModel.getName());
                    f.setAccessible(true);
                    f.set(connector, processVarValue);
                } catch (NoSuchFieldException nsfe) {
                    logger.log(Level.SEVERE, nsfe.getMessage(), nsfe);
                }
            }
            return connector;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            return null;
        }
    }

    public void notifyUsers(List<String> actors) {
        Map<String, Integer> counters = getTaskCountForNotification(actors);
        for (String actor : actors) {
            Integer count = counters.get(actor);
            if (count > 0) {
                HazelcastServer.sendDashboardNotificationEventIfExists(DashboardNotificationEvent.createViewNotification(actor, DashboardConstant.VIEW_URL_TASKS, count.toString()));
            } else {
                HazelcastServer.sendDashboardNotificationEventIfExists(DashboardNotificationEvent.createRemoveViewNotification(actor, DashboardConstant.VIEW_URL_TASKS));
            }
        }
    }

    private Map<String, Integer> getTaskCountForNotification(List<String> actors) {
        Map<String, Integer> result = new HashMap<>(actors.size());
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            for (String actor : actors) {
                List<ODocument> counter1 = database.query(new OSQLSynchQuery<ODocument>(
                        "SELECT COUNT(1) FROM ( SELECT expand(outV('Task') [status= '" + TaskModel.STATUS.TODO + "' ]) FROM ("
                        + "SELECT expand(inE('UserTaskList')) FROM User WHERE @rid = ? )) ", 1), actor);
                Integer count1 = counter1.size() > 0 ? Integer.parseInt(counter1.get(0).field("COUNT").toString()) : 0;
                result.put(actor, count1);
            }
        }
        return result;
    }

    void terminateCase(String caseId) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            database.command(new OCommandSQL("UPDATE Task SET status = 'TERMINATED' WHERE @rid in (SELECT inE('ProcessTaskList')[out= " + caseId + "].in FROM Task) AND status <> 'DONE' AND status <> 'ERROR'")).execute();
            database.command(new OCommandSQL("UPDATE Case SET status = 'TERMINATED' WHERE @rid = '" + caseId + "'")).execute();
            database.commit();
        }
    }

    void deleteCase(String caseId) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            database.command(new OCommandSQL("DELETE VERTEX Task WHERE @rid in (SELECT inE('ProcessTaskList')[out= " + caseId + "].in FROM Task)")).execute();
            database.command(new OCommandSQL("DELETE FROM TaskJoin WHERE caseId = '" + caseId + "'")).execute();
            database.command(new OCommandSQL("DELETE EDGE Variable WHERE out = " + caseId + "")).execute();
            database.command(new OCommandSQL("DELETE VERTEX Comment WHERE case = " + caseId + "")).execute();
            database.command(new OCommandSQL("DELETE VERTEX Case WHERE @rid = " + caseId + "")).execute();
            database.commit();
        }
    }

    private void inheritPermissions(ODocument parent, ODocument doc) {
        Vertex parentV = getOrientGraph().getVertex(parent.getIdentity());
        Vertex docV = getOrientGraph().getVertex(doc.getIdentity());
        Iterable<Edge> permissions = parentV.getEdges(Direction.IN, "Permission");
        for (Edge permission : permissions) {
            Vertex role = permission.getVertex(Direction.OUT);
            Edge perm = getOrientGraph().addEdge("class:Permission", role, docV, null);
            perm.setProperty("permissions", permission.getProperty("permissions"));
        }
    }

    private void saveFiles(Case case1, Map<String, List<File>> files) {
        for (String name : files.keySet()) {
            List<File> files2 = files.get(name);
            for (File f : files2) {
                if (f.getId() == null) { // save only new files
                    ODocument file = new ODocument("File");
                    file.field("name", name);
                    file.field("fileName", f.getFileName());
                    file.field("createDate", new Date());
                    file.field("creator", user.getUsername());
                    file.field("mime", f.getMime());
                    file.field("size", f.getSize());
                    file.field("case", case1.getId());
                    file.field("scope", File.SCOPE.CASE.name());
                    file.save();
                    // save body
                    byte[] body = f.getBody();
                    ODocument fileBody = new ODocument("FileBody");
                    fileBody.field("file", file);
                    ORecordBytes record = new ORecordBytes(body);
                    fileBody.field("body", record);
                    fileBody.save();
                }
            }
        }
    }

    private void saveFiles(String caseId, Map<String, List<File>> files) {
        Case c = getOObjectDatabaseTx().load(new ORecordId(caseId));
        saveFiles(c, files);
    }

    private void deleteFiles(List<String> fileIdsToDelete) {
        for (String id : fileIdsToDelete) {
            getOObjectDatabaseTx().delete(new ORecordId(id));
        }
    }

    protected HashMap<String, Variable> createFirstVariables(ProcessModel processModel) {
        HashMap<String, Variable> result = new HashMap<>();
        for (FieldModel variableModel : processModel.getVariableModels()) {
            Variable variableInstance = new Variable();
            variableInstance.setName(variableModel.getName());
            variableInstance.setClassName(variableModel.getClassName());
            result.put(variableModel.getName(), variableInstance);
        }
        return result;
    }

}
