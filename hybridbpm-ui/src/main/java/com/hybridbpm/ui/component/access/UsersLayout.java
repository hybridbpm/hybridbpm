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

import com.hybridbpm.core.data.access.User;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.component.ConfigureWindow;
import com.hybridbpm.ui.component.LinkButton;
import com.hybridbpm.ui.component.TableButton;
import com.hybridbpm.ui.component.TableButtonBar;
import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class UsersLayout extends AbstractAccessLayout {

    public UsersLayout() {
        super();
        btnAdd.setDescription("Add user");
    }

    @Override
    public void buttonClick(final Button.ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton() instanceof LinkButton) {
            LinkButton<User> linkButton = (LinkButton) event.getButton();
            User ui = linkButton.getCustomData();
            openUserEditor(ui, this);
        }
    }

    @Override
    public void prepareTable() {
        iTable.addContainerProperty("username", Component.class, null, "Username", null, Table.Align.LEFT);
        iTable.addContainerProperty("firstname", String.class, null, "First name", null, Table.Align.LEFT);
        iTable.addContainerProperty("lastname", String.class, null, "Last name", null, Table.Align.LEFT);
        iTable.addContainerProperty("email", String.class, null, "Email", null, Table.Align.LEFT);
        iTable.addContainerProperty("actions", TableButtonBar.class, null, "Actions", null, Table.Align.LEFT);
        iTable.setColumnWidth("actions", 70);
        iTable.setVisibleColumns("username", "firstname", "lastname", "email", "actions");
    }

    @Override
    public void refreshTable() {
        iTable.removeAllItems();

        for (User user : HybridbpmUI.getAccessAPI().getAllUsers()) {
            Item item = iTable.addItem(user.getId());
            item.getItemProperty("username").setValue(new LinkButton(user.getUsername(), user, this));
            item.getItemProperty("firstname").setValue(user.getFirstName());
            item.getItemProperty("lastname").setValue(user.getLastName());
            item.getItemProperty("email").setValue(user.getEmail());
            item.getItemProperty("actions").setValue(getTableButtonBar(user));
        }
        iTable.sort(new Object[]{"title"}, new boolean[]{false});
    }

    private Object getTableButtonBar(User user) {
        TableButton deleteButton = TableButton.createDelete(user.getId().toString(), this);
        deleteButton.setVisible(!user.getUsername().equalsIgnoreCase(User.ADMINISTRATOR));
        return new TableButtonBar(deleteButton);
    }

    @Override
    public void addNew() {
        openUserEditor(null, this);
    }

    public static void openUserEditor(User user, Window.CloseListener closeListener) {
        final UserLayout userLayout = new UserLayout(user);
        final ConfigureWindow configureWindow = new ConfigureWindow(userLayout, "User");
        Button.ClickListener clickListener = new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (event.getButton().equals(configureWindow.btnClose)) {
                    configureWindow.close();
                } else if (event.getButton().equals(configureWindow.btnOk)) {
                    userLayout.save();
                }
            }
        };
        configureWindow.setClickListener(clickListener);
        configureWindow.addCloseListener(closeListener);
        configureWindow.setSizeUndefined();
        HybridbpmUI.getCurrent().addWindow(configureWindow);
    }

}
