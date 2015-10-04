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
import com.hybridbpm.core.data.chart.DbResponse;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * @author Marat Gubaidullin
 */
public class CrudAPI extends AbstractAPI {

    private static final Logger logger = Logger.getLogger(CrudAPI.class.getSimpleName());

    private CrudAPI(User user, String sessionId) {
        super(user, sessionId);
    }

    public static CrudAPI get(User user, String sessionId) {
        return new CrudAPI(user, sessionId);
    }

    public DbResponse queryODocuments(String request) {
        DbResponse response = new DbResponse();
        Map<Integer, Map<String, Object>> data = new HashMap<>();
        try (ODatabaseDocumentTx database = getODatabaseDocumentTx()) {
            List<ODocument> docs = database.query(new OSQLSynchQuery<ODocument>(request));

            for (ODocument document : docs) {
                for (String name : document.fieldNames()) {
                    if (!response.getHeader().containsKey(name)) {
                        if (document.fieldType(name) != null) {
                            response.getHeader().put(name, document.fieldType(name).getDefaultJavaType());
                        } else if (document.field(name) != null){
                            response.getHeader().put(name, document.field(name).getClass());
                        }
                    }
                }
            }
            int i = 0;
            for (ODocument document : docs) {
                data.put(i++, document.toMap());
            }

            response.getData().putAll(data);
        }
        return response;
    }

    public <T> List<T> readList(Class<T> clazz, String where) {
        String request = where == null ? "SELECT FROM " + clazz.getSimpleName() : "SELECT FROM " + clazz.getSimpleName() + "WHERE " + where;
        try (OObjectDatabaseTx database = getOObjectDatabaseTxReloadClasses()) {
            List<T> list = database.query(new OSQLSynchQuery<T>(request));
            return detachList(list);
        }
    }

    public <T> T read(Class<T> clazz, String where) {
        String request = where == null ? "SELECT FROM " + clazz.getSimpleName() : "SELECT FROM " + clazz.getSimpleName() + "WHERE " + where;
        try (OObjectDatabaseTx database = getOObjectDatabaseTxReloadClasses()) {
            List<T> list = database.query(new OSQLSynchQuery<T>(request, 1));
            T result = list.iterator().next();
            return result != null ? detach(result) : result;
        }
    }

    public <T> T save(T object) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTxReloadClasses()) {
            T result = database.save(object);
            return detach(result);
        }
    }

}
