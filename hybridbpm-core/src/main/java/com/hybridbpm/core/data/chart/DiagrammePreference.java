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
package com.hybridbpm.core.data.chart;

import java.io.Serializable;
import java.util.*;

@SuppressWarnings("serial")
public class DiagrammePreference implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String QUERY = "query";
    public static final String CHART_TYPE_SPECIFIC = "chartTypeSpecific";
    public static final String CHART_TYPE = "chartType";
    public static final String REFRESH = "refresh";
    public static final String FIRST_COLUMN_SORT_ORDER = "firstColumnSortOrder";
    public static final String SECOND_COLUMN_SORT_ORDER = "secondColumnSortOrder";
    public static final String VALUES_COLUMN_SORT_ORDER = "valuesColumnSortOrder";
    public static final String FIRST_COLUMN_FIELD = "firstColumnField";
    public static final String FIRST_COLUMN_FIELD_VALUES = "firstColumnFieldValues";
    public static final String SECOND_COLUMN_FIELD = "secondColumnField";
    public static final String SECOND_COLUMN_FIELD_VALUES = "secondColumnFieldValues";
    public static final String VALUES_COLUMN_FIELD = "valuesColumnField";
    public static final String VALUES_COLUMN_FIELD_VALUES = "valuesColumnFieldValues";
    public static final String PLOT_BAND_LIST = "plotBandList";
    public static final String VALUE_COLOUR_MAP = "valueColourMap";
    public static final String MIN_VALUE = "minValue";
    public static final String MAX_VALUE = "maxValue";
    public static final String DATASET_DESCRIPTOR = "dataSetDescriptor";

    private String query;
    private String dataSetDescriptor;
    private String chartTypeSpecific;

    private String chartType;

    private Integer refresh;

    private SortBy firstColumnSortOrder;
    private SortBy secondColumnSortOrder;
    private SortBy valuesColumnSortOrder;

    private DiagrammePreferenceValue firstColumnField;

    private List<DiagrammePreferenceValue> firstColumnFieldValues;

    private DiagrammePreferenceValue secondColumnField;

    private List<DiagrammePreferenceValue> secondColumnFieldValues;

    private DiagrammePreferenceValue valuesColumnField;

    private List<DiagrammePreferenceValue> valuesColumnFieldValues;

    public Map<String, String> valueColourMap;

    private List<PlotBandPreference> plotBandList;

    private double minValue;
    private double maxValue;

    public DiagrammePreference() {
    }

    public static DiagrammePreference createDefault() {
        // TODO Default constructor instead of static function?
        DiagrammePreference diagrammePreference = new DiagrammePreference();
        diagrammePreference.setRefresh(0);
        return diagrammePreference;
    }

    public static DiagrammePreference refresh() {
        DiagrammePreference diagrammePreference = new DiagrammePreference();
        diagrammePreference.setRefresh(0);
        return diagrammePreference;
    }

    public String getQuery() {
        if (query == null) {
            query = "";
        }
        return query;
    }

    public void setQuery(String sqlQuery) {
        this.query = sqlQuery;
    }

    public String getChartTypeSpecific() {
        return chartTypeSpecific;
    }

    public void setChartTypeSpecific(String chartTypeSpecific) {
        this.chartTypeSpecific = chartTypeSpecific;
    }

    public String getChartType() {
        return chartType;
    }

    public void setChartType(String chartType) {
        this.chartType = chartType;
    }

    public Integer getRefresh() {
        return refresh;
    }

    public void setRefresh(Integer refresh) {
        this.refresh = refresh;
    }

    public DiagrammePreferenceValue getFirstColumnField() {
        return firstColumnField;
    }

    public void setFirstColumnField(DiagrammePreferenceValue firstColumnField) {
        this.firstColumnField = firstColumnField;

        if (firstColumnField != null) {
            setDataSetDescriptor(firstColumnField.getName());
        }
    }

    public List<DiagrammePreferenceValue> getFirstColumnFieldValues() {
        if (firstColumnFieldValues == null) {
            firstColumnFieldValues = new ArrayList<>();
        }
        return firstColumnFieldValues;
    }

    public void setFirstColumnFieldValues(List<DiagrammePreferenceValue> firstColumnFieldValues) {
        this.firstColumnFieldValues = firstColumnFieldValues;
    }

    public void setFirstColumnFieldValue(DiagrammePreferenceValue... firstColumnFieldValues) {
        getFirstColumnFieldValues().addAll(Arrays.asList(firstColumnFieldValues));
    }

    public DiagrammePreferenceValue getSecondColumnField() {
        return secondColumnField;
    }

    public void setSecondColumnField(DiagrammePreferenceValue secondColumnField) {
        this.secondColumnField = secondColumnField;
        if (secondColumnField != null && !chartType.equalsIgnoreCase("pie")) {
            setDataSetDescriptor(secondColumnField.getName());
        }
    }

    public List<DiagrammePreferenceValue> getSecondColumnFieldValues() {
        if (secondColumnFieldValues == null) {
            secondColumnFieldValues = new ArrayList<>();
        }
        return secondColumnFieldValues;
    }

    public void setSecondColumnFieldValues(List<DiagrammePreferenceValue> secondColumnFieldValues) {
        this.secondColumnFieldValues = secondColumnFieldValues;
    }
    
    public void setSecondColumnFieldValue(DiagrammePreferenceValue... secondColumnFieldValues) {
        getSecondColumnFieldValues().addAll(Arrays.asList(secondColumnFieldValues));
    }

    public DiagrammePreferenceValue getValuesColumnField() {
        return valuesColumnField;
    }

    public void setValuesColumnField(DiagrammePreferenceValue valuesColumnField) {
        this.valuesColumnField = valuesColumnField;
    }

    public List<DiagrammePreferenceValue> getValuesColumnFieldValues() {
        if (valuesColumnFieldValues == null) {
            valuesColumnFieldValues = new ArrayList<>();
        }
        return valuesColumnFieldValues;
    }

    public void setValuesColumnFieldValues(List<DiagrammePreferenceValue> valuesColumnFieldValues) {
        this.valuesColumnFieldValues = valuesColumnFieldValues;
    }
    
    public void setValuesColumnFieldValue(DiagrammePreferenceValue... valuesColumnFieldValues) {
        getValuesColumnFieldValues().addAll(Arrays.asList(valuesColumnFieldValues));
    }

    public SortBy getFirstColumnSortOrder() {
        return firstColumnSortOrder;
    }

    public void setFirstColumnSortOrder(SortBy order) {
        this.firstColumnSortOrder = order;
    }

    public SortBy getSecondColumnSortOrder() {
        return secondColumnSortOrder;
    }

    public void setSecondColumnSortOrder(SortBy order) {
        this.secondColumnSortOrder = order;
    }

    public SortBy getValuesColumnSortOrder() {
        return valuesColumnSortOrder;
    }

    public void setValuesColumnSortOrder(SortBy order) {
        this.valuesColumnSortOrder = order;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public double getMinValue() {
        return minValue;
    }

    public void setValueColourMap(Map<String, String> colourValue) {
        this.valueColourMap = colourValue;
    }

    public Map<String, String> getValueColourMap() {
        if (valueColourMap == null) {
            valueColourMap = new HashMap<>();
        }
        return valueColourMap;
    }

    public String getDataSetDescriptor() {
        return dataSetDescriptor;
    }

    public void setDataSetDescriptor(String dataSetDescriptor) {
        this.dataSetDescriptor = dataSetDescriptor;
    }

    public void setPlotBandList(List<PlotBandPreference> plotBandList) {
        this.plotBandList = plotBandList;
    }

    public List<PlotBandPreference> getPlotBandList() {
        if (plotBandList == null) {
            plotBandList = new ArrayList<>();
        }
        return plotBandList;
    }

}
