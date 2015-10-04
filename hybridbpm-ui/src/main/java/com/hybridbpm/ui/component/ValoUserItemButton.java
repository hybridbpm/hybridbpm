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

import com.hybridbpm.core.data.access.User;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;

/**
 *
 * @author Marat Gubaidullin
 */
public final class ValoUserItemButton extends Button {

    private final User user;
    private final TYPE type;

    public enum TYPE {

        USER_NAME,
        FULL_NAME;
    }

    public ValoUserItemButton(final User user, TYPE type) {
        this.user = user;
        this.type = type;
        switch (this.type) {
            case FULL_NAME:
                addStyleName(ValoTheme.BUTTON_BORDERLESS);
                addStyleName(ValoTheme.BUTTON_TINY);
                setCaption(user.getFullName());
                break;
            case USER_NAME:
                addStyleName(ValoTheme.BUTTON_LINK);
                addStyleName(ValoTheme.BUTTON_TINY);
                setCaption("@" + user.getUsername());
                break;
        }
        addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
//                UI.getCurrent().getNavigator().navigateTo(view.getUrl());
            }
        });
    }

    public User getUser() {
        return user;
    }

}
