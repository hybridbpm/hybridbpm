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

import com.hybridbpm.core.data.access.Role;
import com.hybridbpm.core.data.development.Module;
import com.hybridbpm.core.util.HybridbpmCoreUtil;
import com.hybridbpm.model.ConnectorModel;
import com.hybridbpm.model.TaskModel;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.component.bpm.designer.ProcessModelLayout;
import com.hybridbpm.ui.component.bpm.window.VariableSuggester;
import com.hybridbpm.ui.component.development.InputInParametersLayout;
import com.hybridbpm.ui.component.development.OutputOutParametersLayout;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import org.vaadin.aceeditor.AceEditor;
import org.vaadin.aceeditor.AceMode;
import org.vaadin.aceeditor.AceTheme;
import org.vaadin.aceeditor.SuggestionExtension;

/**
 *
 * @author Marat Gubaidullin
 */
@SuppressWarnings("serial")
public class TaskConfigureCustomComponent extends CustomComponent implements Button.ClickListener, Property.ValueChangeListener {

    protected TaskConfigureLayout design = new TaskConfigureLayout();
    private final BeanFieldGroup<TaskModel> fieldGroup = new BeanFieldGroup<>(TaskModel.class);
    public static final String NAME = "NAME";
    protected ProcessModelLayout processModelLayout;

    @DesignRoot
    protected static class TaskConfigureLayout extends VerticalLayout {
        private OptionGroup typeOptionGroup;
        private TextField titleTextField;
        private TextField descriptionTextField;
        private OptionGroup splitOptionGroup;
        private OptionGroup joinOptionGroup;
        private TextField xTextField;
        private TextField yTextField;
        private ComboBox roleComboBox;
        private ComboBox formComboBox;
        private ComboBox mobileFormComboBox;
        private AceEditor actorScriptField;
        private VerticalLayout humanTaskLayout;
        private ComboBox connectorComboBox;
        private InputInParametersLayout inputInParametersLayout;
        private OutputOutParametersLayout outputOutParametersLayout;
        private VerticalLayout automaticTaskLayout;
        private TabSheet tabSheet;
    }

    public TaskConfigureCustomComponent() {
        Design.read(design);
        setCompositionRoot(design);
    }

