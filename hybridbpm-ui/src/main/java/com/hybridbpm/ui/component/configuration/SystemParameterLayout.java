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
package com.hybridbpm.ui.component.configuration;

import com.hybridbpm.core.data.Parameter;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import java.util.logging.Level;
import java.util.logging.Logger;

@DesignRoot
@SuppressWarnings("serial")
public final class SystemParameterLayout extends VerticalLayout {

    private Parameter parameterInstance;
    private TextField name;
    private TextField value;
    private ComboBox typeComboBox;
    private BeanFieldGroup<Parameter> binder = new BeanFieldGroup<>(Parameter.class);

    public SystemParameterLayout(Parameter parameterInstance) {
        Design.read(this);
        this.parameterInstance = parameterInstance;
        if (this.parameterInstance == null) {
            this.parameterInstance = new Parameter();
        }

        for (Parameter.PARAM_TYPE type : Parameter.PARAM_TYPE.values()) {
            Item item = typeComboBox.addItem(type);
            typeComboBox.setItemCaption(type, type.name());
        }
        typeComboBox.setItemCaptionMode(AbstractSelect.ItemCaptionMode.EXPLICIT);

        binder.setItemDataSource(this.parameterInstance);
        binder.bind(typeComboBox, "type");
        binder.bind(name, "name");
        binder.bind(value, "value");
        binder.setBuffered(true);
    }
    
    public Parameter getParameter() {
        try {
            binder.commit();
            return binder.getItemDataSource().getBean();
        } catch (FieldGroup.CommitException ex) {
            Logger.getLogger(SystemParameterLayout.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

}
