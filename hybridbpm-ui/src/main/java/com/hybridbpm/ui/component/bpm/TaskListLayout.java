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

import com.hybridbpm.core.HazelcastServer;
import com.hybridbpm.core.data.bpm.Task;
import com.hybridbpm.core.event.DashboardNotificationEvent;
import com.hybridbpm.core.util.DashboardConstant;
import com.hybridbpm.model.TaskModel;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.component.AbstractTableLayout;
import com.vaadin.data.Item;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class TaskListLayout extends AbstractTableLayout {

    private static final Logger logger = Logger.getLogger(TaskListLayout.class.getSimpleName());

    private TabSheet tabSheet;
    private TaskModel.STATUS status;

    public TaskListLayout(TaskModel.STATUS status) {
        super();
        this.status = status;
        btnAdd.setVisible(false);
        setMargin(new MarginInfo(true, false, false, false));
    }

    @Override
    public void buttonClick(final Button.ClickEvent event) {
        try {
            if (event.getButton().equals(btnAdd)) {
                addNew();
            } else if (event.getButton().equals(btnRefresh)) {
                refreshTable();
                HybridbpmUI.getBpmAPI().notifyTaskList();
            } else if (event.getButton().getData() != null && event.getButton().getData() instanceof Task) {
                Task task = (Task) event.getButton().getData();
                TabSheet.Tab tab = tabSheet.addTab(new TaskLayout(task.getId().toString(), task.getProcessModelName(), task.getTaskName(), true), task.getTaskTitle());
                tab.setClosable(true);
                tabSheet.setSelectedTab(tab);
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            Notification.show("Error", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
        }
    }

    @Override
    public void prepareTable() {
        iTable.addContainerProperty("id", String.class, null, "ID", null, Table.Align.LEFT);
        iTable.addContainerProperty("taskPriority", TaskModel.TASK_PRIORITY.class, null, "Priority", null, Table.Align.LEFT);
        iTable.addContainerProperty("taskTitle", String.class, null, "Task", null, Table.Align.LEFT);
        iTable.setColumnExpandRatio("taskTitle", 1f);
        iTable.addGeneratedColumn("taskTitle", new OpenTaskColumnGenerator(this));
        iTable.addContainerProperty("caseTitle", String.class, null, "Process", null, Table.Align.LEFT);
        iTable.setColumnExpandRatio("caseTitle", 1f);
        iTable.addContainerProperty("updateDate", Date.class, null, "Update date", null, Table.Align.LEFT);
        iTable.addContainerProperty("dueDate", Date.class, null, "Due date", null, Table.Align.LEFT);
        iTable.setColumnWidth("dueDate", 150);
        iTable.setColumnWidth("updateDate", 150);
        iTable.addGeneratedColumn("dueDate", new DateColumnGenerator());
        iTable.addGeneratedColumn("updateDate", new DateColumnGenerator());
        iTable.addGeneratedColumn("taskPriority", new PriorityColumnGenerator());
        iTable.setColumnWidth("id", 80);
        iTable.setColumnWidth("taskPriority", 35);
        iTable.setRowHeaderMode(Table.RowHeaderMode.ICON_ONLY);
        iTable.setVisibleColumns("id", "taskTitle", "caseTitle", "updateDate", "dueDate", "taskPriority");
    }

    @Override
    public void refreshTable() {
        iTable.removeAllItems();
        List<Task> tasks = new ArrayList<>(0);

        if (Objects.equals(status, TaskModel.STATUS.TODO)) {
            tasks = HybridbpmUI.getBpmAPI().getMyTasksToDo(HybridbpmUI.getCurrent().getLocale());
        } else if (Objects.equals(status, TaskModel.STATUS.FINISHED)) {
            tasks = HybridbpmUI.getBpmAPI().getMyTasksDone(HybridbpmUI.getCurrent().getLocale());
        }

        for (Task task : tasks) {
            Item item = iTable.addItem(task);
            item.getItemProperty("id").setValue(task.getId().toString());
            item.getItemProperty("taskPriority").setValue(task.getTaskPriority());
            item.getItemProperty("taskTitle").setValue(task.getTaskTitle());
            item.getItemProperty("caseTitle").setValue(task.getCaseTitle());
            item.getItemProperty("dueDate").setValue(task.getDueDate());
            item.getItemProperty("updateDate").setValue(task.getUpdateDate());
            if (HybridbpmUI.getUser().getUsername().equals(task.getExecutor())) {
                iTable.setItemIcon(task, FontAwesome.CHECK_SQUARE_O);
            } else {
                iTable.setItemIcon(task, FontAwesome.SQUARE_O);
            }
        }
        iTable.sort(new Object[]{"updateDate"}, new boolean[]{false});
        if (Objects.equals(TaskModel.STATUS.TODO, status) && tasks.size() > 0) {
            HazelcastServer.sendDashboardNotificationEventIfExists(DashboardNotificationEvent.createViewNotification(HybridbpmUI.getUser().getId().toString(), DashboardConstant.VIEW_URL_TASKS, "" + tasks.size()));
        } else {
            HazelcastServer.sendDashboardNotificationEventIfExists(DashboardNotificationEvent.createRemoveViewNotification(HybridbpmUI.getUser().getId().toString(), DashboardConstant.VIEW_URL_TASKS));
        }
    }

    @Override
    public void addNew() {

    }

    public void setTabSheet(TabSheet tabSheet) {
        this.tabSheet = tabSheet;
    }

}
