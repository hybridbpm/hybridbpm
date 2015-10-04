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
package com.hybridbpm.ui.component;

import com.hybridbpm.model.FieldModel;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.VerticalLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marat Gubaidullin
 */
public class ParametersFieldGroup extends CustomField<List> implements Button.ClickListener {
    
    public static final Logger logger = Logger.getLogger(ParametersFieldGroup.class.getCanonicalName());

    private final VerticalLayout form = new VerticalLayout();

    public ParametersFieldGroup() {
        form.setSpacing(true);
        form.setMargin(false);
        form.setWidth(100, Unit.PERCENTAGE);
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        
    }

    @Override
    protected List getInternalValue() {
        List<FieldModel> result = new ArrayList<>();
        for (Component comp : form) {
            if (comp instanceof ParameterForm) {
                ParameterForm parameterForm = (ParameterForm) comp;
                try {
                    parameterForm.commit();
                } catch (FieldGroup.CommitException ex) {
                    logger.log(Level.SEVERE, ex.getMessage(), ex);
                }
                result.add(parameterForm.getFieldModel());
            }
        }
        return result;
    }

    @Override
    protected void setInternalValue(List value) {
        form.removeAllComponents();
        if (value != null) {
            List<FieldModel> models = value;
            for (FieldModel fieldModel : models) {
                ParameterForm parameterForm = new ParameterForm(fieldModel, ParameterForm.TYPE.INPUT, null, null);
                form.addComponent(parameterForm);
            }
        }
    }

    @Override
    protected Component initContent() {
        return form;
    }

    @Override
    public Class<? extends List> getType() {
        return List.class;
    }
}
