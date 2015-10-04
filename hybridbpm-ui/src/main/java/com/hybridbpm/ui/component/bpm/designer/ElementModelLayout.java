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

import com.hybridbpm.model.TaskModel;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.DragAndDropWrapper;

/**
 *
 * @author Marat Gubaidullin
 */
public class ElementModelLayout extends DragAndDropWrapper implements ClickListener {

    private final Button button = new Button(null, this);
    private TaskModel taskModel;
    private final ProcessModelLayout processModelLayout;

    public ElementModelLayout(TaskModel taskModel, ProcessModelLayout processModelLayout) {
        super(null);
        setCompositionRoot(button);
        setSizeUndefined();
        setDragStartMode(DragAndDropWrapper.DragStartMode.WRAPPER);
        this.taskModel = taskModel;
        this.processModelLayout = processModelLayout;
        button.setCaption(taskModel.getTitle());
        button.setWidth(taskModel.getWidth(), Unit.PIXELS);
        button.setHeight(taskModel.getHeight(), Unit.PIXELS);
        button.addStyleName("process-element");
        setStyleSelected(false);
    }

    public void setStyleSelected(boolean selected) {
        button.addStyleName("element-" + (selected ? "selected" : "unselected"));
        if (taskModel.getTaskType().equals(TaskModel.TASK_TYPE.AUTOMATIC)){
            button.setIcon(FontAwesome.COG);
        } else if (taskModel.getTaskType().equals(TaskModel.TASK_TYPE.HUMAN)){
            button.setIcon(FontAwesome.USER);
        }
    }

    @Override
    public void buttonClick(ClickEvent event) {
        processModelLayout.setTaskActive(taskModel);
    }

    public TaskModel getTaskModel() {
        return taskModel;
    }

    public void setTaskModel(TaskModel taskModel) {
        this.taskModel = taskModel;
    }

}
