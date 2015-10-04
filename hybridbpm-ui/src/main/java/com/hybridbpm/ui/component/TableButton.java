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
package com.hybridbpm.ui.component;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;

/**
 *
 * @author Marat Gubaidullin
 * @param <T>
 */
public final class TableButton<T> extends Button {

    private final TYPE type;

    public enum TYPE {

        PERMISSION(FontAwesome.SHIELD, "Permissions"),
        EDIT(FontAwesome.EDIT, "Edit"),
        DELETE(FontAwesome.TIMES_CIRCLE, "Delete"),
        SYNC(FontAwesome.EXCHANGE, "Sync"),
        DOWNLOAD(FontAwesome.DOWNLOAD, "Download");

        private Resource icon;
        private String description;

        private TYPE(Resource icon, String description) {
            this.icon = icon;
            this.description = description;
        }
    };

    private final T customData;

    public TableButton(TableButton.TYPE type, T data, ClickListener clickListener) {
        this.type = type;
        this.customData = data;
        addStyleName(ValoTheme.BUTTON_LINK);
        setIcon(type.icon);
        setDescription(type.description);
        if (clickListener != null) {
            addClickListener(clickListener);
        }
    }

    public static <T> TableButton createEdit(T data, ClickListener clickListener) {
        return new TableButton(TYPE.EDIT, data, clickListener);
    }

    public static <T> TableButton createDelete(T data, ClickListener clickListener) {
        return new TableButton(TYPE.DELETE, data, clickListener);
    }
    
    public static <T> TableButton createSync(T data, ClickListener clickListener) {
        return new TableButton(TYPE.SYNC, data, clickListener);
    }

    public static <T> TableButton createPermissions(T data, ClickListener clickListener) {
        return new TableButton(TYPE.PERMISSION, data, clickListener);
    }

    public static <T> TableButton createDownload(T data, ClickListener clickListener) {
        return new TableButton(TYPE.DOWNLOAD, data, clickListener);
    }

    public T getCustomData() {
        return customData;
    }

    public TYPE getType() {
        return type;
    }

}
