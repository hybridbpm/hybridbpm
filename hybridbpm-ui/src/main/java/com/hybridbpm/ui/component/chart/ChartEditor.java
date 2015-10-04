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
package com.hybridbpm.ui.component.chart;

import com.hybridbpm.core.data.chart.DiagrammePreferenceValue;
import com.hybridbpm.ui.component.chart.util.DiagrammeChartType;
import com.hybridbpm.core.data.chart.DiagrammePreference;
import com.hybridbpm.core.data.chart.DbResponse;
import com.hybridbpm.core.data.development.Module;
import com.hybridbpm.core.util.HybridbpmCoreUtil;
import com.hybridbpm.ui.HybridbpmUI;
import static com.hybridbpm.core.data.chart.DiagrammePreference.FIRST_COLUMN_FIELD_VALUES;
import static com.hybridbpm.core.data.chart.DiagrammePreference.SECOND_COLUMN_FIELD_VALUES;
import static com.hybridbpm.core.data.chart.DiagrammePreference.VALUES_COLUMN_FIELD_VALUES;
import com.hybridbpm.ui.component.development.AbstractEditor;
import com.hybridbpm.ui.view.DevelopmentView;
import com.hybridbpm.ui.component.chart.configuration.ChartConfigureLayout;
import static com.hybridbpm.ui.component.chart.util.DiagrammeUtil.getPreferenceValue;
import com.hybridbpm.ui.component.chart.configuration.PreferencesLayoutTemplate;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.vaadin.aceeditor.AceEditor;
import org.vaadin.aceeditor.AceMode;
import org.vaadin.aceeditor.AceTheme;

/**
 *
 * @author Marat Gubaidullin
 */
public class ChartEditor extends AbstractEditor implements Property.ValueChangeListener {

    private static final Logger logger = Logger.getLogger(ChartEditor.class.getCanonicalName());

    private Module module;
    private final AceEditor queryEditor = new AceEditor();
    private IndexedContainer container;
    private final Table table = new Table("Data");
    private final Button btnExecute = new Button("Run", this);
    private final VerticalLayout codeEditorLayout = new VerticalLayout(queryEditor, btnExecute, table);

    private final OptionGroup chartTypeGroup = new OptionGroup("Chart type");
    private ChartConfigureLayout chartConfigurationLayoutLayout;
    private PreferencesLayoutTemplate bandLayout;
    private final VerticalLayout designEditorLayout = new VerticalLayout();

    private final Button btnTest = new Button("Test", this);

    private DiagrammePreference diagrammePreference = DiagrammePreference.createDefault();
    private final BeanFieldGroup<DiagrammePreference> preferences = new BeanFieldGroup<>(DiagrammePreference.class);

    public ChartEditor(Module module) {
        super();
        this.module = HybridbpmUI.getDevelopmentAPI().getModuleById(module.getId());

        if (this.module.getDesign() != null && !this.module.getDesign().isEmpty()) {
            this.diagrammePreference = HybridbpmCoreUtil.jsonToObject(this.module.getDesign(), DiagrammePreference.class);
        } else {
            diagrammePreference = DiagrammePreference.createDefault();
        }
        preferences.setItemDataSource(diagrammePreference);

        btnExecute.setIcon(FontAwesome.COG);
        btnExecute.setStyleName(ValoTheme.BUTTON_BORDERLESS);
        btnExecute.addStyleName(ValoTheme.BUTTON_SMALL);

        btnTest.setIcon(FontAwesome.PLAY);
        btnTest.setStyleName(ValoTheme.BUTTON_BORDERLESS);
        btnTest.addStyleName(ValoTheme.BUTTON_SMALL);

        horizontalSplitPanel.addComponents(codeEditorLayout, designEditorLayout);
        horizontalSplitPanel.setSplitPosition(50, Sizeable.Unit.PERCENTAGE);
        buttonBar.addComponent(btnTest, 0);

        table.setSizeFull();
        table.setStyleName(ValoTheme.TABLE_COMPACT);

        queryEditor.setCaption("Request");
        queryEditor.setMode(AceMode.sql);
        queryEditor.setTheme(AceTheme.textmate);
        queryEditor.setShowGutter(true);
        queryEditor.setSizeFull();

        codeEditorLayout.setSizeFull();
        codeEditorLayout.addStyleName("code");
        codeEditorLayout.setMargin(new MarginInfo(false, false, false, true));
        codeEditorLayout.setSpacing(true);
        codeEditorLayout.setExpandRatio(queryEditor, 1f);
        codeEditorLayout.setExpandRatio(table, 2f);

        for (DiagrammeChartType chartType : DiagrammeChartType.values()) {
            chartTypeGroup.addItem(chartType.toString());
            chartTypeGroup.setItemIcon(chartType, chartType.getIcon());
            chartTypeGroup.setItemCaption(chartType, chartType.getName());
        }
        chartTypeGroup.setStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
        chartTypeGroup.addStyleName(ValoTheme.OPTIONGROUP_SMALL);
        chartTypeGroup.setImmediate(true);
        chartTypeGroup.setNullSelectionAllowed(false);
        chartTypeGroup.addValueChangeListener(this);
        addComponent(chartTypeGroup, 1);

        designEditorLayout.setSizeFull();
        designEditorLayout.addStyleName("template");
        designEditorLayout.setMargin(new MarginInfo(false, true, false, true));
        designEditorLayout.setSpacing(true);

        preferences.bind(queryEditor, DiagrammePreference.QUERY);
        preferences.bind(chartTypeGroup, DiagrammePreference.CHART_TYPE);
        refreshChartConfigurationLayout();
    }

