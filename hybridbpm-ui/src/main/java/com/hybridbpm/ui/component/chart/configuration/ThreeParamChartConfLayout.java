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

import com.hybridbpm.ui.component.chart.util.DiagrammeUtil;
import com.hybridbpm.core.data.chart.DiagrammePreference;
import static com.hybridbpm.core.data.chart.DiagrammePreference.SECOND_COLUMN_FIELD;
import static com.hybridbpm.core.data.chart.DiagrammePreference.SECOND_COLUMN_FIELD_VALUES;
import static com.hybridbpm.core.data.chart.DiagrammePreference.SECOND_COLUMN_SORT_ORDER;
import com.hybridbpm.core.data.chart.DiagrammePreferenceValue;
import com.hybridbpm.core.data.chart.SortBy;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.hybridbpm.ui.util.Translate;

import java.util.List;


@SuppressWarnings("serial")
public class ThreeParamChartConfLayout extends ChartConfigureLayout {
    private static final long serialVersionUID = 1L;

    protected final HorizontalLayout groupByAndSortLayout = new HorizontalLayout();
    protected final ComboBox groupByChoice = new ComboBox(Translate.getMessage("group by"),
            new BeanItemContainer<>(DiagrammePreferenceValue.class));
    protected final ComboBox sortByChoiceGroup = new ComboBox(Translate.getMessage("sort by"));

    private Property.ValueChangeListener secondChoiceHandler;

    public ThreeParamChartConfLayout(BeanFieldGroup<DiagrammePreference> preferences) {
        super(preferences);

        groupByChoice.setWidth(100, Sizeable.Unit.PERCENTAGE);
        groupByChoice.setFilteringMode(FilteringMode.CONTAINS);
        groupByChoice.setNewItemsAllowed(false);
        groupByChoice.setImmediate(true);
        groupByChoice.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        groupByChoice.setItemCaptionPropertyId("name");
        groupByChoice.setNullSelectionAllowed(true);
        groupByChoice.setRequired(false);

        sortByChoiceGroup.setWidth(100, Unit.PERCENTAGE);
        sortByChoiceGroup.setRequired(false);
        sortByChoiceGroup.setFilteringMode(FilteringMode.CONTAINS);
        sortByChoiceGroup.setNewItemsAllowed(false);
        sortByChoiceGroup.setImmediate(true);
        sortByChoiceGroup.setNullSelectionAllowed(true);

        groupByAndSortLayout.addComponent(groupByChoice);
        groupByAndSortLayout.addComponent(sortByChoiceGroup);

        groupByAndSortLayout.setExpandRatio(groupByChoice, 0.8f);
        groupByAndSortLayout.setExpandRatio(sortByChoiceGroup, 0.2f);
        groupByAndSortLayout.setSpacing(true);
        groupByAndSortLayout.setSizeFull();

        verticalLayout.addComponent(groupByAndSortLayout);

        sortByChoiceGroup.addItems((Object[])SortBy.values());
    }

    @Override
    protected void setupChartConfig() {
        setupColumns();

        groupByChoice.setEnabled(true);
        sortByChoiceGroup.setEnabled(true);
        valuesColumnSortOrder.setEnabled(false);
        firstColumnSortOrder.setEnabled(true);
    }

    protected void setupColumns() {
        firstColumnChoice.setCaption(Translate.getMessage("x-axis-field"));
        groupByChoice.setCaption(Translate.getMessage("group-by-field"));
    }

    @Override
    public void bindConfigurationValues() {
        super.bindConfigurationValues();

        bindField(sortByChoiceGroup, "secondColumnSortOrder", preferences);

        // fill second column combobox selection list
        fillContainers();

        // set selected value
        bindField(groupByChoice, "secondColumnField", preferences);

        // remove selected chart colors on columns change
//        secondChoiceHandler = new ColorValueChangeListener(SECOND_COLUMN_FIELD,
//                this.chartLayout, preferences);
//        groupByChoice.addValueChangeListener(secondChoiceHandler);
    }


    @Override
    public void unbindConfigurationValues() {
        super.unbindConfigurationValues();
        unbindField(SECOND_COLUMN_SORT_ORDER, preferences);
        unbindField(SECOND_COLUMN_FIELD, preferences);

        groupByChoice.removeValueChangeListener(secondChoiceHandler);
    }

    @Override
    public void commit() {
        super.commit();
        groupByChoice.commit();
        sortByChoiceGroup.commit();
    }

    @Override
    public void updateComboboxes() {
        super.updateComboboxes();
        this.groupByChoice.getContainerDataSource().removeAllItems();

        fillContainers();
    }

    private void fillContainers() {
        List<DiagrammePreferenceValue> secondColumnValues = DiagrammeUtil.getPreferenceValue(
                SECOND_COLUMN_FIELD_VALUES, preferences);

        BeanItemContainer<DiagrammePreferenceValue> secondColumnValuesContainer =
                (BeanItemContainer<DiagrammePreferenceValue>) groupByChoice.getContainerDataSource();

        // values for comboboxes
        updateColumnsContainer(secondColumnValuesContainer, secondColumnValues);
    }
}