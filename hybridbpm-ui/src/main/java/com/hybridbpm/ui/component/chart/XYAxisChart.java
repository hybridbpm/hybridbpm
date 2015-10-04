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
import com.hybridbpm.ui.component.chart.manager.XYSeriesManager;
import static com.hybridbpm.ui.component.chart.util.DiagrammeUtil.checkNotEmpty;
import static com.hybridbpm.ui.component.chart.util.DiagrammeUtil.getCategoriesNames;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.component.chart.color.ChartColorer;
import com.vaadin.addon.charts.model.AbstractPlotOptions;
import com.vaadin.addon.charts.model.XAxis;
import com.vaadin.addon.charts.model.YAxis;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.data.Container;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("serial")
public abstract class XYAxisChart extends ThreeParamChart {

    private XYSeriesManager pointsManager;
    private ChartColorer chartColorer;

    public XYAxisChart(Container container) {
        super(container);

        YAxis y = new YAxis();
        getConfiguration().addyAxis(y);

        XAxis x = new XAxis();
        getConfiguration().addxAxis(x);

        pointsManager = new XYSeriesManager(getConfiguration());
    }

    @Override
    protected void initPreferences() {
        super.initPreferences();
        Map<String, String> valueColourMap = getPreferenceValue(DiagrammePreference.VALUE_COLOUR_MAP);
        this.chartColorer = new ChartColorer(valueColourMap);

        getConfiguration().getyAxis().setTitle(getValuesColumnName());

    }

    @Override
    protected void renderChart(Map<ColumnCoupleKey<?, ?>, Object> data,
                               Set secondColumnValues, Set firstColumnValues) {

        String[] categoriesNames = getCategoriesNames(firstColumnValues);
        getConfiguration().getxAxis().setCategories(categoriesNames);

        pointsManager.beginUpdate();
        for (Object secondColumnValue : secondColumnValues) {

            String secondColumnText = secondColumnValue.toString();

            // Store series data in an array
            Number[] groupedValues = new Number[firstColumnValues.size()];
            Iterator it = firstColumnValues.iterator();
            int i = 0;
            while (it.hasNext()) {
                Object firstColumnValue = it.next();
                Number value = (Number) data.get(new ColumnCoupleKey(secondColumnValue, firstColumnValue));
                if (value == null) {
                    value = 0;
                }
                groupedValues[i] = value;
                i++;
            }

            AbstractPlotOptions options = getOptions();
            SolidColor color = chartColorer.lookupColor(secondColumnText);
            options.setColor(color);

            pointsManager.addPoint(groupedValues, secondColumnText, options);
        }

        // render all items
        pointsManager.renderPoints();

        // save changes in values colors back to the preference
        setPreferenceValue(DiagrammePreference.VALUE_COLOUR_MAP, chartColorer.getValueColourMap());
    }

    /**
     * Descendant charts must return their plot options into this parent class
     *
     * @return descendant's plot options
     */
    protected abstract AbstractPlotOptions getOptions();

    @Override
    protected void checkState() {
        super.checkState();
        checkNotEmpty(chartColorer, HybridbpmUI.getText("color-value-not-set"));
        checkNotEmpty(chartColorer.getValueColourMap(), HybridbpmUI.getText("color-value-not-set"));
    }
}
