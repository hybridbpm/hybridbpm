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
package com.hybridbpm.ui.component.dashboard;

import com.hybridbpm.core.data.access.Permission;
import com.hybridbpm.core.data.access.Role;
import com.hybridbpm.ui.component.TranslatedField;
import com.hybridbpm.core.data.dashboard.ViewDefinition;
import com.hybridbpm.ui.HybridbpmUI;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@DesignRoot
@SuppressWarnings("serial")
public final class ViewConfigurationLayout extends VerticalLayout{

    private ViewDefinition viewDefinition;
    private List<Permission> permissions;
    private TabSheet tabSheet;
    public Table accessTable;
    private TextField urlTextField;
    private TranslatedField titleTextField;
    private ComboBox iconComboBox;
    private BeanFieldGroup<ViewDefinition> binder = new BeanFieldGroup<>(ViewDefinition.class);

    public ViewConfigurationLayout(ViewDefinition viewDefinition) {
        Design.read(this);
        this.viewDefinition = viewDefinition;
        if (this.viewDefinition != null) {
            this.viewDefinition = HybridbpmUI.getDashboardAPI().getViewDefinitionById(viewDefinition.getId());
            permissions = HybridbpmUI.getDashboardAPI().getViewPermissions(this.viewDefinition.getId().toString());
        } else {
            Integer o = HybridbpmUI.getDashboardAPI().getNextViewOrder();
            this.viewDefinition = new ViewDefinition(o, "view" + o, "View " + o, FontAwesome.HTML5.name());
            permissions = HybridbpmUI.getDashboardAPI().getDefaultPermissions();
        }

        for (FontAwesome fontAwesome : FontAwesome.values()) {
            Item item = iconComboBox.addItem(fontAwesome.name());
            iconComboBox.setItemIcon(fontAwesome.name(), fontAwesome);
            iconComboBox.setItemCaption(fontAwesome.name(), fontAwesome.name());
        }
        iconComboBox.setItemCaptionMode(AbstractSelect.ItemCaptionMode.EXPLICIT);

        binder.setItemDataSource(this.viewDefinition);
        binder.bind(urlTextField, "url");
        binder.bind(titleTextField, "title");
        binder.bind(iconComboBox, "icon");
        binder.setBuffered(true);
        
        accessTable.addContainerProperty("role", String.class, null, "Role", null, Table.Align.LEFT);
        accessTable.addContainerProperty("canView", CheckBox.class, null, "Can view", null, Table.Align.CENTER);
        accessTable.setColumnWidth("canView", 100);
        accessTable.setVisibleColumns("role", "canView");
        
        for (Role role : HybridbpmUI.getAccessAPI().getAllRoles()){
            Item item = accessTable.addItem(role);
            item.getItemProperty("role").setValue(role.getName());
            CheckBox checkBox = new CheckBox(null, containsPermission(role));
            checkBox.setEnabled(!Objects.equals(role.getName(), Role.ADMINISTRATOR));
            item.getItemProperty("canView").setValue(checkBox);
        }
    }
    
    private boolean containsPermission(Role role){
        for (Permission permission : permissions){
            if (Objects.deepEquals(role.getName(), permission.getOut().getName())){
                return true;
            }
        }
        return false;
    }
    
    public ViewDefinition getViewDefinition() {
        try {
            binder.commit();
            return binder.getItemDataSource().getBean();
        } catch (FieldGroup.CommitException ex) {
            Logger.getLogger(ViewConfigurationLayout.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }
    
    public List<Permission> getViewPermissions() {
        List<Permission> list = new ArrayList<>();
        for (Object id : accessTable.getItemIds()){
            Role role = (Role) id;
            Item item = accessTable.getItem(id);
            CheckBox checkBox = (CheckBox) item.getItemProperty("canView").getValue();
            if (checkBox.getValue()){
                list.add(Permission.create(null, role, Permission.PERMISSION.VIEW));
            }
        }
        return list;
    }

}
