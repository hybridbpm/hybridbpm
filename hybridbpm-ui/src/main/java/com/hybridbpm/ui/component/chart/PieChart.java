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
package com.hybridbpm.ui.component.chart;

import com.hybridbpm.core.data.chart.DiagrammePreference;
import com.hybridbpm.ui.component.chart.manager.SimpleDataSeriesManager;
import static com.hybridbpm.ui.component.chart.util.DiagrammeUtil.checkNotEmpty;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.component.chart.color.ChartColorer;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.PlotOptionsPie;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.data.Container;

import java.util.Map;
import java.util.Set;


@SuppressWarnings("serial")
public class PieChart extends TwoParamChart {

    private SimpleDataSeriesManager itemsManager;
    private ChartColorer chartColorer;

    public PieChart(Container container) {
        super(container);

        itemsManager = new SimpleDataSeriesManager();

        PlotOptionsPie pieOptions = new PlotOptionsPie();
        pieOptions.setBorderWidth(0.5);
        getConfiguration().setPlotOptions(pieOptions);

        getConfiguration().setSeries(itemsManager.getSeries());
    }

    @Override
    protected void initPreferences() {
        super.initPreferences();
        Map<String, String> valueColourMap = getPreferenceValue(DiagrammePreference.VALUE_COLOUR_MAP);
        this.chartColorer = new ChartColorer(valueColourMap);

        itemsManager.getSeries().setName(getValuesColumnName());
    }

    @Override
    protected void renderChart(Map data) {
        itemsManager.beginUpdate();

        Set<Map.Entry> entries = data.entrySet();
        for (Map.Entry entry : entries) {
            Object column = entry.getKey();
            Object value = entry.getValue();

            // Assume that the column could be either numeric or text
            // and values are always numeric
            String columnLabel = column.toString();
            Number valueNum = (Number) value;

            // draw the item
            SolidColor color = chartColorer.lookupColor(columnLabel);
            itemsManager.addItem(valueNum, columnLabel, color);
        }

        // render all items
        itemsManager.renderItems();

        // save changes in values colors back to the preference
        setPreferenceValue(DiagrammePreference.VALUE_COLOUR_MAP, chartColorer.getValueColourMap());
    }

    public ChartType getChartType() {
        return ChartType.PIE;
    }

    @Override
    protected void checkState() {
        super.checkState();
        checkNotEmpty(chartColorer, HybridbpmUI.getText("color-value-not-set"));
        checkNotEmpty(chartColorer.getValueColourMap(), HybridbpmUI.getText("color-value-not-set"));
    }
}
