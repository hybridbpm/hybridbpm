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
import com.hybridbpm.core.data.bpm.StartProcess;
import com.hybridbpm.core.data.bpm.Task;
import com.hybridbpm.core.data.development.Module;
import com.hybridbpm.core.util.HybridbpmCoreUtil;
import com.hybridbpm.model.ProcessModel;
import com.hybridbpm.model.TaskModel;
import com.hybridbpm.model.Translated;
import com.hybridbpm.model.mobile.MobileTask;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.beanutils.BeanUtils;

/**
 *
 * @author Marat Gubaidullin
 */
public class RestAPI extends AbstractAPI {

    private static final Logger logger = Logger.getLogger(RestAPI.class.getSimpleName());

    private RestAPI(User user, String sessionId) {
        super(user, sessionId);
    }

    public static RestAPI get(User user, String sessionId) {
        return new RestAPI(user, sessionId);
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

    public List<MobileTask> getMyTasksToDo(Locale locale) {
        List<Task> tasks = BpmAPI.get(user, sessionId).getMyTasksToDo(locale);
        List<MobileTask> mobileTasks = new ArrayList<>(tasks.size());
        tasks.stream().forEach((task) -> {
            MobileTask mobileTask = new MobileTask();
            try {
                BeanUtils.copyProperties(mobileTask, task);
                mobileTasks.add(mobileTask);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
        });
        return mobileTasks;
    }

    public List<Task> getMyTasksDone(Locale locale) {
        String request1 = "SELECT FROM Task WHERE executor = ? AND status= ? ORDER BY updateDate DESC";
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            Map<String, Translated> titles = new HashMap<>();
            List<ODocument> docs = database.query(new OSQLSynchQuery<>("select name, title from Module WHERE type = '" + Module.MODULE_TYPE.PROCESS + "'"));
            docs.stream().forEach((document) -> {
                Translated translated = HybridbpmCoreUtil.jsonToObject(document.field("title").toString(), Translated.class);
                titles.put(document.field("name").toString(), translated);
            });

            List<Task> list = database.query(new OSQLSynchQuery<>(request1), user.getUsername(), TaskModel.STATUS.FINISHED);
            list = detachList(list);
            for (Task task : list) {
                task.setCaseTitle(titles.get(task.getProcessModelName()).getValue(locale));
            }
            return list;
        }
    }

}
