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
package com.hybridbpm.core.event;

import com.hazelcast.core.MapStore;
import com.hybridbpm.core.DatabaseServer;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Marat Gubaidullin
 */
public class BpmEventStore implements MapStore<String, BpmEvent> {

    private OObjectDatabaseTx getOObjectDatabaseTx() throws RuntimeException {
        OObjectDatabaseTx objectDatabaseTx = new OObjectDatabaseTx(DatabaseServer.HYBRIDBPM_DATABASE_URL);
        objectDatabaseTx.open(DatabaseServer.getUsername(), DatabaseServer.getPassword());
        return objectDatabaseTx;
    }
    
    @Override
    public void store(String key, BpmEvent bpmEvent) {
        bpmEvent.setMessageId(key);
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            database.save(bpmEvent);
            database.commit();
        }
    }

    @Override
    public void storeAll(Map<String, BpmEvent> map) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            for (String key : map.keySet()) {
                BpmEvent bpmEvent = map.get(key);
                bpmEvent.setMessageId(key);
                database.save(bpmEvent);
            }
            database.commit();
        }
    }

    @Override
    public void delete(String key) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            database.command(new OCommandSQL("DELETE FROM BpmEvent WHERE messageId = '" + key + "'")).execute();
            database.commit();
        }
    }

    @Override
    public void deleteAll(Collection<String> keys) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            for (String key : keys) {
                database.command(new OCommandSQL("DELETE FROM BpmEvent WHERE messageId = '" + key + "'")).execute();
            }
            database.commit();
        }
    }

    @Override
    public BpmEvent load(String key) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            List<BpmEvent> list = database.query(new OSQLSynchQuery<BpmEvent>("SELECT * FROM BpmEvent WHERE messageId = '" + key + "'", 1));
            BpmEvent result = list.isEmpty() ? null : list.get(0);
            return database.detachAll(result, true);
        }
    }

    @Override
    public Map<String, BpmEvent> loadAll(Collection<String> keys) {
        Map<String, BpmEvent> result = new HashMap<>();
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            for (String key : keys) {
                List<BpmEvent> list = database.query(new OSQLSynchQuery<BpmEvent>("SELECT * FROM BpmEvent WHERE messageId = '" + key + "'", 1));
                for (BpmEvent bpmEvent : list) {
                    result.put(bpmEvent.getMessageId(), bpmEvent);
                }
            }
            return detachMap(result, database);
        }
    }
    
    private <K, T> Map<K, T> detachMap(Map<K, T> map, OObjectDatabaseTx database) {
        Map<K, T> result = new HashMap<>(map.size());
        for (K key : map.keySet()) {
            T value = map.get(key);
            value = database.detachAll(value, true);
            result.put(key, value);
        }
        return result;
    }

    @Override
    public Set<String> loadAllKeys() {
        Set<String> result = new HashSet<>();
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            List<BpmEvent> list = database.query(new OSQLSynchQuery<BpmEvent>("SELECT * FROM BpmEvent"));
            for (BpmEvent bpmEvent : list) {
                result.add(bpmEvent.getMessageId());
            }
            return result;
        }
    }

}
