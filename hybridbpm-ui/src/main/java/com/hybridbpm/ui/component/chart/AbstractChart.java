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
import com.hybridbpm.ui.component.chart.util.DiagrammeUtil;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.data.Container;
import com.vaadin.data.fieldgroup.BeanFieldGroup;

import java.io.Serializable;

@SuppressWarnings("serial")
public abstract class AbstractChart implements Serializable {

    protected abstract class ChartContainer implements Serializable {

        private Container container;

        protected ChartContainer(Container container) {
            this.container = container;
        }

        public Container getContainer() {
            return container;
        }
    }

    public AbstractChart(Container container) {
        this.chart = new Chart();
        this.configuration = new Configuration();
        this.container = container;

        chart.setSizeFull();
        chart.setCaption(null);
        chart.setConfiguration(getConfiguration());
        chart.setImmediate(true);

        getConfiguration().getChart().setType(getChartType());
        getConfiguration().getLegend().setEnabled(true);
        getConfiguration().getTitle().setText(null);
        getConfiguration().disableCredits();
    }

    private Chart chart;
    private Configuration configuration;
    private BeanFieldGroup<DiagrammePreference> diagrammePreference;
    private Container container;

    /**
     * Renders the chart with the {@link DiagrammePreference} and data source {@link Container}. Both of them have to be set beforehand.
     */
    public final void render() {
        initPreferences();
        checkState();

        renderChart();
    }

    public abstract ChartType getChartType();

    protected abstract void renderChart();

    /**
     * This hook is called before rendering chart. Descendant charts must override it to check their state.
     */
    protected abstract void checkState();

    protected abstract void initPreferences();

    public final void bind(BeanFieldGroup<DiagrammePreference> diagrammePreference) {
        this.diagrammePreference = diagrammePreference;
    }

    protected Container getContainer() {
        return container;
    }

    /**
     * Set chart data source container
     *
     * @param container a new data source
     */
    public void setContainer(Container container) {
        this.container = container;
    }

    /**
     * Returns wrapped vaadin chart, so it can be attached somewhere
     *
     * @return the wrapped chart
     */
    public Chart drawChart() {
        chart.drawChart(configuration);
        return chart;
    }

    <X> X getPreferenceValue(String propertyName) {
        return DiagrammeUtil.getPreferenceValue(propertyName, diagrammePreference);
    }

    void setPreferenceValue(String propertyName, Object value) {
        if (!getPreferenceValue(propertyName).equals(value)) {
            diagrammePreference.getItemDataSource().getItemProperty(propertyName).setValue(value);
        }
    }

    protected BeanFieldGroup<DiagrammePreference> getDiagrammePreference() {
        return diagrammePreference;
    }

    public Chart getChart() {
        return chart;
    }

    protected Configuration getConfiguration() {
        return configuration;
    }

}
