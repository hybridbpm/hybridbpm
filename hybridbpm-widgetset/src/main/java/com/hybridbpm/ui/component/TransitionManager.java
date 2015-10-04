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
package com.hybridbpm.ui.component;

import com.hybridbpm.model.TaskModel;
import com.hybridbpm.model.TransitionModel;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.JavaScriptFunction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import elemental.json.JsonArray;

/**
 *
 * @author Marat Gubaidullin
 */
@com.vaadin.annotations.JavaScript({"raphael-min.js", "TransitionManager.js"})
public class TransitionManager extends AbstractJavaScriptComponent {
    
    private ProcessModelLayoutInterface processModelLayout;

    public TransitionManager(final ProcessModelLayoutInterface pml) {
        this.processModelLayout = pml;
        
        addFunction("changePosition", new JavaScriptFunction() {
            private static final long serialVersionUID = 1256984845028849243L;

            @Override
            public void call(JsonArray arguments) {
                processModelLayout.setTransitionElementValue(arguments.getString(0), (float)arguments.getNumber(1), (float)arguments.getNumber(2));
            }

        });
        
        addFunction("addTransition", new JavaScriptFunction() {
            private static final long serialVersionUID = 1256984843458849243L;

            @Override
            public void call(JsonArray arguments) {
                processModelLayout.addTransitionElement(arguments.getString(0), arguments.getString(1));
            }
        });
        
        addFunction("addTask", new JavaScriptFunction() {
            private static final long serialVersionUID = 1256984843458849243L;

            @Override
            public void call(JsonArray arguments) {
                processModelLayout.addTaskModel((float)arguments.getNumber(0), (float)arguments.getNumber(1));
            }
        });
        
        addFunction("setTransitionActive", new JavaScriptFunction() {
            private static final long serialVersionUID = 1256984843458849243L;

            @Override
            public void call(JsonArray arguments) {
                processModelLayout.setTransitionActive(arguments.getString(0));
            }
        });
        
        addFunction("setProcessActive", new JavaScriptFunction() {
            private static final long serialVersionUID = 1256984843458849243L;

            @Override
            public void call(JsonArray arguments) {
                processModelLayout.setProcessActive();
            }
        });
    }

    public List<Map<String, Object>> getTransitions() {
        return getState().transitions;
    }

    public void setValue(Map<String, TaskModel> elementModels, Map<String, TransitionModel> transitionModels) {
        getState().taskModels = new ArrayList(elementModels.values());
        getState().transitionModels = new ArrayList(transitionModels.values());
    }
    
    public void setValue(long width, long height) {
        getState().processWidth = width;
        getState().processHeight = height;
    }
    
    
    public void setValue(Map<String, TaskModel> elementModels, Map<String, TransitionModel> transitionModels, TaskModel taskModel) {
        getState().taskModels = new ArrayList(elementModels.values());
        getState().transitionModels = new ArrayList(transitionModels.values());
        getState().taskModel = taskModel;
    }
    
    public void setDragger(TaskModel taskModel) {
        getState().taskModel = taskModel;
    }

    @Override
    protected TransitionManagerState getState() {
        return (TransitionManagerState) super.getState();
    }
    
    
    
}
