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

import com.hybridbpm.core.data.Parameter;
import com.hybridbpm.core.data.access.User;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marat Gubaidullin
 */
public class SystemAPI extends AbstractAPI {

    private static final Logger logger = Logger.getLogger(SystemAPI.class.getSimpleName());
    private String sessionId;

   private SystemAPI(User user, String sessionId) {
        super(user, sessionId);
    }

    public static SystemAPI get(User user, String sessionId) {
        return new SystemAPI(user, sessionId);
    }
    public List<Parameter> getParameters() throws RuntimeException {
        logger.log(Level.FINEST, "getParameters");
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            List<Parameter> result = database.query(new OSQLSynchQuery<Parameter>("SELECT * FROM Parameter"));
            return detachList(result);
        }
    }

    public Parameter getSystemParameter(String name) throws RuntimeException {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            List<Parameter> list = database.query(new OSQLSynchQuery<Parameter>(
                    "SELECT * FROM Parameter WHERE name ='" + name + "' AND type ='" + Parameter.PARAM_TYPE.SYSTEM + "'", 1));
            if (!list.isEmpty()) {
                Parameter result = list.get(0);
                result = detach(result);
                return result;
            } else {
                return null;
            }
        }
    }
    
    public Parameter getContextParameter(String name) throws RuntimeException {
        logger.log(Level.FINEST, "getContextParameter");
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            List<Parameter> list = database.query(new OSQLSynchQuery<Parameter>(
                    "SELECT * FROM Parameter WHERE name ='" + name + "' AND type ='" + Parameter.PARAM_TYPE.CONTEXT + "'", 1));
            if (!list.isEmpty()) {
                Parameter result = list.get(0);
                result = detach(result);
                return result;
            } else {
                return null;
            }
        }
    }
    
    public void deleteContextParameter(Object id) throws RuntimeException {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            database.delete(new ORecordId(id.toString()));
            database.commit();
        }
    }
    
    
    public Parameter saveParameter(Parameter parameterInstance) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            parameterInstance = database.save(parameterInstance);
            database.commit();
            return detach(parameterInstance);
        }
    }

}
