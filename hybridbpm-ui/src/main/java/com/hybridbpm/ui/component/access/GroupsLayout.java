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
public class GroupsLayout extends AbstractAccessLayout {

    public GroupsLayout() {
        super();
        btnAdd.setDescription("Add group");
    }

    @Override
    public void buttonClick(final Button.ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton() instanceof LinkButton) {
            LinkButton<Group> linkButton = (LinkButton) event.getButton();
            Group gi = linkButton.getCustomData();
            openGroupEditor(gi);
        } 
    }

    @Override
    public void prepareTable() {
        iTable.addContainerProperty("name", Component.class, null, "Name", null, Table.Align.LEFT);
        iTable.addContainerProperty("title", String.class, null, "Title", null, Table.Align.LEFT);
        iTable.addContainerProperty("description", String.class, null, "Description", null, Table.Align.LEFT);
        iTable.addContainerProperty("actions", TableButtonBar.class, null, "Actions", null, Table.Align.LEFT);
        iTable.setColumnWidth("actions", 70);
        iTable.setVisibleColumns("name", "title", "description", "actions");
    }

    @Override
    public void refreshTable() {
        iTable.removeAllItems();

        for (Group group : HybridbpmUI.getAccessAPI().getAllGroups()) {
            Item item = iTable.addItem(group.getId());
            item.getItemProperty("name").setValue(new LinkButton(group.getName(), group, this));
            item.getItemProperty("title").setValue(group.getTitle().getValue(HybridbpmUI.getCurrent().getLocale()));
            item.getItemProperty("description").setValue(group.getTitle().getValue(HybridbpmUI.getCurrent().getLocale()));
            item.getItemProperty("actions").setValue(getTableButtonBar(group));
        }
        iTable.sort(new Object[]{"title"}, new boolean[]{false});
    }

    private Object getTableButtonBar(Group group) {
        TableButton deleteButton = TableButton.createDelete(group.getId().toString(), this);
        deleteButton.setVisible(!group.getName().equals(Group.ADMINISTRATORS)
                && !group.getName().equals(Group.DEVELOPERS)
                && !group.getName().equals(Group.USERS));
        return new TableButtonBar(deleteButton);
    }

    @Override
    public void addNew() {
        openGroupEditor(null);
    }

    
    public void openGroupEditor(Group group) {
        final GroupLayout groupLayout = new GroupLayout(group);
        final ConfigureWindow configureWindow = new ConfigureWindow(groupLayout, "Group");
        Button.ClickListener clickListener = new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (event.getButton().equals(configureWindow.btnClose)) {

                } else if (event.getButton().equals(configureWindow.btnOk)) {
                    groupLayout.save();
                }
                configureWindow.close();
            }
        };
        configureWindow.setClickListener(clickListener);
        configureWindow.addCloseListener(this);
        HybridbpmUI.getCurrent().addWindow(configureWindow);
    }
}
