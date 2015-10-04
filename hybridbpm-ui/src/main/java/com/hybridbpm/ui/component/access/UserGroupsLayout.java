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
import com.hybridbpm.core.data.access.User;
import com.hybridbpm.core.data.access.UserGroup;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.component.ConfigureWindow;
import com.hybridbpm.ui.component.TableButton;
import com.hybridbpm.ui.component.TableButtonBar;
import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class UserGroupsLayout extends AbstractAccessLayout {

    public UserGroupsLayout() {
        super();
        btnAdd.setDescription("Add link");
    }

    @Override
    public void buttonClick(final Button.ClickEvent event) {
        super.buttonClick(event);
    }

    @Override
    public void prepareTable() {
        iTable.addContainerProperty("user", String.class, null, "User", null, Table.Align.LEFT);
        iTable.addContainerProperty("group", String.class, null, "Group", null, Table.Align.LEFT);
        iTable.addContainerProperty("actions", TableButtonBar.class, null, "Actions", null, Table.Align.LEFT);
        iTable.setColumnWidth("actions", 70);
        iTable.setVisibleColumns("user", "group", "actions");
    }

    @Override
    public void refreshTable() {
        iTable.removeAllItems();
        for (UserGroup userGroup : HybridbpmUI.getAccessAPI().getAllUserGroups()) {
            Item item = iTable.addItem(userGroup.getId());
            item.getItemProperty("user").setValue(userGroup.getOut().getFullName());
            item.getItemProperty("group").setValue(userGroup.getIn().getName());
            item.getItemProperty("actions").setValue(getTableButtonBar(userGroup));
        }
        iTable.sort(new Object[]{"role"}, new boolean[]{false});
    }

    private Object getTableButtonBar(UserGroup userGroup) {
        TableButton deleteButton = TableButton.createDelete(userGroup.getId().toString(), this);
        if ((userGroup.getOut().getUsername().equals(User.ADMINISTRATOR) && userGroup.getIn().getName().equals(Group.ADMINISTRATORS))
                || (userGroup.getOut().getUsername().equals(User.ADMINISTRATOR) && userGroup.getIn().getName().equals(Group.DEVELOPERS))
                || (userGroup.getOut().getUsername().equals(User.ADMINISTRATOR) && userGroup.getIn().getName().equals(Group.USERS))) {
            deleteButton.setVisible(false);
        }
        return new TableButtonBar(deleteButton);
    }

    @Override
    public void addNew() {
        openMembershipEditor();
    }

    public void openMembershipEditor() {
        final UserGroupLayout membershipLayout = new UserGroupLayout();
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
