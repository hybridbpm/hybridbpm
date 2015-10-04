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
package com.hybridbpm.ui.component.adaptive;

import com.hybridbpm.core.data.bpm.Task;
import com.hybridbpm.ui.component.bpm.TaskFormHeader;
import com.hybridbpm.ui.component.bpm.TaskLayout;
import com.hybridbpm.ui.component.dashboard.tab.DashboardTab;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;

/**
 *
 * @author Marat Gubaidullin
 */
@DesignRoot
public class AdaptiveTaskPanel extends CssLayout implements Button.ClickListener {

    protected DashboardTab panelView;
    protected Layout taskLayout = new VerticalLayout();
    private Task task;
    private TaskFormHeader taskFormHeader;
    private Button btnOpen;
    private VerticalLayout card;
    private HorizontalLayout bottomBar;

    public AdaptiveTaskPanel(DashboardTab panelView, Task task) {
        this.panelView = panelView;
        this.task = task;
        Design.read(this);

        taskFormHeader.initUI(task);
        
//        btnOpen.setIcon(FontAwesome.EXPAND);
        btnOpen.addClickListener(this);
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton().equals(btnOpen)) {
            if (taskLayout != null && card.getComponentIndex(taskLayout) > -1) {
                card.removeComponent(taskLayout);
            }
            taskLayout = new TaskLayout(task.getId().toString(), task.getProcessModelName(), task.getTaskName(), false);
            card.addComponents(taskLayout);
            card.setExpandRatio(taskLayout, 1f);
            card.setSizeFull();
            bottomBar.setVisible(false);
//            panelView.toggleMaximized(this, true);
        }
    }

    public void close() {
        if (taskLayout != null && card.getComponentIndex(taskLayout) > -1) {
            card.removeComponent(taskLayout);
        }
        bottomBar.setVisible(true);
//        panelView.toggleMaximized(this, false);
        panelView.refresh();
    }

}
