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
import com.hybridbpm.core.data.chart.DiagrammePreferenceValue;
import com.hybridbpm.core.data.chart.PlotBandPreference;
import static com.hybridbpm.ui.component.chart.util.DiagrammeUtil.getPreferenceValue;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.component.chart.configuration.PreferencesLayoutTemplate;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.components.colorpicker.ColorChangeEvent;
import com.vaadin.ui.components.colorpicker.ColorChangeListener;
import com.vaadin.ui.themes.ValoTheme;

import java.util.List;

@SuppressWarnings("serial")
public class GaugeBandLayout extends PreferencesLayoutTemplate {

    private final Table colorTable = new Table();

    private final BeanItemContainer<PlotBandPreference> tableContainer = new BeanItemContainer<PlotBandPreference>(PlotBandPreference.class);

    private final VerticalLayout bandAdditionButtonFrame = new VerticalLayout();

    private final Button addBandButton = new Button(HybridbpmUI.getText("add band"));

    public GaugeBandLayout(final BeanFieldGroup<DiagrammePreference> preferences) {
        super(preferences);

        setCaption("Gauge Band Colors");
        setSizeFull();
        setSpacing(true);
        setMargin(false);

        addBandButton.setIcon(FontAwesome.PLUS);
        addBandButton.setStyleName(ValoTheme.BUTTON_BORDERLESS);
        addBandButton.addStyleName(ValoTheme.BUTTON_SMALL);

        bandAdditionButtonFrame.setSpacing(true);
        bandAdditionButtonFrame.addComponent(addBandButton);
        bandAdditionButtonFrame.setComponentAlignment(addBandButton, Alignment.MIDDLE_RIGHT);

        addComponent(bandAdditionButtonFrame);

        addBandButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                addBand();
            }
        });

        colorTable.setHeight("100%");
        colorTable.setWidth("100%");
        colorTable.addStyleName(ValoTheme.TABLE_COMPACT);
        colorTable.addStyleName(ValoTheme.TABLE_SMALL);
        colorTable.addStyleName("color-table");

        colorTable.addContainerProperty(HybridbpmUI.getText("colour"), ColorPicker.class, null);
        colorTable.addContainerProperty(HybridbpmUI.getText("start"), TextField.class, null);
        colorTable.addContainerProperty(HybridbpmUI.getText("end"), TextField.class, null);
        colorTable.addContainerProperty("remove", Button.class, null);

        // COLOR COLUMN
        colorTable.addGeneratedColumn(HybridbpmUI.getText("colour"), new Table.ColumnGenerator() {
            @Override
            public Object generateCell(Table source, Object itemId, Object columnId) {
                final Property<String> colorProp = tableContainer.getItem(itemId).getItemProperty("color");

                int[] col = ColourUtil.decode(colorProp.getValue());
                ColorPicker picker = new ColorPicker();
                picker.addStyleName("diagramme");
                picker.setPosition(Page.getCurrent().getBrowserWindowWidth() / 2 - 246 / 2,
                        Page.getCurrent().getBrowserWindowHeight() / 2 - 507 / 2);
                picker.setColor(new com.vaadin.shared.ui.colorpicker.Color(col[0], col[1], col[2]));

                picker.addColorChangeListener(new ColorChangeListener() {
                    @Override
                    public void colorChanged(ColorChangeEvent event) {
                        colorProp.setValue(event.getColor().getCSS());
                        updateDiagramme();
                    }
                });
                picker.setWidth("25px");

                return picker;
            }
        });

        // BAND START COLUMN
        colorTable.addGeneratedColumn(HybridbpmUI.getText("start"), new Table.ColumnGenerator() {
            @Override
            public Object generateCell(Table source, Object itemId, Object columnId) {
                final Property<Double> startProp = tableContainer.getContainerProperty(
                        itemId, "startValue");

                final ObjectProperty<Double> startValue
                        = new ObjectProperty<Double>(0.0);
                TextField startField = new TextField(startValue);
                //startField.setWidth("60px");
                startField.setSizeFull();
                startField.setNullRepresentation("0");
                startField.addStyleName("tfwb");
                startField.setConvertedValue(startProp.getValue());
                startField.setImmediate(true);

                startValue.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent event) {
                        startProp.setValue((Double) event.getProperty().getValue());
                        updateDiagramme();
                    }
                });

                return startField;
            }
        });

        // BAND END COLUMN
        colorTable.addGeneratedColumn(HybridbpmUI.getText("end"), new Table.ColumnGenerator() {
            @Override
            public Object generateCell(Table source, Object itemId, Object columnId) {
                final Property<Double> endProp = tableContainer.getContainerProperty(
                        itemId, "endValue");

                final ObjectProperty<Double> endValue
                        = new ObjectProperty<Double>(0.0);
                TextField endField = new TextField(endValue);
                //endField.setWidth("60px");
                endField.setSizeFull();
                endField.setNullRepresentation("0");
                endField.addStyleName("tfwb");
                endField.setConvertedValue(endProp.getValue());
                endField.setImmediate(true);

                endValue.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                        endProp.setValue((Double) valueChangeEvent.getProperty().getValue());
                        updateDiagramme();
                    }
                });

                return endField;
            }
        });

        // DELETE BAND COLUMN
        colorTable.addGeneratedColumn("remove", new Table.ColumnGenerator() {

            @Override
            public Object generateCell(Table source, final Object itemId, Object columnId) {

                Button delete = new Button(FontAwesome.TIMES);
                delete.addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        removeBand(itemId);
                    }
                });
                delete.setWidth("43px");

                return delete;
            }
        });

        colorTable.setColumnWidth(HybridbpmUI.getText("colour"), 33);
        colorTable.setColumnWidth("remove", 48);
        colorTable.setColumnExpandRatio(HybridbpmUI.getText("start"), 0.5f);
        colorTable.setColumnExpandRatio(HybridbpmUI.getText("end"), 0.5f);

        colorTable.setSortEnabled(false);
        colorTable.setPageLength(0);
        colorTable.setEditable(true);
        colorTable.setImmediate(true);

        addComponent(colorTable);
        setExpandRatio(colorTable, 1f);
        setComponentAlignment(colorTable, Alignment.TOP_LEFT);
    }

    private void updateDiagramme() {
        List<PlotBandPreference> plotBandList = tableContainer.getItemIds();
        preferences.getItemDataSource().getItemProperty(DiagrammePreference.PLOT_BAND_LIST).setValue(plotBandList);
    }

    private void addBand() {
        PlotBandPreference newBand = new PlotBandPreference();
        newBand.setColor(com.vaadin.shared.ui.colorpicker.Color.WHITE.getCSS());
        newBand.setStartValue(0.0);
        newBand.setEndValue(0.0);

        tableContainer.addBean(newBand);
    }

    private void removeBand(Object itemId) {
        tableContainer.removeItem(itemId);
    }

    @Override
    public void bindConfigurationValues() {
        super.bindConfigurationValues();
        // Set plot bands container
        List<PlotBandPreference> plotBandList = getPreferenceValue(DiagrammePreference.PLOT_BAND_LIST, preferences);

        populateContainer(plotBandList);
    }

    @Override
    public void unbindConfigurationValues() {
        super.unbindConfigurationValues();

    }

    private void populateContainer(List<PlotBandPreference> plotBandList) {
        tableContainer.removeAllItems();

        tableContainer.addAll(plotBandList);
        colorTable.setContainerDataSource(tableContainer);
        colorTable.setVisibleColumns(HybridbpmUI.getText("colour"), HybridbpmUI.getText("start"), HybridbpmUI.getText("end"), "remove");
    }
}
