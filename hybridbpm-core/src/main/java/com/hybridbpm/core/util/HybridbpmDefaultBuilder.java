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
package com.hybridbpm.core.util;

import com.hybridbpm.model.Translated;
import com.hybridbpm.core.data.access.Role;
import com.hybridbpm.core.data.chart.DiagrammePreference;
import com.hybridbpm.core.data.chart.DiagrammePreferenceValue;
import com.hybridbpm.core.data.development.Module;
import com.hybridbpm.core.data.dashboard.PanelDefinition;
import com.hybridbpm.core.data.dashboard.TabDefinition;
import com.hybridbpm.core.data.dashboard.ViewDefinition;
import com.hybridbpm.model.ConnectorModel;
import com.hybridbpm.model.DataModel;
import com.hybridbpm.model.FieldModel;
import com.hybridbpm.model.FormModel;
import com.hybridbpm.model.ProcessModel;
import com.hybridbpm.model.TaskModel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marat Gubaidullin
 */
public class HybridbpmDefaultBuilder {

    private static final Logger logger = Logger.getLogger(HybridbpmDefaultBuilder.class.getSimpleName());

    public static ViewDefinition createViewDefinition(String url, String title, String icon, int order) {
        ViewDefinition view = new ViewDefinition();
        view.setUrl(url);
        view.setTitle(new Translated(title));
        view.setIcon(icon);
        view.setOrder(order);
        return view;
    }

    public static TabDefinition createTabDefinition(ViewDefinition vd, String title, String icon, int order, TabDefinition.LAYOUT_TYPE layout_type) {
        TabDefinition tab = new TabDefinition();
        tab.setViewId(vd);
        tab.setTitle(new Translated(title));
        tab.setIcon(icon);
        tab.setOrder(order);
        tab.setLayout(layout_type);
        return tab;
    }
    
    public static PanelDefinition createPanelDefinition(String title, TabDefinition tabId, Integer column, Integer row, Module.MODULE_TYPE moduleType, String moduleName, Integer order) {
        PanelDefinition panel = new PanelDefinition(title, tabId, column, row, moduleType, moduleName, order);
        return panel;
    }

    public static Module createModuleTemplate(String name, String title, String icon, Module.MODULE_TYPE type, Module.MODULE_SUBTYPE moduleSubtype) {
        Module module = new Module();
        module.setTitle(new Translated(title));
        module.setName(name);
        module.setType(type);
        module.setSubType(moduleSubtype);
        module.setCode(HybridbpmCoreUtil.getScriptTemplateByName(name + "." + type.getCodeExt()));
        if (type.equals(Module.MODULE_TYPE.FORM)) {
            module.setDesign(HybridbpmCoreUtil.getScriptTemplateByName(name + "." + type.getDesignExt()));
        } else if (type.equals(Module.MODULE_TYPE.CONNECTOR)) {
            module.setModel(HybridbpmCoreUtil.getScriptTemplateByName(name + "." + type.getModelExt()));
        }
        module.setConfigurable(false);
        module.setSystem(Boolean.TRUE);
        module.setTemplate(Boolean.TRUE);
        module.setPublishable(false);
        return module;
    }

    public static PanelDefinition createPanelDefinition(String viewId, String title, String moduleName, int order, String width, String height) {
        PanelDefinition panel = new PanelDefinition();
        panel.setTitle(new Translated(title));
        panel.setModuleName(moduleName);
        return panel;
    }

    public static Module createDemoData() {
        DataModel dataModel = new DataModel();
        dataModel.setName("Person");
        dataModel.addField(new FieldModel("firstName", null, String.class.getCanonicalName(), null, FieldModel.COLLECTION_TYPE.NONE, FieldModel.EDITOR_TYPE.TEXT_FIELD));
        dataModel.addField(new FieldModel("lastName", null, String.class.getCanonicalName(), null, FieldModel.COLLECTION_TYPE.NONE, FieldModel.EDITOR_TYPE.TEXT_FIELD));

        Module module = new Module();
        module.setTitle(new Translated("Person"));
        module.setName("Person");
        module.setType(Module.MODULE_TYPE.DATA);
        module.setModel(HybridbpmCoreUtil.objectToJson(dataModel));
        module.setConfigurable(false);
        module.setSystem(Boolean.FALSE);
        module.setPublishable(Boolean.FALSE);
        return module;
    }

