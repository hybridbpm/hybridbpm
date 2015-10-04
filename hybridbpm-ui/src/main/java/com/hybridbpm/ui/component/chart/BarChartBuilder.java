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

import com.vaadin.addon.charts.model.AbstractPlotOptions;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.PlotOptionsBar;
import com.vaadin.data.Container;

@SuppressWarnings("serial")
public class BarChartBuilder {

    private BarChartBuilder() {
        // no instances for you, use the buildChart method
    }

    private static class XYBarChart extends XYAxisChart {

        public XYBarChart(Container container) {
            super(container);
        }

        @Override
        protected AbstractPlotOptions getOptions() {
            return new PlotOptionsBar();
        }

        @Override
        public ChartType getChartType() {
            return ChartType.BAR;
        }
    }

    private static class XBarChart extends XAxisChart {

        public XBarChart(Container container) {
            super(container);
        }

        @Override
        protected AbstractPlotOptions getOptions() {
            return new PlotOptionsBar();
        }

        @Override
        public ChartType getChartType() {
            return ChartType.BAR;
        }
    }

    public static AbstractChart buildChart(Container container, boolean groupingOn) {
        if (groupingOn) {
            return new XYBarChart(container);
        } else {
            return new XBarChart(container);
        }
    }
}
