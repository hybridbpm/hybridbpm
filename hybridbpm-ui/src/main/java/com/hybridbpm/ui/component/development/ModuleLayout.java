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

import com.hybridbpm.ui.component.TranslatedField;
import com.hybridbpm.core.data.development.Module;
import com.hybridbpm.core.util.HybridbpmCoreUtil;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.IcoMoon;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.event.FieldEvents;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Marat Gubaidullin
 */
@DesignRoot
public class ModuleLayout extends VerticalLayout {

    private TextField name;
    private ComboBox iconComboBox;
    private CheckBox configurable;
    private CheckBox publishable;
    private OptionGroup moduleTypeOptionGroup;
    private OptionGroup subTypeOptionGroup;
    private ComboBox templateComboBox;
    private ComboBox processComboBox;
    private HorizontalLayout configurationLayout;
    private final TranslatedField titleTextField = new TranslatedField("Title");
    private final Module module;
    private final BeanFieldGroup<Module> binder = new BeanFieldGroup<>(Module.class);
    private boolean newModule = true;

    public ModuleLayout(Module module) {
        if (module == null) {
            this.module = new Module("", "", Module.MODULE_TYPE.DATA);
        } else {
            this.module = module;
            newModule = false;
        }
        Design.read(this);
        addComponent(titleTextField);
        for (FontAwesome fontAwesome : FontAwesome.values()) {
            iconComboBox.addItem(fontAwesome.name());
            iconComboBox.setItemIcon(fontAwesome.name(), fontAwesome);
            iconComboBox.setItemCaption(fontAwesome.name(), fontAwesome.name());
        }
        iconComboBox.setFilteringMode(FilteringMode.CONTAINS);

        for (Module m : HybridbpmUI.getDevelopmentAPI().getModuleListByType(Module.MODULE_TYPE.PROCESS, false)) {
            Item item = processComboBox.addItem(m.getName());
            processComboBox.setItemCaption(item, m.getTitle().getValue(HybridbpmUI.getCurrent().getLocale()));
            processComboBox.setItemIcon(item, FontAwesome.valueOf(m.getIcon()));
        }

        subTypeOptionGroup.addItem(Module.MODULE_SUBTYPE.TASK_FORM);
        subTypeOptionGroup.setItemCaption(Module.MODULE_SUBTYPE.TASK_FORM, "Task");

        subTypeOptionGroup.addItem(Module.MODULE_SUBTYPE.TEMPLATED_FORM);
        subTypeOptionGroup.setItemCaption(Module.MODULE_SUBTYPE.TEMPLATED_FORM, "Templated");

        moduleTypeOptionGroup.addItem(Module.MODULE_TYPE.DATA);
        moduleTypeOptionGroup.setItemIcon(Module.MODULE_TYPE.DATA, FontAwesome.valueOf(Module.MODULE_TYPE.DATA.getIcon()));

        moduleTypeOptionGroup.addItem(Module.MODULE_TYPE.PROCESS);
        moduleTypeOptionGroup.setItemIcon(Module.MODULE_TYPE.PROCESS, FontAwesome.valueOf(Module.MODULE_TYPE.PROCESS.getIcon()));

        moduleTypeOptionGroup.addItem(Module.MODULE_TYPE.FORM);
        moduleTypeOptionGroup.setItemIcon(Module.MODULE_TYPE.FORM, FontAwesome.valueOf(Module.MODULE_TYPE.FORM.getIcon()));

        moduleTypeOptionGroup.addItem(Module.MODULE_TYPE.CHART);
        moduleTypeOptionGroup.setItemIcon(Module.MODULE_TYPE.CHART, IcoMoon.valueOf(Module.MODULE_TYPE.CHART.getIcon()));

//        moduleTypeOptionGroup.addItem(Module.MODULE_TYPE.MOBILE);
//        moduleTypeOptionGroup.setItemIcon(Module.MODULE_TYPE.MOBILE, FontAwesome.valueOf(Module.MODULE_TYPE.MOBILE.getIcon()));

        moduleTypeOptionGroup.addItem(Module.MODULE_TYPE.CONNECTOR);
        moduleTypeOptionGroup.setItemIcon(Module.MODULE_TYPE.CONNECTOR, FontAwesome.valueOf(Module.MODULE_TYPE.CONNECTOR.getIcon()));

//        moduleTypeOptionGroup.addItem(Module.MODULE_TYPE.SCSS);
//        moduleTypeOptionGroup.setItemIcon(Module.MODULE_TYPE.SCSS, FontAwesome.valueOf(Module.MODULE_TYPE.SCSS.getIcon()));
        
        // add listener after definition of option groups
        moduleTypeOptionGroup.addValueChangeListener(new ModuleTypeChangeListener());
        subTypeOptionGroup.addValueChangeListener(new SubTypeChangeListener());

        binder.setItemDataSource(this.module);
        binder.bind(name, "name");
        binder.bind(titleTextField, "title");
        binder.bind(iconComboBox, "icon");
        binder.bind(configurable, "configurable");
        binder.bind(publishable, "publishable");
        binder.bind(moduleTypeOptionGroup, "type");
        binder.bind(subTypeOptionGroup, "subType");
        binder.bind(templateComboBox, "templateName");
        binder.bind(processComboBox, "processName");
        binder.setBuffered(true);
        name.setEnabled(newModule); // do not edit name
        if (newModule){
            name.addTextChangeListener((FieldEvents.TextChangeEvent event) -> {
                name.setValue(HybridbpmCoreUtil.checkClassName(event.getText()));
            });
        }
        moduleTypeOptionGroup.setEnabled(newModule); // do not edit type
    }

