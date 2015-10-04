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

import com.hybridbpm.core.util.HybridbpmCoreUtil;
import com.hazelcast.config.FileSystemXmlConfig;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ITopic;
import static com.hybridbpm.core.HybridbpmCore.CONFIGURATION_DIRECTORY;
import com.hybridbpm.core.event.BpmEvent;
import com.hybridbpm.core.event.DashboardEvent;
import com.hybridbpm.core.event.DashboardNotificationEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marat Gubaidullin
 */
public class HazelcastServer {

    private static final Logger logger = Logger.getLogger(HazelcastServer.class.getSimpleName());
    private static HazelcastInstance hazelcastInstance;
    public static final String CONFIGURATION_FILENAME = "hazelcast.xml";

    public void start() {
        try {
            logger.info("HazelcastServer starting");
            File configurationFile = new File(CONFIGURATION_DIRECTORY, CONFIGURATION_FILENAME);
            if (!configurationFile.exists()) {
                try (FileOutputStream fos = new FileOutputStream(configurationFile)) {
                    fos.write(HybridbpmCoreUtil.getDefaultHazelcastConfig().getBytes());
                }
            }
            configurationFile = new File(CONFIGURATION_DIRECTORY, CONFIGURATION_FILENAME);
            hazelcastInstance = Hazelcast.newHazelcastInstance(new FileSystemXmlConfig(configurationFile));
            logger.info("HazelcastServer started");
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public void stop() {
        try {
            logger.info("HazelcastServer stopping");
            hazelcastInstance.shutdown();
            logger.info("HazelcastServer stopped");
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public static ITopic<DashboardEvent> getDashboardEventTopic() {
        return hazelcastInstance.getTopic("DASHBOARD_EVENT");
    }

    public static ITopic<DashboardNotificationEvent> getDashboardNotificationEventTopic(String userId) {
        return hazelcastInstance.getTopic(userId);
    }
    
    public static void removeDashboardNotificationEventTopic(String userId, String notificationListenerId) {
        getDashboardNotificationEventTopic(userId).removeMessageListener(notificationListenerId);
        getDashboardNotificationEventTopic(userId).destroy();
    }
    
    private static void sendDashboardNotificationEvent(DashboardNotificationEvent dashboardNotificationEvent) {
        getDashboardNotificationEventTopic(dashboardNotificationEvent.getReceiverName()).publish(dashboardNotificationEvent);
    }

    public static void sendDashboardNotificationEventIfExists(DashboardNotificationEvent dashboardNotificationEvent) {
        Collection<DistributedObject> instances = hazelcastInstance.getDistributedObjects();
        for (DistributedObject instance : instances) {
            if (instance.getServiceName().equals("hz:impl:topicService") && instance.getName().equals(dashboardNotificationEvent.getReceiverName())) {
                sendDashboardNotificationEvent(dashboardNotificationEvent);
            }
        }
    }
    
    private static IMap<String, BpmEvent> getBpmEventMap() {
        return hazelcastInstance.getMap("BPM_EVENT");
    }

    public static void publishBpmEvent(BpmEvent bpmEvent) {
        if (bpmEvent.getMessageId() == null) {
            bpmEvent.setMessageId(UUID.randomUUID().toString());
            getBpmEventMap().put(bpmEvent.getMessageId(), bpmEvent);
        }
    }

    public static void removeBpmEvent(BpmEvent bpmEvent) {
        getBpmEventMap().remove(bpmEvent.getMessageId());
    }

}
