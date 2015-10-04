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

import com.hybridbpm.core.data.development.Module;
import com.hybridbpm.core.util.HybridbpmCoreUtil;
import com.hybridbpm.model.ConnectorModel;
import com.hybridbpm.ui.HybridbpmUI;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marat Gubaidullin
 */
public class ConnectorParametersLayout extends VerticalLayout  {

    public static final Logger logger = Logger.getLogger(ConnectorParametersLayout.class.getCanonicalName());
    private final TabSheet tabSheet = new TabSheet();

    private final InParametersLayout inParametersLayout = new InParametersLayout();
    private final OutParametersLayout outParametersLayout = new OutParametersLayout();

    private final ConnectorModel connectoModel;
    private final Module Module;

    public ConnectorParametersLayout(Module Module) {
        this.Module = Module;
        connectoModel = HybridbpmCoreUtil.jsonToObject(Module.getModel(), ConnectorModel.class);

        inParametersLayout.setConnectoModel(connectoModel);
        inParametersLayout.initUI();
        outParametersLayout.setConnectoModel(connectoModel);
        outParametersLayout.initUI();

        tabSheet.setSizeFull();
        tabSheet.addTab(inParametersLayout, "IN parameters");
        tabSheet.addTab(outParametersLayout, "OUT parameters");

        setSizeFull();
        addComponent(tabSheet);
        setExpandRatio(tabSheet, 1f);
    }

    public void save() {
        try {
            connectoModel.getInParameters().clear();
            connectoModel.getOutParameters().clear();

            for (Component comp : inParametersLayout) {
                if (comp instanceof FieldForm) {
                    FieldForm fieldForm = (FieldForm) comp;
                    fieldForm.commit();
                    connectoModel.getInParameters().add(fieldForm.getFieldModel());
                }
            }

            for (Component comp : outParametersLayout) {
                if (comp instanceof FieldForm) {
                    FieldForm fieldForm = (FieldForm) comp;
                    fieldForm.commit();
                    connectoModel.getOutParameters().add(fieldForm.getFieldModel());
                }
            }
            Module.setModel(HybridbpmCoreUtil.objectToJson(connectoModel));
            HybridbpmUI.getDevelopmentAPI().saveModule(Module);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            Notification.show("Error", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
        }
    }
}
