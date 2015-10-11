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
import com.hybridbpm.ui.component.dashboard.panel.AddColumnButton;
import com.hybridbpm.ui.component.dashboard.panel.DashboardPanel;
import com.hybridbpm.ui.component.dashboard.panel.AddRowButton;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import java.util.Iterator;
import java.util.Objects;

@DesignRoot
@SuppressWarnings("serial")
public final class GridLayoutContainer extends GridLayout implements Button.ClickListener {

    private TabDefinition tabDefinition;
    private final DashboardTab dashboardTab;

    public GridLayoutContainer(DashboardTab dashboardTab, TabDefinition tabDefinition) {
        this.tabDefinition = tabDefinition;
        this.dashboardTab = dashboardTab;
        addStyleName(HybridbpmStyle.OVERFLOW_HIDDEN);
        setSpacing(true);
        refreshUI();
    }

    private void addRowButton(int col, int row) {
        if (HybridbpmUI.getDeveloperMode()) {
            AddRowButton addRowButton = new AddRowButton(this);
            addComponent(addRowButton, col, row, col, row);
            setComponentAlignment(addRowButton, Alignment.MIDDLE_CENTER);
        }
    }

    private void addColumnButton(int col, int row) {
        if (HybridbpmUI.getDeveloperMode()) {
            AddColumnButton addColumnButton = new AddColumnButton(this);
            addComponent(addColumnButton, col, row, col, row);
            setComponentAlignment(addColumnButton, Alignment.MIDDLE_CENTER);
        }
    }

    public void refreshUI() {
        removeAllComponents();
        setRows(tabDefinition.getRows() * 2 + 1);
        setColumns(tabDefinition.getColumns() * 2 + 1);
        for (int i = 0; i < tabDefinition.getRows(); i++) {
            addRowButton(0, i * 2 + 1);
        }
        for (int i = 0; i < tabDefinition.getColumns(); i++) {
            addColumnButton(i * 2 + 1, 0);
        }

        for (PanelDefinition panelDefinition : HybridbpmUI.getDashboardAPI().getPanelDefinitionsByTab(tabDefinition.getId())) {
            addDashboardPanel(panelDefinition, panelDefinition.getColumn() * 2 + 1, panelDefinition.getRow() * 2 + 1);
        }
    }

    public void maximize(DashboardPanel dashboardPanel) {
        setSpacing(false);
        for (Component comp : this) {
            if (!Objects.equals(comp, dashboardPanel)) {
                comp.setVisible(false);
            }
            setColumnExpandRatio(this.getComponentArea(comp).getColumn1(), 0f);
            setRowExpandRatio(this.getComponentArea(comp).getRow1(), 0f);
        }
        setColumnExpandRatio(this.getComponentArea(dashboardPanel).getColumn1(), 1f);
        setRowExpandRatio(this.getComponentArea(dashboardPanel).getRow1(), 1f);
    }

    public void minimize() {
        setSpacing(true);
        for (Component comp : this) {
            comp.setVisible(true);
            if (comp instanceof DashboardPanel) {
                setColumnExpandRatio(this.getComponentArea(comp).getColumn1(), 1f);
                setRowExpandRatio(this.getComponentArea(comp).getRow1(), 1f);
            }
        }

    }

    public void addDashboardPanel(PanelDefinition panelDefinition, int col, int row) {
        DashboardPanel dashboardPanel = new DashboardPanel(dashboardTab, panelDefinition);
        addComponent(dashboardPanel, col, row, col, row);
        setColumnExpandRatio(col, 1f);
        setRowExpandRatio(row, 1f);
        addRowButton(col, row + 1);
        addColumnButton(col + 1, row);
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton() instanceof AddRowButton) {
            Area area = getComponentArea(event.getButton());
            tabDefinition = HybridbpmUI.getDashboardAPI().addRowToGridTabDefinition(tabDefinition, area.getRow1() / 2);
        } else if (event.getButton() instanceof AddColumnButton) {
            Area area = getComponentArea(event.getButton());
            tabDefinition = HybridbpmUI.getDashboardAPI().addColumnToGridTabDefinition(tabDefinition, area.getColumn1() / 2);
        }
        refreshUI();
    }

}
