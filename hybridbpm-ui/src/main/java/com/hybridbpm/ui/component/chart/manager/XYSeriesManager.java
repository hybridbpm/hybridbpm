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
package com.hybridbpm.ui.component.chart.manager;

import com.vaadin.addon.charts.model.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("serial")
public class XYSeriesManager {

    protected static class PointsGroup {
        private DataSeries column;
        private List<DataSeriesItem> items;

        public PointsGroup(DataSeries column, List<DataSeriesItem> items) {
            this.column = column;
            this.items = items;
        }

        public DataSeries getColumn() {
            return column;
        }

        public List<DataSeriesItem> getItems() {
            return items;
        }

    }

    private Configuration configuration;

    private boolean redrawNeeded;
    private Set<String> updatedItems;
    private List<PointsGroup> unrenderedData;

    public XYSeriesManager(Configuration configuration) {
        this.configuration = configuration;
        beginUpdate();
    }

    public void beginUpdate() {
        redrawNeeded = false;
        updatedItems = new HashSet<String>();
        unrenderedData = new ArrayList<PointsGroup>();
    }

    public void addPoint(Number[] values, String pointsGroupName, AbstractPlotOptions plotOptions) {
        PointsGroup pointsGroup = findPointsGroup(pointsGroupName);
        if (pointsGroup != null) {
            // group exists, check the values
            if (pointsGroup.getItems().size() == values.length) {
                // update the series options
                updateOptions(plotOptions, pointsGroup);
                // update the series values
                for (int i = 0; i < values.length; i++) {
                    DataSeriesItem point = pointsGroup.getItems().get(i);
                    point.setY(values[i]);
                }
            } else {
                // the number of values has changed
                // create a new data series instead of adding/removing values
                pointsGroup = createPointsGroup(values, pointsGroupName, plotOptions);
            }
        } else {
            // create a new one
            pointsGroup = createPointsGroup(values, pointsGroupName, plotOptions);
        }
        unrenderedData.add(pointsGroup);
        // remember updated items
        updatedItems.add(pointsGroupName);
    }

    public void renderPoints() {
        checkStaleDate();

        if (redrawNeeded) {
            redrawSeries();
        } else {
            updateSeries();
        }
    }

    private void checkStaleDate() {
        if (!redrawNeeded) {
            for (Series point : configuration.getSeries()) {
                if (!updatedItems.contains(point.getName())) {
                    redrawNeeded = true;
                    return;
                }
            }
        }
    }

    private void redrawSeries() {
        // remove all the old items
        for (Series pointsGroup : configuration.getSeries()) {
            DataSeries pointGroupsImpl = (DataSeries)pointsGroup;
            for (DataSeriesItem point : new ArrayList<DataSeriesItem>(pointGroupsImpl.getData())) {
                pointGroupsImpl.remove(point);
            }
        }

        // add the new series
        List<Series> newSeries = new ArrayList<Series>();
        for (PointsGroup pointsGroup : unrenderedData) {
            newSeries.add(pointsGroup.getColumn());
        }
        configuration.setSeries(newSeries);

        // add values to them
        for (PointsGroup pointsGroup : unrenderedData) {
            for (DataSeriesItem point : pointsGroup.getItems()) {
                pointsGroup.getColumn().add(point);
            }
        }
    }

    /**
     * Updates items values on the client side.
     */
    private void updateSeries() {
        for (PointsGroup pointsGroup : unrenderedData) {
            for (DataSeriesItem point : pointsGroup.getItems()) {
                pointsGroup.getColumn().update(point);
            }
        }
    }

    private PointsGroup createPointsGroup(Number[] values, String pointName, AbstractPlotOptions plotOptions) {
        PointsGroup pointsGroup = new PointsGroup(new DataSeries(pointName), convertValues(values));
        updateOptions(plotOptions, pointsGroup);

        // just drop and redraw the whole chart
        redrawNeeded = true;
        return pointsGroup;
    }

    private void updateOptions(AbstractPlotOptions plotOptions, PointsGroup pointsGroup) {
        if (plotOptions != null) {
            pointsGroup.getColumn().setPlotOptions(plotOptions);
        }
    }

    private List<DataSeriesItem> convertValues(Number[] values) {
        List<DataSeriesItem> valuesList = new ArrayList<DataSeriesItem>();
        for (int i = 0; i < values.length; i++) {
            valuesList.add(new DataSeriesItem(i, values[i]));
        }
        return valuesList;
    }

    private PointsGroup findPointsGroup(String columnName) {
        for (Series seriesPoint : configuration.getSeries()) {
            if (seriesPoint.getName().equals(columnName)) {
                DataSeries seriesPointImpl = (DataSeries) seriesPoint;
                return new PointsGroup(seriesPointImpl, seriesPointImpl.getData());
            }
        }
        return null;
    }

    protected Configuration getConfiguration() {
        return configuration;
    }
}
