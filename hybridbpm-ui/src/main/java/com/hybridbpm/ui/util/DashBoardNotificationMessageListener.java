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
package com.hybridbpm.ui.util;

import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import com.hybridbpm.core.event.DashboardNotificationEvent;
import com.hybridbpm.ui.HybridbpmUI;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marat Gubaidullin
 */
public class DashBoardNotificationMessageListener implements MessageListener<DashboardNotificationEvent> {

    private static final Logger logger = Logger.getLogger(DashBoardNotificationMessageListener.class.getSimpleName());

    private final HybridbpmUI hybridbpmUI;

    public DashBoardNotificationMessageListener(HybridbpmUI hybridbpmUI) {
        this.hybridbpmUI = hybridbpmUI;
    }

    @Override
    public void onMessage(final Message<DashboardNotificationEvent> message) {
        try {
            hybridbpmUI.access(new Runnable() {
                @Override
                public void run() {
                    DashboardNotificationEvent event = message.getMessageObject();
                    switch (event.getAction()) {
                        case SHOW:
                            hybridbpmUI.getMainMenu().changeNotification(event.getViewUrl(), true, event.getMessage());
                            break;
                        case REMOVE:
                            hybridbpmUI.getMainMenu().changeNotification(event.getViewUrl(), false, null);
                            break;
                    }

                    hybridbpmUI.push();
                }
            });
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

}
