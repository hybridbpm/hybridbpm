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

import java.io.Serializable;

/**
 *
 * @author Marat Gubaidullin
 */
public class DashboardEvent implements Serializable  {

    private final String sessionId;
    private UI_CHANGE_TYPE type;
    private String viewUrl;

    public enum UI_CHANGE_TYPE {

        VIEW_CHANGED,
        VIEW_DELETED,
        PANEL_CHANGED,
        PANEL_DELETED,
        MODULE_CHANGED,
        MODULE_DELETED;
    }

    public DashboardEvent(String sessionId, UI_CHANGE_TYPE type, String viewUrl) {
        this.sessionId = sessionId;
        this.type = type;
        this.viewUrl = viewUrl;
    }

    public String getSessionId() {
        return sessionId;
    }
    
    public static DashboardEvent createViewChangeEvent(String sessionId, String viewUrl) {
        return new DashboardEvent(sessionId, UI_CHANGE_TYPE.VIEW_CHANGED, viewUrl);
    }

    public static DashboardEvent createViewDeleteEvent(String sessionId, String viewUrl) {
        return new DashboardEvent(sessionId, UI_CHANGE_TYPE.VIEW_DELETED, viewUrl);
    }
    
    public static DashboardEvent createModuleAddEvent(String sessionId, String name) {
        return new DashboardEvent(sessionId, UI_CHANGE_TYPE.MODULE_CHANGED, name);
    }

    public static DashboardEvent createModuleDeleteEvent(String sessionId, String name) {
        return new DashboardEvent(sessionId, UI_CHANGE_TYPE.MODULE_DELETED, name);
    }
    
    public static DashboardEvent createPanelDeleteEvent(String sessionId, String name) {
        return new DashboardEvent(sessionId, UI_CHANGE_TYPE.PANEL_DELETED, name);
    }

    public static DashboardEvent createPanelAddEvent(String sessionId, String name) {
        return new DashboardEvent(sessionId, UI_CHANGE_TYPE.PANEL_CHANGED, name);
    }

    public UI_CHANGE_TYPE getType() {
        return type;
    }

    public void setType(UI_CHANGE_TYPE type) {
        this.type = type;
    }

    public String getViewUrl() {
        return viewUrl;
    }

    public void setViewId(String viewUrl) {
        this.viewUrl = viewUrl;
    }

}
