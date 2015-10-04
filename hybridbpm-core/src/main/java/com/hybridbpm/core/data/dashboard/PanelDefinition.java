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

import com.hybridbpm.core.data.development.Module;
import com.hybridbpm.model.FieldModel;
import com.hybridbpm.model.Translated;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import javax.persistence.Id;

/**
 *
 * @author Marat Gubaidullin
 */
public class PanelDefinition implements Serializable {

    @Id
    protected Object id;
    protected Translated title;
    private TabDefinition tabId;
    private Integer column;
    private Integer row;
    private Integer order;
    private Module.MODULE_TYPE moduleType;
    private String moduleName;
    private List<FieldModel> parameters;

    public PanelDefinition() {
    }

    public PanelDefinition(String title, TabDefinition tabId, Integer column, Integer row, Module.MODULE_TYPE moduleType, String moduleName, Integer order) {
        this.title = new Translated(title);
        this.tabId = tabId;
        this.column = column;
        this.row = row;
        this.moduleType = moduleType;
        this.moduleName = moduleName;
        this.order = order;
    }
    
    
    public static PanelDefinition createDefault(){
        return new PanelDefinition("Panel", null, 0, 0, null, null, 0);
    }
    
    public static PanelDefinition createDefault(Integer column, Integer row){
        return new PanelDefinition("Panel", null, column, row, null, null, 0);
    }

    public TabDefinition getTabId() {
        return tabId;
    }

    public void setTabId(TabDefinition tabId) {
        this.tabId = tabId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
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
    
    public void setDefaultTitle(String title) {
        if (this.title == null){
            this.title = new Translated();
        }
        this.title.addValue(Locale.US, title);
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Module.MODULE_TYPE getModuleType() {
        return moduleType;
    }

    public void setModuleType(Module.MODULE_TYPE moduleType) {
        this.moduleType = moduleType;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public List<FieldModel> getParameters() {
        return parameters;
    }

    public void setParameters(List<FieldModel> parameters) {
        this.parameters = parameters;
    }
    
}
