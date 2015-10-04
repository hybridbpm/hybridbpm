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
package com.hybridbpm.ui.component.development;

import com.hybridbpm.core.data.development.Module;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.logging.Logger;

/**
 *
 * @author Marat Gubaidullin
 */
public abstract class AbstractEditor extends VerticalLayout implements Button.ClickListener {

    private static final Logger LOGGER = Logger.getLogger(AbstractEditor.class.getCanonicalName());

    protected final HorizontalSplitPanel horizontalSplitPanel = new HorizontalSplitPanel();
    protected final Button btnSave = new Button("Save", this);
    protected final HorizontalLayout buttonBar = new HorizontalLayout(btnSave);

    public AbstractEditor() {
        setSizeFull();
        setSpacing(true);
        setMargin(new MarginInfo(true, false, false, false));
        addComponent(buttonBar);
        setComponentAlignment(buttonBar, Alignment.MIDDLE_RIGHT);
        addComponent(horizontalSplitPanel);
        setExpandRatio(horizontalSplitPanel, 1f);

        horizontalSplitPanel.setSizeFull();
        horizontalSplitPanel.setStyleName("transparent");

        buttonBar.setSpacing(true);
        buttonBar.setSpacing(true);

        btnSave.setIcon(FontAwesome.SAVE);
        btnSave.setStyleName(ValoTheme.BUTTON_BORDERLESS);
        btnSave.addStyleName(ValoTheme.BUTTON_SMALL);
        btnSave.addStyleName(ValoTheme.BUTTON_PRIMARY);
    }

    public abstract Module getModule();
}
