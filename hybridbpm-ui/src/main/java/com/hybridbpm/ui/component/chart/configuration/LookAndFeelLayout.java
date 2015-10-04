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

import com.hybridbpm.ui.component.chart.AbstractChart;
import com.hybridbpm.ui.component.chart.util.DiagrammeChartType;
import com.hybridbpm.core.data.chart.DiagrammePreference;
import com.hybridbpm.core.data.chart.DiagrammePreferenceValue;
import static com.hybridbpm.ui.component.chart.util.DiagrammeUtil.getPreferenceValue;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.VerticalLayout;
import com.hybridbpm.ui.HybridbpmUI;

/**
 * @author mgubaidullin
 */
@SuppressWarnings("serial")
public class LookAndFeelLayout extends PreferencesLayoutTemplate {

    private AbstractChart chart;
    private PreferencesLayoutTemplate colorLayout;

    private final Button btnPreview = new Button(HybridbpmUI.getText("preview"));

    private final HorizontalSplitPanel splitLayout = new HorizontalSplitPanel();
    private final VerticalLayout chartSplit = new VerticalLayout();
    private final VerticalLayout chartContainer = new VerticalLayout();
    private final VerticalLayout colourContainer = new VerticalLayout();

    public LookAndFeelLayout(BeanFieldGroup<DiagrammePreference> preferences) {
        super(preferences);
        addComponent(splitLayout);

        chartSplit.addComponent(chartContainer);
        chartSplit.addComponent(btnPreview);

        btnPreview.setIcon(FontAwesome.PLAY);
        chartSplit.setComponentAlignment(chartContainer, Alignment.MIDDLE_CENTER);
        chartSplit.setExpandRatio(chartContainer, 1f);

        chartSplit.setSizeFull();
        colourContainer.setSizeFull();

        splitLayout.setSizeFull();
        splitLayout.addComponent(chartSplit);
        splitLayout.setSplitPosition(70f);
        splitLayout.addComponent(colourContainer);

        bindHandlers();
    }

    /**
     * Attaches listeners to ui elements
     */
    private void bindHandlers() {
        btnPreview.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                rebuildChart();
            }
        });
    }

    @Override
    public void bindConfigurationValues() {
        super.bindConfigurationValues();
        if (colorLayout != null) {
            colorLayout.bindConfigurationValues();
        }
    }

    @Override
    public void unbindConfigurationValues() {
        super.unbindConfigurationValues();
        if (colorLayout != null) {
            colorLayout.unbindConfigurationValues();
        }
    }

    /**
     * Rebuilds the whole chart.
     */
    public void rebuildChart() {
        chartContainer.removeAllComponents();

        final BeanFieldGroup<DiagrammePreference> preference = preferences;
        DiagrammeChartType chartType = getPreferenceValue(DiagrammePreference.CHART_TYPE, preference);
        DiagrammePreferenceValue secondFieldValue = getPreferenceValue(DiagrammePreference.SECOND_COLUMN_FIELD, preference);
        boolean groupingOn = secondFieldValue != null;

        if (chartType != null) {
//            Container container = chartLayout.getContainerData().getContainer();
//            chart = chartType.createChart(container, groupingOn);
//
//            // bind the new one
//            chart.bind(preference);
//
//            chart.render();
//            chartContainer.addComponent(chart.drawChart());
        }
    }

    @Override
    public void commit() {
    }

    @Override
    public void updateComboboxes() {
        if (colorLayout != null) {
            colorLayout.updateComboboxes();
        }
    }

    public void showColorLayout(DiagrammeChartType chartType) {
        colourContainer.removeAllComponents();

        if (colorLayout != null) {
            colorLayout.unbindConfigurationValues();
        }

        colorLayout = chartType.getColorLayout(preferences);
        colorLayout.bindConfigurationValues();

        colourContainer.addComponent(colorLayout);
    }

    public void renderChart() {
        if (chart != null) {
            // rebind the chart to update bands
            chart.bind(preferences);
            chart.render();
            chart.drawChart();
        }
    }
}
