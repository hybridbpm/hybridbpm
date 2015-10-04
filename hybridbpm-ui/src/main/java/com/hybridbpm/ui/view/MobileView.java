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

import com.hybridbpm.ui.component.AbstractTabLayout;
import com.hybridbpm.ui.component.AbstractTableLayout;
import com.hybridbpm.ui.component.AbstractTreeTableLayout;
import com.hybridbpm.ui.component.sync.MobileAdministrationLayout;
import com.hybridbpm.ui.component.sync.MobileFormListLayout;
import com.hybridbpm.ui.component.sync.MobileTaskListLayout;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;

@DesignRoot
@SuppressWarnings("serial")
public final class MobileView extends AbstractView implements View, TabSheet.SelectedTabChangeListener {

    public static final String VIEW_URL = "mobile";
    public static final String TITLE = "Mobile";
    public static final String ICON = FontAwesome.MOBILE_PHONE.name();
    public static final Integer ORDER = Integer.MAX_VALUE - 5;

    public VerticalLayout panelLayout;
    public TabSheet tabSheet;

    private final MobileAdministrationLayout mobileAdministrationLayout = new MobileAdministrationLayout();
    private final MobileTaskListLayout mobileTaskListLayout = new MobileTaskListLayout();
    private final MobileFormListLayout mobileFormListLayout = new MobileFormListLayout();

    public MobileView() {
        Design.read(this);
        Responsive.makeResponsive(panelLayout);
        tabSheet.addTab(mobileAdministrationLayout, "Administration", FontAwesome.WRENCH);
        tabSheet.addTab(mobileTaskListLayout, "Mobile Tasks", FontAwesome.TASKS);
        tabSheet.addTab(mobileFormListLayout, "Mobile Forms", FontAwesome.MOBILE_PHONE);
        tabSheet.addSelectedTabChangeListener(this);
        mobileAdministrationLayout.refreshData();
        mobileTaskListLayout.setTabSheet(tabSheet);
        mobileFormListLayout.setTabSheet(tabSheet);
    }

    @Override
    public void enter(ViewChangeEvent event) {
    }

    @Override
    public void selectedTabChange(TabSheet.SelectedTabChangeEvent event) {
        Component comp = event.getTabSheet().getSelectedTab();
        if (comp instanceof AbstractTableLayout) {
            ((AbstractTableLayout) comp).refreshTable();
        }
        if (comp instanceof AbstractTabLayout) {
            ((AbstractTabLayout) comp).refreshData();
        }
        if (comp instanceof AbstractTreeTableLayout) {
            ((AbstractTreeTableLayout) comp).refreshTable();
        }
    }

}
