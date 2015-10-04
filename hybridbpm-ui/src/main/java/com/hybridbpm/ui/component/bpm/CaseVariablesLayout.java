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
import com.hybridbpm.core.data.bpm.Variable;
import com.hybridbpm.core.util.HybridbpmCoreUtil;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.component.AbstractTableLayout;
import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.themes.ValoTheme;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class CaseVariablesLayout extends AbstractTableLayout {

    private static final Logger logger = Logger.getLogger(CaseVariablesLayout.class.getSimpleName());
    private final String caseId;

    public CaseVariablesLayout(String caseId) {
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
        iTable.addContainerProperty("name", String.class, null, "Name", null, Table.Align.LEFT);
        iTable.addContainerProperty("className", String.class, null, "Class", null, Table.Align.LEFT);
        iTable.addContainerProperty("value", String.class, null, "Value", null, Table.Align.LEFT);
        iTable.setColumnExpandRatio("name", 1f);
        iTable.addContainerProperty("updateDate", Date.class, null, "Updated", null, Table.Align.LEFT);
        iTable.setColumnWidth("updateDate", 150);
        iTable.addGeneratedColumn("updateDate", new DateColumnGenerator());
        iTable.setColumnWidth("id", 80);
        iTable.setVisibleColumns("id", "name", "className", "updateDate", "value");
    }

    @Override
    public void refreshTable() {
        iTable.removeAllItems();

        Map<String, Object> variables = HybridbpmUI.getBpmAPI().getVariableValues(caseId);

        for (Variable variableInstance : HybridbpmUI.getBpmAPI().getProcessVariables(caseId)) {
            Item item = iTable.addItem(variableInstance);
            item.getItemProperty("id").setValue(variableInstance.getId().toString());
            item.getItemProperty("name").setValue(variableInstance.getName());
            item.getItemProperty("className").setValue(variableInstance.getClassName());
            item.getItemProperty("updateDate").setValue(variableInstance.getUpdateDate());
            item.getItemProperty("value").setValue(HybridbpmCoreUtil.objectToJson(variables.get(variableInstance.getName())));
        }
        iTable.sort(new Object[]{"id"}, new boolean[]{false});
    }

    @Override
    public void addNew() {

    }

}
