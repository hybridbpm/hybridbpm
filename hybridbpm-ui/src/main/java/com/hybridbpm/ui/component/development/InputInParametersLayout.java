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
import com.hybridbpm.ui.component.ParameterForm;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.vaadin.aceeditor.Suggester;

/**
 *
 * @author Marat Gubaidullin
 */
public class InputInParametersLayout extends VerticalLayout {

    private ConnectorModel connectoModel;

    public InputInParametersLayout() {
    }

    public void initUI(Suggester suggester, Map<String, String> values) {
        removeAllComponents();
        setMargin(true);
        setSpacing(true);
        setWidth(100, Sizeable.Unit.PERCENTAGE);
        for (FieldModel fieldModel : connectoModel.getInParameters()) {
            ParameterForm fieldForm = new ParameterForm(fieldModel, ParameterForm.TYPE.INPUT, suggester, values.get(fieldModel.getName()));
            addComponent(fieldForm);
        }
    }

    public ConnectorModel getConnectoModel() {
        return connectoModel;
    }

    public void setConnectoModel(ConnectorModel connectoModel) {
        this.connectoModel = connectoModel;
    }
    
    public Map<String, String> getValues(){
        Map<String, String> result = new HashMap<>();
        for (Component comp : this){
            if (comp instanceof ParameterForm){
                try {
                    ((ParameterForm) comp).commit();
                    FieldModel fieldModel = ((ParameterForm) comp).getFieldModel();
                    result.put(fieldModel.getName(), fieldModel.getDefaultValue());
                } catch (FieldGroup.CommitException ex) {
                    Logger.getLogger(InputInParametersLayout.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        }
        return result;
    }

}
