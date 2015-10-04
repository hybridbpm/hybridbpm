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
package com.hybridbpm.ui.component.bpm.window;

import com.hybridbpm.ui.component.bpm.designer.FileEditorLayout;
import com.hybridbpm.ui.component.bpm.designer.ProcessModelLayout;
import com.hybridbpm.ui.component.bpm.designer.VariableEditorLayout;
import com.hybridbpm.ui.component.development.FieldForm;
import com.hybridbpm.ui.component.development.FileForm;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marat Gubaidullin
 */
public class ProcessConfigureWindow extends Window implements Button.ClickListener {

    public static final Logger logger = Logger.getLogger(ProcessConfigureWindow.class.getCanonicalName());
    private final VerticalLayout layout = new VerticalLayout();
    private final TabSheet tabSheet = new TabSheet();

    private final VariableEditorLayout variableEditorLayout = new VariableEditorLayout();
    private final FileEditorLayout fileEditorLayout = new FileEditorLayout();

    protected HorizontalLayout toolbar = new HorizontalLayout();
    private final Button btnOk = new Button(null, this);
    private final Button btnClose = new Button(null, this);
    private ProcessModelLayout processModelLayout;

    public ProcessConfigureWindow() {
    }

    public void initUI(ProcessModelLayout processModelLayout) {
        this.processModelLayout = processModelLayout;
        variableEditorLayout.setProcessModel(processModelLayout.getProcessModel());
        variableEditorLayout.initUI();
        fileEditorLayout.setProcessModel(processModelLayout.getProcessModel());
        fileEditorLayout.initUI();

        toolbar.setSpacing(true);
        toolbar.addStyleName("toolbar");

        btnOk.addStyleName(ValoTheme.BUTTON_PRIMARY);
        btnOk.setCaption("OK");
        toolbar.addComponent(btnOk);
        toolbar.setComponentAlignment(btnOk, Alignment.MIDDLE_RIGHT);

        btnClose.setCaption("Close");
        toolbar.addComponent(btnClose);
        toolbar.setComponentAlignment(btnClose, Alignment.MIDDLE_RIGHT);

//        cssLayout.addComponent(variableEditorLayout);
//        cssLayout.setSizeFull();
//        cssLayout.addStyleName("scrollable");
        tabSheet.setSizeFull();
        tabSheet.addTab(variableEditorLayout, "Data");
        tabSheet.addTab(fileEditorLayout, "Files");
        
        layout.setSizeFull();
        layout.setMargin(true);
        layout.addComponent(tabSheet);
        layout.setExpandRatio(tabSheet, 1f);
        layout.addComponent(toolbar);
        layout.setComponentAlignment(toolbar, Alignment.BOTTOM_RIGHT);

        setCaption("Process data");
        center();
        setResizable(false);
//        setClosable(false);
        setModal(true);
        addStyleName("no-vertical-drag-hints");
        addStyleName("no-horizontal-drag-hints");
        setContent(layout);
        setWidth(80, Unit.PERCENTAGE);
        setHeight(80, Unit.PERCENTAGE);
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton().equals(btnClose)) {
            close();
        } else if (event.getButton().equals(btnOk)) {
            try {
                processModelLayout.getProcessModel().getVariableModels().clear();
                processModelLayout.getProcessModel().getFileModels().clear();
                
                for (Component comp : variableEditorLayout) {
                    if (comp instanceof FieldForm) {
                        FieldForm fieldForm = (FieldForm) comp;
                        fieldForm.commit();
                        processModelLayout.getProcessModel().getVariableModels().add(fieldForm.getFieldModel());
                    }
                }
                
                for (Component comp : fileEditorLayout) {
                    if (comp instanceof FileForm) {
                        FileForm fileForm = (FileForm) comp;
                        fileForm.commit();
                        processModelLayout.getProcessModel().getFileModels().add(fileForm.getFileModel());
                    }
                }
                close();
            } catch (Exception ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
                Notification.show("Error", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
            }
        }
    }
}
