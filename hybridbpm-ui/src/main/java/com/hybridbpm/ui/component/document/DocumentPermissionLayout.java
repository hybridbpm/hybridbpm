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
package com.hybridbpm.ui.component.document;

import com.hybridbpm.core.data.access.Permission;
import com.hybridbpm.core.data.access.Role;
import com.hybridbpm.core.data.document.Document;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.component.PermissionsField;
import com.hybridbpm.ui.util.Translate;
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
public final class DocumentPermissionLayout extends VerticalLayout {

    public static final String NAME = "NAME";

    private final Document document;
    private PermissionsField permissionsField;
    private ComboBox comboBoxRole;
    private final BeanFieldGroup<Permission> binder = new BeanFieldGroup<>(Permission.class);

    public DocumentPermissionLayout(Document document, Permission permission) {
        Design.read(this);
        comboBoxRole.setCaption(Translate.getMessage("comboBoxRole"));
        permissionsField.setCaption(Translate.getMessage("permissionsField"));
        
        this.document = document;
        permission = permission == null ? new Permission() : permission;
        
        permissionsField.setPermissionsClass(Document.class);
        comboBoxRole.addContainerProperty(NAME, String.class, null);
        comboBoxRole.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        comboBoxRole.setItemCaptionPropertyId(NAME);
        for (Role instance : HybridbpmUI.getAccessAPI().getAllRoles()) {
            Item item = comboBoxRole.addItem(instance);
            item.getItemProperty(NAME).setValue(instance.getName());
        }
        comboBoxRole.setNullSelectionAllowed(false);
        
        binder.setItemDataSource(permission);
        binder.bind(permissionsField, "permissions");
        binder.bind(comboBoxRole, "out");
        binder.setBuffered(true);

        
    }

    public void save() {
        try {
            binder.commit();
            HybridbpmUI.getDocumentAPI().saveDocumentPermission(document, binder.getItemDataSource().getBean());
        } catch (FieldGroup.CommitException ex) {
            Logger.getLogger(DocumentPermissionLayout.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

}
