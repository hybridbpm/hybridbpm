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

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.MapEvent;
import com.hybridbpm.core.HazelcastServer;
import com.hybridbpm.core.api.InternalAPI;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marat Gubaidullin
 */
public class BpmEventListener implements EntryListener<String, BpmEvent> {

    private static final Logger logger = Logger.getLogger(BpmEventListener.class.getSimpleName());

    @Override
    public void entryAdded(EntryEvent<String, BpmEvent> event) {
        BpmEvent bpmEvent = event.getValue();
        logger.log(Level.INFO, "BpmEventListener.entryAdded executor {0}", new Object[]{bpmEvent.getExecutor()});
        try {
            BpmEvent.EXECUTOR executor = bpmEvent.getExecutor();
            switch (executor) {
                case ACTOR_RESOLVER:
                    InternalAPI.get().executeActorResolver(bpmEvent.getCaseId(), bpmEvent.getTaskId());
                    break;
                case CONNECTOR:
                    InternalAPI.get().executeConnectorOut(bpmEvent.getCaseId(), bpmEvent.getTaskId());
                    break;
                case JOIN:
                    InternalAPI.get().executeJoin(bpmEvent.getCaseId(), bpmEvent.getTaskId(), bpmEvent.getJoinId());
                    break;
                case TRANSITION:
                    InternalAPI.get().executeTransition(bpmEvent.getCaseId(), bpmEvent.getTaskId());
                    break;
            }
            HazelcastServer.removeBpmEvent(bpmEvent);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    @Override
    public void entryRemoved(EntryEvent<String, BpmEvent> ee) {
    }

    @Override
    public void entryUpdated(EntryEvent<String, BpmEvent> ee) {
    }

    @Override
    public void entryEvicted(EntryEvent<String, BpmEvent> ee) {
    }

    @Override
    public void mapEvicted(MapEvent me) {
    }

    @Override
    public void mapCleared(MapEvent me) {
    }

}
