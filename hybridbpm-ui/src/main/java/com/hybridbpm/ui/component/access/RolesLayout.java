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

import com.hybridbpm.core.data.access.Role;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.component.ConfigureWindow;
import com.hybridbpm.ui.component.LinkButton;
import com.hybridbpm.ui.component.TableButton;
import com.hybridbpm.ui.component.TableButtonBar;
import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class RolesLayout extends AbstractAccessLayout {

    public RolesLayout() {
        super();
        btnAdd.setDescription("Add role");
    }

    @Override
    public void buttonClick(final Button.ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton() instanceof LinkButton) {
            LinkButton<Role> linkButton = (LinkButton) event.getButton();
            Role ri = linkButton.getCustomData();
            openRoleEditor(ri);
        } else if (event.getButton() instanceof TableButton && ((TableButton) event.getButton()).getType().equals(TableButton.TYPE.SYNC)) {
//            HybridbpmUI.getSyncAPI().createCouchbaseRole(((TableButton) event.getButton()).getCustomData().toString());
            refreshTable();
        }
    }

    @Override
    public void prepareTable() {
        iTable.addContainerProperty("name", Component.class, null, "Name", null, Table.Align.LEFT);
        iTable.addContainerProperty("title", String.class, null, "Title", null, Table.Align.LEFT);
        iTable.addContainerProperty("description", String.class, null, "Description", null, Table.Align.LEFT);
        iTable.addContainerProperty("sync", Boolean.class, null, "Sync", null, Table.Align.LEFT);
        iTable.addContainerProperty("actions", TableButtonBar.class, null, "Actions", null, Table.Align.LEFT);
        iTable.setColumnWidth("actions", 70);
        iTable.setVisibleColumns("name", "title", "description", "sync", "actions");
    }

    @Override
    public void refreshTable() {
        iTable.removeAllItems();
        for (Role role : HybridbpmUI.getAccessAPI().getAllRoles()) {
            Item item = iTable.addItem(role);
            item.getItemProperty("name").setValue(new LinkButton(role.getName(), role, this));
            item.getItemProperty("title").setValue(role.getTitle().getValue(HybridbpmUI.getCurrent().getLocale()));
            item.getItemProperty("description").setValue(role.getTitle().getValue(HybridbpmUI.getCurrent().getLocale()));
            item.getItemProperty("sync").setValue(role.getSync());
            item.getItemProperty("actions").setValue(getTableButtonBar(role));
        }
        iTable.sort(new Object[]{"title"}, new boolean[]{false});
    }

    private Object getTableButtonBar(Role role) {
        TableButton deleteButton = TableButton.createDelete(role.getId().toString(), this);
        deleteButton.setVisible(!role.getName().equals(Role.ADMINISTRATOR)
                && !role.getName().equals(Role.DEVELOPER)
                && !role.getName().equals(Role.USER));

        TableButton syncButton = TableButton.createSync(role.getName(), this);
        return new TableButtonBar(syncButton, deleteButton);
    }

    @Override
    public void addNew() {
        openRoleEditor(null);
    }

    public void openRoleEditor(Role role) {
        final RoleLayout roleLayout = new RoleLayout(role);
        final ConfigureWindow configureWindow = new ConfigureWindow(roleLayout, "Role");
        Button.ClickListener clickListener = new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (event.getButton().equals(configureWindow.btnClose)) {

                } else if (event.getButton().equals(configureWindow.btnOk)) {
                    roleLayout.save();
                }
                configureWindow.close();
            }
        };
        configureWindow.setClickListener(clickListener);
        configureWindow.addCloseListener(this);
        HybridbpmUI.getCurrent().addWindow(configureWindow);
    }

}
