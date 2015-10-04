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

import com.hybridbpm.model.TransitionModel;
import com.hybridbpm.ui.component.bpm.designer.ProcessModelLayout;
import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.aceeditor.AceEditor;
import org.vaadin.aceeditor.AceMode;
import org.vaadin.aceeditor.AceTheme;
import org.vaadin.aceeditor.SuggestionExtension;

/**
 *
 * @author Marat Gubaidullin
 */
public class TransitionConfigureWindow extends Window implements Button.ClickListener {

    private final BeanFieldGroup<TransitionModel> fieldGroup = new BeanFieldGroup<>(TransitionModel.class);
    private final VerticalLayout layout = new VerticalLayout();
    private final GridLayout configurationLayout = new GridLayout(2, 3);
    private final TextField nameTextField = new TextField();
    private final AceEditor expressionTextField = new AceEditor();
    private final CheckBox defaultCheckBox = new CheckBox();
    private final TextField xTextField = new TextField();
    private final TextField yTextField = new TextField();

    protected HorizontalLayout toolbar = new HorizontalLayout();
    private final Button btnOk = new Button(null, this);
    private final Button btnClose = new Button(null, this);
    private final Button btnShowMore = new Button(null, this);
    private ProcessModelLayout processModelLayout;

    public TransitionConfigureWindow() {
    }

    public void initUI(ProcessModelLayout processModelLayout) {
        this.processModelLayout = processModelLayout;
        nameTextField.setCaption(("Name"));
        nameTextField.setWidth(300, Unit.PIXELS);
        nameTextField.setNullRepresentation("");

        expressionTextField.setCaption(("Condition Expression"));
        expressionTextField.setWidth(100, Unit.PERCENTAGE);
        expressionTextField.setHeight(50, Unit.PIXELS);
        expressionTextField.setMode(AceMode.groovy);
        expressionTextField.setTheme(AceTheme.chrome);
        expressionTextField.setShowGutter(false);
        SuggestionExtension extension = new SuggestionExtension(new VariableSuggester(this.processModelLayout.getProcessModel()));
        extension.setSuggestOnDot(false);
        extension.extend(expressionTextField);

        defaultCheckBox.setCaption(("Default Transition"));

        yTextField.setCaption(("Y"));
        yTextField.setWidth(100, Unit.PIXELS);
        yTextField.setNullRepresentation("");
        yTextField.setVisible(false);

        xTextField.setCaption(("Y"));
        xTextField.setWidth(100, Unit.PIXELS);
        xTextField.setNullRepresentation("");
        xTextField.setVisible(false);

//        configurationLayout.setCaption("Transition properties");
//        configurationLayout.addStyleName("process-config");
        configurationLayout.setMargin(new MarginInfo(true, true, true, true));
        configurationLayout.setSpacing(true);
        configurationLayout.addComponent(nameTextField, 0, 0, 0, 0);
        configurationLayout.addComponent(defaultCheckBox, 1, 0, 1, 0);
        configurationLayout.setComponentAlignment(defaultCheckBox, Alignment.BOTTOM_RIGHT);
        configurationLayout.addComponent(expressionTextField, 0, 1, 1, 1);
        configurationLayout.addComponent(xTextField, 0, 2, 0, 2);
        configurationLayout.addComponent(yTextField, 1, 2, 1, 2);
        configurationLayout.setColumnExpandRatio(0, 1f);
        configurationLayout.setColumnExpandRatio(1, 1f);
        configurationLayout.setColumnExpandRatio(2, 1f);

        fieldGroup.setBuffered(true); //important
        fieldGroup.bind(xTextField, "x");
        fieldGroup.bind(yTextField, "y");
        fieldGroup.bind(nameTextField, "name");
        fieldGroup.bind(expressionTextField, "expression");
        fieldGroup.bind(defaultCheckBox, "defaultTransition");
        fieldGroup.setItemDataSource(processModelLayout.getActiveElement().getTransitionModel());

        toolbar.setSpacing(true);
        toolbar.setWidth(100, Unit.PERCENTAGE);
        toolbar.addStyleName("toolbar");

        btnShowMore.setCaption(("Show more"));
        btnShowMore.setStyleName(ValoTheme.BUTTON_LINK);
        toolbar.addComponent(btnShowMore);
        toolbar.setComponentAlignment(btnShowMore, Alignment.MIDDLE_LEFT);
        toolbar.setExpandRatio(btnShowMore, 1f);

        btnOk.addStyleName(ValoTheme.BUTTON_PRIMARY);
        btnOk.setCaption(("OK"));
        toolbar.addComponent(btnOk);
        toolbar.setComponentAlignment(btnOk, Alignment.MIDDLE_RIGHT);

        btnClose.setCaption(("Close"));
        toolbar.addComponent(btnClose);
        toolbar.setComponentAlignment(btnClose, Alignment.MIDDLE_RIGHT);

        layout.addStyleName("process-config-layout");
        layout.setSpacing(true);
        layout.setMargin(true);
        layout.addComponent(configurationLayout);
        layout.addComponent(toolbar);
        layout.setComponentAlignment(toolbar, Alignment.MIDDLE_RIGHT);

        setCaption("Transition");
        center();
        setResizable(false);
//        setClosable(false);
        setModal(true);
        addStyleName("no-vertical-drag-hints");
        addStyleName("no-horizontal-drag-hints");
        addStyleName("process-config-window");
        setContent(layout);
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton().equals(btnClose)) {
            close();
        } else if (event.getButton().equals(btnShowMore)) {
            xTextField.setVisible(!xTextField.isVisible());
            yTextField.setVisible(!yTextField.isVisible());
        } else if (event.getButton().equals(btnOk)) {
            try {
                // check transition name uniqueness
                if (!processModelLayout.getActiveElement().getTransitionModel().getName().equalsIgnoreCase(nameTextField.getValue())
                        && processModelLayout.getProcessModel().getTransitionModelByName(nameTextField.getValue()) != null) {
                    throw new Validator.InvalidValueException("Transition name already exists!");
                }
                fieldGroup.commit();
                close();
            } catch (Validator.InvalidValueException | FieldGroup.CommitException ex) {
                ex.printStackTrace();
                Notification.show(("error"), ex.getMessage(), Notification.Type.ERROR_MESSAGE);
            }
        }
    }

}
