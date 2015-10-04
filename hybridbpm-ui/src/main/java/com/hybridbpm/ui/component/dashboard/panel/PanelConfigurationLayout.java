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
package com.hybridbpm.ui.component.dashboard.panel;

import com.hybridbpm.core.data.development.Module;
import com.hybridbpm.core.data.dashboard.PanelDefinition;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.component.TranslatedField;
import com.hybridbpm.ui.component.ParametersFieldGroup;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Marat Gubaidullin
 */
@DesignRoot
public class PanelConfigurationLayout extends VerticalLayout {

    private TabSheet tabSheet;
    private VerticalLayout root1;
    private VerticalLayout root2;
    private TranslatedField titleTextField;
    private OptionGroup moduleType;
    private ComboBox moduleName;
    private ParametersFieldGroup parametersFieldGroup;
    private PanelDefinition panelDefinition;
    private final BeanFieldGroup<PanelDefinition> binder = new BeanFieldGroup<>(PanelDefinition.class);

    public PanelConfigurationLayout(PanelDefinition pd) {
        this.panelDefinition = HybridbpmUI.getDashboardAPI().getPanelDefinitionsById(pd.getId());
        Design.read(this);
        root1.addComponent(titleTextField, 0);
        root2.addComponent(parametersFieldGroup);

        binder.setItemDataSource(this.panelDefinition);
        binder.bind(titleTextField, "title");
        binder.bind(moduleType, "moduleType");
        binder.bind(moduleName, "moduleName");
        binder.bind(parametersFieldGroup, "parameters");
        binder.setBuffered(true);

        Item item = moduleType.addItem(Module.MODULE_TYPE.FORM);
        item = moduleType.addItem(Module.MODULE_TYPE.CHART);
        moduleType.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                moduleName.removeAllItems();
                List<Module> modules = new ArrayList<>();
                if (Objects.equals(event.getProperty().getValue(), Module.MODULE_TYPE.CHART)){
                    modules.addAll(HybridbpmUI.getDevelopmentAPI().getModuleListByType(Module.MODULE_TYPE.CHART, false));
                } else if (Objects.equals(event.getProperty().getValue(), Module.MODULE_TYPE.FORM)){
                    modules.addAll(HybridbpmUI.getDevelopmentAPI().getModuleListByType(Module.MODULE_TYPE.FORM, Module.MODULE_SUBTYPE.TEMPLATED_FORM, false));
                }
                for (Module m : modules) {
                    Item i = moduleName.addItem(m.getName());
                    moduleName.setItemCaption(i, m.getTitle().getValue(HybridbpmUI.getCurrent().getLocale()));
                    moduleName.setItemIcon(i, FontAwesome.valueOf(m.getIcon()));
                }
            }
        });
        
        moduleName.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                
            }
        });

    }

    public void save() {
        try {
            binder.commit();
            HybridbpmUI.getDashboardAPI().savePanelDefinition(panelDefinition);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