    public void save() {
        try {
            binder.commit();
            HybridbpmUI.getDevelopmentAPI().saveModule(module);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void configure(Module.MODULE_TYPE moduleType) {
        if (moduleType.equals(Module.MODULE_TYPE.DATA)) {
            configurationLayout.setVisible(false);
        } else if (moduleType.equals(Module.MODULE_TYPE.PROCESS)) {
            configurationLayout.setVisible(false);
            subTypeOptionGroup.setValue(null);
        } else if (moduleType.equals(Module.MODULE_TYPE.FORM)) {
            configurationLayout.setVisible(true);
            publishable.setValue(false);
            publishable.setVisible(false);
            subTypeOptionGroup.setVisible(true);
            templateComboBox.setVisible(true);
            subTypeOptionGroup.setValue(subTypeOptionGroup.getValue() != null ? subTypeOptionGroup.getValue() : Module.MODULE_SUBTYPE.TASK_FORM);
        } else if (moduleType.equals(Module.MODULE_TYPE.MOBILE)) {
            configurationLayout.setVisible(true);
            publishable.setValue(false);
            publishable.setVisible(false);
            subTypeOptionGroup.setVisible(true);
            templateComboBox.setValue(null);
            templateComboBox.setVisible(false);
            subTypeOptionGroup.setValue(subTypeOptionGroup.getValue() != null ? subTypeOptionGroup.getValue() : Module.MODULE_SUBTYPE.TASK_FORM);
            subTypeOptionGroup.setEnabled(false);
        } else if (moduleType.equals(Module.MODULE_TYPE.CONNECTOR)) {
            configurationLayout.setVisible(true);
            configurable.setVisible(false);
            publishable.setVisible(false);
            subTypeOptionGroup.setVisible(false);
            subTypeOptionGroup.setValue(null);
            templateComboBox.setVisible(true);
            templateComboBox.setCaption("Connector template");
            processComboBox.setVisible(false);
            fillTemplateComboBox(moduleType, null, true);
        } else if (moduleType.equals(Module.MODULE_TYPE.SCSS)) {
            configurationLayout.setVisible(false);
        }
    }

    private void fillTemplateComboBox(Module.MODULE_TYPE type, Module.MODULE_SUBTYPE subType, boolean template) {
        templateComboBox.removeAllItems();
        List<Module> modules = new ArrayList<>();
        if (Module.MODULE_TYPE.FORM.equals(type)) {
            modules.addAll(HybridbpmUI.getDevelopmentAPI().getModuleListByType(type, subType, template));
        } else {
            modules.addAll(HybridbpmUI.getDevelopmentAPI().getModuleListByType(type, template));
        }
        for (Module m : modules) {
            Item item = templateComboBox.addItem(m.getName());
            templateComboBox.setItemCaption(item, m.getTitle().getValue(HybridbpmUI.getCurrent().getLocale()));
            templateComboBox.setItemIcon(item, FontAwesome.valueOf(m.getIcon()));
        }
    }

    private void configureFormParameters(Module.MODULE_SUBTYPE moduleSubtype) {
        templateComboBox.setCaption("Form template");
        fillTemplateComboBox(Module.MODULE_TYPE.FORM, moduleSubtype, true);
        if (Module.MODULE_SUBTYPE.TEMPLATED_FORM.equals(moduleSubtype)) {
            publishable.setVisible(false);
            configurable.setVisible(true);
            processComboBox.setValue(null);
            processComboBox.setVisible(false);
        } else if (Module.MODULE_SUBTYPE.TASK_FORM.equals(moduleSubtype)) {
            publishable.setVisible(false);
            publishable.setValue(false);
            configurable.setVisible(false);
            configurable.setValue(false);
            processComboBox.setVisible(true);
        }
    }

    class ModuleTypeChangeListener implements ValueChangeListener {

        @Override
        public void valueChange(Property.ValueChangeEvent event) {
            configure((Module.MODULE_TYPE) event.getProperty().getValue());
        }

    }

    class SubTypeChangeListener implements ValueChangeListener {

        @Override
        public void valueChange(Property.ValueChangeEvent event) {
            configureFormParameters((Module.MODULE_SUBTYPE) event.getProperty().getValue());
        }
    }
}