    public static Module createDemoProcess() {
        ProcessModel processModel = new ProcessModel()
                .addHumanTaskModel("task0", "Create task", Role.USER, TaskModel.GATE_TYPE.EXLUSIVE, TaskModel.GATE_TYPE.EXLUSIVE, 50f, 100f)
                .addAutomaticTaskModel("task1", "Log data", "SimpleConnector", TaskModel.GATE_TYPE.EXLUSIVE, TaskModel.GATE_TYPE.EXLUSIVE, 300f, 100f)
                .addHumanTaskModel("task2", "Execute task", Role.USER, TaskModel.GATE_TYPE.EXLUSIVE, TaskModel.GATE_TYPE.EXLUSIVE, 600f, 100f)
                .addDefaultTransitionModel("default", "task0", "task1", 200f, 100f)
                .addDefaultTransitionModel("default", "task1", "task2", 450f, 100f)
                .addVariableModel("client", "Person", null, null)
                .addVariableModel("partner", "Person", null, null)
                .addFileModel("attachment", "Attachment", false)
                .addFileModel("attachments", "Attachments", true);
        processModel.setName("SimpleProcess");
        processModel.setTitle(new Translated("SimpleProcess"));
        processModel.getTaskModelByName("task0").setForm("SimpleProcessForm");
        processModel.getTaskModelByName("task0").setMobileForm("SimpleProcessMobileForm");
        processModel.getTaskModelByName("task2").setForm("SimpleProcessForm");
        processModel.getTaskModelByName("task2").setMobileForm("SimpleProcessMobileForm");

        processModel.getTaskModelByName("task1").getInParameters().put("inParam", "client.firstName + ' ' + client.lastName");
        processModel.getTaskModelByName("task1").getOutParameters().put("outParam", "partner.firstName");

        Module module = new Module();
        module.setTitle(new Translated("Simple process"));
        module.setName("SimpleProcess");
        module.setModel(HybridbpmCoreUtil.objectToJson(processModel));
        module.setType(Module.MODULE_TYPE.PROCESS);
        module.setConfigurable(false);
        module.setSystem(Boolean.FALSE);
        module.setPublishable(Boolean.FALSE);
        module.setTemplate(Boolean.FALSE);
        return module;
    }

    public static Module createDemoForm() {
        FormModel formModel = FormModel.create("SimpleProcessForm");
        formModel.addParameter(new FieldModel("title", null, String.class.getCanonicalName(), null, FieldModel.COLLECTION_TYPE.NONE, FieldModel.EDITOR_TYPE.TEXT_FIELD));
        formModel.addParameter(new FieldModel("description", null, String.class.getCanonicalName(), null, FieldModel.COLLECTION_TYPE.NONE, FieldModel.EDITOR_TYPE.TEXT_AREA));

        Module module = new Module();
        module.setTitle(new Translated("Simple process form"));
        module.setName("SimpleProcessForm");
        module.setModel(HybridbpmCoreUtil.objectToJson(formModel));
        module.setType(Module.MODULE_TYPE.FORM);
        module.setSubType(Module.MODULE_SUBTYPE.TASK_FORM);
        module.setProcessName("SimpleProcess");
        module.setTemplateName("TaskFormTemplate");
        module.setConfigurable(false);
        module.setSystem(Boolean.FALSE);
        module.setPublishable(Boolean.FALSE);
        module.setTemplate(Boolean.FALSE);
        return module;
    }

    public static Module createDemoMobileForm() {
        FormModel formModel = FormModel.create("SimpleProcessMobileForm");
        formModel.addParameter(new FieldModel("title", null, String.class.getCanonicalName(), null, FieldModel.COLLECTION_TYPE.NONE, FieldModel.EDITOR_TYPE.TEXT_FIELD));
        formModel.addParameter(new FieldModel("description", null, String.class.getCanonicalName(), null, FieldModel.COLLECTION_TYPE.NONE, FieldModel.EDITOR_TYPE.TEXT_AREA));

        Module module = new Module();
        module.setTitle(new Translated("Simple process mobile form"));
        module.setName("SimpleProcessMobileForm");
        module.setModel(HybridbpmCoreUtil.objectToJson(formModel));
        module.setType(Module.MODULE_TYPE.MOBILE);
        module.setSubType(Module.MODULE_SUBTYPE.TASK_FORM);
        module.setProcessName("SimpleProcess");
        module.setConfigurable(false);
        module.setSystem(Boolean.FALSE);
        module.setPublishable(Boolean.FALSE);
        module.setTemplate(Boolean.FALSE);
        return module;
    }

    public static Module createDemoConnector() {
        ConnectorModel connectorModel = ConnectorModel.createDefault();
        connectorModel.setName("SimpleConnector");

        Module module = new Module();
        module.setTitle(new Translated("Simple connector"));
        module.setName("SimpleConnector");
        module.setModel(HybridbpmCoreUtil.objectToJson(connectorModel));
        module.setType(Module.MODULE_TYPE.CONNECTOR);
        module.setTemplateName("SimpleConnectorTemplate");
        module.setConfigurable(true);
        module.setSystem(Boolean.FALSE);
        module.setPublishable(Boolean.FALSE);
        module.setTemplate(Boolean.FALSE);
        return module;
    }

