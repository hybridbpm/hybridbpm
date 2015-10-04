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
package com.hybridbpm.ui.component.sync;

import com.hybridbpm.core.data.sync.MobileTask;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.component.AbstractTableLayout;
import com.hybridbpm.ui.component.bpm.DateColumnGenerator;
import com.vaadin.data.Item;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class MobileTaskListLayout extends AbstractTableLayout {

    private static final Logger logger = Logger.getLogger(MobileTaskListLayout.class.getSimpleName());

    private TabSheet tabSheet;

    public MobileTaskListLayout() {
        super();
        btnAdd.setVisible(false);
        setMargin(new MarginInfo(true, false, false, false));
    }

    @Override
    public void buttonClick(final Button.ClickEvent event) {
        super.buttonClick(event);
        try {
            if (event.getButton().getData() != null && event.getButton().getData() instanceof MobileTask) {
//                Case case1 = (Case) event.getButton().getData();
//                TabSheet.Tab tab = tabSheet.addTab(new CaseLayout(case1.getId().toString()), "Process " + case1.getId().toString());
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
        iTable.addContainerProperty("id", String.class, null, "ID", null, Table.Align.LEFT);
        iTable.addContainerProperty("caseTitle", String.class, null, "Case title", null, Table.Align.LEFT);
        iTable.setColumnExpandRatio("caseTitle", 1f);
        iTable.addContainerProperty("taskTitle", String.class, null, "Task title", null, Table.Align.LEFT);
        iTable.setColumnExpandRatio("taskTitle", 1f);
        iTable.addContainerProperty("updateDate", Date.class, null, "Updated", null, Table.Align.LEFT);
        iTable.addContainerProperty("status", String.class, null, "Status", null, Table.Align.LEFT);
        iTable.addContainerProperty("executor", String.class, null, "Executor", null, Table.Align.LEFT);
        iTable.addContainerProperty("priority", String.class, null, "Priority", null, Table.Align.LEFT);
        iTable.addContainerProperty("form", String.class, null, "Form", null, Table.Align.LEFT);
        iTable.addContainerProperty("channels", List.class, null, "Form", null, Table.Align.LEFT);

        iTable.setColumnWidth("status", 120);
        iTable.setColumnWidth("priority", 120);
        iTable.setColumnWidth("executor", 120);
        iTable.setColumnWidth("updateDate", 150);
//        iTable.addGeneratedColumn("caseTitle", new OpenCaseColumnGenerator(this));
        iTable.addGeneratedColumn("updateDate", new DateColumnGenerator());
        
         iTable.setVisibleColumns("id", "caseTitle", "taskTitle", "updateDate", "executor", "status", "priority", "form", "channels");
  
    }

    @Override
    public void refreshTable() {
        iTable.removeAllItems();

        List<MobileTask> mobileTasks = HybridbpmUI.getSyncAPI().getMobileTasks();
        for (MobileTask mobileTask : mobileTasks) {
            Item item = iTable.addItem(mobileTask);
            item.getItemProperty("id").setValue(mobileTask.getId());
            item.getItemProperty("caseTitle").setValue(mobileTask.getCaseTitle());
            item.getItemProperty("taskTitle").setValue(mobileTask.getTaskTitle());
            item.getItemProperty("updateDate").setValue(mobileTask.getUpdateDate());
            item.getItemProperty("status").setValue(mobileTask.getStatus());
            item.getItemProperty("executor").setValue(mobileTask.getExecutor());
            item.getItemProperty("priority").setValue(mobileTask.getTaskPriority());
            item.getItemProperty("form").setValue(mobileTask.getForm());
            item.getItemProperty("channels").setValue(mobileTask.getChannels());
        }

        iTable.sort(new Object[]{"updateDate"}, new boolean[]{false});
    }

    @Override
    public void addNew() {

    }

    public void setTabSheet(TabSheet tabSheet) {
        this.tabSheet = tabSheet;
    }

}
