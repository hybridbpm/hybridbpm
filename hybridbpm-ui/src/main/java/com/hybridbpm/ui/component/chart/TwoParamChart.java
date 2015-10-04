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

import com.hybridbpm.core.data.chart.DiagrammePreference;
import com.hybridbpm.core.data.chart.DiagrammePreferenceValue;
import com.hybridbpm.core.data.chart.SortBy;
import static com.hybridbpm.ui.component.chart.util.DiagrammeUtil.checkNotEmpty;
import com.hybridbpm.ui.HybridbpmUI;
import com.vaadin.data.Container;
import com.vaadin.data.Item;

import java.util.*;
import java.util.logging.Logger;


@SuppressWarnings("serial")
public abstract class TwoParamChart extends AbstractChart {

    protected class FirstColumnOrderedContainer extends TwoParamContainer {

        FirstColumnOrderedContainer(Container container, SortBy sortOrder) {
            super(container, sortOrder);
        }

        @Override
        public Map getData() {
            SortBy sortOrder = getSortOrder();
            Map dataMap = populateDataMap(createSortMap(sortOrder));
            return dataMap;
        }

        protected Map createSortMap(SortBy sortOrder) {
            Map map;
            if (sortOrder == SortBy.ASCENDING) {
                map = new TreeMap();
            } else if (sortOrder == SortBy.DESCENDING) {
                map = new TreeMap(Collections.reverseOrder());
            } else {
                map = new HashMap();
            }
            return map;
        }
    }

    protected class ValuesColumnOrderedContainer extends TwoParamContainer {

        ValuesColumnOrderedContainer(Container container, SortBy sortOrder) {
            super(container, sortOrder);
        }

        @Override
        public Map getData() {
            Map unsortedMap = populateDataMap(new HashMap());

            SortBy sortOrder = getSortOrder();
            if (sortOrder != null) {
                Map sortedMap = createSortMap(unsortedMap, sortOrder);
                return sortedMap;
            } else {
                return unsortedMap;
            }
        }

        protected Map createSortMap(Map unsortedMap, SortBy sortOrder) {
            ValueComparator comparator = new ValueComparator(unsortedMap, sortOrder);
            Map sortedMap = new TreeMap(comparator);
            sortedMap.putAll(unsortedMap);
            return sortedMap;
        }
    }

    protected abstract class TwoParamContainer extends ChartContainer {

        private SortBy sortOrder;

        TwoParamContainer(Container container, SortBy sortOrder) {
            super(container);
            this.sortOrder = sortOrder;
        }

        public abstract Map getData();

        protected Map populateDataMap(Map dataMap) {
            for (Object itemId : getContainer().getItemIds()) {
                Item item = getContainer().getItem(itemId);

                Object keyOfFirstColumn = item.getItemProperty(firstColumnName).getValue();
                Object valueOfValuesColumn = item.getItemProperty(valuesColumnName).getValue();
                dataMap.put(keyOfFirstColumn, valueOfValuesColumn);
            }

            return dataMap;
        }

        protected SortBy getSortOrder() {
            return sortOrder;
        }
    }

    private static class ValueComparator implements Comparator {

        Map base;
        SortBy sortOrder;

        public ValueComparator(Map base, SortBy sortOrder) {
            this.base = base;
            this.sortOrder = sortOrder;
        }

        public int compare(Object a, Object b) {
            Comparable compA = (Comparable) base.get(a);
            Comparable compB = (Comparable) base.get(b);
            int compare = compA.compareTo(compB);
            if (compare * sortOrder.getCoef() >= 0) {
                return 1;
            } else {
                return -1;
            } // don't return 0 or keys will merge
        }
    }

    private static final Logger LOG = Logger.getLogger(TwoParamChart.class.getName());

    public TwoParamChart(Container container) {
        super(container);
    }

    private String firstColumnName;
    private String valuesColumnName;
    private SortBy firstColumnSortOrder;
    private SortBy valuesColumnSortOrder;

    private TwoParamContainer chartContainer;

    @Override
    protected void initPreferences() {
        DiagrammePreferenceValue firstColumn = getPreferenceValue(DiagrammePreference.FIRST_COLUMN_FIELD);
        DiagrammePreferenceValue valuesColumn = getPreferenceValue(DiagrammePreference.VALUES_COLUMN_FIELD);
        this.firstColumnName = firstColumn.getId();
        this.valuesColumnName = valuesColumn.getId();
        this.firstColumnSortOrder = getPreferenceValue(DiagrammePreference.FIRST_COLUMN_SORT_ORDER);
        this.valuesColumnSortOrder = getPreferenceValue(DiagrammePreference.VALUES_COLUMN_SORT_ORDER);
    }

    protected abstract void renderChart(Map data);

    protected void renderChart() {
        LOG.fine("Building two parameter chart");
        createContainer();

        Map data = chartContainer.getData();

        // call descendant, he knows what to do
        renderChart(data);
    }

    private void createContainer() {
        if (firstColumnSortOrder != null) {
            this.chartContainer = new FirstColumnOrderedContainer(getContainer(), firstColumnSortOrder);
        } else {
            this.chartContainer = new ValuesColumnOrderedContainer(getContainer(), valuesColumnSortOrder);
        }
    }

    @Override
    protected void checkState() {
        checkNotEmpty(firstColumnName, HybridbpmUI.getText("first-column-not-set"));
        checkNotEmpty(valuesColumnName, HybridbpmUI.getText("values-column-not-set"));
        checkNotEmpty(getContainer(), HybridbpmUI.getText("container-not-set"));
    }

    protected String getFirstColumnName() {
        return firstColumnName;
    }

    protected String getValuesColumnName() {
        return valuesColumnName;
    }
}