    public void initUI(final ProcessModelLayout processModelLayout) {
        try {
            this.processModelLayout = processModelLayout;
            design.actorScriptField.setMode(AceMode.groovy);
            design.actorScriptField.setTheme(AceTheme.textmate);
            design.actorScriptField.setShowGutter(false);
            SuggestionExtension extension = new SuggestionExtension(new VariableSuggester(this.processModelLayout.getProcessModel()));
            extension.setSuggestOnDot(false);
            extension.extend(design.actorScriptField);

            design.typeOptionGroup.addValueChangeListener(new Property.ValueChangeListener() {

                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    if (event.getProperty().getValue().equals(TaskModel.TASK_TYPE.AUTOMATIC)) {
                        design.tabSheet.getTab(design.humanTaskLayout).setVisible(false);
                        design.tabSheet.getTab(design.automaticTaskLayout).setVisible(true);
                    } else {
                        design.tabSheet.getTab(design.humanTaskLayout).setVisible(true);
                        design.tabSheet.getTab(design.automaticTaskLayout).setVisible(false);
                    }
                }
            });
            for (TaskModel.TASK_TYPE type : TaskModel.TASK_TYPE.values()) {
                design.typeOptionGroup.addItem(type);
                design.typeOptionGroup.setItemCaption(type, type.name());
            }

            for (TaskModel.GATE_TYPE type : TaskModel.GATE_TYPE.values()) {
                design.joinOptionGroup.addItem(type);
                design.joinOptionGroup.setItemCaption(type, type.name());
            }

            for (TaskModel.GATE_TYPE type : TaskModel.GATE_TYPE.values()) {
                design.splitOptionGroup.addItem(type);
                design.splitOptionGroup.setItemCaption(type, type.name());
            }

            design.roleComboBox.addContainerProperty(NAME, String.class, null);
            design.roleComboBox.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
            design.roleComboBox.setItemCaptionPropertyId(NAME);
            for (Role instance : HybridbpmUI.getAccessAPI().getAllRoles()) {
                Item item = design.roleComboBox.addItem(instance.getName());
                item.getItemProperty(NAME).setValue(instance.getName());
            }

            design.formComboBox.addContainerProperty(NAME, String.class, null);
            design.formComboBox.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
            design.formComboBox.setItemCaptionPropertyId(NAME);
            for (Module module : HybridbpmUI.getDevelopmentAPI().getFormListForProcess(this.processModelLayout.getProcessModel().getName())) {
                Item item = design.formComboBox.addItem(module.getName());
                item.getItemProperty(NAME).setValue(module.getName());
            }
            
            design.mobileFormComboBox.addContainerProperty(NAME, String.class, null);
            design.mobileFormComboBox.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
            design.mobileFormComboBox.setItemCaptionPropertyId(NAME);
            for (Module module : HybridbpmUI.getDevelopmentAPI().getMobileFormListForProcess(this.processModelLayout.getProcessModel().getName())) {
                Item item = design.mobileFormComboBox.addItem(module.getName());
                item.getItemProperty(NAME).setValue(module.getName());
            }

            design.connectorComboBox.addContainerProperty(NAME, String.class, null);
            design.connectorComboBox.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
            design.connectorComboBox.setItemCaptionPropertyId(NAME);
            for (Module module : HybridbpmUI.getDevelopmentAPI().getModuleListByType(Module.MODULE_TYPE.CONNECTOR, false)) {
                Item item = design.connectorComboBox.addItem(module.getName());
                item.getItemProperty(NAME).setValue(module.getName());
            }
            design.connectorComboBox.addValueChangeListener(this);

            fieldGroup.bind(design.titleTextField, "name");
            fieldGroup.bind(design.xTextField, "x");
            fieldGroup.bind(design.yTextField, "y");
            fieldGroup.bind(design.joinOptionGroup, "joinType");
            fieldGroup.bind(design.splitOptionGroup, "splitType");
            fieldGroup.bind(design.typeOptionGroup, "taskType");
            fieldGroup.bind(design.titleTextField, "title");
            fieldGroup.bind(design.descriptionTextField, "description");
            fieldGroup.bind(design.roleComboBox, "role");
            fieldGroup.bind(design.actorScriptField, "actorScript");
            fieldGroup.bind(design.formComboBox, "form");
            fieldGroup.bind(design.mobileFormComboBox, "mobileForm");
            fieldGroup.bind(design.connectorComboBox, "connector");
            fieldGroup.setItemDataSource(this.processModelLayout.getActiveElement().getTaskModel());
        } catch (Exception ex) {
            ex.printStackTrace();
            Notification.show("Error", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
        }

    }

    protected void setOptionGroupProperties(OptionGroup optionGroup) {
        optionGroup.setNullSelectionAllowed(false);
        optionGroup.setHtmlContentAllowed(true);
        optionGroup.setImmediate(true);
    }

    public void save() {
        try {
            fieldGroup.commit();
            this.processModelLayout.getActiveElement().getTaskModel().getInParameters().clear();
            this.processModelLayout.getActiveElement().getTaskModel().getInParameters().putAll(design.inputInParametersLayout.getValues());
            this.processModelLayout.getActiveElement().getTaskModel().getOutParameters().clear();
            this.processModelLayout.getActiveElement().getTaskModel().getOutParameters().putAll(design.outputOutParametersLayout.getValues());
        } catch (Validator.InvalidValueException | FieldGroup.CommitException ex) {
            ex.printStackTrace();
            Notification.show("Error", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
        }
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
    }

    @Override
    public void valueChange(Property.ValueChangeEvent event) {
        String moduleName = (String) event.getProperty().getValue();
        Module module = HybridbpmUI.getDevelopmentAPI().getModuleByName(moduleName);
        ConnectorModel connectorModel = HybridbpmCoreUtil.jsonToObject(module.getModel(), ConnectorModel.class);
        design.inputInParametersLayout.setConnectoModel(connectorModel);
        design.inputInParametersLayout.initUI(new VariableSuggester(this.processModelLayout.getProcessModel()), this.processModelLayout.getActiveElement().getTaskModel().getInParameters());
        design.outputOutParametersLayout.setConnectoModel(connectorModel);
        design.outputOutParametersLayout.initUI(new VariableSuggester(this.processModelLayout.getProcessModel()), this.processModelLayout.getActiveElement().getTaskModel().getOutParameters());
    }

}
