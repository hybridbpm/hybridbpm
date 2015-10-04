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
package com.hybridbpm.ui.component.crud;

import com.hybridbpm.core.data.development.Module;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.dashboard.DashboardEventListener;
import com.hybridbpm.ui.dashboard.DashboardMessage;
import com.vaadin.ui.*;
import com.vaadin.ui.declarative.*;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;

@DesignRoot
public class CrudTable extends VerticalLayout implements ValueChangeListener, DashboardEventListener {

    /* generated datasources start */
    private final BeanItemContainer<Module> beanItemContainer = new BeanItemContainer<>(Module.class);
    /* generated datasources end */

    /* generated components start */
    private Table dataTable;
    /* generated components end */

    public CrudTable() {
        Design.read(this);
        /* generated bindings start */
        dataTable.setContainerDataSource(beanItemContainer);
        dataTable.addValueChangeListener(this);
        dataTable.setSelectable(true);
        dataTable.setMultiSelect(false);
        dataTable.setColumnHeader("name", "Name");
        dataTable.setColumnHeader("id", "ID");
        dataTable.setColumnHeader("updateDate", "Update date");
        dataTable.setVisibleColumns("id", "name", "updateDate");
        /* generated bindings end */
    }

    @Override
    public void attach() {
        super.attach();
        /* generated loads start */
        beanItemContainer.addAll(HybridbpmUI.getCrudAPI().readList(Module.class, null));
        /* generated loads end */
    }

    @Override
    public void valueChange(Property.ValueChangeEvent event) {
        Module Module = (Module) dataTable.getValue();
        HybridbpmUI.sendMessage(DashboardMessage.createCustom("Module", Module.getId()));
    }
    
        @Override
    public void onMessage(DashboardMessage message) {
        System.out.println(message.getName() + " " + message.getBody());
    }
}
