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
package com.hybridbpm.ui.component.chart.color;

import com.vaadin.addon.charts.model.style.SolidColor;

import java.io.Serializable;
import java.util.Map;

@SuppressWarnings("serial")
public class ChartColorer implements Serializable {

    private Map<String, String> valueColourMap;

    public ChartColorer(Map<String, String> valueColourMap) {
        this.valueColourMap = valueColourMap;
    }

    public SolidColor lookupColor(String columnName) {
        SolidColor color;
        if (valueColourMap.get(columnName) == null) {
            color = ColourUtil.getNextColour(valueColourMap.size());
            valueColourMap.put(columnName, color.toString());
        } else {
            // a new column value appeared
            // add a new color for it
            int[] colorCodes = ColourUtil.decode(valueColourMap.get(columnName));
            color = new SolidColor(colorCodes[0], colorCodes[1], colorCodes[2]);
        }
        return color;
    }

    public Map<String, String> getValueColourMap() {
        return valueColourMap;
    }
}
