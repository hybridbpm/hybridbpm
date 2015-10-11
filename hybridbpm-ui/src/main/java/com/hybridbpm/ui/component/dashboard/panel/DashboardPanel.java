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
package com.hybridbpm.ui.component.dashboard.panel;

import com.hybridbpm.core.data.chart.DbResponse;
import com.hybridbpm.core.data.chart.DiagrammePreferenceValue;
import com.hybridbpm.ui.component.dashboard.tab.DashboardTab;
import com.hybridbpm.core.data.development.Module;
import com.hybridbpm.core.data.dashboard.PanelDefinition;
import com.hybridbpm.core.util.HybridbpmCoreUtil;
import com.hybridbpm.ui.util.HybridbpmStyle;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.util.HybridbpmUiUtil;
import com.hybridbpm.ui.component.ConfigureWindow;
import com.hybridbpm.ui.component.chart.AbstractChart;
import com.hybridbpm.core.data.chart.DiagrammePreference;
import com.hybridbpm.ui.component.chart.util.DiagrammeChartType;
import static com.hybridbpm.ui.component.chart.util.DiagrammeUtil.getPreferenceValue;
import com.vaadin.addon.charts.Chart;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import java.util.Map;
import java.util.Objects;
import org.vaadin.dialogs.ConfirmDialog;

/**
 *
 * @author Marat Gubaidullin
 */
public class DashboardPanel extends VerticalLayout {

    protected DashboardTab dashboardTab;
    private PanelDefinition panelDefinition;
    private Label caption = new Label();
    private final PanelMenuBar menuBar = new PanelMenuBar(this);
    private HorizontalLayout toolbar = new HorizontalLayout(caption, menuBar);
    private boolean maximized = false;

    public DashboardPanel(DashboardTab panelView, PanelDefinition pd) {
        this.dashboardTab = panelView;
        this.panelDefinition = pd;

        toolbar = new HorizontalLayout();
        toolbar.setMargin(new MarginInfo(false, false, false, true));
        toolbar.setWidth(99, Unit.PERCENTAGE);
        toolbar.setStyleName(HybridbpmStyle.LAYOUT_PADDING8);

        caption.addStyleName(ValoTheme.LABEL_H4);
        caption.addStyleName(ValoTheme.LABEL_COLORED);
        caption.addStyleName(ValoTheme.LABEL_NO_MARGIN);

        toolbar.removeAllComponents();
        toolbar.addComponents(caption, new PanelMenuBar(this));
        toolbar.setExpandRatio(caption, 1);
        toolbar.setComponentAlignment(caption, Alignment.MIDDLE_LEFT);

        setSizeFull();
        setStyleName(ValoTheme.LAYOUT_CARD);
        addStyleName("dashboard-panel");

        configureModule();
    }

    private void configureModule() {
        removeAllComponents();
        addComponent(toolbar);
        panelDefinition = HybridbpmUI.getDashboardAPI().getPanelDefinitionsById(panelDefinition.getId().toString());
        caption.setValue(panelDefinition.getTitle().getValue(HybridbpmUI.getCurrent().getLocale()));
        if (panelDefinition.getModuleType() != null && panelDefinition.getModuleName() != null) {
            Module module = HybridbpmUI.getDevelopmentAPI().getModuleByName(panelDefinition.getModuleName());
            if (Objects.equals(panelDefinition.getModuleType(), Module.MODULE_TYPE.CHART)) {
                DiagrammePreference diagrammePreference = HybridbpmCoreUtil.jsonToObject(module.getDesign(), DiagrammePreference.class);
                BeanFieldGroup<DiagrammePreference> preferences = new BeanFieldGroup<>(DiagrammePreference.class);
                preferences.setItemDataSource(diagrammePreference);
                DiagrammePreferenceValue secondFieldValue = getPreferenceValue(DiagrammePreference.SECOND_COLUMN_FIELD, preferences);
                boolean groupingOn = secondFieldValue != null;
                IndexedContainer container = prepareData(diagrammePreference.getQuery());
                AbstractChart abstractChart =  DiagrammeChartType.valueOf(diagrammePreference.getChartType()).createChart(container, groupingOn);
                abstractChart.bind(preferences);
                abstractChart.render();
                Chart chart = abstractChart.drawChart();
                addComponent(chart);
                setExpandRatio(chart, 1f);
            } else if (Objects.equals(panelDefinition.getModuleType(), Module.MODULE_TYPE.FORM)) {
                Component component = (Component) HybridbpmUiUtil.generateFormObject(module);
                addComponent(component);
                setExpandRatio(component, 1f);
            }
        }
    }

