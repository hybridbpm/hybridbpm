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
package com.hybridbpm.core.data.dashboard;

import com.hybridbpm.model.Translated;
import java.io.Serializable;
import javax.persistence.Id;

/**
 *
 * @author Marat Gubaidullin
 */
public class TabDefinition implements Serializable {

    @Id
    protected Object id;
    protected Translated title;
    private ViewDefinition viewId;
    private String icon;
    private Integer order;
    private Integer rows;
    private Integer columns;
    private LAYOUT_TYPE layout;

    public enum LAYOUT_TYPE {

        VERTICAL,
        HORIZONTAL,
        GRID

    };

    public TabDefinition() {
    }

    public TabDefinition(String title, ViewDefinition viewId, String icon, LAYOUT_TYPE layout, Integer order, Integer rows, Integer columns) {
        this.title = new Translated(title);
        this.viewId = viewId;
        this.icon = icon;
        this.order = order;
        this.layout = layout;
        this.rows = rows;
        this.columns = columns;
    }
    
    public static TabDefinition createDefaultVertical(){
        return new TabDefinition("Tab", null, "DASHBOARD", LAYOUT_TYPE.VERTICAL, 0, 1, 1);
    }
    
    public static TabDefinition createDefaultGrid(){
        return new TabDefinition("Tab", null, "DASHBOARD", LAYOUT_TYPE.GRID, 0, 2, 2);
    }

    public ViewDefinition getViewId() {
        return viewId;
    }

    public void setViewId(ViewDefinition viewId) {
        this.viewId = viewId;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Object getId() {
        return id;
    }

    public Translated getTitle() {
        return title;
    }

    public void setTitle(Translated title) {
        this.title = title;
    }

    public LAYOUT_TYPE getLayout() {
        return layout;
    }

    public void setLayout(LAYOUT_TYPE layout) {
        this.layout = layout;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Integer getColumns() {
        return columns;
    }

    public void setColumns(Integer columns) {
        this.columns = columns;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

}
