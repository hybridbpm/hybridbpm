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

//import com.couchbase.lite.Database;
//import com.couchbase.lite.Document;
//import com.couchbase.lite.Emitter;
//import com.couchbase.lite.Mapper;
//import com.couchbase.lite.QueryEnumerator;
//import com.couchbase.lite.QueryRow;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hybridbpm.core.CouchbaseLiteServer;
import com.hybridbpm.core.data.Parameter;
import com.hybridbpm.core.data.access.Role;
import com.hybridbpm.core.data.access.User;
import com.hybridbpm.core.data.sync.CouchbaseRole;
import com.hybridbpm.core.data.sync.CouchbaseUser;
import com.hybridbpm.core.data.sync.MobileForm;
import com.hybridbpm.core.data.sync.MobileTask;
import com.hybridbpm.core.data.sync.SessionResponse;
import com.hybridbpm.core.util.SyncConstant;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Marat Gubaidullin
 */
public class SyncAPI extends AbstractAPI {

    private static final Logger logger = Logger.getLogger(SyncAPI.class.getSimpleName());
    public static final String USER_PATH = "/_user/";
    public static final String ROLE_PATH = "/_role/";
    public static final String SESSION_PATH = "/_session";
    private final Client client = ClientBuilder.newBuilder().build();

    private SyncAPI(User user, String sessionId) {
        super(user, sessionId);
    }

    public static SyncAPI get(User user, String sessionId) {
        return new SyncAPI(user, sessionId);
    }

    public String getSyncInterface() throws RuntimeException {
        Parameter parameter = SystemAPI.get(user, sessionId).getSystemParameter(SyncConstant.COUCHBASE_SYNC_INTERFACE);
        return parameter.getValue();
    }

    public String getSyncDatabase() throws RuntimeException {
        Parameter parameter = SystemAPI.get(user, sessionId).getSystemParameter(SyncConstant.COUCHBASE_SYNC_DATABASE);
        return parameter.getValue();
    }

    public String getSyncUsername() throws RuntimeException {
        Parameter parameter = SystemAPI.get(user, sessionId).getSystemParameter(SyncConstant.COUCHBASE_SYNC_USERNAME);
        return parameter.getValue();
    }

    public String getSyncPassword() throws RuntimeException {
        Parameter parameter = SystemAPI.get(user, sessionId).getSystemParameter(SyncConstant.COUCHBASE_SYNC_PASSWORD);
        return parameter.getValue();
    }

    public List<MobileTask> getMobileTasks() {
        List<MobileTask> result = new ArrayList<>();
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.setDateFormat(new SimpleDateFormat(SyncConstant.COUCHBASE_DATE_FORMAT));
//        try {
//            Database database = CouchbaseLiteServer.getManager().getDatabase(getSyncDatabase());
//            com.couchbase.lite.View view = database.getView("tasks");
//            if (view.getMap() == null) {
//                Mapper mapper = new Mapper() {
//                    @Override
//                    public void map(Map<String, Object> document, Emitter emitter) {
//                        String type = (String) document.get("type");
//                        if (Objects.equals(type, SyncConstant.COUCHBASE_TASK_DOCUMENT_TYPE)) {
//                            emitter.emit(document.get("id"), document);
//                        }
//                    }
//                };
//                view.setMap(mapper, "1");
//            }
//            QueryEnumerator enumerator = view.createQuery().run();
//            for (Iterator<QueryRow> it = enumerator; it.hasNext();) {
//                QueryRow row = it.next();
//                MobileTask mobileTask = new MobileTask();
//                mobileTask.setId(row.getDocument().getId());
//                mobileTask.setTaskTitle((String) row.getDocument().getProperty("taskTitle"));
//                mobileTask.setCaseTitle((String) row.getDocument().getProperty("caseTitle"));
//                mobileTask.setExecutor((String) row.getDocument().getProperty("executor"));
//                mobileTask.setForm((String) row.getDocument().getProperty("form"));
//                mobileTask.setChannels((List) row.getDocument().getProperty("channels"));
//                mobileTask.setUpdateDate(objectMapper.readValue((String) row.getDocument().getProperty("updateDate"), Date.class));
//                result.add(mobileTask);
//            }
//        } catch (Exception ex) {
//            Logger.getLogger(SyncAPI.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
//        }
        return result;
    }

