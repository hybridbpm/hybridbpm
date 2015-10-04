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

import com.hybridbpm.core.data.access.User;
import java.io.Serializable;

/**
 *
 * @author Marat Gubaidullin
 */
public class DashboardNotificationEvent implements Serializable {

    private final String senderName;
    private final String receiverName;
    private final NOTIFICATION_TYPE type;
    private final NOTIFICATION_ACTION action;
    private final String viewUrl;
    private final String message;

    public enum NOTIFICATION_TYPE {

        VIEW_NOTIFICATION,
        USER_NOTIFICATION;
    }
    
    public enum NOTIFICATION_ACTION {

        SHOW,
        REMOVE;
    }

    public DashboardNotificationEvent(String senderName, String receiverName, NOTIFICATION_TYPE type, NOTIFICATION_ACTION action, String viewUrl, String message) {
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.type = type;
        this.viewUrl = viewUrl;
        this.message = message;
        this.action = action;
    }

    public static DashboardNotificationEvent createViewNotification(String receiverName, String viewUrl, String message) {
        return new DashboardNotificationEvent(User.SYSTEM, receiverName, NOTIFICATION_TYPE.VIEW_NOTIFICATION, NOTIFICATION_ACTION.SHOW, viewUrl, message);
    }
    
    public static DashboardNotificationEvent createRemoveViewNotification(String receiverName, String viewUrl) {
        return new DashboardNotificationEvent(User.SYSTEM, receiverName, NOTIFICATION_TYPE.VIEW_NOTIFICATION, NOTIFICATION_ACTION.REMOVE, viewUrl, null);
    }

    public static DashboardNotificationEvent createSystemUserNotification(String receiverName, String message) {
        return new DashboardNotificationEvent(User.SYSTEM, receiverName, NOTIFICATION_TYPE.USER_NOTIFICATION, NOTIFICATION_ACTION.SHOW, null, message);
    }

    public static DashboardNotificationEvent createUserNotification(String senderName, String receiverName, String message) {
        return new DashboardNotificationEvent(senderName, receiverName, NOTIFICATION_TYPE.USER_NOTIFICATION, NOTIFICATION_ACTION.SHOW, null, message);
    }

    public String getSenderName() {
        return senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public NOTIFICATION_TYPE getType() {
        return type;
    }

    public String getViewUrl() {
        return viewUrl;
    }

    public String getMessage() {
        return message;
    }

    public NOTIFICATION_ACTION getAction() {
        return action;
    }

}
