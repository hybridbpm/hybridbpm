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
package com.hybridbpm.core.api;

import com.hybridbpm.core.util.HybridbpmCoreUtil;
import static com.hybridbpm.core.HybridbpmCore.HYBRIDBPM_DIRECTORY;
import com.hybridbpm.core.data.access.Permission;
import com.hybridbpm.model.Translated;
import com.hybridbpm.model.DataModel;
import com.hybridbpm.core.data.development.Module;
import com.hybridbpm.core.data.access.User;
import com.hybridbpm.model.ConnectorModel;
import com.hybridbpm.model.FieldModel;
import com.hybridbpm.model.FormModel;
import com.hybridbpm.model.ProcessModel;
import com.hybridbpm.model.TaskModel;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;
import groovy.lang.GroovyClassLoader;
import groovy.util.GroovyScriptEngine;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngine;
import org.codehaus.groovy.jsr223.GroovyScriptEngineFactory;

/**
 *
 * @author Marat Gubaidullin
 */
public class DevelopmentAPI extends AbstractAPI {

    private static final Logger logger = Logger.getLogger(DevelopmentAPI.class.getSimpleName());
    private static final String GROOVY_SOURCE_DIRECTORY = "src";
    private static GroovyClassLoader groovyClassLoader;

    private final static GroovyScriptEngineFactory scriptEngineFactory = new GroovyScriptEngineFactory();

    public static ScriptEngine getScriptEngine() {
        Thread.currentThread().setContextClassLoader(DevelopmentAPI.getGroovyClassLoader());
        return scriptEngineFactory.getScriptEngine();
    }

    private DevelopmentAPI(User user, String sessionId) {
        super(user, sessionId);
    }

    public static DevelopmentAPI get(User user, String sessionId) {
        return new DevelopmentAPI(user, sessionId);
    }

