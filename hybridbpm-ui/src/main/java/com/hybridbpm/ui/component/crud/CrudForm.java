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
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import java.util.logging.Level;
import java.util.logging.Logger;

@DesignRoot
public class CrudForm extends VerticalLayout implements DashboardEventListener {
    
    private static final Logger logger = Logger.getLogger(CrudForm.class.getSimpleName());

    /* generated datasources start */
    private final BeanFieldGroup<Module> beanFieldGroup = new BeanFieldGroup<>(Module.class);
    /* generated  datasources */

    /* generated components start */
    private TextField idTextField;
    private TextField versionTextField;
    private TextField nameTextField;
    /* generated components end */
    
    public CrudForm() {
        Design.read(this);
        /* generated bindings start */
        beanFieldGroup.bind(idTextField, "id");
        /* generated bindings end */
    }
    
    @Override
    public void attach() {
        super.attach();
        /* generated loads start */
        beanFieldGroup.setItemDataSource(HybridbpmUI.getCrudAPI().read(Module.class, null));
        /* generated loads end */
    }
    
    @Override
    public void onMessage(DashboardMessage message) {
        if (message.getType().equals(DashboardMessage.EVENT_TYPE.CUSTOM) && message.getName().equals("Module")) {
            beanFieldGroup.setItemDataSource(HybridbpmUI.getCrudAPI().read(Module.class, "where @rid = " + message.getBody()));
        }
    }
    
    public void save() {
        try {
            /* generated commits start */
            beanFieldGroup.commit();
            beanFieldGroup.setItemDataSource(HybridbpmUI.getCrudAPI().save(beanFieldGroup.getItemDataSource().getBean()));
            /* generated commits end */
        } catch (FieldGroup.CommitException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}
