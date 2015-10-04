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
package com.hybridbpm.ui.component.access;

import com.hybridbpm.core.data.access.Group;
import com.hybridbpm.core.data.access.RoleGroup;
import com.hybridbpm.core.data.access.Role;
import com.hybridbpm.ui.HybridbpmUI;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import java.util.logging.Level;
import java.util.logging.Logger;

@DesignRoot
@SuppressWarnings("serial")
public final class RoleGroupLayout extends VerticalLayout {
    
    public static final String NAME = "NAME";

    private ComboBox roleComboBox;
    private ComboBox groupComboBox;

    public RoleGroupLayout() {
        Design.read(this);
        
        roleComboBox.addContainerProperty(NAME, String.class, null);
        roleComboBox.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        roleComboBox.setItemCaptionPropertyId(NAME);
        for (Role instance : HybridbpmUI.getAccessAPI().getAllRoles()) {
            Item item = roleComboBox.addItem(instance.getId());
            item.getItemProperty(NAME).setValue(instance.getName());
        }
        groupComboBox.addContainerProperty(NAME, String.class, null);
        groupComboBox.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        groupComboBox.setItemCaptionPropertyId(NAME);
        for (Group instance : HybridbpmUI.getAccessAPI().getAllGroups()) {
            Item item = groupComboBox.addItem(instance.getId());
            item.getItemProperty(NAME).setValue(instance.getName());
        }
        roleComboBox.setNullSelectionAllowed(false);
        groupComboBox.setNullSelectionAllowed(false);
    }

    public void save() {
        try {
            HybridbpmUI.getAccessAPI().addRoleGroup(roleComboBox.getValue().toString(), groupComboBox.getValue().toString());
        } catch (Exception ex) {
            Logger.getLogger(RoleGroupLayout.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

}