    public List<MobileForm> getMobileForms() {
        List<MobileForm> result = new ArrayList<>();
//        try {
//            Database database = CouchbaseLiteServer.getManager().getDatabase(getSyncDatabase());
//            com.couchbase.lite.View view = database.getView("forms");
//            if (view.getMap() == null) {
//                Mapper mapper = new Mapper() {
//                    @Override
//                    public void map(Map<String, Object> document, Emitter emitter) {
//                        String type = (String) document.get("type");
//                        if (Objects.equals(type, SyncConstant.COUCHBASE_FORM_DOCUMENT_TYPE)) {
//                            emitter.emit(document.get("id"), document);
//                        }
//                    }
//                };
//                view.setMap(mapper, "1");
//            }
//            QueryEnumerator enumerator = view.createQuery().run();
//            for (Iterator<QueryRow> it = enumerator; it.hasNext();) {
//                QueryRow row = it.next();
//                MobileForm mobileForm = new MobileForm();
//                mobileForm.setId(row.getDocument().getId());
//                mobileForm.setFormTitle((String) row.getDocument().getProperty("formTitle"));
//                mobileForm.setCaseTitle((String) row.getDocument().getProperty("caseTitle"));
//                mobileForm.setBody((String) row.getDocument().getProperty("body"));
//                mobileForm.setForm((String) row.getDocument().getProperty("form"));
//                mobileForm.setChannels((List) row.getDocument().getProperty("channels"));
//                result.add(mobileForm);
//            }
//        } catch (Exception ex) {
//            Logger.getLogger(SyncAPI.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
//        }
        return result;
    }

    public SessionResponse getSessionToken(String username, String password) throws Exception {
        try {
            String url = SystemAPI.get(User.getSystemUser(), null).getSystemParameter(SyncConstant.COUCHBASE_SYNC_ADMIN_INTERFACE).getValue();
            String database = SystemAPI.get(User.getSystemUser(), null).getSystemParameter(SyncConstant.COUCHBASE_SYNC_DATABASE).getValue();
            if (!isCouchbaseUserExists(username, url, database)) {
                createCouchbaseUser(username, password, url, database);
            }
            return getCouchbaseSessionToken(username, url, database);
        } catch (RuntimeException re) {
            return null;
        }
    }

    public boolean isCouchbaseRoleExists(String name) {
        try {
            String url = SystemAPI.get(User.getSystemUser(), null).getSystemParameter(SyncConstant.COUCHBASE_SYNC_ADMIN_INTERFACE).getValue();
            String database = SystemAPI.get(User.getSystemUser(), null).getSystemParameter(SyncConstant.COUCHBASE_SYNC_DATABASE).getValue();
            return isCouchbaseRoleExists(name, url, database);
        } catch (RuntimeException re) {
            return false;
        }
    }

