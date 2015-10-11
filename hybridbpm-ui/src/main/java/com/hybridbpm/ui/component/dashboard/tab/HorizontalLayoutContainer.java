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

import com.hybridbpm.core.data.dashboard.PanelDefinition;
import com.hybridbpm.core.data.dashboard.TabDefinition;
import com.hybridbpm.ui.util.HybridbpmStyle;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.component.dashboard.panel.DashboardPanel;
import com.hybridbpm.ui.component.dashboard.panel.AddColumnButton;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;

@DesignRoot
@SuppressWarnings("serial")
public final class HorizontalLayoutContainer extends HorizontalLayout implements Button.ClickListener {

    private TabDefinition tabDefinition;
    private final DashboardTab dashboardTab;

    public HorizontalLayoutContainer(DashboardTab dashboardTab, TabDefinition tabDefinition) {
        this.tabDefinition = tabDefinition;
        this.dashboardTab = dashboardTab;
        addStyleName(HybridbpmStyle.OVERFLOW_HIDDEN);
        setSpacing(true);
        refreshUI();
    }

    private void addColumnButton(int index) {
        if (HybridbpmUI.getDeveloperMode()) {
            AddColumnButton addColumnButton = new AddColumnButton(this);
            addComponent(addColumnButton, index);
            setComponentAlignment(addColumnButton, Alignment.MIDDLE_CENTER);
        }
    }

    public void refreshUI() {
        removeAllComponents();
        addColumnButton(0);
        for (PanelDefinition panelDefinition : HybridbpmUI.getDashboardAPI().getPanelDefinitionsByTab(tabDefinition.getId())) {
            addDashboardPanel(panelDefinition);
        }
    }

    public void addDashboardPanel(PanelDefinition panelDefinition) {
        DashboardPanel dashboardPanel = new DashboardPanel(dashboardTab, panelDefinition);
        addComponent(dashboardPanel);
        setExpandRatio(dashboardPanel, 1f);
        addColumnButton(getComponentIndex(dashboardPanel) + 1);
    }

    public void addDashboardPanel(PanelDefinition panelDefinition, int index) {
        DashboardPanel dashboardPanel = new DashboardPanel(dashboardTab, panelDefinition);
        addComponent(dashboardPanel, index);
        setExpandRatio(dashboardPanel, 1f);
        addColumnButton(getComponentIndex(dashboardPanel) + 1);
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton() instanceof AddColumnButton) {
            tabDefinition = HybridbpmUI.getDashboardAPI().addToVerticalTabDefinition(tabDefinition, getComponentIndex(event.getButton()) / 2);
        }
        refreshUI();
    }

}
