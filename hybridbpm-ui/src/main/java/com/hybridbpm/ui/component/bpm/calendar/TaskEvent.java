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
package com.hybridbpm.ui.component.bpm.calendar;

import com.hybridbpm.core.data.bpm.Task;
import com.vaadin.ui.components.calendar.event.BasicEvent;
import java.util.Date;

/**
 *
 * @author Marat Gubaidullin
 */
public class TaskEvent extends BasicEvent {
    
    private Task task;

    public TaskEvent(Task task, String caption, String description, Date date) {
        super(caption, description, date);
        this.task = task;
    }

    public TaskEvent(Task task, String caption, String description, Date startDate, Date endDate) {
        super(caption, description, startDate, endDate);
        this.task = task;
    }

    public Task getTaskInstance() {
        return task;
    }
    
}