    @Override
    public Module getModule() {
        return module;
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton().equals(btnSave)) {
            save();
        } else if (event.getButton().equals(btnTest)) {
            run();
        } else if (event.getButton().equals(btnExecute)) {
            executeRequest();
        }
    }

    private void save() {
        try {
            preferences.commit();
            module.setCode(preferences.getItemDataSource().getBean().getQuery());
            module.setDesign(HybridbpmCoreUtil.objectToJson(preferences.getItemDataSource().getBean()));

            module = HybridbpmUI.getDevelopmentAPI().saveModule(module);
        } catch (FieldGroup.CommitException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private DevelopmentView getDevelopmentView() {
        Component result = this;
        while (!(result instanceof DevelopmentView)) {
            result = result.getParent();
            if (result == null) {
                return null;
            }
        }
        return (DevelopmentView) result;
    }

    private void run() {
        try {
            save();

            DiagrammePreferenceValue secondFieldValue = getPreferenceValue(DiagrammePreference.SECOND_COLUMN_FIELD, preferences);
            boolean groupingOn = secondFieldValue != null;

            DiagrammeChartType chartType = DiagrammeChartType.valueOf(chartTypeGroup.getValue().toString());
            prepareData();
            AbstractChart chart = chartType.createChart(container, groupingOn);
            chart.bind(preferences);
            chart.render();
            getDevelopmentView().openTab(chart.drawChart(), module);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            Notification.show("Error", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
        }
    }

    private void executeRequest() {
        try {
            prepareData();
            table.setContainerDataSource(container);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            Notification.show("Error", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
        }
    }

    private void prepareData() {
        try {
            container = new IndexedContainer();
            DbResponse response = HybridbpmUI.getCrudAPI().queryODocuments(queryEditor.getValue());
            Map<Integer, Map<String, Object>> data = response.getData();
            for (String name : response.getHeader().keySet()) {
                container.addContainerProperty(name, response.getHeader().get(name), null);
            }
            for (Integer rowId : data.keySet()) {
                Item item = container.addItem(rowId);
                Map<String, Object> map = data.get(rowId);
                for (String name : response.getHeader().keySet()) {
                    item.getItemProperty(name).setValue(map.get(name));
                }
            }
            refreshChartConfigurationLayout();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void valueChange(Property.ValueChangeEvent event) {
        if (chartConfigurationLayoutLayout != null && Objects.equals(designEditorLayout, chartConfigurationLayoutLayout.getParent())) {
            designEditorLayout.removeComponent(chartConfigurationLayoutLayout);
        }
        DiagrammeChartType chartType = DiagrammeChartType.valueOf(event.getProperty().getValue().toString());
        chartConfigurationLayoutLayout = chartType.createConfigLayout(preferences);
        designEditorLayout.addComponent(chartConfigurationLayoutLayout);
        designEditorLayout.setExpandRatio(chartConfigurationLayoutLayout, 1f);
        refreshChartConfigurationLayout();

        if (bandLayout != null && Objects.equals(designEditorLayout, bandLayout.getParent())) {
            designEditorLayout.removeComponent(bandLayout);
        }

        if (Objects.equals(chartType, DiagrammeChartType.GAUGE)) {
            bandLayout = chartType.getColorLayout(preferences);
            bandLayout.bindConfigurationValues();
            designEditorLayout.addComponent(bandLayout);
            designEditorLayout.setExpandRatio(bandLayout, 1f);
        }
    }

    private void refreshChartConfigurationLayout() {
        if (container != null) {
            List<DiagrammePreferenceValue> firstColumnFieldValues = new ArrayList<>();
            List<DiagrammePreferenceValue> secondColumnFieldValues = new ArrayList<>();
            List<DiagrammePreferenceValue> valuesColumnFieldValues = new ArrayList<>();

            for (Object object : container.getContainerPropertyIds()) {
                DiagrammePreferenceValue column = new DiagrammePreferenceValue(object.toString(),
                        object.toString() + " (" + container.getType(object).getCanonicalName() + ")");
                firstColumnFieldValues.add(column);
                secondColumnFieldValues.add(column);
                if (Number.class.isAssignableFrom(container.getType(object)) || int.class.isAssignableFrom(container.getType(object)) || long.class.isAssignableFrom(container.getType(object))) {
                    valuesColumnFieldValues.add(column);
                }
            }

            preferences.getItemDataSource().getItemProperty(FIRST_COLUMN_FIELD_VALUES).setValue(firstColumnFieldValues);
            preferences.getItemDataSource().getItemProperty(SECOND_COLUMN_FIELD_VALUES).setValue(secondColumnFieldValues);
            preferences.getItemDataSource().getItemProperty(VALUES_COLUMN_FIELD_VALUES).setValue(valuesColumnFieldValues);
        }
        if (chartConfigurationLayoutLayout != null) {
            chartConfigurationLayoutLayout.unbindConfigurationValues();
            chartConfigurationLayoutLayout.updateComboboxes();
            chartConfigurationLayoutLayout.bindConfigurationValues();
        }
    }

}
