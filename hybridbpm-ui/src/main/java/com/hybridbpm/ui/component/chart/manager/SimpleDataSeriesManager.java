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

import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.style.SolidColor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("serial")
public class SimpleDataSeriesManager extends DataSeriesManager {

    Set<String> updatedItems;

    public SimpleDataSeriesManager() {
        beginUpdate();
    }


    public void beginUpdate() {
        super.beginUpdate();
        updatedItems = new HashSet<String>();
    }
    public void addItem(Number value, String itemName, SolidColor color) {
        DataSeriesItem item = getSeries().get(itemName);
        if (item != null) {
            // update the existing item
            item.setY(value);
            item.setColor(color);
            // remember updated items
            updatedItems.add(item.getName());
        } else {
            // add a new item to the circle
            item = new DataSeriesItem(itemName, value, color);
            // adding new item won't work without this
            item.setX(0);

            // redraw the complete chart if anything was added, highcharts lib doesn't
            // handle render changes correctly
            setRedrawNeeded();
        }

        getUnrenderedData().add(item);
    }

    protected void checkStaleData() {
        if (!isRedrawNeeded()) {
            List<DataSeriesItem> items = new ArrayList<DataSeriesItem>(getSeries().getData());
            for (DataSeriesItem item : items) {
                if (!updatedItems.contains(item.getName())) {

                    // redraw the complete chart if anything has to be removed, highcharts doesn't
                    // render changes correctly otherwise
                    setRedrawNeeded();
                    return;
                }
            }
        }
    }
}
