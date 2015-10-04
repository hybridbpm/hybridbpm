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
package com.hybridbpm.ui.component.chart.configuration;

import com.hybridbpm.ui.component.chart.util.DiagrammeUtil;
import com.hybridbpm.ui.component.chart.util.DiagrammeChartType;
import com.hybridbpm.core.data.chart.DiagrammePreference;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;
import com.hybridbpm.ui.HybridbpmUI;


@SuppressWarnings("serial")
public class ChartTypeLayout extends PreferencesLayoutTemplate implements Property.ValueChangeListener {

    private final OptionGroup chartTypeGroup = new OptionGroup(HybridbpmUI.getText("choose-chart-type"));
    private final VerticalLayout chartConfigureContainer = new VerticalLayout();
    private final HorizontalLayout mainLayout = new HorizontalLayout(chartTypeGroup, chartConfigureContainer);
    private ChartConfigureLayout currentLayout;

    public ChartTypeLayout(BeanFieldGroup<DiagrammePreference> preferences) {
        super(preferences);
        mainLayout.setMargin(false);
        mainLayout.setSpacing(true);
        mainLayout.setWidth(100, Unit.PERCENTAGE);
        chartTypeGroup.setRequired(true);
        mainLayout.setExpandRatio(chartTypeGroup, 1f);
        mainLayout.setExpandRatio(chartConfigureContainer, 2f);
        addComponent(mainLayout);

        for (DiagrammeChartType chartType : DiagrammeChartType.values()) {
            chartTypeGroup.addItem(chartType);
            chartTypeGroup.setItemIcon(chartType, chartType.getIcon());
            chartTypeGroup.setItemCaption(chartType, chartType.getName());
        }
        chartTypeGroup.setImmediate(true);
        chartTypeGroup.setNullSelectionAllowed(false);
    }

    @Override
    public void bindConfigurationValues() {
        bindField(chartTypeGroup, DiagrammePreference.CHART_TYPE, preferences);
        bindCurrentLayout();

        chartTypeGroup.addValueChangeListener(this);
        DiagrammeChartType chartType = DiagrammeUtil.getPreferenceValue(DiagrammePreference.CHART_TYPE, preferences);
        if (chartType != null) {
            showCurrentChartType(chartType);
        }
    }

    @Override
    public void unbindConfigurationValues() {
        unbindField(DiagrammePreference.CHART_TYPE, preferences);
        unbindCurrentLayout();
    }

    private void bindCurrentLayout() {
        if (currentLayout != null) {
//            chartLayout.updateDependencies();
            currentLayout.bindConfigurationValues();
        }
    }

    private void unbindCurrentLayout() {
        if (currentLayout != null) {
            currentLayout.unbindConfigurationValues();
        }
    }

    public DiagrammeChartType getChartType() {
        return (DiagrammeChartType) chartTypeGroup.getValue();
    }

    @Override
    public void valueChange(Property.ValueChangeEvent event) {
        // remove all colors preferences, we have a new chart type selected
//        chartLayout.flushColors();

        showCurrentChartType((DiagrammeChartType) event.getProperty().getValue());
    }

    private void showCurrentChartType(DiagrammeChartType chartType) {
        // undo all bindings
        unbindCurrentLayout();

        chartConfigureContainer.removeAllComponents();

        currentLayout = chartType.createConfigLayout(preferences);
        chartConfigureContainer.addComponent(currentLayout);

        bindCurrentLayout();
    }

    @Override
    public void commit() {
        chartTypeGroup.commit();
    }

    @Override
    public void updateComboboxes() {
        if (currentLayout != null) {
            currentLayout.updateComboboxes();
        }
    }

}
