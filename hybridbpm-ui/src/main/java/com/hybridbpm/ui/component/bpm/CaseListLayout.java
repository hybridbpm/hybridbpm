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
import com.hybridbpm.core.data.development.Module;
import com.hybridbpm.model.Translated;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.component.AbstractTableLayout;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class CaseListLayout extends AbstractTableLayout {

    private static final Logger logger = Logger.getLogger(CaseListLayout.class.getSimpleName());

    private TabSheet tabSheet;
    private final ComboBox caseType = new ComboBox("Type");
    private final ComboBox processModelComboBox = new ComboBox("Process model");
    private final ComboBox caseStatus = new ComboBox("Case status");
    private final PopupDateField fromDate = new PopupDateField("Start from:");
    private final PopupDateField toDate = new PopupDateField("Start to:");

    private final CASE_LIST columns;

    public enum CASE_LIST {

        DONE, IN_PROGRESS, ADMINISTRATION;
    };

    public CaseListLayout(CASE_LIST columns) {
        super();
        this.columns = columns;
        btnAdd.setVisible(false);
//        tools.addComponent(toDate, 0);
//        tools.addComponent(fromDate, 0);
//        tools.addComponent(caseStatus, 0);
//        tools.addComponent(processModelComboBox, 0);
//        tools.addComponent(caseType, 0);
//        tools.setComponentAlignment(processModelComboBox, Alignment.MIDDLE_LEFT);
        setMargin(new MarginInfo(true, false, false, false));

        for (Module module : HybridbpmUI.getDevelopmentAPI().getModuleListByType(Module.MODULE_TYPE.PROCESS, false)) {
            Item item = processModelComboBox.addItem(module.getName());
            processModelComboBox.setItemCaption(item, module.getTitle().getValue(HybridbpmUI.getCurrent().getLocale()));
            processModelComboBox.setItemIcon(item, FontAwesome.valueOf(module.getIcon()));
        }
        for (Case.STATUS stat : Case.STATUS.values()) {
            caseStatus.addItem(stat);
        }
        for (Case.TYPE type : Case.TYPE.values()) {
            caseType.addItem(type);
        }
        caseType.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                if (Objects.equals(event.getProperty().getValue(), Case.TYPE.ADAPTIVE)) {
                    processModelComboBox.setValue(null);
                    processModelComboBox.setEnabled(false);
                    processModelComboBox.setReadOnly(true);
                } else {
                    processModelComboBox.setEnabled(true);
                    processModelComboBox.setReadOnly(false);
                }
            }
        });
        switch (columns) {
            case DONE:
                iTable.setVisibleColumns("id", "caseTitle", "startDate", "finishDate", "status");
                caseStatus.setVisible(false);
                break;
            case IN_PROGRESS:
                iTable.setVisibleColumns("id", "caseTitle", "startDate", "updateDate");
                caseStatus.setVisible(false);
                break;
            case ADMINISTRATION:
                iTable.setVisibleColumns("id", "caseTitle", "initiator", "startDate", "updateDate", "finishDate", "status");
                caseStatus.setVisible(true);
                break;
        }
    }

    @Override
    public void buttonClick(final Button.ClickEvent event) {
        super.buttonClick(event);
        try {
            if (event.getButton().getData() != null && event.getButton().getData() instanceof Case) {
                Case case1 = (Case) event.getButton().getData();
                TabSheet.Tab tab = tabSheet.addTab(new CaseLayout(case1.getId().toString()), "Process " + case1.getId().toString());
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
        iTable.addContainerProperty("type", String.class, null, "Type", null, Table.Align.LEFT);
        iTable.addContainerProperty("caseTitle", String.class, null, "Title", null, Table.Align.LEFT);
        iTable.setColumnExpandRatio("caseTitle", 1f);
        iTable.addContainerProperty("startDate", Date.class, null, "Start", null, Table.Align.LEFT);
        iTable.addContainerProperty("updateDate", Date.class, null, "Update", null, Table.Align.LEFT);
        iTable.addContainerProperty("finishDate", Date.class, null, "Finish", null, Table.Align.LEFT);
        iTable.addContainerProperty("status", String.class, null, "Status", null, Table.Align.LEFT);
        iTable.addContainerProperty("initiator", String.class, null, "Initiator", null, Table.Align.LEFT);

        iTable.setColumnWidth("status", 120);
        iTable.setColumnWidth("startDate", 150);
        iTable.setColumnWidth("updateDate", 150);
        iTable.setColumnWidth("finishDate", 150);
        iTable.addGeneratedColumn("caseTitle", new OpenCaseColumnGenerator(this));
        iTable.addGeneratedColumn("startDate", new DateColumnGenerator());
        iTable.addGeneratedColumn("updateDate", new DateColumnGenerator());
        iTable.addGeneratedColumn("finishDate", new DateColumnGenerator());
    }

    @Override
    public void refreshTable() {
        iTable.removeAllItems();
        Map<String, Translated> titles = HybridbpmUI.getBpmAPI().getProcessModelTitles();

        List<Case> cases = new ArrayList<>();
        switch (columns) {
            case DONE:
                cases.addAll(HybridbpmUI.getBpmAPI().getMyCases((Case.TYPE) caseType.getValue(), (String) processModelComboBox.getValue(), fromDate.getValue(), toDate.getValue(), Case.STATUS.FINISHED));
                break;
            case IN_PROGRESS:
                cases.addAll(HybridbpmUI.getBpmAPI().getMyCases((Case.TYPE) caseType.getValue(), (String) processModelComboBox.getValue(), fromDate.getValue(), toDate.getValue(), Case.STATUS.STARTED));
                break;
            case ADMINISTRATION:
                if (caseStatus.getValue() != null) {
                    cases.addAll(HybridbpmUI.getBpmAPI().getCases((Case.TYPE) caseType.getValue(), (String) processModelComboBox.getValue(), null, fromDate.getValue(), toDate.getValue(), (Case.STATUS) caseStatus.getValue()));
                } else {
                    cases.addAll(HybridbpmUI.getBpmAPI().getCases((Case.TYPE) caseType.getValue(), (String) processModelComboBox.getValue(), null, fromDate.getValue(), toDate.getValue(), Case.STATUS.ERROR, Case.STATUS.FINISHED, Case.STATUS.STARTED, Case.STATUS.TERMINATED));
                }
                break;
        }

        for (Case c : cases) {
            Item item = iTable.addItem(c);
            item.getItemProperty("type").setValue(c.getType().name());
            StringBuilder title = new StringBuilder();
            if (Objects.equals(Case.TYPE.ADAPTIVE, c.getType())) {
                title.append(c.getTitle());
            } else {
                title.append(titles.get(c.getModelName()).getValue(HybridbpmUI.getCurrent().getLocale()));
            }
            item.getItemProperty("caseTitle").setValue(title.toString());
            item.getItemProperty("id").setValue(c.getId().toString());
            item.getItemProperty("initiator").setValue(c.getInitiator());
            item.getItemProperty("startDate").setValue(c.getStartDate());
            item.getItemProperty("updateDate").setValue(c.getUpdateDate());
            item.getItemProperty("finishDate").setValue(c.getFinishDate());
            item.getItemProperty("status").setValue(c.getStatus().toString());
        }
        iTable.sort(new Object[]{"startDate"}, new boolean[]{false});
    }

    @Override
    public void addNew() {

    }

    public void setTabSheet(TabSheet tabSheet) {
        this.tabSheet = tabSheet;
    }

}
