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
package com.hybridbpm.ui.component.dashboard.tab;

import com.hybridbpm.core.data.access.Permission;
import com.hybridbpm.core.data.access.Role;
import com.hybridbpm.core.data.dashboard.TabDefinition;
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
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@DesignRoot
@SuppressWarnings("serial")
public final class TabConfigurationLayout extends VerticalLayout{

    private TabDefinition tabDefinition;
    private List<Permission> permissions;
    private TabSheet tabSheet;
    public Table accessTable;
    protected OptionGroup layoutOptionGroup;
    private TranslatedField titleTextField;
    private ComboBox iconComboBox;
    private BeanFieldGroup<TabDefinition> binder = new BeanFieldGroup<>(TabDefinition.class);

    public TabConfigurationLayout(TabDefinition tab, ViewDefinition viewDefinition) {
        Design.read(this);
        this.tabDefinition = tab;
        if (this.tabDefinition != null) {
            this.tabDefinition = HybridbpmUI.getDashboardAPI().getTabDefinitionById(this.tabDefinition.getId());
            permissions = HybridbpmUI.getDashboardAPI().getTabPermissions(this.tabDefinition.getId().toString());
        } else {
            Integer o = HybridbpmUI.getDashboardAPI().getNextTabOrder(viewDefinition.getId().toString());
            this.tabDefinition = TabDefinition.createDefaultVertical();
            this.tabDefinition.setViewId(viewDefinition);
            this.tabDefinition.setOrder(o);
            permissions = HybridbpmUI.getDashboardAPI().getDefaultPermissions();
        }
        this.tabDefinition.setViewId(viewDefinition);

        for (FontAwesome fontAwesome : FontAwesome.values()) {
            Item item = iconComboBox.addItem(fontAwesome.name());
            iconComboBox.setItemIcon(fontAwesome.name(), fontAwesome);
            iconComboBox.setItemCaption(fontAwesome.name(), fontAwesome.name());
        }
        iconComboBox.setItemCaptionMode(AbstractSelect.ItemCaptionMode.EXPLICIT);
        
        for (TabDefinition.LAYOUT_TYPE layout : TabDefinition.LAYOUT_TYPE.values()) {
            layoutOptionGroup.addItem(layout);
        }

        binder.setItemDataSource(this.tabDefinition);
        binder.bind(titleTextField, "title");
        binder.bind(iconComboBox, "icon");
        binder.bind(layoutOptionGroup, "layout");
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
    
    public TabDefinition getTabDefinition() {
        try {
            binder.commit();
            return binder.getItemDataSource().getBean();
        } catch (FieldGroup.CommitException ex) {
            Logger.getLogger(TabConfigurationLayout.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
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
