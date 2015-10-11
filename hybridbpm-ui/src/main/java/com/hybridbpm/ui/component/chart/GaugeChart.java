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
import com.hybridbpm.core.data.chart.DiagrammePreferenceValue;
import com.hybridbpm.core.data.chart.PlotBandPreference;
import com.hybridbpm.ui.component.chart.manager.GaugeSeriesManager;
import static com.hybridbpm.ui.component.chart.util.DiagrammeUtil.checkNotEmpty;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.component.chart.color.ColourUtil;
import com.hybridbpm.ui.util.Translate;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.data.Container;

import java.util.*;


@SuppressWarnings("serial")
public class GaugeChart extends TwoParamChart {

    private GaugeSeriesManager pointsManager;
    private List<PlotBandPreference> plotBandList;
    private Number maxValue;
    private Number minValue;

    public GaugeChart(Container container) {
        super(container);

        getConfiguration().getPane().setStartAngle(-120);
        getConfiguration().getPane().setEndAngle(120);

        PlotOptionsGauge options = new PlotOptionsGauge();
        options.setDataLabels(new Labels(false));
        getConfiguration().setPlotOptions(options);

        YAxis yAxis = getConfiguration().getyAxis();
        yAxis.setMinorTickInterval("auto");
        yAxis.setMinorTickWidth(1);
        yAxis.setMinorTickLength(10);
        yAxis.setTickPosition(TickPosition.INSIDE);
        yAxis.setShowFirstLabel(false);

        pointsManager = new GaugeSeriesManager(getConfiguration());
    }

    @Override
    protected void initPreferences() {
        super.initPreferences();
        this.plotBandList = getPreferenceValue(DiagrammePreference.PLOT_BAND_LIST);
        this.maxValue = getPreferenceValue(DiagrammePreference.MAX_VALUE);
        this.minValue = getPreferenceValue(DiagrammePreference.MIN_VALUE);

        getConfiguration().getyAxis().setMin(minValue);
        getConfiguration().getyAxis().setMax(maxValue);

        // Plot bands creation
        PlotBand[] plotBands = createPlotBands();
        getConfiguration().getyAxis().setPlotBands(plotBands);
    }

    @Override
    protected void renderChart(Map data) {
        pointsManager.beginUpdate();

        // fill series with chart data
        Iterator<Map.Entry> it = data.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = it.next();
            Object column = entry.getKey();
            Object value = entry.getValue();

            // Assume that the column could be either numeric or text
            // and values are always numeric
            String columnLabel = column.toString();
            Number valueNum = (Number) value;

            pointsManager.addPoint(valueNum, columnLabel);
        }

        // render all items
        pointsManager.renderPoints();
    }

    private PlotBand[] createPlotBands() {
        PlotBand[] plotBands = new PlotBand[plotBandList.size()];
        for (int i = 0; i < plotBandList.size(); i++) {
            PlotBandPreference plot = plotBandList.get(i);
            int[] col = ColourUtil.decode(plot.getColor());
            plotBands[i] = new PlotBand(plot.getStartValue(), plot.getEndValue(), new SolidColor(col[0], col[1], col[2]));
        }
        return plotBands;
    }

    @Override
    protected void checkState() {
        super.checkState();
        checkNotEmpty(plotBandList, Translate.getMessage("plot-band-not-set"));
        checkNotEmpty(maxValue, Translate.getMessage("max-value-not-set"));
        checkNotEmpty(minValue, Translate.getMessage("min-value-not-set"));
    }

    @Override
    public ChartType getChartType() {
        return ChartType.GAUGE;
    }
}
