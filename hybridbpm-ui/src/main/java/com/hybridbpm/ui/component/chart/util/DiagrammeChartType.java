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
package com.hybridbpm.ui.component.chart.util;

import com.hybridbpm.core.data.chart.DiagrammePreference;
import com.hybridbpm.ui.util.Translate;
import com.hybridbpm.ui.util.IcoMoon;
import com.hybridbpm.ui.component.chart.AbstractChart;
import com.hybridbpm.ui.component.chart.AreaChartBuilder;
import com.hybridbpm.ui.component.chart.BarChartBuilder;
import com.hybridbpm.ui.component.chart.ColumnChartBuilder;
import com.hybridbpm.ui.component.chart.DonutChart;
import com.hybridbpm.ui.component.chart.GaugeChart;
import com.hybridbpm.ui.component.chart.LineChartBuilder;
import com.hybridbpm.ui.component.chart.PieChart;
import com.hybridbpm.ui.component.chart.configuration.ChartConfigureLayout;
import com.hybridbpm.ui.component.chart.configuration.DonutChartConfLayout;
import com.hybridbpm.ui.component.chart.configuration.GaugeChartConfLayout;
import com.hybridbpm.ui.component.chart.configuration.PieChartConfLayout;
import com.hybridbpm.ui.component.chart.configuration.PreferencesLayoutTemplate;
import com.hybridbpm.ui.component.chart.configuration.ThreeParamChartConfLayout;
import com.hybridbpm.ui.component.chart.color.ChartColorLayout;
import com.hybridbpm.ui.component.chart.color.GaugeBandLayout;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.data.Container;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;

/**
 *
 * @author Marat Gubaidullin
 */
public enum DiagrammeChartType {

    PIE("Pie", ChartType.PIE, ChartGroupBy.NO_GROUP_BY, IcoMoon.PIE, new GeneralChartFactory() {
        @Override
        public ChartConfigureLayout createChartConfigLayout(BeanFieldGroup<DiagrammePreference> preferences) {
            return new PieChartConfLayout(preferences);
        }

        @Override
        public AbstractChart createChart(Container container, boolean groupBySecondColumn) {
            return new PieChart(container);
        }
    }),

    DONUT("Donut", ChartType.PIE, ChartGroupBy.ONLY_GROUPED, IcoMoon.DONUT, new GeneralChartFactory() {
        @Override
        public ChartConfigureLayout createChartConfigLayout(BeanFieldGroup<DiagrammePreference> preferences) {
            return new DonutChartConfLayout(preferences);
        }

        @Override
        public AbstractChart createChart(Container container, boolean groupBySecondColumn) {
            if (!groupBySecondColumn) {
                throw new IllegalStateException("Donut chart cannot show ungrouped data.");
            }
            return new DonutChart(container);
        }
    }),

    BAR("Bar", ChartType.BAR, IcoMoon.BAR, new GeneralChartFactory() {
        @Override
        public ChartConfigureLayout createChartConfigLayout(BeanFieldGroup<DiagrammePreference> preferences) {
            return new ThreeParamChartConfLayout(preferences);
        }

        @Override
        public AbstractChart createChart(Container container, boolean groupBySecondColumn) {
            return BarChartBuilder.buildChart(container, groupBySecondColumn);
        }
    }),

    COLUMN("Column", ChartType.COLUMN, IcoMoon.COLUMN, new GeneralChartFactory() {
        @Override
        public ChartConfigureLayout createChartConfigLayout(BeanFieldGroup<DiagrammePreference> preferences) {
            return new ThreeParamChartConfLayout(preferences);
        }

        @Override
        public AbstractChart createChart(Container container, boolean groupBySecondColumn) {
            return ColumnChartBuilder.buildChart(container, groupBySecondColumn);
        }
    }),

    LINE("Line", ChartType.LINE, IcoMoon.LINE, new GeneralChartFactory() {
        @Override
        public ChartConfigureLayout createChartConfigLayout(BeanFieldGroup<DiagrammePreference> preferences) {
            return new ThreeParamChartConfLayout(preferences);
        }

        @Override
        public AbstractChart createChart(Container container, boolean groupBySecondColumn) {
            return LineChartBuilder.buildChart(container, groupBySecondColumn);
        }
    }),

