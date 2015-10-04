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
package com.hybridbpm.ui.dashboard;

/**
 *
 * @author Marat Gubaidullin
 */
public class DashboardMessage {
    
    private String name;
    private EVENT_TYPE type;
    private Object body;
    
    public enum EVENT_TYPE {

        RESRESH_VIEW,
        PANEL_MAXIMIZED,
        PANEL_NORMALIZED,
        CUSTOM;
    }

    public DashboardMessage() {
    }

    public DashboardMessage(String name, EVENT_TYPE type, Object body) {
        this.name = name;
        this.type = type;
        this.body = body;
    }
    
    public static DashboardMessage createCustom(String name, Object body) {
        return new DashboardMessage(name, EVENT_TYPE.CUSTOM, body);
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EVENT_TYPE getType() {
        return type;
    }

    public void setType(EVENT_TYPE type) {
        this.type = type;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    
    
}
