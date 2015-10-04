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
package com.hybridbpm.ui.component.chart.configuration;

import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.core.data.chart.DiagrammePreference;
import static com.hybridbpm.core.data.chart.DiagrammePreference.MAX_VALUE;
import static com.hybridbpm.core.data.chart.DiagrammePreference.MIN_VALUE;
import com.hybridbpm.core.data.chart.DiagrammePreferenceValue;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.converter.StringToDoubleConverter;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public class GaugeChartConfLayout extends ChartConfigureLayout {

    private class MinMaxValueChangeListener implements ValueChangeListener {
        private static final long serialVersionUID = 1L;

        private static final String DEFAULT_VALUE = "0";

        private TextField valueField;

        /**
         * Creates the change listener
         * @param valueField The field on which the listener listens for events
         */
        public MinMaxValueChangeListener(TextField valueField) {
            this.valueField = valueField;
        }

        @Override
        public void valueChange(ValueChangeEvent event) {
            Object value = event.getProperty().getValue();
            if(value == null) {
                valueField.setValue(DEFAULT_VALUE);
            }
        }

    }

    protected final HorizontalLayout gaugeAdditionsLayout = new HorizontalLayout();

    protected final TextField minValue = new TextField(HybridbpmUI.getText("minimum-value"));
    protected final TextField maxValue = new TextField(HybridbpmUI.getText("maximum-value"));

    private ValueChangeListener valuesChoiceHandler;

    public GaugeChartConfLayout(BeanFieldGroup<DiagrammePreference> preferences) {
        super(preferences);

        gaugeAdditionsLayout.setWidth(80, Unit.PERCENTAGE);
        gaugeAdditionsLayout.setSpacing(true);


        gaugeAdditionsLayout.addComponent(minValue);
        minValue.setNullRepresentation("0");
        minValue.setSizeFull();
        minValue.setConverter(new StringToDoubleConverter());

        gaugeAdditionsLayout.addComponent(maxValue);
        maxValue.setNullRepresentation("0");
        maxValue.setConverter(new StringToDoubleConverter());
        maxValue.setSizeFull();

        gaugeAdditionsLayout.setVisible(false);

        verticalLayout.addComponent(gaugeAdditionsLayout);
    }

    @Override
    protected void setupChartConfig() {
        valuesColumnSortOrder.setEnabled(false);
        firstColumnSortOrder.setEnabled(false);

        firstColumnChoice.setCaption(HybridbpmUI.getText("labels"));
        gaugeAdditionsLayout.setVisible(true);

        maxValue.setRequired(true);
        minValue.setRequired(true);
    }

    @Override
    public void bindConfigurationValues() {
        super.bindConfigurationValues();
        bindField(minValue, MIN_VALUE, preferences);
        bindField(maxValue, MAX_VALUE, preferences);

        valuesChoiceHandler = new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                checkMaxValue((DiagrammePreferenceValue) event.getProperty().getValue());
            }
        };
        valuesColumnChoice.addValueChangeListener(valuesChoiceHandler);
        minValue.addValueChangeListener(new MinMaxValueChangeListener(minValue));
        maxValue.addValueChangeListener(new MinMaxValueChangeListener(maxValue));
    }

    @Override
    public void unbindConfigurationValues() {
        super.unbindConfigurationValues();
        unbindField(MIN_VALUE, preferences);
        unbindField(MAX_VALUE, preferences);

        valuesColumnChoice.removeValueChangeListener(valuesChoiceHandler);
    }

    @Override
    public void commit() {
        super.commit();
        maxValue.commit();
        minValue.commit();
    }

    private void checkMaxValue(DiagrammePreferenceValue valueColumnName) {
//        if (valueColumnName != null &&
//                minValue.getValue().equals(maxValue.getValue())) {
//            Container container = chartLayout.getContainerData().getContainer();
//            Double max = (Double)maxValue.getConvertedValue();
//            for (Object itemId : container.getItemIds()) {
//                Property valueProp = container.getContainerProperty(itemId,
//                        valueColumnName.getId());
//                if (valueProp.getValue() != null) {
//                    Number value = (Number)valueProp.getValue();
//                    max = max.compareTo(value.doubleValue()) < 0 ? value.doubleValue() : max;
//                }
//            }
//            maxValue.setConvertedValue(max);
//        }
    }
}
