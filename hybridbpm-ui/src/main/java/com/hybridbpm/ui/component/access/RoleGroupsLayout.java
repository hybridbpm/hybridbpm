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
import com.hybridbpm.ui.component.ConfigureWindow;
import com.hybridbpm.ui.component.TableButton;
import com.hybridbpm.ui.component.TableButtonBar;
import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class RoleGroupsLayout extends AbstractAccessLayout {

    public RoleGroupsLayout() {
        super();
        btnAdd.setDescription("Add link");
    }

    @Override
    public void buttonClick(final Button.ClickEvent event) {
        super.buttonClick(event);
    }

    @Override
    public void prepareTable() {
        iTable.addContainerProperty("role", String.class, null, "Role", null, Table.Align.LEFT);
        iTable.addContainerProperty("group", String.class, null, "Group", null, Table.Align.LEFT);
        iTable.addContainerProperty("actions", TableButtonBar.class, null, "Actions", null, Table.Align.LEFT);
        iTable.setColumnWidth("actions", 70);
        iTable.setVisibleColumns("role", "group", "actions");
    }

    @Override
    public void refreshTable() {
        iTable.removeAllItems();
        for (RoleGroup roleGroup : HybridbpmUI.getAccessAPI().getAllRoleGroups()) {
            Item item = iTable.addItem(roleGroup.getId());
            item.getItemProperty("role").setValue(roleGroup.getOut().getName());
            item.getItemProperty("group").setValue(roleGroup.getIn().getName());
            item.getItemProperty("actions").setValue(getTableButtonBar(roleGroup));
        }
        iTable.sort(new Object[]{"role"}, new boolean[]{false});
    }

    private Object getTableButtonBar(RoleGroup roleGroup) {
        TableButton deleteButton = TableButton.createDelete(roleGroup.getId().toString(), this);
        if ((roleGroup.getOut().getName().equals(Role.ADMINISTRATOR) && roleGroup.getIn().getName().equals(Group.ADMINISTRATORS))
                || (roleGroup.getOut().getName().equals(Role.DEVELOPER) && roleGroup.getIn().getName().equals(Group.DEVELOPERS))
                || (roleGroup.getOut().getName().equals(Role.USER) && roleGroup.getIn().getName().equals(Group.USERS))) {
            deleteButton.setVisible(false);
        }
        return new TableButtonBar(deleteButton);
    }

    @Override
    public void addNew() {
        openMembershipEditor();
    }

    public void openMembershipEditor() {
        final RoleGroupLayout membershipLayout = new RoleGroupLayout();
        final ConfigureWindow configureWindow = new ConfigureWindow(membershipLayout, "Membership");
        Button.ClickListener clickListener = new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (event.getButton().equals(configureWindow.btnClose)) {

                } else if (event.getButton().equals(configureWindow.btnOk)) {
                    membershipLayout.save();
                }
                configureWindow.close();
            }
        };
        configureWindow.setClickListener(clickListener);
        configureWindow.addCloseListener(this);
        configureWindow.setSizeUndefined();
        HybridbpmUI.getCurrent().addWindow(configureWindow);
    }

}
