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
import com.vaadin.addon.charts.model.style.Color;
import com.vaadin.addon.charts.model.style.SolidColor;

import java.util.HashSet;
import java.util.Set;

import static com.hybridbpm.ui.component.chart.ThreeParamChart.ColumnCoupleKey;

@SuppressWarnings("serial")
public class GroupedDataSeriesManager extends DataSeriesManager {

    private static class GroupedDataSeriesItem extends DataSeriesItem {

        public GroupedDataSeriesItem(String name, Number y, Color color, String groupName) {
            super(name, y, color);
            this.groupName = groupName;
        }

        private String groupName;

        public String getGroupName() {
            return groupName;
        }
    }

    Set<ColumnCoupleKey<String, String>> updatedItems;

    public GroupedDataSeriesManager() {
        super();
        beginUpdate();
    }

    public void beginUpdate() {
        super.beginUpdate();
        updatedItems = new HashSet<ColumnCoupleKey<String, String>>();
    }

    public void addItem(Number value, String itemName, SolidColor color, String groupName) {

        GroupedDataSeriesItem outerItem = findItem(itemName, groupName);
        if (outerItem != null) {
            // update the existing item
            outerItem.setY(value);
            outerItem.setColor(color);
            // remember updated items
            updatedItems.add(new ColumnCoupleKey<String, String>(outerItem.getGroupName(), outerItem.getName()));
        } else {
            // add a new item to the ring
            outerItem = new GroupedDataSeriesItem(itemName, value, color, groupName);
            // adding new item won't work without this
            outerItem.setX(0);

            // redraw the complete chart if anything was added, highcharts lib doesn't
            // handle render changes correctly
            setRedrawNeeded();
        }
        getUnrenderedData().add(outerItem);
    }

    private GroupedDataSeriesItem findItem(String itemName, String groupName) {
        for (DataSeriesItem item : getSeries().getData()) {
            GroupedDataSeriesItem outerItem = (GroupedDataSeriesItem) item;
            if (outerItem.getName().equals(itemName) &&
                    outerItem.getGroupName().equals(groupName)) {
                return outerItem;
            }
        }
        return null;
    }


    protected void checkStaleData() {
        if (!isRedrawNeeded()) {
            for (DataSeriesItem item : getSeries().getData()) {
                GroupedDataSeriesItem outerItem = (GroupedDataSeriesItem) item;
                if (!updatedItems.contains(new ColumnCoupleKey<String, String>(outerItem.getGroupName(), outerItem.getName()))) {

                    // redraw the complete chart if anything has to be removed, highcharts doesn't
                    // render changes correctly otherwise
                    setRedrawNeeded();
                    return;
                }
            }
        }
    }
}
