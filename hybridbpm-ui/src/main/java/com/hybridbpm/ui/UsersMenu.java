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
package com.hybridbpm.ui;

import com.hybridbpm.core.data.access.User;
import com.hybridbpm.ui.component.UserImageSource;
import com.hybridbpm.ui.component.ValoUserItemButton;
import com.vaadin.data.Item;
import com.vaadin.event.FieldEvents;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.List;
import java.util.UUID;

/**
 */
@SuppressWarnings({"serial", "unchecked"})
public final class UsersMenu extends VerticalLayout {

    private final TextField textSearch = new TextField();
    private final Table table = new Table();

    public UsersMenu() {
        textSearch.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        textSearch.setWidth(100, Unit.PERCENTAGE);
        textSearch.setNullRepresentation("");
        textSearch.setWidth(100, Unit.PERCENTAGE);
        textSearch.setInputPrompt("type to search users");
        textSearch.addTextChangeListener(new FieldEvents.TextChangeListener() {

            @Override
            public void textChange(FieldEvents.TextChangeEvent event) {
                search(event.getText());
            }
        });

        table.addStyleName(ValoTheme.TABLE_BORDERLESS);
        table.addStyleName(ValoTheme.TABLE_NO_HEADER);
        table.addStyleName(ValoTheme.TABLE_NO_VERTICAL_LINES);
        table.addStyleName(ValoTheme.TABLE_SMALL);
        table.addStyleName(ValoTheme.TABLE_COMPACT);
        table.addStyleName(ValoTheme.TABLE_NO_STRIPES);
        table.addContainerProperty("user", User.class, null, "Username", null, Table.Align.LEFT);
        table.addContainerProperty("tasks", Label.class, null, "Tasks", null, Table.Align.LEFT);
        table.setColumnExpandRatio("user", 1f);
        table.setVisibleColumns("user", "tasks");
        table.setSizeFull();
        table.setColumnExpandRatio("user", 1f);
        table.setSelectable(false);
        table.addGeneratedColumn("user", new UserColumnGenerator());

        addComponents(textSearch, table);
        setExpandRatio(table, 1f);
        setHeight(100, Unit.PERCENTAGE);
        setWidth(300, Unit.PIXELS);
        addStyleName("users-list");
        setVisible(false);
    }

    public void search(String text) {
        table.removeAllItems();
        List<User> list = HybridbpmUI.getAccessAPI().findUsersByName(text);
        for (User u : list) {
            Item item = table.addItem(u);
            Label notificationsBadge = new Label("45");
            notificationsBadge.addStyleName(ValoTheme.MENU_BADGE);
            notificationsBadge.addStyleName(ValoTheme.LABEL_TINY);
            notificationsBadge.setWidthUndefined();
            notificationsBadge.setDescription("45 task todo");
            item.getItemProperty("tasks").setValue(notificationsBadge);
        }
//        table.select(list.get(0));
    }

    public final class UserColumnGenerator implements Table.ColumnGenerator {

        @Override
        public Object generateCell(Table source, Object itemId, Object columnId) {
            User user = (User) itemId;
            Image image = new Image();
            image.addStyleName("users-menu-image");
            if (user.getImage() != null) {
                StreamResource.StreamSource imagesource = new UserImageSource(user.getImage().toStream());
                StreamResource resource = new StreamResource(imagesource, UUID.randomUUID().toString());
                image.setSource(resource);
            } else {
                image.setSource(new ThemeResource("img/profile-pic-300px.jpg"));
            }

            ValoUserItemButton btnUsername = new ValoUserItemButton(user, ValoUserItemButton.TYPE.USER_NAME);
            ValoUserItemButton btnFullName = new ValoUserItemButton(user, ValoUserItemButton.TYPE.FULL_NAME);

            VerticalLayout nameLayout = new VerticalLayout(btnFullName, btnUsername);
            nameLayout.setSizeFull();
            nameLayout.setComponentAlignment(btnFullName, Alignment.BOTTOM_LEFT);
            nameLayout.setComponentAlignment(btnUsername, Alignment.TOP_LEFT);

            HorizontalLayout usersHorizontalLayout = new HorizontalLayout(image, nameLayout);
            usersHorizontalLayout.setComponentAlignment(image, Alignment.MIDDLE_CENTER);
            usersHorizontalLayout.setComponentAlignment(nameLayout, Alignment.MIDDLE_LEFT);
            usersHorizontalLayout.setExpandRatio(nameLayout, 1f);
            usersHorizontalLayout.addStyleName("users-horizontal-layout");
            usersHorizontalLayout.setWidth(100, Unit.PERCENTAGE);
            usersHorizontalLayout.setHeight(45, Unit.PIXELS);
            usersHorizontalLayout.setSpacing(true);

            return usersHorizontalLayout;
        }

    }

}
