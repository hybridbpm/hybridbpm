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
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marat Gubaidullin
 */
public class ConnectorExecutionLayout extends VerticalLayout  {

    private static final Logger logger = Logger.getLogger(ConnectorExecutionLayout.class.getCanonicalName());
    private final TabSheet tabSheet = new TabSheet();

    private final InputInParametersLayout inputInParametersLayout = new InputInParametersLayout();
    private final OutputOutParametersLayout outputOutParametersLayout = new OutputOutParametersLayout();

    private final ConnectorModel connectoModel;
    private final Module Module;

    public ConnectorExecutionLayout(Module Module) {
        this.Module = Module;
        connectoModel = HybridbpmCoreUtil.jsonToObject(Module.getModel(), ConnectorModel.class);

        inputInParametersLayout.setConnectoModel(connectoModel);
        inputInParametersLayout.initUI(null, new HashMap<String, String>());
        outputOutParametersLayout.setConnectoModel(connectoModel);
        outputOutParametersLayout.initUI(null, new HashMap<String, String>());

        tabSheet.setSizeFull();
        tabSheet.addTab(inputInParametersLayout, "IN parameters");
        tabSheet.addTab(outputOutParametersLayout, "OUT parameters");

        setSizeFull();
        addComponent(tabSheet);
        setExpandRatio(tabSheet, 1f);
    }

    public void run() {
        try {
            Map<String, String> input = inputInParametersLayout.getValues();
            Map<String, Object> result = HybridbpmUI.getBpmAPI().executeConnector(connectoModel, input);
            if (result !=null && !result.isEmpty()){
                outputOutParametersLayout.setValues(result);
            }
            tabSheet.setSelectedTab(outputOutParametersLayout);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            Notification.show("Error", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
        }
    }
}
