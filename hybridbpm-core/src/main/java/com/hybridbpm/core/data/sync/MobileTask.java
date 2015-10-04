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
package com.hybridbpm.core.data.sync;

import com.hybridbpm.core.data.bpm.Task.ACTOR_OPTION;
import com.hybridbpm.core.data.bpm.Task.STATUS;
import com.hybridbpm.model.TaskModel.TASK_PRIORITY;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Marat Gubaidullin
 */
public class MobileTask {

    
    private String id;
    private Date updateDate;
    private Date scheduleStartDate;
    private Date scheduleEndDate;
    private STATUS status;
    private ACTOR_OPTION option;
    private String form;
    private String executor;
    private String taskTitle;
    private String caseTitle;
    private String caseCode;
    private Date dueDate;
    private TASK_PRIORITY taskPriority;
    private Boolean assigned;
    private List<String> channels;
    private Map<String, Object> data;

    public MobileTask() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Date getScheduleStartDate() {
        return scheduleStartDate;
    }

    public void setScheduleStartDate(Date scheduleStartDate) {
        this.scheduleStartDate = scheduleStartDate;
    }

    public Date getScheduleEndDate() {
        return scheduleEndDate;
    }

    public void setScheduleEndDate(Date scheduleEndDate) {
        this.scheduleEndDate = scheduleEndDate;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public ACTOR_OPTION getOption() {
        return option;
    }

    public void setOption(ACTOR_OPTION option) {
        this.option = option;
    }

    public String getExecutor() {
        return executor;
    }

    public void setExecutor(String executor) {
        this.executor = executor;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getCaseTitle() {
        return caseTitle;
    }

    public void setCaseTitle(String caseTitle) {
        this.caseTitle = caseTitle;
    }

    public String getCaseCode() {
        return caseCode;
    }

    public void setCaseCode(String caseCode) {
        this.caseCode = caseCode;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public TASK_PRIORITY getTaskPriority() {
        return taskPriority;
    }

    public void setTaskPriority(TASK_PRIORITY taskPriority) {
        this.taskPriority = taskPriority;
    }

    public Boolean getAssigned() {
        return assigned;
    }

    public void setAssigned(Boolean assigned) {
        this.assigned = assigned;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public List<String> getChannels() {
        return channels;
    }

    public void setChannels(List<String> channels) {
        this.channels = channels;
    }

    public Map<String, Object> getData() {
        if (data == null){
            data = new HashMap<>();
        }
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
    
}
