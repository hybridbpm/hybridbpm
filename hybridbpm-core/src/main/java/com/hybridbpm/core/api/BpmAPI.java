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

import com.hybridbpm.core.data.access.User;
import com.hybridbpm.core.data.bpm.Case;
import com.hybridbpm.core.data.bpm.File;
import com.hybridbpm.core.data.bpm.FileBody;
import com.hybridbpm.core.data.bpm.StartProcess;
import com.hybridbpm.core.data.bpm.Task;
import com.hybridbpm.core.data.bpm.Variable;
import com.hybridbpm.core.data.development.Module;
import com.hybridbpm.core.data.document.Document;
import com.hybridbpm.core.util.HybridbpmCoreUtil;
import com.hybridbpm.model.ConnectorModel;
import com.hybridbpm.model.ProcessModel;
import com.hybridbpm.model.TaskModel;
import com.hybridbpm.model.Translated;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.tinkerpop.blueprints.Direction;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

/**
 *
 * @author Marat Gubaidullin
 */
public class BpmAPI extends AbstractAPI {

    private static final Logger logger = Logger.getLogger(BpmAPI.class.getSimpleName());

    private BpmAPI(User user, String sessionId) {
        super(user, sessionId);
    }

    public static BpmAPI get(User user, String sessionId) {
        return new BpmAPI(user, sessionId);
    }

