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

import com.hybridbpm.core.data.bpm.Case;
import com.hybridbpm.core.data.bpm.File;
import com.hybridbpm.core.data.bpm.Task;
import com.hybridbpm.core.data.development.Module;
import com.hybridbpm.core.util.HybridbpmCoreUtil;
import com.hybridbpm.model.ProcessModel;
import com.hybridbpm.model.TaskModel;
import com.hybridbpm.ui.util.HybridbpmStyle;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.util.HybridbpmUiUtil;
import com.hybridbpm.ui.component.ConfigureWindow;
import com.hybridbpm.ui.component.adaptive.AdaptiveTaskEditor;
import com.hybridbpm.ui.component.adaptive.AdaptiveTaskPanel;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author Marat Gubaidullin
 */
@SuppressWarnings("serial")
public class TaskLayout extends VerticalLayout implements Button.ClickListener, MenuBar.Command, Window.CloseListener {

    private final String taskId;
    private final String processName;
    private final String taskName;

    private Module processModule;
    private Case hCase;
    private Task task;
    private ProcessModel processModel;
    private TaskModel taskModel;

    private final TabSheet tabSheet = new TabSheet();
    private final MenuBar priorityMenubar = new MenuBar();
    private final MenuBar.MenuItem priorityItem = priorityMenubar.addItem("", null);
    private final MenuBar.MenuItem normalItem = priorityItem.addItem(TaskModel.TASK_PRIORITY.NORMAL.name(), this);
    private final MenuBar.MenuItem highItem = priorityItem.addItem(TaskModel.TASK_PRIORITY.IMPORTANT.name(), FontAwesome.valueOf(TaskModel.TASK_PRIORITY.IMPORTANT.getIcon()), this);
    private final Button btnAccept = new Button("Accept", this);
    private final Button btnSchedule = new Button("Schedule", this);
    private final Button btnEscalate = new Button("Escalate", this);
    private final Button btnDelegate = new Button("Delegate", this);
    private final Button btnSave = new Button("Save", this);
    private final Button btnSend = new Button("Send", this);
    private final Button btnClose = new Button("Close", this);
    private final HorizontalLayout buttonBar = new HorizontalLayout(/*priorityMenubar, */btnAccept, btnSchedule, /*btnEscalate, btnDelegate, */ btnSave, btnSend, btnClose);
    private final TaskFormHeader taskFormHeader = new TaskFormHeader();

    private TaskForm taskForm;
    private TaskCommentsLayout taskCommentsLayout;
    private Date startDate = new Date();
    private boolean showHeader;

    public TaskLayout(String taskId, String processName, String taskName, boolean showHeader) {
        this.taskId = taskId;
        this.processName = processName;
        this.taskName = taskName;
        this.showHeader = showHeader;

        Responsive.makeResponsive(this);
        btnAccept.setIcon(FontAwesome.CHECK);
        btnEscalate.setIcon(FontAwesome.ARROW_UP);
        btnDelegate.setIcon(FontAwesome.ARROW_DOWN);
        btnSchedule.setIcon(FontAwesome.CALENDAR);
        btnSave.setIcon(FontAwesome.SAVE);
        btnSend.setIcon(FontAwesome.SEND);
//        btnClose.setIcon(FontAwesome.TIMES);

        btnSend.setStyleName(ValoTheme.BUTTON_PRIMARY);

        btnAccept.addStyleName(ValoTheme.BUTTON_SMALL);
        btnEscalate.addStyleName(ValoTheme.BUTTON_SMALL);
        btnDelegate.addStyleName(ValoTheme.BUTTON_SMALL);
        btnSchedule.addStyleName(ValoTheme.BUTTON_SMALL);
        btnSave.addStyleName(ValoTheme.BUTTON_SMALL);
        btnSend.addStyleName(ValoTheme.BUTTON_SMALL);
        btnClose.addStyleName(ValoTheme.BUTTON_SMALL);

        priorityMenubar.setStyleName(ValoTheme.MENUBAR_SMALL);

//        buttonBar.setWidth(100, Unit.PERCENTAGE);
        buttonBar.setSpacing(true);
        buttonBar.addStyleName("toolbar");
//        buttonBar.setExpandRatio(btnAccept, 1f);
//        buttonBar.setComponentAlignment(priorityMenubar, Alignment.MIDDLE_LEFT);
        buttonBar.setComponentAlignment(btnAccept, Alignment.MIDDLE_RIGHT);
//        buttonBar.setComponentAlignment(btnEscalate, Alignment.MIDDLE_RIGHT);
//        buttonBar.setComponentAlignment(btnDelegate, Alignment.MIDDLE_RIGHT);
        buttonBar.setComponentAlignment(btnSchedule, Alignment.MIDDLE_RIGHT);
        buttonBar.setComponentAlignment(btnSave, Alignment.MIDDLE_RIGHT);
        buttonBar.setComponentAlignment(btnSend, Alignment.MIDDLE_RIGHT);
        buttonBar.setComponentAlignment(btnClose, Alignment.MIDDLE_RIGHT);

        tabSheet.setStyleName(ValoTheme.TABSHEET_COMPACT_TABBAR);
        tabSheet.setStyleName(ValoTheme.TABSHEET_FRAMED);
        tabSheet.setSizeFull();

        setSizeFull();
        setSpacing(true);
        if (showHeader) {
            addComponent(taskFormHeader);
        }
        addComponent(buttonBar);
        setComponentAlignment(buttonBar, Alignment.MIDDLE_RIGHT);
        addComponent(tabSheet);
        setExpandRatio(tabSheet, 1f);
        loadForm();
    }

