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

import com.hybridbpm.core.data.chart.DiagrammePreference;
import static com.hybridbpm.ui.component.chart.util.DiagrammeUtil.getPreferenceValue;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.component.chart.configuration.PreferencesLayoutTemplate;
import com.vaadin.addon.charts.model.style.Color;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ColorPicker;
import com.vaadin.ui.Table;
import com.vaadin.ui.components.colorpicker.ColorChangeEvent;
import com.vaadin.ui.components.colorpicker.ColorChangeListener;

import java.util.Iterator;
import java.util.Map;


@SuppressWarnings("serial")
public class ChartColorLayout extends PreferencesLayoutTemplate {

    private Table colorTable = new Table();

    public ChartColorLayout(BeanFieldGroup<DiagrammePreference> preferences) {
        super(preferences);

        colorTable.setSizeFull();
        colorTable.addStyleName("compact");
        colorTable.addStyleName("small");
        colorTable.addStyleName("color-table");

        setSpacing(true);
        addComponent(colorTable);

        colorTable.setWidth("100%");
        colorTable.setEditable(false);

        colorTable.addContainerProperty(HybridbpmUI.getText("identity"), String.class, null);
        colorTable.addContainerProperty(HybridbpmUI.getText("colour"), ColorPicker.class, null);
        colorTable.setColumnWidth(HybridbpmUI.getText("colour"), 58);

        colorTable.setSortEnabled(false);
        colorTable.setPageLength(9);

        // TODO dataset descriptor seems to be redundant
        String header = HybridbpmUI.getText("identity");
        String dataSetDescriptor = getPreferenceValue(DiagrammePreference.DATASET_DESCRIPTOR, preferences);
        if (dataSetDescriptor != null) {
            header = dataSetDescriptor.substring(0, dataSetDescriptor.indexOf("(java"));
        }

        colorTable.setColumnHeaders(header, HybridbpmUI.getText("colour"));

        fillTable();

        setComponentAlignment(colorTable, Alignment.MIDDLE_CENTER);
    }

    @Override
    public void bindConfigurationValues() {
        super.bindConfigurationValues();
        fillTable();
    }

    private void fillTable() {
        colorTable.removeAllItems();

        int rowIndex = 0;
        final Map<String, String> valueColourMap = getPreferenceValue(DiagrammePreference.VALUE_COLOUR_MAP, preferences);
        Iterator<Map.Entry<String, String>> it = valueColourMap.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry<String, String> entry = it.next();

            int[] d = ColourUtil.decode(entry.getValue());

            final Color c = new SolidColor(d[0], d[1], d[2]);

            ColorPicker picker = new ColorPicker();
            picker.setColor(new com.vaadin.shared.ui.colorpicker.Color(ColourUtil.decode(c.toString())[0],
                    ColourUtil.decode(c.toString())[1], ColourUtil.decode(c.toString())[2]));
            picker.setPosition(Page.getCurrent().getBrowserWindowWidth() / 2 - 246 / 2,
                    Page.getCurrent().getBrowserWindowHeight() / 2 - 507 / 2);

            picker.addColorChangeListener(new ColorChangeListener() {

                @Override
                public void colorChanged(ColorChangeEvent event) {
                    valueColourMap.put(entry.getKey(), event.getColor().getCSS());

                    preferences.getItemDataSource().getItemProperty(DiagrammePreference.VALUE_COLOUR_MAP).setValue(valueColourMap);

//                    chartLayout.getConfigurationLayout().getLookAndFeelLayout().renderChart();
                }
            });
            colorTable.addItem(new Object[]{entry.getKey(), picker}, rowIndex);
            rowIndex++;
        }
    }

}
