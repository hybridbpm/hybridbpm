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

import com.hybridbpm.core.*;
import static com.hybridbpm.core.DatabaseServer.HYBRIDBPM_DATABASE_URL;
import com.hybridbpm.core.data.access.Role;
import com.hybridbpm.core.data.access.User;
import com.hybridbpm.core.data.dashboard.PanelDefinition;
import com.hybridbpm.model.FieldModel;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marat Gubaidullin
 */
public abstract class AbstractAPI {

    private static final Logger logger = Logger.getLogger(AbstractAPI.class.getSimpleName());
    protected final User user;
    protected final String sessionId;
    private final ODatabaseDocumentTx databaseDocumentTx;
    private final OObjectDatabaseTx objectDatabaseTx;
    private final OrientGraph orientGraph;

    public AbstractAPI(User user, String sessionId) {
        this.user = user;
        this.sessionId = sessionId;

        if (ODatabaseRecordThreadLocal.INSTANCE.isDefined()) {
            this.databaseDocumentTx = (ODatabaseDocumentTx) ODatabaseRecordThreadLocal.INSTANCE.get();
        } else {
            this.databaseDocumentTx = new ODatabaseDocumentTx(HYBRIDBPM_DATABASE_URL);
            if (this.databaseDocumentTx.isClosed()) {
                this.databaseDocumentTx.open(DatabaseServer.getUsername(), DatabaseServer.getPassword());
            }
        }
        databaseDocumentTx.getLocalCache().setEnable(false);
        databaseDocumentTx.setMVCC(false);
        this.objectDatabaseTx = new OObjectDatabaseTx(databaseDocumentTx);
        this.orientGraph = new OrientGraph(databaseDocumentTx);
        this.objectDatabaseTx.activateOnCurrentThread();
        this.databaseDocumentTx.activateOnCurrentThread();
    }

    public OObjectDatabaseTx getOObjectDatabaseTx() throws RuntimeException {
        if (databaseDocumentTx.isClosed()) {
            databaseDocumentTx.open(DatabaseServer.getUsername(), DatabaseServer.getPassword());
        }
        databaseDocumentTx.activateOnCurrentThread();
        databaseDocumentTx.getLocalCache().setEnable(false);
        databaseDocumentTx.setMVCC(false);
        return objectDatabaseTx;
    }

    public OObjectDatabaseTx getOObjectDatabaseTxReloadClasses() throws RuntimeException {
        if (databaseDocumentTx.isClosed()) {
            databaseDocumentTx.open(DatabaseServer.getUsername(), DatabaseServer.getPassword());
        }
        databaseDocumentTx.activateOnCurrentThread();
        databaseDocumentTx.getLocalCache().setEnable(false);
        databaseDocumentTx.setMVCC(false);
        registerDataEntities();
        return objectDatabaseTx;
    }

    public ODatabaseDocumentTx getODatabaseDocumentTx() throws RuntimeException {
        if (databaseDocumentTx.isClosed()) {
            databaseDocumentTx.open(DatabaseServer.getUsername(), DatabaseServer.getPassword());
        }
        databaseDocumentTx.activateOnCurrentThread();
        databaseDocumentTx.getLocalCache().setEnable(false);
        databaseDocumentTx.setMVCC(false);
        return databaseDocumentTx;
    }

    public static OrientGraphNoTx getOrientGraphNoTx() throws RuntimeException {
        OrientGraphNoTx database = new OrientGraphNoTx(HYBRIDBPM_DATABASE_URL, DatabaseServer.getUsername(), DatabaseServer.getPassword());
        return database;
    }

    public OrientGraph getOrientGraph() throws RuntimeException {
        if (databaseDocumentTx.isClosed()) {
            databaseDocumentTx.open(DatabaseServer.getUsername(), DatabaseServer.getPassword());
        }
        databaseDocumentTx.activateOnCurrentThread();
        databaseDocumentTx.getLocalCache().setEnable(false);
        databaseDocumentTx.setMVCC(false);
        return orientGraph;
    }

    protected void registerDataEntities() {
        if (DevelopmentAPI.getGroovyClassLoader() != null) {
            List<ODocument> dataModules = objectDatabaseTx.query(new OSQLSynchQuery<ODocument>("SELECT name FROM Module WHERE type = 'DATA' AND template = 'false'"));
            for (ODocument document : dataModules) {
                String className = document.field("name").toString();
                try {
                    objectDatabaseTx.getEntityManager().registerEntityClass(DevelopmentAPI.getGroovyClassLoader().loadClass(className));
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        }
    }

    protected <T> List<T> detachList(List<T> list) {
        if (list != null) {
            List<T> result = new ArrayList<>(list.size());
            for (T object : list) {
                object = objectDatabaseTx.detachAll(object, true);
                result.add(object);
            }
            return result;
        } else {
            return new ArrayList<>(0);
        }
    }

    protected <K, T> Map<K, T> detachMap(Map<K, T> map) {
        Map<K, T> result = new HashMap<>(map.size());
        for (K key : map.keySet()) {
            T value = map.get(key);
            value = objectDatabaseTx.detachAll(value, true);
            result.put(key, value);
        }
        return result;
    }

    protected <T> T detach(T object) {
        if (object != null) {
            object = objectDatabaseTx.detachAll(object, true);
//            if (object instanceof PanelDefinition) {
//                PanelDefinition panelDefinition = (PanelDefinition) object;
//                List<FieldModel> fields = new ArrayList();
//                for (FieldModel fieldModel : panelDefinition.getParameters()) {
//                    FieldModel fm = objectDatabaseTx.detachAll(fieldModel, true);
//                    fields.add(fm);
//                }
//                panelDefinition.setParameters(fields);
//                object = (T) panelDefinition;
//            }
        }
        return object;
    }

    protected Role getRole(String name) {
        if (name != null) {
            List<Role> list = objectDatabaseTx.query(new OSQLSynchQuery<Role>("SELECT * FROM Role WHERE name = '" + name + "'", 1));
            Role result = list.isEmpty() ? null : list.get(0);
            return detach(result);
        } else {
            return null;
        }
    }

}
