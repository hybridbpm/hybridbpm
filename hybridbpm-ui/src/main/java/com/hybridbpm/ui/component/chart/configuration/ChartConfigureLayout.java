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
import static com.hybridbpm.core.data.chart.DiagrammePreference.FIRST_COLUMN_FIELD;
import static com.hybridbpm.core.data.chart.DiagrammePreference.FIRST_COLUMN_FIELD_VALUES;
import static com.hybridbpm.core.data.chart.DiagrammePreference.FIRST_COLUMN_SORT_ORDER;
import static com.hybridbpm.core.data.chart.DiagrammePreference.REFRESH;
import static com.hybridbpm.core.data.chart.DiagrammePreference.VALUES_COLUMN_FIELD;
import static com.hybridbpm.core.data.chart.DiagrammePreference.VALUES_COLUMN_FIELD_VALUES;
import static com.hybridbpm.core.data.chart.DiagrammePreference.VALUES_COLUMN_SORT_ORDER;
import com.hybridbpm.core.data.chart.DiagrammePreferenceValue;
import com.hybridbpm.core.data.chart.SortBy;
import static com.hybridbpm.ui.component.chart.util.DiagrammeUtil.getPreferenceValue;
import com.vaadin.data.Property;
import com.hybridbpm.ui.util.Translate;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import com.hybridbpm.ui.HybridbpmUI;

import java.util.List;

@SuppressWarnings("serial")
public abstract class ChartConfigureLayout extends PreferencesLayoutTemplate {

    private static final long serialVersionUID = 1L;

    protected final TextField refresh = new TextField(Translate.getMessage("refresh"));
    protected final VerticalLayout verticalLayout = new VerticalLayout(refresh);
    protected final HorizontalLayout horizontalLayout = new HorizontalLayout(verticalLayout);
    protected final HorizontalLayout firstColumnAndSortLayout = new HorizontalLayout();
    protected final HorizontalLayout valuesColumnAndSortLayout = new HorizontalLayout();

    protected final ComboBox firstColumnChoice = new ComboBox(Translate.getMessage("x-axis"), new BeanItemContainer<>(DiagrammePreferenceValue.class));
    protected final ComboBox valuesColumnChoice = new ComboBox(Translate.getMessage("values"), new BeanItemContainer<>(DiagrammePreferenceValue.class));
    protected final ComboBox firstColumnSortOrder = new ComboBox(Translate.getMessage("sort by"));
    protected final ComboBox valuesColumnSortOrder = new ComboBox(Translate.getMessage("sort by"));

    private Property.ValueChangeListener firstChoiceHandler;

    public ChartConfigureLayout(BeanFieldGroup<DiagrammePreference> preferences) {
        super(preferences);

        setCaption("Configuration");
        setMargin(false);
        setSpacing(true);
        refresh.setConverter(new StringToIntegerConverter());
        refresh.setStyleName(ValoTheme.TEXTFIELD_SMALL);
        horizontalLayout.setExpandRatio(verticalLayout, 1f);

        verticalLayout.setSpacing(true);

        firstColumnChoice.setWidth(100, Sizeable.Unit.PERCENTAGE);
        firstColumnChoice.setRequired(true);
        firstColumnChoice.setStyleName(ValoTheme.COMBOBOX_SMALL);

        firstColumnChoice.setFilteringMode(FilteringMode.CONTAINS);
        firstColumnChoice.setNullSelectionAllowed(false);
        firstColumnChoice.setNewItemsAllowed(false);
        firstColumnChoice.setImmediate(true);
        firstColumnChoice.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        firstColumnChoice.setItemCaptionPropertyId("name");

        firstColumnSortOrder.setWidth(100, Unit.PERCENTAGE);
        firstColumnSortOrder.setRequired(false);
        firstColumnSortOrder.setFilteringMode(FilteringMode.CONTAINS);
        firstColumnSortOrder.setNewItemsAllowed(false);
        firstColumnSortOrder.setImmediate(true);
        firstColumnSortOrder.setNullSelectionAllowed(true);
        firstColumnSortOrder.setStyleName(ValoTheme.COMBOBOX_SMALL);

        firstColumnAndSortLayout.addComponent(firstColumnChoice);
        firstColumnAndSortLayout.addComponent(firstColumnSortOrder);
        firstColumnAndSortLayout.setExpandRatio(firstColumnChoice, 0.8f);
        firstColumnAndSortLayout.setExpandRatio(firstColumnSortOrder, 0.2f);
        firstColumnAndSortLayout.setSpacing(true);
        firstColumnAndSortLayout.setSizeFull();
        firstColumnAndSortLayout.setStyleName(ValoTheme.COMBOBOX_SMALL);

        verticalLayout.addComponent(firstColumnAndSortLayout);

        valuesColumnChoice.setWidth(100, Sizeable.Unit.PERCENTAGE);
        valuesColumnChoice.setRequired(true);
        valuesColumnChoice.setFilteringMode(FilteringMode.CONTAINS);
        valuesColumnChoice.setNullSelectionAllowed(false);
        valuesColumnChoice.setNewItemsAllowed(false);
        valuesColumnChoice.setImmediate(true);
        valuesColumnChoice.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        valuesColumnChoice.setItemCaptionPropertyId("name");
        valuesColumnChoice.setStyleName(ValoTheme.COMBOBOX_SMALL);

        valuesColumnSortOrder.setWidth(100, Unit.PERCENTAGE);
        valuesColumnSortOrder.setRequired(false);
        valuesColumnSortOrder.setFilteringMode(FilteringMode.CONTAINS);
        valuesColumnSortOrder.setNewItemsAllowed(false);
        valuesColumnSortOrder.setImmediate(true);
        valuesColumnSortOrder.setNullSelectionAllowed(true);
        valuesColumnSortOrder.setStyleName(ValoTheme.COMBOBOX_SMALL);

        valuesColumnAndSortLayout.addComponent(valuesColumnChoice);
        valuesColumnAndSortLayout.addComponent(valuesColumnSortOrder);

        valuesColumnAndSortLayout.setExpandRatio(valuesColumnChoice, 0.8f);
        valuesColumnAndSortLayout.setExpandRatio(valuesColumnSortOrder, 0.2f);
        valuesColumnAndSortLayout.setSpacing(true);
        valuesColumnAndSortLayout.setSizeFull();

        verticalLayout.addComponent(valuesColumnAndSortLayout);

        addComponent(horizontalLayout);
        horizontalLayout.setSpacing(true);
        horizontalLayout.setSizeFull();

        setComboBoxChoices();
    }

