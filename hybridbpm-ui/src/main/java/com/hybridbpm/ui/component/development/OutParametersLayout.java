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
package com.hybridbpm.ui.component.development;

import com.hybridbpm.model.ConnectorModel;
import com.hybridbpm.model.FieldModel;
import com.hybridbpm.ui.component.development.FieldForm;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 *
 * @author Marat Gubaidullin
 */
public class OutParametersLayout extends VerticalLayout implements Button.ClickListener {

    private ConnectorModel connectoModel;
    private final Button btnAdd = new Button(null, this);

    public OutParametersLayout() {
    }

    public void initUI() {
        removeAllComponents();
        btnAdd.setCaption(("Add parameter"));
        btnAdd.setStyleName(ValoTheme.BUTTON_LINK);
        btnAdd.setIcon(FontAwesome.PLUS_CIRCLE);
        setMargin(true);
        setSpacing(true);
        setWidth(100, Sizeable.Unit.PERCENTAGE);
        for (FieldModel fieldModel : connectoModel.getOutParameters()) {
            FieldForm fieldForm = new FieldForm(FieldForm.CLASS_LIST_TYPE.BOTH);
            fieldForm.setFieldModel(fieldModel);
            addComponent(fieldForm);
        }
        addComponent(btnAdd);
        setComponentAlignment(btnAdd, Alignment.MIDDLE_RIGHT);
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton().equals(btnAdd)) {
            FieldForm fieldForm = new FieldForm(FieldForm.CLASS_LIST_TYPE.BOTH);
            fieldForm.setFieldModel(new FieldModel("outParam", "", String.class.getCanonicalName(), null, FieldModel.COLLECTION_TYPE.NONE, FieldModel.EDITOR_TYPE.TEXT_FIELD));
            addComponent(fieldForm, getComponentIndex(btnAdd));
        }
    }

    public ConnectorModel getConnectoModel() {
        return connectoModel;
    }

    public void setConnectoModel(ConnectorModel connectoModel) {
        this.connectoModel = connectoModel;
    }

}