    private void loadForm() {
        tabSheet.removeAllComponents();
        prepareData();
        if (showHeader) {
            taskFormHeader.initUI(task);
        }
        createTaskTab();
        createDiscussionTab();
        configureVisibilityAndAccess();
    }

    private void prepareData() {
        processModule = HybridbpmUI.getDevelopmentAPI().getModuleByName(processName);
        processModel = HybridbpmCoreUtil.jsonToObject(processModule.getModel(), ProcessModel.class);
        taskModel = processModel.getTaskModelByName(taskName);
        if (taskId != null) {
            hCase = HybridbpmUI.getBpmAPI().getCaseByTaskId(taskId);
            task = HybridbpmUI.getBpmAPI().getTaskById(taskId);
            task.setCaseTitle(processModule.getTitle().getValue(HybridbpmUI.getCurrent().getLocale()));
            task.setCaseCode(hCase.getCode() != null ? hCase.getCode() : hCase.getId().toString());
        } else {
            task = new Task();
            task.setInitiator(HybridbpmUI.getUser().getUsername());
            task.setStartDate(startDate);
            task.setStatus(TaskModel.STATUS.TODO);
            task.setCaseTitle(processModule.getTitle().getValue(HybridbpmUI.getCurrent().getLocale()));
            task.setTaskTitle(taskModel.getTitle());
//            task.setCaseCode("#");
        }
    }

    private void createTaskTab() {
        taskForm = HybridbpmUiUtil.generateTaskFormObject(taskModel.getForm());
        taskForm.setStyleName(HybridbpmStyle.LAYOUT_PADDING16);
        taskForm.setProcessData(hCase != null ? hCase.getId().toString() : null, processModel);
        tabSheet.addTab(taskForm, taskModel.getTitle(), FontAwesome.LIST_ALT);
    }

    private void createDiscussionTab() {
        if (hCase != null) {
            taskCommentsLayout = new TaskCommentsLayout(hCase.getId().toString(), taskId);
            taskCommentsLayout.initUI();
            tabSheet.addTab(taskCommentsLayout, "Comments", FontAwesome.COMMENTS);
        }
    }

    public HorizontalLayout getButtonBar() {
        return buttonBar;
    }

    public TaskForm getTaskForm() {
        return taskForm;
    }

    public TaskCommentsLayout getTaskCommentsLayout() {
        return taskCommentsLayout;
    }

    public void setTaskForm(TaskForm taskForm) {
        this.taskForm = taskForm;
    }

