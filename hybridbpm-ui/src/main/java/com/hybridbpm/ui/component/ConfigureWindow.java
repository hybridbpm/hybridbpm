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

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import java.util.logging.Logger;

/**
 *
 * @author Marat Gubaidullin
 */
public class ConfigureWindow extends Window {

    private static final Logger LOG = Logger.getLogger(ConfigureWindow.class.getCanonicalName());
    private final VerticalLayout layout = new VerticalLayout();
    private final CssLayout cssLayout = new CssLayout();

    private final HorizontalLayout toolbar = new HorizontalLayout();
    public final Button btnOk = new Button();
    public final Button btnClose = new Button();
    public final Label errorMessage = new Label();

    private final Component dataLayout;

    public static ConfigureWindow create(Layout dataLayout, String caption) {
        return new ConfigureWindow(dataLayout, caption);
    }

    public static ConfigureWindow createSizeUndefined(Layout dataLayout, String caption) {
        ConfigureWindow configureWindow = new ConfigureWindow(dataLayout, caption);
        configureWindow.setSizeUndefined();
        return configureWindow;
    }

    public ConfigureWindow(Component dataLayout, String caption) {
        this.dataLayout = dataLayout;
        setCaption(caption);

        errorMessage.setVisible(false);
        errorMessage.setStyleName(ValoTheme.LABEL_FAILURE);

        toolbar.setSpacing(true);
        toolbar.addStyleName("toolbar");

        btnOk.addStyleName(ValoTheme.BUTTON_PRIMARY);
        btnOk.addStyleName(ValoTheme.BUTTON_SMALL);
        btnOk.setCaption("OK");
        toolbar.addComponent(btnOk);
        toolbar.setComponentAlignment(btnOk, Alignment.MIDDLE_RIGHT);

        btnClose.setCaption("Close");
        btnClose.addStyleName(ValoTheme.BUTTON_SMALL);
        
        toolbar.addComponent(btnClose);
        toolbar.setComponentAlignment(btnClose, Alignment.MIDDLE_RIGHT);

        cssLayout.addComponent(this.dataLayout);
        cssLayout.setSizeFull();
        cssLayout.addStyleName("scrollable");

        layout.setSizeFull();
        layout.setMargin(true);
        layout.addComponent(cssLayout);
        layout.setExpandRatio(cssLayout, 1f);
        layout.addComponent(errorMessage);
        layout.addComponent(toolbar);
        layout.setComponentAlignment(toolbar, Alignment.BOTTOM_RIGHT);

        center();
        setResizable(false);
//        setClosable(false);
        setModal(true);
        addStyleName("no-vertical-drag-hints");
        addStyleName("no-horizontal-drag-hints");
        setContent(layout);
        setWidth(80, Unit.PERCENTAGE);
        setHeight(80, Unit.PERCENTAGE);
    }

    public void setClickListener(Button.ClickListener clickListener) {
        btnOk.addClickListener(clickListener);
        btnClose.addClickListener(clickListener);
    }

    public void setSize(float width, float height) {
        setWidth(width, Unit.PIXELS);
        setHeight(height, Unit.PIXELS);
    }

    @Override
    public void setSizeUndefined() {
        super.setSizeUndefined();
        if (layout != null) {
            layout.setSizeUndefined();
            cssLayout.setSizeUndefined();
        }
    }

    public void setError(String message) {
        if (message != null && !message.isEmpty()) {
            errorMessage.setVisible(true);
            errorMessage.setValue(message);
        } else {
            errorMessage.setVisible(false);
        }
    }

}
