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
package com.hybridbpm.ui.component.bpm.designer;

import com.hybridbpm.model.ProcessModel;
import com.hybridbpm.model.TaskModel;
import com.hybridbpm.model.TransitionModel;


/**
 *
 * @author Marat Gubaidullin
 */
public class ActiveElement {
    
    private TYPE type;
    private ProcessModel processModel;
    private TaskModel taskModel;
    private TransitionModel transitionModel;
    
    public enum TYPE {PROCESS, TRANSITION, TASK};

    public ActiveElement(TYPE type, ProcessModel processModel, TaskModel taskModel, TransitionModel transitionModel) {
        this.type = type;
        this.processModel =  processModel;
        this.taskModel = taskModel;
        this.transitionModel = transitionModel;
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
        if (type == TYPE.PROCESS){
            taskModel = null;
            transitionModel = null;
        }
    }

    public TaskModel getTaskModel() {
        return taskModel;
    }

    public void setTaskModel(TaskModel taskModel) {
        this.taskModel = taskModel;
        this.transitionModel = null;
        this.type = TYPE.TASK;
    }

    public TransitionModel getTransitionModel() {
        return transitionModel;
    }

    public void setTransitionModel(TransitionModel transitionModel) {
        this.transitionModel = transitionModel;
        this.taskModel = null;
        this.type = TYPE.TRANSITION;
    }

    public ProcessModel getProcessModel() {
        return processModel;
    }

    public void setProcessModel(ProcessModel processModel) {
        this.processModel = processModel;
        this.transitionModel = null;
        this.taskModel = null;
        this.type = TYPE.PROCESS;
    }
    
    
    
}
