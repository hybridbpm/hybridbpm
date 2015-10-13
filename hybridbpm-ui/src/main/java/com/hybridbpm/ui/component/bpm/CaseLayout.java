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
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.util.Translate;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.dialogs.ConfirmDialog;

/**
 *
 * @author Marat Gubaidullin
 */
@SuppressWarnings("serial")
public class CaseLayout extends VerticalLayout implements Button.ClickListener {

    private final String caseId;

    private Case hCase;

    private final TabSheet tabSheet = new TabSheet();
    private final Button btnTerminate = new Button("Terminate", this);
    private final Button btnDelete = new Button("Delete", this);
    private final HorizontalLayout buttonBar = new HorizontalLayout(btnDelete, btnTerminate);
    private final CaseFormHeader caseFormHeader = new CaseFormHeader();

    private CaseTaskListLayout caseTaskListLayout;
    private CaseVariablesLayout caseVariablesLayout;
    private TaskCommentsLayout taskCommentsLayout;

    public CaseLayout(String caseId) {
        this.caseId = caseId;

        Responsive.makeResponsive(this);
        btnDelete.setIcon(FontAwesome.TIMES);
        btnTerminate.setIcon(FontAwesome.STOP);
        btnTerminate.setStyleName(ValoTheme.BUTTON_PRIMARY);

        buttonBar.setWidth(100, Unit.PERCENTAGE);
        buttonBar.setSpacing(true);
        buttonBar.addStyleName("toolbar");
        buttonBar.setExpandRatio(btnDelete, 1f);
        buttonBar.setComponentAlignment(btnDelete, Alignment.MIDDLE_RIGHT);
        buttonBar.setComponentAlignment(btnTerminate, Alignment.MIDDLE_RIGHT);

        tabSheet.setStyleName(ValoTheme.TABSHEET_FRAMED);
        tabSheet.setSizeFull();

        setSizeFull();
        setSpacing(true);
        addComponent(caseFormHeader);
        addComponent(tabSheet);
        addComponent(buttonBar);
        setExpandRatio(tabSheet, 1f);

        loadForm();
    }

    private void loadForm() {
        prepareData();
        prepareHeader();
        createCaseTaskListLayout();
        createDiscussionTab();
        configureButtons();
    }

    private void prepareData() {
        hCase = HybridbpmUI.getBpmAPI().getCaseById(caseId);
    }

    private void prepareHeader() {
        caseFormHeader.setData(hCase);
    }

    private void createCaseTaskListLayout() {
        caseTaskListLayout = new CaseTaskListLayout(caseId);
        caseTaskListLayout.refreshTable();
        tabSheet.addTab(caseTaskListLayout, "Tasks");
        if (HybridbpmUI.getAccessAPI().isAdministrator()) {
            caseVariablesLayout = new CaseVariablesLayout(caseId);
            caseVariablesLayout.refreshTable();
            tabSheet.addTab(caseVariablesLayout, "Variables");
        }
    }

    private void createDiscussionTab() {
        taskCommentsLayout = new TaskCommentsLayout(caseId, null);
        taskCommentsLayout.initUI();
        tabSheet.addTab(taskCommentsLayout, "Comments");
    }

    private void configureButtons() {
        if (hCase.getStatus().equals(Case.STATUS.STARTED)) {
            btnTerminate.setEnabled(true);
            btnDelete.setEnabled(false);
        } else if (hCase.getStatus().equals(Case.STATUS.ERROR)) {
            btnTerminate.setEnabled(true);
            btnDelete.setEnabled(false);
        } else if (hCase.getStatus().equals(Case.STATUS.FINISHED)) {
            btnTerminate.setEnabled(false);
            btnDelete.setEnabled(true);
        } else if (hCase.getStatus().equals(Case.STATUS.TERMINATED)) {
            btnTerminate.setEnabled(false);
            btnDelete.setEnabled(true);
        }
        if (!HybridbpmUI.getAccessAPI().isAdministrator()) {
            buttonBar.setVisible(false);
        }
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton().equals(btnDelete)) {
            ConfirmDialog.show(UI.getCurrent(), Translate.getMessage("windowTitleConfirm"), "Delete case and all data?", Translate.getMessage("btnOK"), Translate.getMessage("btnCancel"), new ConfirmDialog.Listener() {

                @Override
                public void onClose(ConfirmDialog dialog) {
                    if (dialog.isConfirmed()) {
                        HybridbpmUI.getBpmAPI().deleteCase(caseId);
                        close();
                    }
                }
            });
        } else if (event.getButton().equals(btnTerminate)) {
            ConfirmDialog.show(UI.getCurrent(), Translate.getMessage("windowTitleConfirm"), "Terminate case and all tasks?", Translate.getMessage("btnOK"), Translate.getMessage("btnCancel"), new ConfirmDialog.Listener() {

                @Override
                public void onClose(ConfirmDialog dialog) {
                    if (dialog.isConfirmed()) {
                        HybridbpmUI.getBpmAPI().terminateCase(caseId);
                        loadForm();
                    }
                }
            });
        }
    }

    private void close() {
        if (getParent() instanceof TabSheet) {
            TabSheet parent = (TabSheet) getParent();
            parent.removeTab(parent.getTab(this));
            parent.setSelectedTab(2);
        }
    }
}
