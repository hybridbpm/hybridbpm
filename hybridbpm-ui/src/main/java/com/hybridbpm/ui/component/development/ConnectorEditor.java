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
package com.hybridbpm.ui.component.development;

import com.hybridbpm.core.data.development.Module;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.component.ConfigureWindow;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.vaadin.aceeditor.AceEditor;
import org.vaadin.aceeditor.AceMode;
import org.vaadin.aceeditor.AceTheme;

/**
 *
 * @author Marat Gubaidullin
 */
public class ConnectorEditor extends AbstractEditor implements Window.CloseListener {

    private static final Logger logger = Logger.getLogger(ConnectorEditor.class.getCanonicalName());

    private Module Module;
    private final AceEditor codeEditor = new AceEditor();
    private final VerticalLayout codeEditorLayout = new VerticalLayout(codeEditor);

    private Button btnRun = new Button("Run test", this);
    private Button btnParameters = new Button("Parameters", this);
    private BeanFieldGroup<Module> binder = new BeanFieldGroup<>(Module.class);

    public ConnectorEditor(Module Module) {
        super();
        this.Module = HybridbpmUI.getDevelopmentAPI().getModuleById(Module.getId());;

        btnRun.setIcon(FontAwesome.PLAY);
        replaceComponent(horizontalSplitPanel, codeEditorLayout);
        btnParameters.setIcon(FontAwesome.WRENCH);
        buttonBar.addComponent(btnParameters, 0);
        buttonBar.addComponent(btnRun, 0);

        codeEditorLayout.setSizeFull();
        codeEditorLayout.addStyleName("code");
        codeEditorLayout.setMargin(new MarginInfo(false, false, false, true));

        binder.setItemDataSource(this.Module);
        binder.bind(codeEditor, "code");

        switch (Module.getType()) {
            case CONNECTOR:
                codeEditor.setCaption("Code");
                codeEditor.setMode(AceMode.groovy);
                codeEditor.setTheme(AceTheme.textmate);
                codeEditor.setShowGutter(true);
                codeEditor.setSizeFull();
                break;
        }
    }

    @Override
    public Module getModule() {
        return Module;
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton().equals(btnSave)) {
            save();
        } else if (event.getButton().equals(btnRun)) {
            openExecutionWindow();
        } else if (event.getButton().equals(btnParameters)) {
            openParameterConfigurationWindow();
        }
    }

    private void save() {
        try {
            binder.commit();
            Module = HybridbpmUI.getDevelopmentAPI().saveModule(Module);
            binder.setItemDataSource(Module);
        } catch (FieldGroup.CommitException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    @Override
    public void windowClose(Window.CloseEvent e) {
        Module = HybridbpmUI.getDevelopmentAPI().getModuleById(Module.getId());
        binder.setItemDataSource(Module);
    }

    private void openParameterConfigurationWindow() {
        final ConnectorParametersLayout connectorParametersLayout = new ConnectorParametersLayout(Module);
        final ConfigureWindow configureWindow = new ConfigureWindow(connectorParametersLayout, "Connector parameters");
        Button.ClickListener clickListener = new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (event.getButton().equals(configureWindow.btnClose)) {

                } else if (event.getButton().equals(configureWindow.btnOk)) {
                    connectorParametersLayout.save();
                }
                configureWindow.close();
            }
        };
        configureWindow.setClickListener(clickListener);
        configureWindow.addCloseListener(this);
        HybridbpmUI.getCurrent().addWindow(configureWindow);
    }
    
    private void openExecutionWindow() {
        final ConnectorExecutionLayout connectorExecutionLayout = new ConnectorExecutionLayout(Module);
        final ConfigureWindow configureWindow = new ConfigureWindow(connectorExecutionLayout, "Execute Connector");
        configureWindow.btnOk.setCaption("Run");
        Button.ClickListener clickListener = new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (event.getButton().equals(configureWindow.btnClose)) {
                    configureWindow.close();
                } else if (event.getButton().equals(configureWindow.btnOk)) {
                    connectorExecutionLayout.run();
                }
            }
        };
        configureWindow.setClickListener(clickListener);
        configureWindow.addCloseListener(this);
        HybridbpmUI.getCurrent().addWindow(configureWindow);
    }

}
