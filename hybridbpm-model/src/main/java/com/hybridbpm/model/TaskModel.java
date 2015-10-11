/* 
 * Copyright (c) 2011-2015 Marat Gubaidullin. 
 * 
 * This file is part of HYBRIDBPM.
 * 
 * HybridBPM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * HybridBPM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with HybridBPM.  If not, see <http ://www.gnu.org/licenses/>.
 */
package com.hybridbpm.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author Marat Gubaidullin
 */
public class TaskModel implements Serializable {

    private String id;
    private String name;
    private List<TransitionModel> incomingTransitionModel;
    private List<TransitionModel> outgoingTransitionModel;
    private String connector;
    private Map<String, String> inParameters;
    private Map<String, String> outParameters;
    private Float x;
    private Float y;
    private Float width;
    private Float height;
    private GATE_TYPE joinType;
    private GATE_TYPE splitType;
    private TASK_TYPE taskType;
    private String title;
    private String description;
    private String dueDateScript;
    private String role;
    private String actorScript;
    private String form;
    private String mobileForm;
    public static final Float defaultWidth = 100f;
    public static final Float defaultHeight = 50f;

    public enum GATE_TYPE {

        PARALLEL, EXLUSIVE//, INCLUSIVE, COMPLEX
    }

    public enum TASK_TYPE {

        HUMAN, AUTOMATIC
    }
    private TASK_PRIORITY taskPriority;

    public enum TASK_PRIORITY {

        NORMAL(null), IMPORTANT("EXCLAMATION");

        private final String icon;

        private TASK_PRIORITY(String icon) {
            this.icon = icon;
        }

        public String getIcon() {
            return icon;
        }
    }

    public enum STATUS {

        CREATED, // task created  
        PUBLISHED, //   connector published 
        TODO, // task ready for user (actor resolved)
        FINISHED, // task done by user or engine
        TERMINATED, // task execution terminated
        ERROR // task execution error
    };

    private ACTOR_OPTION option;

    public enum ACTOR_OPTION {

        ESCALATED, // task escalated  
        DELEGATED // task delegated
    };

    public TaskModel() {
        this.id = UUID.randomUUID().toString();
    }

    public TaskModel(String name, String title, TASK_TYPE taskType, GATE_TYPE joinType, GATE_TYPE splitType, TASK_PRIORITY taskPriority, Float x, Float y) {
        this();
        this.name = name;
        this.title = title != null ? title : name;
        this.x = x;
        this.y = y;
        this.width = defaultWidth;
        this.height = defaultHeight;
        this.joinType = joinType;
        this.splitType = splitType;
        this.taskType = taskType;
        this.taskPriority = taskPriority;
    }

    public TaskModel(String name, String title, TASK_TYPE taskType, GATE_TYPE joinType, GATE_TYPE splitType, String connector, Float x, Float y) {
        this();
        this.name = name;
        this.title = title != null ? title : name;
        this.connector = connector;
        this.x = x;
        this.y = y;
        this.width = defaultWidth;
        this.height = defaultHeight;
        this.joinType = joinType;
        this.splitType = splitType;
        this.taskType = taskType;
    }

    public TaskModel(String name, String title, TASK_TYPE taskType, GATE_TYPE joinType, GATE_TYPE splitType, String role, TASK_PRIORITY taskPriority, Float x, Float y) {
        this();
        this.name = name;
        this.title = title != null ? title : name;
        this.role = role;
        this.x = x;
        this.y = y;
        this.width = defaultWidth;
        this.height = defaultHeight;
        this.joinType = joinType;
        this.splitType = splitType;
        this.taskType = taskType;
        this.taskPriority = taskPriority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getX() {
        return x;
    }

    public void setX(Float x) {
        this.x = x;
    }

    public Float getY() {
        return y;
    }

    public void setY(Float y) {
        this.y = y;
    }

    public Float getWidth() {
        return width;
    }

    public void setWidth(Float width) {
        this.width = width;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public GATE_TYPE getJoinType() {
        return joinType;
    }

    public void setJoinType(GATE_TYPE joinType) {
        this.joinType = joinType;
    }

    public GATE_TYPE getSplitType() {
        return splitType;
    }

    public void setSplitType(GATE_TYPE splitType) {
        this.splitType = splitType;
    }

    public void removeTransitionById(String id) {
        List<TransitionModel> incoming = new ArrayList();
        for (TransitionModel transitionModel : getIncomingTransitionModel()) {
            if (!transitionModel.getId().equals(id)) {
                incoming.add(transitionModel);
            }
        }
        setIncomingTransitionModel(incoming);

        List<TransitionModel> outgoing = new ArrayList();
        for (TransitionModel transitionModel : getOutgoingTransitionModel()) {
            if (!transitionModel.getId().equals(id)) {
                outgoing.add(transitionModel);
            }
        }
        setOutgoingTransitionModel(outgoing);
    }

    public List<TransitionModel> getIncomingTransitionModel() {
        if (incomingTransitionModel == null) {
            incomingTransitionModel = new ArrayList<>();
        }
        return incomingTransitionModel;
    }

    public void setIncomingTransitionModel(List<TransitionModel> incomingTransitionModel) {
        this.incomingTransitionModel = incomingTransitionModel;
    }

    public List<TransitionModel> getOutgoingTransitionModel() {
        if (outgoingTransitionModel == null) {
            outgoingTransitionModel = new ArrayList<>();
        }
        return outgoingTransitionModel;
    }

    public void setOutgoingTransitionModel(List<TransitionModel> outgoingTransitionModel) {
        this.outgoingTransitionModel = outgoingTransitionModel;
    }

    public String getConnector() {
        return connector;
    }

    public void setConnector(String connector) {
        this.connector = connector;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public TASK_TYPE getTaskType() {
        return taskType;
    }

    public void setTaskType(TASK_TYPE taskType) {
        this.taskType = taskType;
    }

    public TASK_PRIORITY getTaskPriority() {
        return taskPriority;
    }

    public void setTaskPriority(TASK_PRIORITY taskPriority) {
        this.taskPriority = taskPriority;
    }

    public String getDueDateScript() {
        return dueDateScript;
    }

    public void setDueDateScript(String dueDateScript) {
        this.dueDateScript = dueDateScript;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getActorScript() {
        return actorScript;
    }

    public void setActorScript(String actorScript) {
        this.actorScript = actorScript;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getMobileForm() {
        return mobileForm;
    }

    public void setMobileForm(String mobileForm) {
        this.mobileForm = mobileForm;
    }

    public Map<String, String> getInParameters() {
        if (inParameters == null) {
            inParameters = new HashMap<>();
        }
        return inParameters;
    }

    public void setInParameters(Map<String, String> inParameters) {
        this.inParameters = inParameters;
    }

    public Map<String, String> getOutParameters() {
        if (outParameters == null) {
            outParameters = new HashMap<>();
        }
        return outParameters;
    }

    public void setOutParameters(Map<String, String> outParameters) {
        this.outParameters = outParameters;
    }

}
