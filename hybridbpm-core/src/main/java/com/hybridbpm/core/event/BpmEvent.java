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
public class BpmEvent implements Serializable {
    
    private String messageId;
    private String caseId;
    private String taskId;
    private String joinId;
    private EXECUTOR executor;
    
    public enum EXECUTOR {

        JOIN, CONNECTOR, TRANSITION, ACTOR_RESOLVER, SYNC_MOBILE;

    }

    public BpmEvent() {
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getJoinId() {
        return joinId;
    }

    public void setJoinId(String joinId) {
        this.joinId = joinId;
    }

    public EXECUTOR getExecutor() {
        return executor;
    }

    public void setExecutor(EXECUTOR executor) {
        this.executor = executor;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
