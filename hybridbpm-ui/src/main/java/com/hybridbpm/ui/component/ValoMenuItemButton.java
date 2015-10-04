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

import com.hybridbpm.core.data.dashboard.ViewDefinition;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;

/**
 *
 * @author Marat Gubaidullin
 */
public final class ValoMenuItemButton extends Button {

    private final ViewDefinition view;
    private final String parameters;

    public ValoMenuItemButton(final ViewDefinition view, final String parameters) {
        this.view = view;
        this.parameters = parameters;
        setPrimaryStyleName("valo-menu-item");
        if (view.getIcon() != null) {
            setIcon(FontAwesome.valueOf(view.getIcon()));
        }
        setCaption(view.getTitle().getValue());
        addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                UI.getCurrent().getNavigator().navigateTo(view.getUrl());
            }
        });
    }

    public ViewDefinition getView() {
        return view;
    }

    public String getParameters() {
        return parameters;
    }

}
