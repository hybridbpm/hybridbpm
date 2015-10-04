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
package com.hybridbpm.ui.component.dashboard.tab;

import com.hybridbpm.core.data.dashboard.TabDefinition;
import com.hybridbpm.core.data.dashboard.ViewDefinition;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.component.dashboard.ViewManager;
import com.hybridbpm.ui.component.dashboard.panel.DashboardPanel;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;

@DesignRoot
@SuppressWarnings("serial")
public final class DashboardTab extends VerticalLayout implements Button.ClickListener {

    protected HorizontalLayout tools;
    protected Button btnEdit;
    protected DashboardPanelContainer panels;

    private final TabDefinition tabDefinition;
    private final ViewDefinition viewDefinition;

    public DashboardTab(TabDefinition tab, ViewDefinition viewDefinition) {
        this.tabDefinition = tab;
        this.viewDefinition = viewDefinition;
        Design.read(this);
        Responsive.makeResponsive(this);
        btnEdit.setIcon(FontAwesome.EDIT);
        btnEdit.setDescription("Edit view");
        btnEdit.addClickListener(this);
        refresh();
        checkDeveloperMode();
    }

    private void checkDeveloperMode() {
        Boolean devMode = HybridbpmUI.getDeveloperMode();
        tools.setVisible(devMode);
        tools.setEnabled(devMode);
        btnEdit.setEnabled(devMode);
    }

    public void refresh() {
        switch (tabDefinition.getLayout()) {
            case VERTICAL:
                panels.setRoot(new VerticalLayoutContainer(this, tabDefinition));
                break;
            case HORIZONTAL:
                panels.setRoot(new HorizontalLayoutContainer(this, tabDefinition));
                break;
            case GRID:
                panels.setRoot(new GridLayoutContainer(this, tabDefinition));
                break;
        }

    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton().equals(btnEdit)) {
            ViewManager.editTabDefinitionWindow(tabDefinition, viewDefinition);
        }
    }

    public void toggleMaximized(final DashboardPanel dashboardPanel, final boolean maximized) {
        if (panels.root instanceof VerticalLayoutContainer || panels.root instanceof HorizontalLayoutContainer) {
            for (Component c : panels.root) {
                c.setVisible(!maximized);
            }
        } else if (panels.root instanceof GridLayoutContainer) {
            if (maximized) {
                ((GridLayoutContainer) panels.root).maximize(dashboardPanel);
            } else {
                ((GridLayoutContainer) panels.root).minimize();
            }
        }
        if (maximized) {
            dashboardPanel.setVisible(true);
            dashboardPanel.addStyleName("max");
        } else {
            dashboardPanel.removeStyleName("max");
        }
    }

    public TabDefinition getTabDefinition() {
        return tabDefinition;
    }

}
