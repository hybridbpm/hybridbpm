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
package com.hybridbpm.core;

//import com.couchbase.lite.Database;
//import com.couchbase.lite.Emitter;
//import com.couchbase.lite.JavaContext;
//import com.couchbase.lite.LiveQuery;
//import com.couchbase.lite.Manager;
//import com.couchbase.lite.Mapper;
//import com.couchbase.lite.Query;
//import com.couchbase.lite.QueryRow;
//import com.couchbase.lite.auth.BasicAuthenticator;
//import com.couchbase.lite.replicator.Replication;
import com.hybridbpm.core.api.SyncAPI;
import com.hybridbpm.core.api.SystemAPI;
import com.hybridbpm.core.data.access.User;
import com.hybridbpm.core.util.SyncConstant;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marat Gubaidullin
 */
public class CouchbaseLiteServer {

    private static final Logger logger = Logger.getLogger(CouchbaseLiteServer.class.getSimpleName());
//    private static Manager manager;
    private static Boolean sync = Boolean.FALSE;
//    private Replication push;
//    private Replication pull;

    public void start() {
        try {
            logger.info("CouchbaseLiteServer starting");
            SystemAPI systemAPI = SystemAPI.get(User.getSystemUser(), null);
            if (Boolean.TRUE.toString().equalsIgnoreCase(systemAPI.getSystemParameter(SyncConstant.COUCHBASE_SYNC_AUTOSTART).getValue())) {
//                SyncAPI syncAPI = SyncAPI.get(User.getSystemUser(), null);
//                syncAPI.getSessionToken(syncAPI.getSyncUsername(), syncAPI.getSyncPassword());
//
//                manager = new Manager(new JavaContext(), Manager.DEFAULT_OPTIONS);
//                Database database = manager.getDatabase(syncAPI.getSyncDatabase());
//                URL url = new URL(syncAPI.getSyncInterface() + "/" + syncAPI.getSyncDatabase());
//                push = database.createPushReplication(url);
//                pull = database.createPullReplication(url);
//
//                pull.setContinuous(true);
//                push.setContinuous(true);
//
//                push.setAuthenticator(new BasicAuthenticator(syncAPI.getSyncUsername(), syncAPI.getSyncPassword()));
//                pull.setAuthenticator(new BasicAuthenticator(syncAPI.getSyncUsername(), syncAPI.getSyncPassword()));
//                push.start();
//                pull.start();

//                com.couchbase.lite.View view = database.getView("tasks");
//                if (view.getMap() == null) {
//                    Mapper mapper = new Mapper() {
//                        @Override
//                        public void map(Map<String, Object> document, Emitter emitter) {
//                            String type = (String) document.get("type");
//                            if ("TASK".equals(type)) {
//                                emitter.emit(document.get("id"), document);
//                            }
//                        }
//                    };
//                    view.setMap(mapper, "1");
//                }
//                Query query = view.createQuery();
//                LiveQuery liveQuery = query.toLiveQuery();
//                liveQuery.addChangeListener(new LiveQuery.ChangeListener() {
//
//                    @Override
//                    public void changed(LiveQuery.ChangeEvent event) {
//                        for (QueryRow queryRow : event.getRows()) {
//                            System.out.println(" DONE : " + String.valueOf(queryRow.getDocument().getProperties()));
//                        }
//                    }
//                });
//                liveQuery.start();
                sync = Boolean.TRUE;
            }
            logger.info("CouchbaseLiteServer started");
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public void stop() {
        try {
            logger.info("CouchbaseLiteServer stopping");
//            sync = Boolean.FALSE;
//            if (push != null) {
//                push.stop();
//            }
//            if (pull != null) {
//                pull.stop();
//            }
            logger.info("CouchbaseLiteServer stopped");
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

//    public static Manager getManager() {
//        return manager;
//    }

    public static Boolean getSync() {
        return sync;
    }

}