    private IndexedContainer prepareData(String query) {
        try {
            IndexedContainer container = new IndexedContainer();
            DbResponse response = HybridbpmUI.getCrudAPI().queryODocuments(query);
            Map<Integer, Map<String, Object>> data = response.getData();
            for (String name : response.getHeader().keySet()) {
                container.addContainerProperty(name, response.getHeader().get(name), null);
            }
            for (Integer rowId : data.keySet()) {
                Item item = container.addItem(rowId);
                Map<String, Object> map = data.get(rowId);
                for (String name : response.getHeader().keySet()) {
                    item.getItemProperty(name).setValue(map.get(name));
                }
            }
            return container;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public PanelDefinition getPanelDefinition() {
        return panelDefinition;
    }

    public boolean isMaximized() {
        return maximized;
    }

    public void setMaximized(boolean maximized) {
        this.maximized = maximized;
    }

    public class PanelMenuBar extends MenuBar implements MenuBar.Command, Window.CloseListener {

        private final DashboardPanel dashboardPanel;
        private MenuBar.MenuItem max;
        private MenuBar.MenuItem menuCog;
        private MenuBar.MenuItem configure;
        private MenuBar.MenuItem deletePanel;

        public PanelMenuBar(DashboardPanel dp) {
            this.dashboardPanel = dp;
            addStyleName(ValoTheme.MENUBAR_BORDERLESS);
            addStyleName(ValoTheme.MENUBAR_SMALL);

            if (HybridbpmUI.getDeveloperMode()) {
                menuCog = addItem("", FontAwesome.COG, null);

                configure = menuCog.addItem("Configure", FontAwesome.WRENCH, this);
                deletePanel = menuCog.addItem("Delete", FontAwesome.TIMES_CIRCLE, this);
            } else {
                max = addItem("", FontAwesome.EXPAND, this);
                max.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
            }
        }

        @Override
        public void menuSelected(MenuBar.MenuItem selectedItem) {
            if (selectedItem.equals(max)) {
                selectedItem.setIcon(dashboardPanel.isMaximized() ? FontAwesome.EXPAND : FontAwesome.COMPRESS);
                dashboardTab.toggleMaximized(dashboardPanel, !dashboardPanel.isMaximized());
                dashboardPanel.setMaximized(!dashboardPanel.isMaximized());
            } else if (selectedItem.equals(configure)) {
                openPanelConfigurationWindow();
            } else if (selectedItem.equals(deletePanel)) {
                deletePanel();
            }
        }

        @Override
        public void windowClose(Window.CloseEvent e) {
            configureModule();
        }

        private void openPanelConfigurationWindow() {
            final PanelConfigurationLayout panelLayout = new PanelConfigurationLayout(panelDefinition);
            final ConfigureWindow configureWindow = new ConfigureWindow(panelLayout, "Panel parameters");
            Button.ClickListener clickListener = new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    if (event.getButton().equals(configureWindow.btnClose)) {

                    } else if (event.getButton().equals(configureWindow.btnOk)) {
                        panelLayout.save();
                    }
                    configureWindow.close();
                }
            };
            configureWindow.setClickListener(clickListener);
            configureWindow.addCloseListener(this);
            HybridbpmUI.getCurrent().addWindow(configureWindow);
        }

        private void deletePanel() {
            ConfirmDialog.show(UI.getCurrent(), "Please Confirm:", "Delete panel?", "OK", "Cancel", new ConfirmDialog.Listener() {

                @Override
                public void onClose(ConfirmDialog dialog) {
                    if (dialog.isConfirmed()) {
                        HybridbpmUI.getDashboardAPI().deletePanelDefinition(panelDefinition.getId().toString(), false);
                        dashboardTab.refresh();
                    }
                }
            });
        }

    }
}
