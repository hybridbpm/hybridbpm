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

import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class GaugeSeriesManager extends XYSeriesManager {

    private static class Point extends PointsGroup {

        public Point(DataSeries column, final DataSeriesItem item) {
            super(column,  new ArrayList<DataSeriesItem>() {{
                add(item);
            }});
        }

        public DataSeriesItem getItem() {
            return getItems().iterator().next();
        }
    }

    public GaugeSeriesManager(Configuration configuration) {
        super(configuration);
    }

    public void addPoint(Number value, String pointName) {
        addPoint(new Number[] {value}, pointName, null);
    }
}