    @Override
    public void bindConfigurationValues() {
        bindField(refresh, REFRESH, preferences);
        bindField(firstColumnSortOrder, FIRST_COLUMN_SORT_ORDER, preferences);
        bindField(valuesColumnSortOrder, VALUES_COLUMN_SORT_ORDER, preferences);

        // refresh comboboxes values
        fillContainers();

        // set selected value
        bindField(firstColumnChoice, FIRST_COLUMN_FIELD, preferences);
        bindField(valuesColumnChoice, VALUES_COLUMN_FIELD, preferences);

        // remove selected chart colors on columns change
//        firstChoiceHandler = new ColorValueChangeListener(FIRST_COLUMN_FIELD, this.chartLayout, preferences);
//        firstColumnChoice.addValueChangeListener(firstChoiceHandler);

        setupChartConfig();
    }

    protected void updateColumnsContainer(BeanItemContainer<DiagrammePreferenceValue> container, List<DiagrammePreferenceValue> values) {
        container.removeAllItems();
        container.addAll(values);
    }

    @Override
    public void unbindConfigurationValues() {
        unbindField(REFRESH, preferences);
        unbindField(FIRST_COLUMN_SORT_ORDER, preferences);
        unbindField(VALUES_COLUMN_SORT_ORDER, preferences);
        unbindField(FIRST_COLUMN_FIELD, preferences);
        unbindField(VALUES_COLUMN_FIELD, preferences);

        firstColumnChoice.removeValueChangeListener(firstChoiceHandler);
    }

    protected abstract void setupChartConfig();

    protected void setComboBoxChoices() {
        valuesColumnSortOrder.addItems((Object[]) SortBy.values());
        firstColumnSortOrder.addItems((Object[]) SortBy.values());
    }

    @Override
    public void commit() {
        refresh.commit();
        firstColumnChoice.commit();
        valuesColumnChoice.commit();
        firstColumnSortOrder.commit();
        valuesColumnSortOrder.commit();
    }

    @Override
    public void updateComboboxes() {
        this.firstColumnChoice.getContainerDataSource().removeAllItems();
        this.valuesColumnChoice.getContainerDataSource().removeAllItems();

        fillContainers();
    }

    private void fillContainers() {
        List<DiagrammePreferenceValue> firstColumnValues = getPreferenceValue(FIRST_COLUMN_FIELD_VALUES, preferences);
        List<DiagrammePreferenceValue> valuesColumnValues = getPreferenceValue(VALUES_COLUMN_FIELD_VALUES, preferences);

        BeanItemContainer<DiagrammePreferenceValue> firstColumnValuesContainer = (BeanItemContainer<DiagrammePreferenceValue>) firstColumnChoice.getContainerDataSource();
        BeanItemContainer<DiagrammePreferenceValue> valuesColumnValuesContainer = (BeanItemContainer<DiagrammePreferenceValue>) valuesColumnChoice.getContainerDataSource();

        // initial values for comboboxes
        updateColumnsContainer(firstColumnValuesContainer, firstColumnValues);
        updateColumnsContainer(valuesColumnValuesContainer, valuesColumnValues);
    }

}
