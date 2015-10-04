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
package com.hybridbpm.ui.component.bpm.designer;

import com.hybridbpm.ui.component.development.*;
import com.hybridbpm.core.util.HybridbpmCoreUtil;
import com.hybridbpm.core.data.development.Module;
import com.hybridbpm.model.ProcessModel;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.component.bpm.window.ProcessConfigureWindow;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marat Gubaidullin
 */
public class ProcessEditor extends AbstractEditor {

    private static final Logger logger = Logger.getLogger(ProcessEditor.class.getCanonicalName());

    private ProcessModel processModel;
    private Module Module;
    private final ProcessModelLayout processModelLayout = new ProcessModelLayout();
    private final HorizontalLayout editorBackground = new HorizontalLayout();
    private final CssLayout cssLayout = new CssLayout();
    protected final Panel mainPanel = new Panel();
    protected final Button btnData = new Button("Data", this);
    private final BeanFieldGroup<Module> binder = new BeanFieldGroup<>(Module.class);

    @Override
    public Module getModule() {
        return Module;
    }

    public ProcessEditor(Module Module) {
        super();
        this.Module = HybridbpmUI.getDevelopmentAPI().getModuleById(Module.getId());
        this.processModel = HybridbpmCoreUtil.jsonToObject(this.Module.getModel(), ProcessModel.class);
        btnData.setIcon(FontAwesome.DATABASE);
        btnData.setStyleName(ValoTheme.BUTTON_BORDERLESS);
        btnData.addStyleName(ValoTheme.BUTTON_SMALL);
        buttonBar.addComponent(btnData, 0);
        removeComponent(horizontalSplitPanel);
        mainPanel.setSizeFull();
        addComponent(mainPanel);
        setExpandRatio(mainPanel, 1f);
        prepareModeler();
    }

    private void prepareModeler() {

        processModelLayout.setProcessEditor(this);
        final DragAndDropWrapper pane = new DragAndDropWrapper(processModelLayout);
        pane.setDragStartMode(DragAndDropWrapper.DragStartMode.NONE);
        pane.setDropHandler(processModelLayout.dropHandler);
        processModelLayout.setProcessModel(processModel);
        processModelLayout.initUI();

        cssLayout.addComponent(pane);
        cssLayout.addStyleName("process-editor");
        cssLayout.setSizeFull();

        editorBackground.setSizeFull();
        editorBackground.setMargin(false);
        editorBackground.setSpacing(false);
        editorBackground.addComponent(cssLayout);

        mainPanel.setContent(editorBackground);
    }

    public void setProcessModel(ProcessModel processModel) {
        this.processModel = processModel;
        processModelLayout.setProcessModel(this.processModel);
        processModelLayout.initUI();
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        try {
            if (event.getButton().equals(btnSave)) {
                processModel.setName(Module.getName());
                Module.setModel(HybridbpmCoreUtil.objectToJson(processModel));
                Module = HybridbpmUI.getDevelopmentAPI().saveModule(Module);
                binder.setItemDataSource(Module);
            } else if (event.getButton().equals(btnData)) {
                ProcessConfigureWindow pcw = new ProcessConfigureWindow();
                pcw.initUI(processModelLayout);
                pcw.addCloseListener(new Window.CloseListener() {

                    @Override
                    public void windowClose(Window.CloseEvent e) {
                        setProcessModel(processModelLayout.getProcessModel());
                    }
                });
                HybridbpmUI.getCurrent().addWindow(pcw);
            }
        } catch (IllegalArgumentException | NullPointerException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

}