    public static Module createDemoChart1() {

        DiagrammePreference diagrammePreference = new DiagrammePreference();
        diagrammePreference.setChartType("PIE");
        diagrammePreference.setQuery("SELECT count(1), status FROM Task GROUP BY status");

        diagrammePreference.setDataSetDescriptor("status (java.lang.String)");
        diagrammePreference.setFirstColumnField(new DiagrammePreferenceValue("status", "status (java.lang.String)"));
        diagrammePreference.setFirstColumnFieldValue(new DiagrammePreferenceValue("count", "count (java.lang.Long)"));
        diagrammePreference.setFirstColumnFieldValue(new DiagrammePreferenceValue("status", "status (java.lang.String)"));

        diagrammePreference.setSecondColumnFieldValue(new DiagrammePreferenceValue("count", "count (java.lang.Long)"));
        diagrammePreference.setSecondColumnFieldValue(new DiagrammePreferenceValue("status", "status (java.lang.String)"));

        diagrammePreference.setValuesColumnField(new DiagrammePreferenceValue("count", "count (java.lang.Long)"));
        diagrammePreference.setValuesColumnFieldValue(new DiagrammePreferenceValue("count", "count (java.lang.Long)"));

        diagrammePreference.setRefresh(0);
        diagrammePreference.setMaxValue(0);
        diagrammePreference.setMinValue(0);

        Module module = new Module();
        module.setTitle(new Translated("Task by status"));
        module.setName("TaskByStatusChart");
        module.setCode(diagrammePreference.getQuery());
        module.setDesign(HybridbpmCoreUtil.objectToJson(diagrammePreference));
        module.setType(Module.MODULE_TYPE.CHART);
        module.setConfigurable(false);
        module.setSystem(Boolean.FALSE);
        module.setPublishable(Boolean.TRUE);
        module.setTemplate(Boolean.FALSE);
        return module;
    }
    
    public static Module createDemoChart2() {

        DiagrammePreference diagrammePreference = new DiagrammePreference();
        diagrammePreference.setChartType("PIE");
        diagrammePreference.setQuery("SELECT count(1), modelName FROM Case GROUP BY modelName");

        diagrammePreference.setDataSetDescriptor("modelName (java.lang.String)");
        diagrammePreference.setFirstColumnField(new DiagrammePreferenceValue("modelName", "modelName (java.lang.String)"));
        diagrammePreference.setFirstColumnFieldValue(new DiagrammePreferenceValue("count", "count (java.lang.Long)"));
        diagrammePreference.setFirstColumnFieldValue(new DiagrammePreferenceValue("modelName", "modelName (java.lang.String)"));

        diagrammePreference.setSecondColumnFieldValue(new DiagrammePreferenceValue("count", "count (java.lang.Long)"));
        diagrammePreference.setSecondColumnFieldValue(new DiagrammePreferenceValue("modelName", "modelName (java.lang.String)"));

        diagrammePreference.setValuesColumnField(new DiagrammePreferenceValue("count", "count (java.lang.Long)"));
        diagrammePreference.setValuesColumnFieldValue(new DiagrammePreferenceValue("count", "count (java.lang.Long)"));

        diagrammePreference.setRefresh(0);
        diagrammePreference.setMaxValue(0);
        diagrammePreference.setMinValue(0);

        Module module = new Module();
        module.setTitle(new Translated("Process instances"));
        module.setName("ProcessInstancesChart");
        module.setCode(diagrammePreference.getQuery());
        module.setDesign(HybridbpmCoreUtil.objectToJson(diagrammePreference));
        module.setType(Module.MODULE_TYPE.CHART);
        module.setConfigurable(false);
        module.setSystem(Boolean.FALSE);
        module.setPublishable(Boolean.TRUE);
        module.setTemplate(Boolean.FALSE);
        return module;
    }

    public static String getModuleFormHtmlTemplate() {
        try {
            return HybridbpmCoreUtil.createFromSource("/template/Form.html");
        } catch (Exception ex) {
            Logger.getLogger(HybridbpmDefaultBuilder.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    public static String getChartHtmlTemplate() {
        try {
            return HybridbpmCoreUtil.createFromSource("/template/Chart.html");
        } catch (Exception ex) {
            Logger.getLogger(HybridbpmDefaultBuilder.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    public static String getChartGroovyTemplate() {
        try {
            return HybridbpmCoreUtil.createFromSource("/template/Chart.groovy");
        } catch (Exception ex) {
            Logger.getLogger(HybridbpmDefaultBuilder.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    public static String getCustomCssTemplate() {
        try {
            return HybridbpmCoreUtil.createFromSource("/template/custom.scss");
        } catch (Exception ex) {
            Logger.getLogger(HybridbpmDefaultBuilder.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

}