    AREA("Area", ChartType.AREA, IcoMoon.AREA, new GeneralChartFactory() {
        @Override
        public ChartConfigureLayout createChartConfigLayout(BeanFieldGroup<DiagrammePreference> preferences) {
            return new ThreeParamChartConfLayout(preferences);
        }

        @Override
        public AbstractChart createChart(Container container, boolean groupBySecondColumn) {
            return AreaChartBuilder.buildChart(container, groupBySecondColumn);
        }
    }),

    GAUGE("Gauge", ChartType.GAUGE, ChartGroupBy.NO_GROUP_BY, FontAwesome.TACHOMETER, new ChartFactory() {
        @Override
        public ChartConfigureLayout createChartConfigLayout(BeanFieldGroup<DiagrammePreference> preferences) {
            return new GaugeChartConfLayout(preferences);
        }

        @Override
        public AbstractChart createChart(Container container, boolean groupBySecondColumn) {
            // even if group by second column is set just ignore it
            // as gauge chart cannot show grouped data
            return new GaugeChart(container);
        }

        @Override
        public PreferencesLayoutTemplate createColorLayout(BeanFieldGroup<DiagrammePreference> preferences) {
            return new GaugeBandLayout(preferences);
        }
    });

    public enum ChartGroupBy {
        NO_GROUP_BY, CAN_BE_GROUPED, ONLY_GROUPED
    }

    public interface ChartFactory {

        ChartConfigureLayout createChartConfigLayout(BeanFieldGroup<DiagrammePreference> preferences);

        AbstractChart createChart(Container container, boolean groupBySecondColumn);

        PreferencesLayoutTemplate createColorLayout(BeanFieldGroup<DiagrammePreference> preferences);
    }

    public abstract static class GeneralChartFactory implements ChartFactory {

        @Override
        public PreferencesLayoutTemplate createColorLayout(BeanFieldGroup<DiagrammePreference> preferences) {
            return new ChartColorLayout(preferences);
        }
    }

    private final String name;
    private final ChartType chartType;
    private final ChartGroupBy groupByOption;
    private final Resource icon;
    private final ChartFactory factory;

    DiagrammeChartType(String name, ChartType chartType, ChartGroupBy groupByOption, Resource icon, ChartFactory factory) {
        this.name = name;
        this.chartType = chartType;
        this.icon = icon;
        this.factory = factory;
        this.groupByOption = groupByOption;
    }

    DiagrammeChartType(String name, ChartType chartType, Resource icon, ChartFactory factory) {
        this.name = name;
        this.chartType = chartType;
        this.icon = icon;
        this.factory = factory;
        this.groupByOption = ChartGroupBy.CAN_BE_GROUPED;
    }
    
    /**
     * Returns the name of the chart type
     * @return The name of the chart type, as displayed in the UI
     */
    public String getName() {
        return Translate.getMessage(name);
    }

    /**
     * Returns the icon representing the chart type
     * @return The icon representing the chart type
     */
    public Resource getIcon() {
        return icon;
    }

    /**
     * Returns the Vaadin chart type
     * @return The Vaadin chart type
     */
    public ChartType getChartType() {
        return chartType;
    }

    /**
     * Indicates if the chart type can be grouped by the second column
     * @return {@link ChartGroupBy} that indicates if the chart type can have grouping
     */
    public ChartGroupBy getGroupByOption() {
        return groupByOption;
    }

    public ChartConfigureLayout createConfigLayout(BeanFieldGroup<DiagrammePreference> preferences) {
        return factory.createChartConfigLayout(preferences);
    }

    public AbstractChart createChart(Container container, boolean groupBySecondColumn) {
        return factory.createChart(container, groupBySecondColumn);
    }

    public PreferencesLayoutTemplate getColorLayout(BeanFieldGroup<DiagrammePreference> preferences) {
        return factory.createColorLayout(preferences);
    }
}
