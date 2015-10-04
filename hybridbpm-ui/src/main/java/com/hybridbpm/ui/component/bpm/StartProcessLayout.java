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

import com.hybridbpm.core.data.bpm.StartProcess;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.component.AbstractTableLayout;
import com.vaadin.data.Item;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class StartProcessLayout extends AbstractTableLayout {

    private static final Logger logger = Logger.getLogger(StartProcessLayout.class.getSimpleName());

    private TabSheet tabSheet;

    public StartProcessLayout() {
        super();
        btnAdd.setVisible(false);
        setMargin(new MarginInfo(true, false, false, false));
    }

    @Override
    public void buttonClick(final Button.ClickEvent event) {
        super.buttonClick(event);
        try {
            if (event.getButton().getData() != null && event.getButton().getData() instanceof StartProcess) {
                StartProcess spd = (StartProcess) event.getButton().getData();
                TabSheet.Tab tab = tabSheet.addTab(new TaskLayout(null, spd.getProcessModel().getName(), spd.getTaskName(), true), spd.getProcessModel().getTaskModelByName(spd.getTaskName()).getTitle());
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
        iTable.addContainerProperty("taskName", String.class, null, "Task", null, Table.Align.LEFT);
        iTable.addContainerProperty("processName", String.class, null, "Process", null, Table.Align.LEFT);
        iTable.addGeneratedColumn("processName", new StartProcessColumnGenerator(this));
        iTable.setVisibleColumns("processName", "taskName");
    }

    @Override
    public void refreshTable() {
        try {
            iTable.removeAllItems();
            for (StartProcess startProcess : HybridbpmUI.getBpmAPI().getMyProcessToStart()) {
                Item item = iTable.addItem(startProcess);
                item.getItemProperty("taskName").setValue(startProcess.getProcessModel().getTaskModelByName(startProcess.getTaskName()).getTitle());
                item.getItemProperty("processName").setValue(startProcess.getProcessModel().getTitle().getValue(HybridbpmUI.getCurrent().getLocale()));
            }
            iTable.sort(new Object[]{"processName"}, new boolean[]{true});
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    @Override
    public void addNew() {

    }

    public void setTabSheet(TabSheet tabSheet) {
        this.tabSheet = tabSheet;
    }

}
