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
package com.hybridbpm.ui.component.bpm;

import com.hybridbpm.core.data.bpm.Task;
import com.hybridbpm.ui.HybridbpmUI;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marat Gubaidullin
 */
@DesignRoot
@SuppressWarnings("serial")
public class TaskOptionLayout extends VerticalLayout {

    private static final Logger logger = Logger.getLogger(TaskOptionLayout.class.getSimpleName());

    private final BeanFieldGroup<Task> binder = new BeanFieldGroup<>(Task.class);
    private PopupDateField scheduleStartDate;
    private PopupDateField scheduleEndDate;

    public TaskOptionLayout(Task task) {
        Design.read(this);
        Responsive.makeResponsive(this);
        scheduleStartDate.setResolution(Resolution.MINUTE);
        scheduleEndDate.setResolution(Resolution.MINUTE);
        setDefaultScheduler(task);
        binder.setItemDataSource(task);
        binder.bind(scheduleStartDate, "scheduleStartDate");
        binder.bind(scheduleEndDate, "scheduleEndDate");
        binder.setBuffered(true);
    }

    private void setDefaultScheduler(Task task) {
        if (task.getScheduleStartDate() == null && task.getScheduleEndDate() == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR, 1);
            calendar.set(Calendar.MINUTE, 0);
            task.setScheduleStartDate(calendar.getTime());
            calendar.add(Calendar.HOUR, 1);
            task.setScheduleEndDate(calendar.getTime());
        }

    }

    public void save() {
        try {
            binder.commit();
            HybridbpmUI.getBpmAPI().saveTask(binder.getItemDataSource().getBean(), null, null, null);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

}
