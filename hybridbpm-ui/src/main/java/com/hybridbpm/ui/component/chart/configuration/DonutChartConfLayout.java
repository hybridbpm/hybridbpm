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

import com.hybridbpm.core.data.chart.DiagrammePreference;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.hybridbpm.ui.HybridbpmUI;

@SuppressWarnings("serial")
public class DonutChartConfLayout extends ThreeParamChartConfLayout {

    public DonutChartConfLayout(BeanFieldGroup<DiagrammePreference> preferences) {
        super(preferences);
    }

    @Override
    protected void setupColumns() {
        groupByChoice.setRequired(true);
        groupByChoice.setNullSelectionAllowed(false);

        firstColumnChoice.setCaption(HybridbpmUI.getText("outer-donut-field"));
        groupByChoice.setCaption(HybridbpmUI.getText("inner-donut-field"));
    }
}