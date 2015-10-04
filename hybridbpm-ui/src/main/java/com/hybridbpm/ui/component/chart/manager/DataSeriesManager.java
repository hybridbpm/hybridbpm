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

import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public abstract class DataSeriesManager {

    private final DataSeries dataSeries;
    private List<DataSeriesItem> unrenderedData;
    private boolean redrawNeeded;

    public DataSeriesManager() {
        this.dataSeries = new DataSeries();
    }

    abstract protected void checkStaleData();

    public void beginUpdate() {
        unrenderedData = new ArrayList<DataSeriesItem>();
        redrawNeeded = false;
    }

    public void renderItems() {
        checkStaleData();

        if (redrawNeeded) {
            redrawSeries();
        } else {
            updateSeries();
        }
    }

    public DataSeries getSeries() {
        return dataSeries;
    }

    protected boolean isRedrawNeeded() {
        return redrawNeeded;
    }

    protected void setRedrawNeeded() {
        this.redrawNeeded = true;
    }

    protected List<DataSeriesItem> getUnrenderedData() {
        return unrenderedData;
    }

    private void redrawSeries() {
        // remove all the old items
        List<DataSeriesItem> items = new ArrayList<DataSeriesItem>(dataSeries.getData());
        for (DataSeriesItem item : items) {
            dataSeries.remove(item);
        }

        // add the new ones
        for (DataSeriesItem item : unrenderedData) {
            dataSeries.add(item);
        }
    }

    private void updateSeries() {
        for (DataSeriesItem item : unrenderedData) {
            dataSeries.update(item);
        }
    }
}