    public List<Module> getModuleList(Boolean template) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            List<Module> list = database.query(new OSQLSynchQuery<Module>("SELECT * FROM Module WHERE template = '" + template + "' ORDER BY name"));
            return detachList(list);
        }
    }

    public List<Module> getModuleListByType(Module.MODULE_TYPE module_type, Module.MODULE_SUBTYPE subType, Boolean template) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            List<Module> list = database.query(new OSQLSynchQuery<Module>(
                    "SELECT * FROM Module WHERE type = '" + module_type + "' AND template = '" + template + "' AND subType = '" + subType + "' ORDER BY name"));
            return detachList(list);
        }
    }

    public List<Module> getModuleListByType(Module.MODULE_TYPE module_type, Boolean template) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            List<Module> list = database.query(new OSQLSynchQuery<Module>(
                    "SELECT * FROM Module WHERE type = '" + module_type + "' AND template = '" + template + "' ORDER BY name"));
            return detachList(list);
        }
    }

    public List<Module> getFormListForProcess(String processName) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            List<Module> list = database.query(new OSQLSynchQuery<Module>(
                    "SELECT * FROM Module WHERE type = '" + Module.MODULE_TYPE.FORM + "'"
                    + " AND subType = '" + Module.MODULE_SUBTYPE.TASK_FORM + "'"
                    + " AND processName = '" + processName + "'"
                    + " AND template = false"
                    + " ORDER BY name"));
            return detachList(list);
        }
    }

    public List<Module> getMobileFormListForProcess(String processName) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            List<Module> list = database.query(new OSQLSynchQuery<Module>(
                    "SELECT * FROM Module WHERE type = '" + Module.MODULE_TYPE.MOBILE + "'"
                    + " AND subType = '" + Module.MODULE_SUBTYPE.TASK_FORM + "'"
                    + " AND processName = '" + processName + "'"
                    + " AND template = false"
                    + " ORDER BY name"));
            return detachList(list);
        }
    }

    public Module getModuleByName(String name) {
        if (name != null) {
            try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
                List<Module> list = database.query(new OSQLSynchQuery<Module>("SELECT * FROM Module WHERE name = '" + name + "'", 1));
                Module result = list.isEmpty() ? null : list.get(0);
                return detach(result);
            }
        } else {
            return null;
        }
    }

    public Module getModuleById(Object id) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            Module result = database.load(new ORecordId(id.toString()));
            return detach(result);
        }
    }

    public List<Module> getModulesPublishable() {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            List<Module> list = database.query(new OSQLSynchQuery<Module>("SELECT * FROM Module WHERE publishable = TRUE AND type = '" + Module.MODULE_TYPE.FORM + "'"));
            return detachList(list);
        }
    }

    public List<Module> getModulesByType(Module.MODULE_TYPE type) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            List<Module> list = database.query(new OSQLSynchQuery<Module>("SELECT * FROM Module ORDER BY type = '" + type + "'"));
            return detachList(list);
        }
    }

    public Module saveModule(Module module) throws RuntimeException {
        try {
            String moduleName = HybridbpmCoreUtil.checkClassName(module.getName());
            if (getModuleByName(moduleName) != null){
                throw new RuntimeException("Module name should be unique!");
            }
            module.setName(moduleName);
            switch (module.getType()) {
                case FORM:
                    return saveFormModule(module);
                case MOBILE:
                    return saveMobileFormModule(module);
                case CHART:
                    return saveChartModule(module);
                case CONNECTOR:
                    return saveConnectorModule(module);
                case DATA:
                    return saveDataModule(module);
                case PROCESS:
                    return saveProcessModule(module);
                default:
                    return module;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Module save(Module module) {
        module.setUpdateDate(new Date());
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            module = database.save(module);
            database.commit();
            return detach(module);
        }
    }

    private Module saveChartModule(Module module) {
//            ProcessModel processModel = HybridbpmCoreUtil.jsonToObject(process.getModel(), ProcessModel.class);
//            FormModel formModel = module.getModel() == null ? FormModel.create(module.getName()) : HybridbpmCoreUtil.jsonToObject(module.getModel(), FormModel.class);
//            formModel.setTitle(module.getTitle());
//            formModel.setParameters(processModel.getVariableModels());
//            formModel.setFiles(processModel.getFileModels());
//            if (module.getDesign() == null || module.getDesign().isEmpty()) {
//                module.setDesign(HybridbpmCoreUtil.updateMobileFormDesign(formModel));
//            }
        return save(module);
    }

    private Module saveMobileFormModule(Module module) {
        if (module.getTemplate()) {
            // save template
        } else {
            // save module
            prepareMobileModule(module);
        }
        return save(module);
    }

    private void prepareMobileModule(Module module) {
        if (module.getSubType().equals(Module.MODULE_SUBTYPE.TEMPLATED_FORM)) {
            // generate from template
//            Module template = getModuleByName(md.getTemplateName());
//            FormModel formModel = md.getModel() == null ? FormModel.create(md.getName()) : HybridbpmCoreUtil.jsonToObject(md.getModel(), FormModel.class);
//            updateFormModule(md, template, formModel);
        } else if (module.getSubType().equals(Module.MODULE_SUBTYPE.TASK_FORM)) {
            // generate from process
            Module process = getModuleByName(module.getProcessName());
            ProcessModel processModel = HybridbpmCoreUtil.jsonToObject(process.getModel(), ProcessModel.class);
            FormModel formModel = module.getModel() == null ? FormModel.create(module.getName()) : HybridbpmCoreUtil.jsonToObject(module.getModel(), FormModel.class);
            formModel.setTitle(module.getTitle());
            formModel.setParameters(processModel.getVariableModels());
            formModel.setFiles(processModel.getFileModels());
            if (module.getDesign() == null || module.getDesign().isEmpty()) {
                module.setDesign(HybridbpmCoreUtil.updateMobileFormDesign(formModel));
            }
        }
    }

    private Module saveFormModule(Module module) {
        if (module.getTemplate()) {
            // save template
        } else {
            // save module
            prepareModule(module);
        }
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            module.setUpdateDate(new Date());
            module = database.save(module);
            database.commit();
            module = detach(module);
        }
        regenerateGroovySources();
        return module;
    }

    private void prepareModule(Module md) {
        if (md.getSubType().equals(Module.MODULE_SUBTYPE.TEMPLATED_FORM)) {
            // generate from template
            Module template = getModuleByName(md.getTemplateName());
            FormModel formModel = md.getModel() == null ? FormModel.create(md.getName()) : HybridbpmCoreUtil.jsonToObject(md.getModel(), FormModel.class);
            updateFormModule(md, template, formModel);
        } else if (md.getSubType().equals(Module.MODULE_SUBTYPE.TASK_FORM)) {
            // generate from process
            Module template = getModuleByName(md.getTemplateName());
            Module process = getModuleByName(md.getProcessName());
            ProcessModel processModel = HybridbpmCoreUtil.jsonToObject(process.getModel(), ProcessModel.class);
            FormModel formModel = md.getModel() == null ? FormModel.create(md.getName()) : HybridbpmCoreUtil.jsonToObject(md.getModel(), FormModel.class);
            formModel.setParameters(processModel.getVariableModels());
            formModel.setFiles(processModel.getFileModels());
            updateFormModule(md, template, formModel);
        }
    }

    private void updateFormModule(Module module, Module template, FormModel formModel) {
        module.setModel(HybridbpmCoreUtil.objectToJson(formModel));
        if (module.getCode() == null) {
            module.setCode(HybridbpmCoreUtil.generateModuleCodeFromTemplate(module.getName(), template.getCode()));
        }
        if (module.getDesign() == null) {
            module.setDesign(template.getDesign());
        }
        module.setCode(HybridbpmCoreUtil.updateFormCodeWithDatasources(formModel, module.getCode()));
        module.setCode(HybridbpmCoreUtil.updateFormCodeWithFiles(formModel, module.getCode()));
        module.setCode(HybridbpmCoreUtil.updateFormCodeWithComponents(formModel, module.getCode()));
        module.setCode(HybridbpmCoreUtil.updateFormCodeWithBindings(formModel, module.getCode()));
        module.setCode(HybridbpmCoreUtil.updateFormCodeWithCommits(formModel, module.getCode()));
        module.setDesign(HybridbpmCoreUtil.updateFormDesign(formModel, module.getDesign()));
    }

    private Module saveConnectorModule(Module module) {
        if (module.getTemplate()) {

        } else {
            if (module.getModel() == null) {
                Module template = getModuleByName(module.getTemplateName());
                ConnectorModel connectorModel = HybridbpmCoreUtil.jsonToObject(template.getModel(), ConnectorModel.class);
                connectorModel.setName(module.getName());
                module.setModel(HybridbpmCoreUtil.objectToJson(connectorModel));
            }
            if (module.getCode() == null) {
                Module template = getModuleByName(module.getTemplateName());
                String code = HybridbpmCoreUtil.generateModuleCodeFromTemplate(module.getName(), template.getCode());
                module.setCode(code);
            }
            module.setCode(HybridbpmCoreUtil.updateConnectorCodeInParameters(module));
            module.setCode(HybridbpmCoreUtil.updateConnectorCodeOutParameters(module));
        }
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            module.setUpdateDate(new Date());
            module = database.save(module);
            database.commit();
            module = detach(module);
        }
        regenerateGroovySources();
        return module;
    }

    private Module saveDataModule(Module module) {
        DataModel dataModel;
        if (module.getModel() == null) {
            dataModel = DataModel.createDefault();
            dataModel.setName(module.getName());
            module.setModel(HybridbpmCoreUtil.objectToJson(dataModel));
        } else {
            dataModel = HybridbpmCoreUtil.jsonToObject(module.getModel(), DataModel.class);
        }
        module.setCode(HybridbpmCoreUtil.createDataCode(dataModel));

        OrientGraphNoTx graphNoTx = getOrientGraphNoTx();
        OrientVertexType vertexType;
        if (!graphNoTx.getRawGraph().getMetadata().getSchema().existsClass(module.getName())) {
            vertexType = graphNoTx.createVertexType(module.getName());
        } else {
            vertexType = graphNoTx.getVertexType(module.getName());
        }

        for (FieldModel fieldModel : dataModel.getFields()) {
            createProperty(vertexType, fieldModel, graphNoTx);
        }
        // TODO: check if we need to remove or not
//        for (OProperty property : vertexType.declaredProperties()) {
//            try {
//                if (!dataModel.containsField(property.getName())) {
//                    vertexType.dropProperty(property.getName());
//                }
//            } catch (Exception ex) {
//                Logger.getLogger(DevelopmentAPI.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
//            }
//        }
        graphNoTx.commit();

        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            module.setUpdateDate(new Date());
            module = database.save(module);
            database.commit();
            module = detach(module);
        }
        regenerateGroovySources();
        return module;
    }

    private void createProperty(OrientVertexType vertexType, FieldModel fieldModel, OrientGraphNoTx graphNoTx) {
        if (vertexType.getProperty(fieldModel.getName()) == null) {

            if (fieldModel.getCollection().equals(FieldModel.COLLECTION_TYPE.LIST)) {
                try {

                    Class c = Class.forName(fieldModel.getClassName());
                    vertexType.createProperty(fieldModel.getName(), OType.EMBEDDEDLIST, OType.getTypeByClass(c));
                } catch (ClassNotFoundException ex) {
                    try {
                        OClass oc = graphNoTx.getRawGraph().getMetadata().getSchema().getClass(fieldModel.getClassName());
                        vertexType.createProperty(fieldModel.getName(), OType.LINKLIST, oc);
                    } catch (Exception ex2) {
                        Logger.getLogger(DevelopmentAPI.class.getName()).log(Level.SEVERE, ex.getMessage(), ex2);
                    }
                }
            } else {
                try {
                    Class c = Class.forName(fieldModel.getClassName());
                    vertexType.createProperty(fieldModel.getName(), OType.getTypeByClass(c));
                } catch (ClassNotFoundException ex) {
                    try {
                        OClass oc = graphNoTx.getRawGraph().getMetadata().getSchema().getClass(fieldModel.getClassName());
                        vertexType.createProperty(fieldModel.getName(), OType.LINK, oc);
                    } catch (Exception ex2) {
                        Logger.getLogger(DevelopmentAPI.class.getName()).log(Level.SEVERE, ex.getMessage(), ex2);
                    }
                }
            }
        }
    }

    private Module saveProcessModule(Module module) {
        ProcessModel processModel;
        if (module.getModel() == null) {
            processModel = ProcessModel.createDefault();
            processModel.setName(module.getName());
            processModel.setTitle(module.getTitle());
            module.setModel(HybridbpmCoreUtil.objectToJson(processModel));
        } else {
            processModel = HybridbpmCoreUtil.jsonToObject(module.getModel(), ProcessModel.class);
        }
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            module.setUpdateDate(new Date());
            module = database.save(module);

            database.command(new OCommandSQL("DELETE EDGE Permission WHERE in = " + module.getId())).execute();

            Vertex pv = getOrientGraph().getVertex(module.getId());
            List<Permission.PERMISSION> permissions = new ArrayList<>();
            for (Permission.PERMISSION permission : Permission.PERMISSION.getPermissionsForClass(Module.class)) {
                permissions.add(permission);
            }

            for (TaskModel taskModel : processModel.getStartTaskModels().values()) {
                if (taskModel.getRole() != null && !taskModel.getRole().isEmpty()) {
                    Vertex role = getOrientGraph().getVertex(getRole(taskModel.getRole()).getId());
                    Edge perm = getOrientGraph().addEdge("class:Permission", role, pv, null);
                    perm.setProperty("permissions", permissions);
                    perm.setProperty("parameter", taskModel.getName());
                }
            }

            database.commit();
            module = detach(module);
            return module;
        }
    }

    public Map<String, Translated> getModuleTitles(Module.MODULE_TYPE type) {
        Map<String, Translated> result = new HashMap<>();
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            List<ODocument> docs = database.query(new OSQLSynchQuery<ODocument>("select name, title from Module WHERE type = '" + type + "'"));
            for (ODocument document : docs) {
                Translated translated = HybridbpmCoreUtil.jsonToObject(document.field("title").toString(), Translated.class);
                result.put(document.field("name").toString(), translated);
            }
        }
        return result;
    }

    public void deleteDefinition(Object id, boolean notify) throws RuntimeException {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            Vertex vertex = getOrientGraph().getVertex(id);
            getOrientGraph().removeVertex(vertex);
            getOrientGraph().commit();
        }
    }

    public static void regenerateGroovySources() {
        DevelopmentAPI developmentAPI = new DevelopmentAPI(User.getSystemUser(), null);
        groovyClassLoader = developmentAPI.getGroovyScriptEngine(true).getGroovyClassLoader();
    }

    private GroovyScriptEngine getGroovyScriptEngine(boolean cleanBefore) {
        try {
            saveScriptsToFileSystem(cleanBefore);
            String sourceFolder = HYBRIDBPM_DIRECTORY + System.getProperty("file.separator") + GROOVY_SOURCE_DIRECTORY + System.getProperty("file.separator");
            // generate structure
            String[] roots = new String[]{sourceFolder};
            GroovyScriptEngine groovyScriptEngine = new GroovyScriptEngine(roots, Thread.currentThread().getContextClassLoader());
            return groovyScriptEngine;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    public void saveScriptsToFileSystem(boolean cleanBefore) {
        try {
            String sourceFolder = HYBRIDBPM_DIRECTORY + System.getProperty("file.separator") + GROOVY_SOURCE_DIRECTORY + System.getProperty("file.separator");
            if (cleanBefore) {
                HybridbpmCoreUtil.deleteDirectory(sourceFolder);
            }
            new File(sourceFolder).mkdirs();

            List<Module> dataModules = getModuleListByType(Module.MODULE_TYPE.DATA, false);
            for (Module md : dataModules) {
                writeToFile(sourceFolder, md.getName() + "." + Module.MODULE_TYPE.DATA.getCodeExt(), md.getCode());
                writeToFile(sourceFolder, md.getName() + "." + Module.MODULE_TYPE.DATA.getModelExt(), md.getModel());
            }
            dataModules = getModuleListByType(Module.MODULE_TYPE.FORM, false);
            for (Module md : dataModules) {
                writeToFile(sourceFolder, md.getName() + "." + Module.MODULE_TYPE.FORM.getCodeExt(), md.getCode());
                writeToFile(sourceFolder, md.getName() + "." + Module.MODULE_TYPE.FORM.getModelExt(), md.getModel());
                writeToFile(sourceFolder, md.getName() + "." + Module.MODULE_TYPE.FORM.getDesignExt(), md.getDesign());
            }
            dataModules = getModuleListByType(Module.MODULE_TYPE.CONNECTOR, false);
            for (Module md : dataModules) {
                writeToFile(sourceFolder, md.getName() + "." + Module.MODULE_TYPE.CONNECTOR.getCodeExt(), md.getCode());
                writeToFile(sourceFolder, md.getName() + "." + Module.MODULE_TYPE.CONNECTOR.getModelExt(), md.getModel());
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public void updateScriptsFromFileSystem() {
        try {
            String sourceFolder = HYBRIDBPM_DIRECTORY + System.getProperty("file.separator") + GROOVY_SOURCE_DIRECTORY + System.getProperty("file.separator");

            List<Module> dataModules = getModuleListByType(Module.MODULE_TYPE.DATA, false);
            for (Module md : dataModules) {
                String code = readFromFile(sourceFolder, md.getName() + "." + Module.MODULE_TYPE.DATA.getCodeExt());
                md.setCode(code);
                String model = readFromFile(sourceFolder, md.getName() + "." + Module.MODULE_TYPE.DATA.getModelExt());
                md.setModel(model);
                save(md);
            }
            dataModules = getModuleListByType(Module.MODULE_TYPE.FORM, false);
            for (Module md : dataModules) {
                String code = readFromFile(sourceFolder, md.getName() + "." + Module.MODULE_TYPE.FORM.getCodeExt());
                md.setCode(code);
                String model = readFromFile(sourceFolder, md.getName() + "." + Module.MODULE_TYPE.FORM.getModelExt());
                md.setModel(model);
                String design = readFromFile(sourceFolder, md.getName() + "." + Module.MODULE_TYPE.FORM.getDesignExt());
                md.setDesign(design);
                save(md);
            }
            dataModules = getModuleListByType(Module.MODULE_TYPE.CONNECTOR, false);
            for (Module md : dataModules) {
                String code = readFromFile(sourceFolder, md.getName() + "." + Module.MODULE_TYPE.CONNECTOR.getCodeExt());
                md.setCode(code);
                String model = readFromFile(sourceFolder, md.getName() + "." + Module.MODULE_TYPE.CONNECTOR.getModelExt());
                md.setModel(model);
                save(md);
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private String readFromFile(String folder, String file) {
        StringBuilder result = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(folder, file)))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line).append(System.lineSeparator());
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return result.toString();
    }

    private void writeToFile(String folder, String file, String source) {
        if (source != null) {
            try (FileOutputStream fos = new FileOutputStream(new File(folder, file))) {
                fos.write(source.getBytes());
            } catch (Exception ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    public static GroovyClassLoader getGroovyClassLoader() {
        return groovyClassLoader;
    }
}
