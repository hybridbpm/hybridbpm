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
import com.vaadin.shared.ui.JavaScriptComponentState;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Marat Gubaidullin
 */
public class TransitionManagerState extends JavaScriptComponentState {
    
    public List<Map<String, Object>> transitions = new ArrayList<>();
    public List<Map<String, Object>> steps = new ArrayList<>();
    
    public List< TaskModel> taskModels = new ArrayList<>();
    public List< TransitionModel> transitionModels= new ArrayList<>();
    
    public long processWidth = 100;
    public long processHeight = 100;
    
// selected element or transition
    public TaskModel taskModel = new TaskModel();
    public TransitionModel transitionModel = new TransitionModel();
    
    
}