    private void createCouchbaseUser(String username, String password, String url, String database) throws Exception {
        WebTarget target = client.target(url + "/" + database + USER_PATH + username);

        CouchbaseUser couchbaseUser = new CouchbaseUser(username, password);
        couchbaseUser.getAdmin_roles().addAll(AccessAPI.get(User.getSystemUser(), null).getUserRoles(username));

        ObjectMapper mapper = new ObjectMapper();
        Entity entity = Entity.entity(mapper.writeValueAsString(new CouchbaseUser(username, password)), MediaType.APPLICATION_JSON);
        Response response = target.request().put(entity);
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
        } else {
        }
        response.close();
    }

    private boolean isCouchbaseUserExists(String username, String url, String database) {
        WebTarget target = client.target(url + "/" + database + USER_PATH + username);
        Response response = target.request().get();
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            response.close();  // You should close connections!
            return true;
        } else {
            response.close();
            return false;
        }
    }

    private boolean isCouchbaseRoleExists(String name, String url, String database) {
        WebTarget target = client.target(url + "/" + database + ROLE_PATH + name);
        Response response = target.request().get();
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            response.close();  // You should close connections!
            return true;
        } else {
            response.close();
            return false;
        }
    }

    private SessionResponse getCouchbaseSessionToken(String username, String url, String database) throws Exception {
        WebTarget target = client.target(url + "/" + database + SESSION_PATH);
        ObjectMapper mapper = new ObjectMapper();
        Entity entity = Entity.entity(mapper.writeValueAsString(new CouchbaseUser(username)), MediaType.APPLICATION_JSON);
        Response response = target.request().post(entity);
        String sr = response.readEntity(String.class);
        SessionResponse sessionResponse = mapper.readValue(sr, SessionResponse.class);
        response.close();  // You should close connections!
        return sessionResponse;
    }

    protected void saveMobileTask(MobileTask mobileTask) {
        try {
            mobileTask.getChannels().add(getSyncUsername());

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setDateFormat(new SimpleDateFormat(SyncConstant.COUCHBASE_DATE_FORMAT));

            Map<String, Object> properties = new HashMap<>();
            properties.put("type", SyncConstant.COUCHBASE_TASK_DOCUMENT_TYPE);
            properties.put("updateDate", objectMapper.writeValueAsString(mobileTask.getUpdateDate()));
            properties.put("scheduleStartDate", objectMapper.writeValueAsString(mobileTask.getUpdateDate()));
            properties.put("scheduleEndDate", objectMapper.writeValueAsString(mobileTask.getUpdateDate()));
            properties.put("dueDate", objectMapper.writeValueAsString(mobileTask.getUpdateDate()));

            properties.put("form", mobileTask.getForm());
            properties.put("executor", mobileTask.getExecutor());
            properties.put("taskTitle", mobileTask.getTaskTitle());
            properties.put("caseTitle", mobileTask.getCaseTitle());
            properties.put("caseCode", mobileTask.getCaseCode());

            properties.put("taskPriority", mobileTask.getTaskPriority().name());
            properties.put("assigned", mobileTask.getAssigned());
            properties.put("channels", mobileTask.getChannels());

//            Database database = CouchbaseLiteServer.getManager().getDatabase(getSyncDatabase());
//            Document document = database.getDocument(mobileTask.getId());
//            document.putProperties(properties);
//            database.close();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

    protected void saveMobileForm(MobileForm mobileForm) {
        try {
            mobileForm.getChannels().add(getSyncUsername());

            Map<String, Object> properties = new HashMap<>();
            properties.put("type", SyncConstant.COUCHBASE_FORM_DOCUMENT_TYPE);
            properties.put("form", mobileForm.getForm());
            properties.put("formTitle", mobileForm.getFormTitle());
            properties.put("caseTitle", mobileForm.getCaseTitle());
            properties.put("initial", mobileForm.getInitial());
            properties.put("channels", mobileForm.getChannels());

//            Database database = CouchbaseLiteServer.getManager().getDatabase(getSyncDatabase());
//            Document document = database.getDocument(mobileForm.getId());
//            document.putProperties(properties);
//            database.close();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

    public void syncAllRoles() {
        List<Role> roles = AccessAPI.get(User.getSystemUser(), null).getAllRoles();
        for (Role role : roles) {
            try {
                createCouchbaseRole(role.getName());
            } catch (Exception ex) {
                Logger.getLogger(SyncAPI.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    public void createCouchbaseRole(String name) {
        try {
            String url = SystemAPI.get(User.getSystemUser(), null).getSystemParameter(SyncConstant.COUCHBASE_SYNC_ADMIN_INTERFACE).getValue();
            String database = SystemAPI.get(User.getSystemUser(), null).getSystemParameter(SyncConstant.COUCHBASE_SYNC_DATABASE).getValue();
            WebTarget target = client.target(url + "/" + database + ROLE_PATH + name);
            ObjectMapper mapper = new ObjectMapper();
            Entity entity = Entity.entity(mapper.writeValueAsString(new CouchbaseRole(name)), MediaType.APPLICATION_JSON);
            Response response = target.request().put(entity);
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            } else {
            }
            response.close();
        } catch (JsonProcessingException ex) {
            Logger.getLogger(SyncAPI.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

}
