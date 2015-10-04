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
package com.hybridbpm.ui.view;

import com.hybridbpm.core.util.DashboardConstant;
import com.hybridbpm.ui.component.access.AbstractAccessLayout;
import com.hybridbpm.ui.component.access.GroupsLayout;
import com.hybridbpm.ui.component.access.RoleGroupsLayout;
import com.hybridbpm.ui.component.access.RolesLayout;
import com.hybridbpm.ui.component.access.UserGroupsLayout;
import com.hybridbpm.ui.component.access.UsersLayout;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;

@DesignRoot
@SuppressWarnings("serial")
public final class AccessView extends AbstractView implements View, Button.ClickListener, TabSheet.SelectedTabChangeListener {
    
    public static final String VIEW_URL = DashboardConstant.VIEW_URL_ACCESS;
    public static final String TITLE = "Access";
    public static final String ICON = FontAwesome.SHIELD.name();
    public static final Integer ORDER = Integer.MAX_VALUE - 2;

    public VerticalLayout panelLayout;
    public TabSheet tabSheet;
    
    private final UsersLayout usersLayout = new UsersLayout();
    private final RolesLayout rolesLayout = new RolesLayout();
    private final GroupsLayout groupsLayout = new GroupsLayout();
    private final RoleGroupsLayout roleGroupsLayout = new RoleGroupsLayout();
    private final UserGroupsLayout userGroupsLayout = new UserGroupsLayout();

    public AccessView() {
        Design.read(this);
        Responsive.makeResponsive(panelLayout);
        tabSheet.addTab(usersLayout, "Users", FontAwesome.USER);
        tabSheet.addTab(userGroupsLayout, "User Group", FontAwesome.SITEMAP);
        tabSheet.addTab(groupsLayout, "Group", FontAwesome.USERS);
        tabSheet.addTab(roleGroupsLayout, "Group Role", FontAwesome.SITEMAP);
        tabSheet.addTab(rolesLayout, "Roles", FontAwesome.USERS);
        
        tabSheet.addSelectedTabChangeListener(this);
        usersLayout.refreshTable();
    }

    @Override
    public void enter(ViewChangeEvent event) {
    }
    
    @Override
    public void buttonClick(Button.ClickEvent event) {
    }

    @Override
    public void selectedTabChange(TabSheet.SelectedTabChangeEvent event) {
        Component comp = event.getTabSheet().getSelectedTab();
        if (comp instanceof AbstractAccessLayout){
            ((AbstractAccessLayout) comp).refreshTable();
        }
    }

}