    public List<StartProcess> getMyProcessToStart() {
        List<StartProcess> result = new ArrayList<>();
        String request = "SELECT FROM ("
                + "SELECT expand(in) FROM PERMISSION WHERE in.@class = 'Module' AND in.type = 'PROCESS' AND permissions IN ('START') "
                + "AND out in (SELECT FROM Role WHERE @rid IN (SELECT out('UserGroup').in('RoleGroup') FROM User WHERE @rid = " + user.getId() + "))"
                + ")";
//        String request = "SELECT FROM Module WHERE @rid IN "
//                + " (SELECT in FROM (TRAVERSE * FROM " + user.getId() + " WHILE $depth < 7 strategy BREADTH_FIRST ) WHERE @class = 'Permission' AND in.@class = 'Module' AND permissions IN ('START')) "
//                + " AND type = 'PROCESS' ORDER BY name";
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            List<Module> list = database.query(new OSQLSynchQuery<Module>(request));
            for (Module module : list) {
                ProcessModel processModel = HybridbpmCoreUtil.jsonToObject(module.getModel(), ProcessModel.class);
                List<ODocument> taskNames = database.query(new OSQLSynchQuery<ODocument>("SELECT parameter FROM Permission WHERE in = " + module.getId()));
                for (ODocument taskName : taskNames) {
                    result.add(new StartProcess(processModel, taskName.field("parameter").toString(), module.getIcon()));
                }
            }
        }
        return result;
    }

    public List<Task> getMyTasksToDo(Locale locale) {
        String request1 = "SELECT FROM (SELECT expand(outV('Task') [status= ? ]) FROM ("
                + "SELECT expand(inE('UserTaskList')) FROM User WHERE username = ? ) )"
                + " WHERE (assigned = false OR (assigned = true AND executor = ?)) AND status= ? ORDER BY updateDate DESC";
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            Map<String, Translated> titles = new HashMap<>();
            List<ODocument> docs = database.query(new OSQLSynchQuery<ODocument>("select name, title from Module WHERE type = '" + Module.MODULE_TYPE.PROCESS + "'"));
            for (ODocument document : docs) {
                Translated translated = HybridbpmCoreUtil.jsonToObject(document.field("title").toString(), Translated.class);
                titles.put(document.field("name").toString(), translated);
            }

            List<Task> list = database.query(new OSQLSynchQuery<Task>(request1), TaskModel.STATUS.TODO, user.getUsername(), user.getUsername(), TaskModel.STATUS.TODO);
            list = detachList(list);
            for (Task task : list){
                task.setCaseTitle(titles.get(task.getProcessModelName()).getValue(locale));
            } 
            return list;
        }
    }

    public List<Task> getMyTasksDone(Locale locale) {
        String request1 = "SELECT FROM Task WHERE executor = ? AND status= ? ORDER BY updateDate DESC";
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            Map<String, Translated> titles = new HashMap<>();
            List<ODocument> docs = database.query(new OSQLSynchQuery<ODocument>("select name, title from Module WHERE type = '" + Module.MODULE_TYPE.PROCESS + "'"));
            for (ODocument document : docs) {
                Translated translated = HybridbpmCoreUtil.jsonToObject(document.field("title").toString(), Translated.class);
                titles.put(document.field("name").toString(), translated);
            }
            
            List<Task> list = database.query(new OSQLSynchQuery<Task>(request1), user.getUsername(), TaskModel.STATUS.FINISHED);
            list = detachList(list);
            for (Task task : list){
                task.setCaseTitle(titles.get(task.getProcessModelName()).getValue(locale));
            } 
            return list;
        }
    }

    public List<Task> getTasks(String caseId) {
        String request = "SELECT FROM Task WHERE @rid in (SELECT in FROM ProcessTaskList WHERE out.@rid = ? )";
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            List<Task> list = database.query(new OSQLSynchQuery<Task>(request), caseId);
            return detachList(list);
        }
    }

    public List<Case> getMyCases(Case.TYPE type, String name, Date startFrom, Date endTo, Case.STATUS... statuses) {
        return getCases(type, name, user.getUsername(), startFrom, endTo, statuses);
    }

    public List<Case> getCases(Case.TYPE type, String name, String initiator, Date startFrom, Date endTo, Case.STATUS... statuses) {
        StringBuilder statusCondition = new StringBuilder("status in [");
        for (Case.STATUS stat : statuses) {
            statusCondition.append("'").append(stat.name()).append("',");
        }
        statusCondition.deleteCharAt(statusCondition.length() - 1).append("] ");

        StringBuilder request = new StringBuilder("SELECT FROM Case WHERE ");
        request.append(statusCondition);
        if (Objects.equals(type, Case.TYPE.PROCESS)) {
            request.append(" AND type = '").append(Case.TYPE.PROCESS.name()).append("'");
            request.append(name != null ? " AND name = '" + name + "'" : "");
        } else if (Objects.equals(type, Case.TYPE.ADAPTIVE)) {
            request.append(" AND type = '").append(Case.TYPE.ADAPTIVE.name()).append("'");
        }
        request.append(initiator != null ? " AND initiator = '" + initiator + "'" : "");
        request.append(startFrom != null ? " AND startDate >= date('" + new SimpleDateFormat("yyyy-MM-dd").format(startFrom) + "', 'yyyy-MM-dd')" : "");
        request.append(endTo != null ? " AND finishDate <= date('" + new SimpleDateFormat("yyyy-MM-dd").format(endTo) + "', 'yyyy-MM-dd')" : "");
        System.out.println(request.toString());
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            List<Case> list = database.query(new OSQLSynchQuery<Case>(request.toString()));
            return detachList(list);
        }
    }

    public Map<String, Translated> getProcessModelTitles() {
        Map<String, Translated> result = new HashMap<>();
        // TODO: improve request to get only titles insted of whole processes
        List<Module> processes = DevelopmentAPI.get(user, sessionId).getModuleListByType(Module.MODULE_TYPE.PROCESS, Boolean.FALSE);
        for (Module md : processes) {
            result.put(md.getName(), md.getTitle());
        }
        return result;
    }

    public Integer getMyTaskCount(TaskModel.STATUS status) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            List<ODocument> counter = database.query(new OSQLSynchQuery<ODocument>("SELECT COUNT(1) FROM Task WHERE status = '" + status + "' AND '" + user.getUsername() + "' in actors", 1));
            Integer count = counter.size() > 0 ? Integer.parseInt(counter.get(0).field("COUNT").toString()) : 0;
            return count;
        }
    }

    public List<Task> getTasks() {
        String request = "SELECT * FROM Task ";
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            List<Task> list = database.query(new OSQLSynchQuery<Task>(request));
            return detachList(list);
        }
    }

    public void notifyTaskList() {
        InternalAPI.get().notifyUsers(Arrays.asList(new String[]{user.getId().toString()}));
    }

    public Case startCase(String processModelName, User initiator, String taskName, Date startDate, Map<String, Object> variables, Map<String, List<File>> files) {
        return InternalAPI.get().startCase(processModelName, initiator, taskName, startDate, variables, files);
    }

    public void finishTask(Task task, Map<String, Object> variables, Map<String, List<File>> files, List<String> fileIdsToDelete) {
        InternalAPI.get().finishTask(task, variables, files, fileIdsToDelete);
    }

    public void saveTask(Task task, Map<String, Object> variables, Map<String, List<File>> files, List<String> fileIdsToDelete) {
        InternalAPI.get().saveTask(task, variables, files, fileIdsToDelete);
    }

    public void acceptTask(String taskId) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            Task task = database.load(new ORecordId(taskId));
            task.setExecutor(user.getUsername());
            task.setAssigned(true);
            database.save(task);
        }
    }

    public void denyTask(String taskId) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            Task task = database.load(new ORecordId(taskId));
            task.setExecutor(null);
            task.setAssigned(false);
            database.save(task);
        }
    }

    public void setTaskPriority(String taskId, TaskModel.TASK_PRIORITY priority) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            Task task = database.load(new ORecordId(taskId));
            task.setTaskPriority(priority);
            database.save(task);
        }
    }

    public Case getCaseById(String caseId) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            Case result = database.load(new ORecordId(caseId));
            return detach(result);
        }
    }

    public Case getCaseByTaskId(String taskId) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            Object caseId = getOrientGraph().getVertex(taskId).getEdges(Direction.IN, "ProcessTaskList").iterator().next().getVertex(Direction.OUT).getId();
            Case result = database.load(new ORecordId(caseId.toString()));
            return detach(result);
        }
    }

    public void terminateCase(String caseId) {
        InternalAPI.get().terminateCase(caseId);
    }

    public void deleteCase(String caseId) {
        InternalAPI.get().deleteCase(caseId);
    }

    public Task getTaskById(String taskId) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            Task result = database.load(new ORecordId(taskId));
            return detach(result);
        }
    }

    public Map<String, Object> getVariableValues(String caseId) {
        Map<String, Object> variables = InternalAPI.get().getVariableValues(caseId);
        return variables;
    }

    public List<Variable> getProcessVariables(String caseId) {
        return InternalAPI.get().getProcessVariables(caseId);
    }

    public Map<String, Variable> createFirstVariables(ProcessModel processModel) {
        return InternalAPI.get().createFirstVariables(processModel);
    }

    public Map<String, Object> executeConnector(ConnectorModel connectorModel, Map<String, String> variableScripts) throws RuntimeException {
        return InternalAPI.get().executeConnector(connectorModel, variableScripts);
    }

    public File getFileById(Object id, boolean withBody) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            File result = database.load(new ORecordId(id.toString()));
            result = detach(result);
            if (withBody) {
                List<FileBody> list = database.query(new OSQLSynchQuery<FileBody>("SELECT FROM FileBody WHERE file = " + id, 1));
                FileBody fileBody = list.get(0);
                result.setBody(fileBody.getBody().toStream());
            }
            return result;
        }
    }

    public Map<String, List<File>> getCaseFiles(String caseId) {
        Map<String, List<File>> result = new HashMap<>();
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            List<File> files = database.query(new OSQLSynchQuery<Document>("SELECT FROM File WHERE case = " + caseId));
            files = detachList(files);
            List<String> names = new ArrayList<>();
            for (File f : files) {
                names.add(f.getName());
            }
            for (String name : names) {
                List<File> filesToAdd = new ArrayList<>();
                for (File f : files) {
                    if (name.equals(f.getName())) {
                        filesToAdd.add(f);
                    }
                }
                result.put(name, filesToAdd);
            }
        }
        return result;
    }
}
