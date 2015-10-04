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

import com.vaadin.annotations.DesignRoot;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Button;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.declarative.Design;

@DesignRoot
@SuppressWarnings("serial")
public abstract class AbstractTreeTableLayout extends VerticalLayout implements Button.ClickListener, Window.CloseListener {

    protected Button btnAdd;
    protected Button btnRefresh;
    protected TreeTable iTable;

    public AbstractTreeTableLayout() {
        Design.read(this);
        Responsive.makeResponsive(this);

        btnAdd.setIcon(FontAwesome.PLUS_CIRCLE);
        btnAdd.addClickListener(this);

        btnRefresh.setIcon(FontAwesome.REFRESH);
        btnRefresh.addClickListener(this);

        setExpandRatio(iTable, 1f);
        prepareTable();
    }

    @Override
    public void buttonClick(final Button.ClickEvent event) {
        if (event.getButton().equals(btnAdd)) {
            addNew();
        } else if (event.getButton().equals(btnRefresh)) {
            refreshTable();
        }
    }

    @Override
    public void windowClose(Window.CloseEvent e) {
        refreshTable();
    }

    public abstract void prepareTable();

    public abstract void refreshTable();

    public abstract void addNew();

}
