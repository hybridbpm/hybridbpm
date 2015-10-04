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
import com.hybridbpm.model.TaskModel;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.component.AbstractTableLayout;
import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.themes.ValoTheme;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class CaseTaskListLayout extends AbstractTableLayout {

    private static final Logger logger = Logger.getLogger(CaseTaskListLayout.class.getSimpleName());
    private final String caseId;

    public CaseTaskListLayout(String caseId) {
        super();
        this.caseId = caseId;
        tools.setVisible(false);
    }

    @Override
    public void buttonClick(final Button.ClickEvent event) {
        try {
            if (event.getButton().getData() != null && event.getButton().getData() instanceof Task) {
//                TaskInstance task = (TaskInstance) event.getButton().getData();
//                TabSheet.Tab tab = tabSheet.addTab(new TaskLayout(task.getProcessInstanceId(), task.getId().toString(), task.getProcessName(), task.getTaskName()), task.getTaskTitle());
//                tab.setClosable(true);
//                tabSheet.setSelectedTab(tab);
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            Notification.show("Error", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
        }
    }

    @Override
    public void prepareTable() {
        iTable.setStyleName(ValoTheme.TABLE_BORDERLESS);
        iTable.addContainerProperty("id", String.class, null, "ID", null, Table.Align.LEFT);
        iTable.addContainerProperty("taskPriority", TaskModel.TASK_PRIORITY.class, null, "Priority", null, Table.Align.LEFT);
        iTable.addContainerProperty("taskType", TaskModel.TASK_TYPE.class, null, "Type", null, Table.Align.LEFT);
        iTable.addContainerProperty("taskUser", String.class, null, "Task user", null, Table.Align.LEFT);
        iTable.addContainerProperty("status", Task.STATUS.class, null, "Status", null, Table.Align.LEFT);
        iTable.addContainerProperty("taskTitle", String.class, null, "Task", null, Table.Align.LEFT);
        iTable.setColumnExpandRatio("taskTitle", 1f);
        iTable.addGeneratedColumn("taskTitle", new OpenTaskColumnGenerator(this));
        iTable.addContainerProperty("startDate", Date.class, null, "Start", null, Table.Align.LEFT);
        iTable.addContainerProperty("finishDate", Date.class, null, "Finish", null, Table.Align.LEFT);
        iTable.addContainerProperty("updateDate", Date.class, null, "Update", null, Table.Align.LEFT);
        iTable.setColumnWidth("updateDate", 150);
        iTable.setColumnWidth("finishDate", 150);
        iTable.setColumnWidth("startDate", 150);
        iTable.addGeneratedColumn("startDate", new DateColumnGenerator());
        iTable.addGeneratedColumn("finishDate", new DateColumnGenerator());
        iTable.addGeneratedColumn("updateDate", new DateColumnGenerator());
        iTable.addGeneratedColumn("taskPriority", new PriorityColumnGenerator());
        iTable.setColumnWidth("id", 80);
        iTable.setColumnWidth("taskPriority", 35);
        iTable.setVisibleColumns("id", "taskTitle", "taskUser", "startDate", "updateDate", "finishDate", "taskType", "status", "taskPriority");
    }

    @Override
    public void refreshTable() {
        iTable.removeAllItems();
        for (Task task : HybridbpmUI.getBpmAPI().getTasks(caseId)) {
            Item item = iTable.addItem(task);
            item.getItemProperty("id").setValue(task.getId().toString());
            item.getItemProperty("taskPriority").setValue(task.getTaskPriority());
            item.getItemProperty("taskTitle").setValue(task.getTaskTitle());
            item.getItemProperty("dueDate").setValue(task.getDueDate());
            item.getItemProperty("updateDate").setValue(task.getUpdateDate());
            item.getItemProperty("startDate").setValue(task.getStartDate());
            item.getItemProperty("finishDate").setValue(task.getFinishDate());
            item.getItemProperty("taskUser").setValue(task.getExecutor());
            item.getItemProperty("taskType").setValue(task.getTaskType());
            item.getItemProperty("status").setValue(task.getStatus());
        }
        iTable.sort(new Object[]{"id"}, new boolean[]{false});
    }

    @Override
    public void addNew() {

    }

}
