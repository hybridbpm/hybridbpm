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
import com.hybridbpm.ui.util.HybridbpmStyle;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.util.HybridbpmUiUtil;
import com.hybridbpm.ui.component.bpm.window.FormConfigureWindow;
import com.hybridbpm.ui.view.DevelopmentView;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.sass.SassCompiler;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Sizeable;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.vaadin.aceeditor.AceEditor;
import org.vaadin.aceeditor.AceMode;
import org.vaadin.aceeditor.AceTheme;

/**
 *
 * @author Marat Gubaidullin
 */
public class FormEditor extends AbstractEditor implements Window.CloseListener {

    private static final Logger logger = Logger.getLogger(FormEditor.class.getCanonicalName());

    private Module module;
    private final AceEditor codeEditor = new AceEditor();
    private final AceEditor designEditor = new AceEditor();
    private final VerticalLayout codeEditorLayout = new VerticalLayout(codeEditor);
    private final VerticalLayout designEditorLayout = new VerticalLayout(designEditor);

    private Button btnRun = new Button("Test", this);
    private Button btnParameters = new Button("Parameters", this);
    private BeanFieldGroup<Module> binder = new BeanFieldGroup<>(Module.class);

    public FormEditor(Module module) {
        super();
        this.module = HybridbpmUI.getDevelopmentAPI().getModuleById(module.getId());;

        btnRun.setIcon(FontAwesome.PLAY);
        btnRun.setStyleName(ValoTheme.BUTTON_BORDERLESS);
        btnRun.addStyleName(ValoTheme.BUTTON_SMALL);

        btnParameters.setIcon(FontAwesome.WRENCH);
        btnParameters.setStyleName(ValoTheme.BUTTON_BORDERLESS);
        btnParameters.addStyleName(ValoTheme.BUTTON_SMALL);

        horizontalSplitPanel.addComponents(designEditorLayout, codeEditorLayout);
        buttonBar.addComponent(btnParameters, 0);
        buttonBar.addComponent(btnRun, 0);

        codeEditorLayout.setSizeFull();
        codeEditorLayout.addStyleName("code");
        codeEditorLayout.setMargin(new MarginInfo(false, false, false, true));
        designEditorLayout.setSizeFull();
        designEditorLayout.addStyleName("template");
        designEditorLayout.setMargin(new MarginInfo(false, true, false, false));

        binder.setItemDataSource(this.module);
        binder.bind(codeEditor, "code");
        binder.bind(designEditor, "design");

        switch (module.getType()) {
            case FORM:
                designEditor.setCaption("Design");
                designEditor.setMode(AceMode.html);
                designEditor.setTheme(AceTheme.textmate);
                designEditor.setShowGutter(true);
                designEditor.setSizeFull();
                codeEditor.setCaption("Code");
                codeEditor.setMode(AceMode.groovy);
                codeEditor.setTheme(AceTheme.textmate);
                codeEditor.setShowGutter(true);
                codeEditor.setSizeFull();
                horizontalSplitPanel.setSplitPosition(50, Sizeable.Unit.PERCENTAGE);
                btnRun.setCaption("Test");
                break;
        }
        if (module.getTemplate()) {
            btnRun.setVisible(false);
            btnParameters.setVisible(false);
        }
    }

    @Override
    public Module getModule() {
        return module;
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton().equals(btnSave)) {
            save();
        } else if (event.getButton().equals(btnRun)) {
            run();
        } else if (event.getButton().equals(btnParameters)) {
            FormConfigureWindow fcw = new FormConfigureWindow(module);
            fcw.addCloseListener(this);
            HybridbpmUI.getCurrent().addWindow(fcw);
        }
    }

    private void save() {
        try {
            binder.commit();
            module = HybridbpmUI.getDevelopmentAPI().saveModule(module);
            binder.setItemDataSource(module);
        } catch (FieldGroup.CommitException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private DevelopmentView getDevelopmentView() {
        Component result = this;
        while (!(result instanceof DevelopmentView)) {
            result = result.getParent();
            if (result == null) {
                return null;
            }
        }
        return (DevelopmentView) result;
    }

    private void run() {
        try {
            save();
            Component component = (Component) HybridbpmUiUtil.generateFormObject(module);
            if (component != null) {
                getDevelopmentView().openTab(component, module);
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            Notification.show("Error", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
        }
    }

    private void compileTheme() {
        try {
            binder.commit();
            ServletContext context = VaadinServlet.getCurrent().getServletContext();
            String fullPath = context.getRealPath("/VAADIN/themes/dashboard");
            String customScssFileName = fullPath + "/custom.scss";
            SassCompiler.writeFile(customScssFileName, module.getModel());

            ProcessBuilder processBuilder = new ProcessBuilder("java", "-cp", "../../../WEB-INF/lib/*", "com.vaadin.sass.SassCompiler", "styles.scss", "styles.css");
            processBuilder.directory(new File(fullPath));
            File error = new File(fullPath, "custom.scss.log");
            processBuilder.redirectErrorStream(true);
            processBuilder.redirectError(Redirect.PIPE);
            Process process = processBuilder.start();
            process.waitFor();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder builder = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                builder.append(line);
                line = reader.readLine();
            }
            reader.close();
            if (!builder.toString().trim().isEmpty()) {
                throw new Exception(builder.toString());
            }

        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            Notification.show("Error", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
        }
    }

    @Override
    public void windowClose(Window.CloseEvent e) {
        module = HybridbpmUI.getDevelopmentAPI().getModuleById(module.getId());
        binder.setItemDataSource(module);
    }

    private class ShowWindow extends Window {

        private VerticalLayout layout = new VerticalLayout();

        public ShowWindow(String caption, Component component) {
            super(caption);
            setContent(layout);
            layout.setSizeFull();
            layout.addComponent(component);
            layout.setMargin(true);
            layout.setStyleName("card");
            layout.setStyleName(HybridbpmStyle.LAYOUT_PADDING16);
            center();
            setResizable(true);
            setWidth(80, Unit.PERCENTAGE);
            setHeight(80, Unit.PERCENTAGE);
        }

    }

}
