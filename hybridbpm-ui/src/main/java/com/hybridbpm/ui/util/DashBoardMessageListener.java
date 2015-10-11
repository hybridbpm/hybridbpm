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
import com.hybridbpm.core.event.DashboardEvent;
import com.hybridbpm.ui.HybridbpmUI;
import com.vaadin.server.VaadinSession;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marat Gubaidullin
 */
public class DashBoardMessageListener implements MessageListener<DashboardEvent> {

    private static final Logger logger = Logger.getLogger(DashBoardMessageListener.class.getSimpleName());

    private final HybridbpmUI hybridbpmUI;

    public DashBoardMessageListener(HybridbpmUI hybridbpmUI) {
        this.hybridbpmUI = hybridbpmUI;
    }

    @Override
    public void onMessage(final Message<DashboardEvent> message) {
        try {
            hybridbpmUI.access(new Runnable() {
                @Override
                public void run() {
                    DashboardEvent event = message.getMessageObject();
                    if (!VaadinSession.getCurrent().getSession().getId().equals(event.getSessionId())) { // if changes from another session
                        switch (event.getType()) {
                            case VIEW_CHANGED:
                                if (hybridbpmUI.getNavigator().getState().equals(event.getViewUrl())) {
                                    hybridbpmUI.buildMenu(event.getViewUrl(), true, hybridbpmUI.getNavigator().getState());
                                } else {
                                    hybridbpmUI.buildMenu(null, false, hybridbpmUI.getNavigator().getState());
                                }
                                break;
                            case VIEW_DELETED:
                                if (hybridbpmUI.getNavigator().getState().equals(event.getViewUrl())) {
                                    hybridbpmUI.buildMenu(null, true, hybridbpmUI.getNavigator().getState());
                                } else {
                                    hybridbpmUI.buildMenu(null, false, hybridbpmUI.getNavigator().getState());
                                }
                                break;
                        }
                    } else { // if changes from this session
                        switch (event.getType()) {
                            case VIEW_CHANGED:
                                hybridbpmUI.buildMenu(event.getViewUrl(), true, null);
                                break;
                            case VIEW_DELETED:
                                if (hybridbpmUI.getNavigator().getState().equals(event.getViewUrl())) {
                                    hybridbpmUI.buildMenu(null, true, null);
                                }
                                break;
                        }
                    }
                    hybridbpmUI.push();
                }
            });
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

}