    private void configureVisibilityAndAccess() {
        if (hCase == null) {
            btnAccept.setVisible(false);
            btnSave.setVisible(false);
            btnEscalate.setVisible(false);
            btnSchedule.setVisible(false);
            btnDelegate.setVisible(false);
            priorityMenubar.setVisible(false);
        } else {
            priorityItem.setText(task.getTaskPriority() != null ? task.getTaskPriority().name() : TaskModel.TASK_PRIORITY.NORMAL.name());
            priorityItem.setIcon(task.getTaskPriority() != null && task.getTaskPriority().getIcon() != null ? FontAwesome.valueOf(task.getTaskPriority().getIcon()) : null);
            if (task.getExecutor() == null) {
                priorityMenubar.setEnabled(false);
                btnAccept.setIcon(FontAwesome.CHECK);
                btnAccept.setCaption("Accept");
                btnAccept.setEnabled(true);
                btnEscalate.setEnabled(false);
                btnDelegate.setEnabled(false);
                btnSchedule.setEnabled(false);
                btnSave.setEnabled(false);
                btnSend.setEnabled(false);
                tabSheet.setReadOnly(true);
                tabSheet.setEnabled(false);
            } else if (task.getExecutor().equals(HybridbpmUI.getUser().getUsername())) {
                priorityMenubar.setEnabled(true);
                btnAccept.setIcon(FontAwesome.TIMES);
                btnAccept.setCaption("Deny");
                btnAccept.setEnabled(true);
                btnEscalate.setEnabled(true);
                btnSchedule.setEnabled(true);
                btnDelegate.setEnabled(true);
                btnSave.setEnabled(true);
                btnSend.setEnabled(true);
                tabSheet.setReadOnly(false);
                tabSheet.setEnabled(true);
            }
        }
        // for other statuses than TODO
        if (!Objects.equals(task.getStatus(), TaskModel.STATUS.TODO)) {
            btnAccept.setVisible(false);
            btnSave.setVisible(false);
            btnSend.setVisible(false);
            btnEscalate.setVisible(false);
            btnSchedule.setVisible(false);
            btnDelegate.setVisible(false);
            priorityMenubar.setVisible(false);
            for (Component comp : tabSheet) {
                comp.setReadOnly(true);
                comp.setEnabled(false);
            }
        }
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton().equals(btnSave)) {
            taskForm.commit();
            Map<String, Object> variables = taskForm.getProcessVariablesValues();
            Map<String, List<File>> files = taskForm.getProcessFiles();
            List<String> idsToDelete = taskForm.getFilesIdToDelete();
            task.setStartDate(task.getStartDate() != null ? task.getStartDate() : startDate);
            HybridbpmUI.getBpmAPI().saveTask(task, variables, files, idsToDelete);
            close();
        } else if (event.getButton().equals(btnSend)) {
            taskForm.commit();
            Map<String, Object> variables = taskForm.getProcessVariablesValues();
            Map<String, List<File>> files = taskForm.getProcessFiles();
            if (hCase == null && taskId == null) {
                HybridbpmUI.getBpmAPI().startCase(processName, HybridbpmUI.getUser(), taskName, startDate, variables, files);
            } else {
                List<String> idsToDelete = taskForm.getFilesIdToDelete();
                task.setStartDate(task.getStartDate() != null ? task.getStartDate() : startDate);
                HybridbpmUI.getBpmAPI().finishTask(task, variables, files, idsToDelete);
            }
            close();
        } else if (event.getButton().equals(btnAccept)) {
            if (btnAccept.getIcon().equals(FontAwesome.CHECK)) {
                HybridbpmUI.getBpmAPI().acceptTask(taskId);
                loadForm();
            } else {
                HybridbpmUI.getBpmAPI().denyTask(taskId);
                loadForm();
            }
        } else if (event.getButton().equals(btnSchedule)) {
            openTaskOptions();
        } else if (event.getButton().equals(btnClose)) {
            close();
        }
    }

    private void close() {
        if (getParent() instanceof TabSheet) {
            TabSheet parent = (TabSheet) getParent();
            parent.removeTab(parent.getTab(this));
            parent.setSelectedTab(0);
        } else if (getParent() != null && getParent() instanceof VerticalLayout && getParent().getParent() instanceof AdaptiveTaskPanel) {
            AdaptiveTaskPanel adaptiveTaskPanel = (AdaptiveTaskPanel) getParent().getParent();
            adaptiveTaskPanel.close();
        } else if (getParent() != null && getParent() instanceof VerticalLayout && getParent().getParent() instanceof AdaptiveTaskEditor) {
            AdaptiveTaskEditor adaptiveTaskEditor = (AdaptiveTaskEditor) getParent().getParent();
            adaptiveTaskEditor.close();
        }
    }

    @Override
    public void menuSelected(MenuBar.MenuItem selectedItem) {
        TaskModel.TASK_PRIORITY priority = TaskModel.TASK_PRIORITY.valueOf(selectedItem.getText());
        if (!Objects.equals(task.getTaskPriority(), priority)) {
            HybridbpmUI.getBpmAPI().setTaskPriority(taskId, priority);
            loadForm();
        }
    }

    public void openTaskOptions() {
        final TaskOptionLayout taskOptionLayout = new TaskOptionLayout(task);
        final ConfigureWindow configureWindow = new ConfigureWindow(taskOptionLayout, "Task options");
        Button.ClickListener clickListener = new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (event.getButton().equals(configureWindow.btnClose)) {

                } else if (event.getButton().equals(configureWindow.btnOk)) {
                    taskOptionLayout.save();
                }
                configureWindow.close();
            }
        };
        configureWindow.setClickListener(clickListener);
        configureWindow.addCloseListener(this);
        configureWindow.setSizeUndefined();
        HybridbpmUI.getCurrent().addWindow(configureWindow);
    }

    @Override
    public void windowClose(Window.CloseEvent e) {
        loadForm();
    }
}
