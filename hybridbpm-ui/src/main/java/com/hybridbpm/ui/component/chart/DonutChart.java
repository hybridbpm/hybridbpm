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

import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.core.data.chart.DiagrammePreference;
import com.hybridbpm.ui.component.chart.manager.GroupedDataSeriesManager;
import com.hybridbpm.ui.component.chart.manager.SimpleDataSeriesManager;
import static com.hybridbpm.ui.component.chart.util.DiagrammeUtil.checkNotEmpty;
import com.hybridbpm.ui.component.chart.color.ChartColorer;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Labels;
import com.vaadin.addon.charts.model.PlotOptionsPie;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.data.Container;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@SuppressWarnings("serial")
public class DonutChart extends ThreeParamChart {

    private ChartColorer chartColorer;
    private SimpleDataSeriesManager innerItemsManager;
    private GroupedDataSeriesManager outerItemsManager;

    public DonutChart(Container container) {
        super(container);

        innerItemsManager = new SimpleDataSeriesManager();
        outerItemsManager = new GroupedDataSeriesManager();

        // Setup outer ring
        PlotOptionsPie outerOptions = new PlotOptionsPie();
        outerOptions.setInnerSize(200);
        outerOptions.setSize("80%");
        outerItemsManager.getSeries().setPlotOptions(outerOptions);
        outerOptions.setBorderWidth(0.5);

        // Setup inner aggregation ring
        PlotOptionsPie innerOptions = new PlotOptionsPie();
        innerOptions.setSize(200);
        innerItemsManager.getSeries().setPlotOptions(innerOptions);
        innerOptions.setDataLabels(new Labels());
        innerOptions.getDataLabels().setFormatter("this.y > 5 ? this.point.name : null");
        innerOptions.getDataLabels().setColor(new SolidColor(255, 255, 255));
        innerOptions.getDataLabels().setDistance(-35);
        innerOptions.setBorderWidth(0.5);

        getConfiguration().setSeries(innerItemsManager.getSeries(), outerItemsManager.getSeries());
    }

    @Override
    protected void initPreferences() {
        super.initPreferences();
        Map<String, String> valueColourMap = getPreferenceValue(DiagrammePreference.VALUE_COLOUR_MAP);
        this.chartColorer = new ChartColorer(valueColourMap);

        outerItemsManager.getSeries().setName(getFirstColumnName());
        innerItemsManager.getSeries().setName(getSecondColumnName());
    }

    @Override
    protected void renderChart(Map<ColumnCoupleKey<?, ?>, Object> data,
                               Set secondColumnValues, Set firstColumnValues) {

        outerItemsManager.beginUpdate();

        // setup outer data
        Map<String, Number> totals = new HashMap<String, Number>();
        for (Object secondColumnValue : secondColumnValues) {
            for (Object firstColumnValue : firstColumnValues) {

                // Assume that the column could be either numeric or text
                // and values are always numeric
                Number value = (Number) data.get(new ColumnCoupleKey(secondColumnValue, firstColumnValue));
                if (value != null) {
                    String secondColumnTxt = secondColumnValue.toString();
                    String firstColumnTxt = firstColumnValue.toString();

                    // draw the outer ring item
                    SolidColor color = chartColorer.lookupColor(secondColumnTxt);
                    // changes are accumulated in the manager and will be rendered all at once afterwards
                    outerItemsManager.addItem(value, firstColumnTxt, color, secondColumnTxt);

                    // count total for the inner circle
                    Number columnTotal = totals.get(secondColumnTxt);
                    if (columnTotal == null) {
                        columnTotal = 0;
                    }
                    totals.put(secondColumnTxt, columnTotal.doubleValue() + value.doubleValue());
                }
            }
        }

        // render all outer ring items
        outerItemsManager.renderItems();

        // setup inner data
        innerItemsManager.beginUpdate();
        for (Object secondColumnObject : secondColumnValues) {
            String secondColumnTxt = secondColumnObject.toString();
            SolidColor color = chartColorer.lookupColor(secondColumnTxt);
            // draw the inner circle item
            innerItemsManager.addItem(totals.get(secondColumnTxt), secondColumnTxt, color);
        }
        // render all inner circle items
        innerItemsManager.renderItems();

        // save changes in values colors back to the preference
        setPreferenceValue(DiagrammePreference.VALUE_COLOUR_MAP, chartColorer.getValueColourMap());
    }

    @Override
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
