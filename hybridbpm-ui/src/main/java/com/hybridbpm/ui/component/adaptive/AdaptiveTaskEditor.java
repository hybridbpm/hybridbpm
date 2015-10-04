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

import com.hybridbpm.core.data.bpm.Case;
import com.hybridbpm.core.data.bpm.StartProcess;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.component.TextComplete;
import com.hybridbpm.ui.component.bpm.TaskLayout;
import com.hybridbpm.ui.component.dashboard.tab.DashboardTab;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.event.FieldEvents;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.themes.ValoTheme;
import java.util.Arrays;

/**
 *
 * @author Marat Gubaidullin
 */
@DesignRoot
public class AdaptiveTaskEditor extends CssLayout implements Button.ClickListener {

    protected DashboardTab panelView;
    protected Layout taskLayout = new VerticalLayout();
    protected Button btnBack = new Button("Back");
    private Case case1;
    private TextArea taskTitle;
    private Button btnSend;
    private Button btnProcess;
    private VerticalLayout card;
    private VerticalLayout templateLayout;
    private VerticalLayout topLayout;
    private VerticalLayout adaptiveLayout;

    public AdaptiveTaskEditor(DashboardTab panelView, Case case1) {
        this.panelView = panelView;
        this.case1 = case1;
        Design.read(this);

        btnSend.setIcon(FontAwesome.SEND);
        btnSend.addClickListener(this);
        btnProcess.addClickListener(this);

        TextComplete textComplete = new TextComplete();
        textComplete.extend(taskTitle,
                HybridbpmUI.getAccessAPI().findUserNamesByName(null),
                Arrays.asList(new String[]{"loan", "creditcard"}));

        taskTitle.addTextChangeListener(new FieldEvents.TextChangeListener() {

            @Override
            public void textChange(FieldEvents.TextChangeEvent event) {
                String text = event.getText();
                String[] lines = text.split(System.getProperty("line.separator"));
                if (lines != null && (lines.length > 1)) {
                    taskTitle.setRows(lines.length + 2);
                }
            }
        });

        btnBack.addClickListener(this);
        btnBack.setStyleName(ValoTheme.BUTTON_LINK);
        btnBack.addStyleName(ValoTheme.BUTTON_SMALL);
        btnBack.setIcon(FontAwesome.ARROW_LEFT);
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton().equals(btnSend)) {
        } else if (event.getButton().equals(btnProcess)) {
            adaptiveLayout.setVisible(false);
            templateLayout.removeAllComponents();
            templateLayout.setVisible(true);
            for (StartProcess startProcess : HybridbpmUI.getBpmAPI().getMyProcessToStart()) {
                String startTaskTitle = startProcess.getProcessModel().getTaskModelByName(startProcess.getTaskName()).getTitle();
                String processTitle = startProcess.getProcessModel().getTitle().getValue(HybridbpmUI.getCurrent().getLocale());
                Button button = new Button(processTitle + " (" + startTaskTitle + ")");
                button.setData(startProcess);
                button.addClickListener(this);
                button.setStyleName(ValoTheme.BUTTON_LINK);
                button.addStyleName(ValoTheme.BUTTON_SMALL);
                button.setIcon(FontAwesome.valueOf(startProcess.getIcon()));
                templateLayout.addComponent(button);
            }
            templateLayout.addComponent(btnBack);
            templateLayout.setComponentAlignment(btnBack, Alignment.BOTTOM_RIGHT);
        } else if (event.getButton().getData() instanceof StartProcess) {
            StartProcess spd = (StartProcess) event.getButton().getData();
            if (taskLayout != null && card.getComponentIndex(taskLayout) > -1) {
                card.removeComponent(taskLayout);
            }
            taskLayout = new TaskLayout(null, spd.getProcessModel().getName(), spd.getTaskName(), true);
            card.addComponents(taskLayout);
            card.setExpandRatio(taskLayout, 1f);
            card.setSizeFull();
            topLayout.setVisible(false);
//            panelView.toggleMaximized(this, true);
        } else if (event.getButton().equals(btnBack)) {
            adaptiveLayout.setVisible(true);
            templateLayout.setVisible(false);
        }
    }

    public void close() {
        if (taskLayout != null && card.getComponentIndex(taskLayout) > -1) {
            card.removeComponent(taskLayout);
        }
        topLayout.setVisible(true);
        adaptiveLayout.setVisible(true);
//        panelView.toggleMaximized(this, false);
        panelView.refresh();
    }

}
