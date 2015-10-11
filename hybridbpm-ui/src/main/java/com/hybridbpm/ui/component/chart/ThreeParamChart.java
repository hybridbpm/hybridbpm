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
import com.hybridbpm.ui.util.Translate;
import com.vaadin.data.Container;
import com.vaadin.data.Item;

import java.util.*;
import java.util.logging.Logger;


@SuppressWarnings("serial")
public abstract class ThreeParamChart extends AbstractChart {

    private class ThreeParamContainer extends ChartContainer {

        private Map<ColumnCoupleKey<?, ?>, Object> dataMap;
        private Set secondColumnValues;
        private Set firstColumnValues;

        protected ThreeParamContainer(Container container) {
            super(container);
            this.dataMap = new HashMap<ColumnCoupleKey<?, ?>, Object>();
            this.secondColumnValues = createSortSet(secondColumnSortOrder);
            this.firstColumnValues = createSortSet(firstColumnSortOrder);

            populateData();
        }

        protected void populateData() {
            for (Object itemId : getContainer().getItemIds()) {
                Item item = getContainer().getItem(itemId);

                Object keyOfSecondColumn = item.getItemProperty(secondColumnName).getValue();
                Object keyOfFirstColumn = item.getItemProperty(firstColumnName).getValue();
                Object valueOfValuesColumn = item.getItemProperty(valuesColumnName).getValue();

                // store data
                dataMap.put(new ColumnCoupleKey(keyOfSecondColumn, keyOfFirstColumn), valueOfValuesColumn);

                // sort second column values
                secondColumnValues.add(keyOfSecondColumn);

                // sort first column values
                firstColumnValues.add(keyOfFirstColumn);
            }
        }

        public Map<ColumnCoupleKey<?, ?>, Object> getData() {
            return dataMap;
        }

        public Set getSecondColumnValues() {
            return secondColumnValues;
        }

        public Set getFirstColumnValues() {
            return firstColumnValues;
        }
    }

    protected Set<?> createSortSet(SortBy sortOrder) {
        Set<?> set;
        if (sortOrder == SortBy.ASCENDING) {
            set = new TreeSet();
        } else if (sortOrder == SortBy.DESCENDING) {
            set = new TreeSet(Collections.reverseOrder());
        } else {
            set = new HashSet();
        }

        return set;
    }

    public static class ColumnCoupleKey<C1, C2> {

        private C2 secondColumnValue;
        private C1 firstColumnValue;

        public ColumnCoupleKey(C2 secondColumnValue, C1 firstColumnValue) {
            this.secondColumnValue = secondColumnValue;
            this.firstColumnValue = firstColumnValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ColumnCoupleKey that = (ColumnCoupleKey) o;

            if (!secondColumnValue.equals(that.secondColumnValue)) return false;
            if (!firstColumnValue.equals(that.firstColumnValue)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = secondColumnValue.hashCode();
            result = 31 * result + firstColumnValue.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "ColumnCoupleKey{" +
                    "secondColumnValue=" + secondColumnValue +
                    ", firstColumnValue=" + firstColumnValue +
                    '}';
        }

        public C2 getSecondColumnValue() {
            return secondColumnValue;
        }

        public C1 getFirstColumnValue() {
            return firstColumnValue;
        }
    }

    private static final Logger LOG = Logger.getLogger(ThreeParamChart.class.getName());

    private String firstColumnName;
    private String valuesColumnName;
    private String secondColumnName;
    private SortBy firstColumnSortOrder;
    private SortBy secondColumnSortOrder;

    public ThreeParamChart(Container container) {
        super(container);
    }

    private ThreeParamContainer chartContainer;

    protected abstract void renderChart(Map<ColumnCoupleKey<?, ?>, Object> data,
                                        Set secondColumnValues, Set firstColumnValues);

    @Override
    protected void initPreferences() {
        DiagrammePreferenceValue firstColumn = getPreferenceValue(DiagrammePreference.FIRST_COLUMN_FIELD);
        DiagrammePreferenceValue valuesColumn = getPreferenceValue(DiagrammePreference.VALUES_COLUMN_FIELD);
        DiagrammePreferenceValue secondColumn = getPreferenceValue(DiagrammePreference.SECOND_COLUMN_FIELD);
        this.firstColumnName = firstColumn.getId();
        this.valuesColumnName = valuesColumn.getId();
        this.secondColumnName = secondColumn.getId();
        this.firstColumnSortOrder = getPreferenceValue(DiagrammePreference.FIRST_COLUMN_SORT_ORDER);
        this.secondColumnSortOrder = getPreferenceValue(DiagrammePreference.SECOND_COLUMN_SORT_ORDER);
    }

    @Override
    protected void renderChart() {
        LOG.fine("Building three parameter chart");
        this.chartContainer = new ThreeParamContainer(getContainer());

        Map<ColumnCoupleKey<?, ?>, Object> data = chartContainer.getData();
        Set secondColumnValues = chartContainer.getSecondColumnValues();
        Set firstColumnValues = chartContainer.getFirstColumnValues();

        // call the descendant, he knows what to do
        renderChart(data, secondColumnValues, firstColumnValues);
    }

    @Override
    protected void checkState() {
        checkNotEmpty(firstColumnName, Translate.getMessage("first-column-not-set"));
        checkNotEmpty(secondColumnName, Translate.getMessage("second-column-not-set"));
        checkNotEmpty(valuesColumnName, Translate.getMessage("values-column-not-set"));
        checkNotEmpty(getContainer(), Translate.getMessage("container-not-set"));
    }

    protected String getFirstColumnName() {
        return firstColumnName;
    }

    protected String getValuesColumnName() {
        return valuesColumnName;
    }

    protected String getSecondColumnName() {
        return secondColumnName;
    }
}
