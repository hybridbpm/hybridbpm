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
package com.hybridbpm.core;

import com.hybridbpm.core.util.HybridbpmCoreUtil;
import com.hybridbpm.core.util.HybridbpmDefaultBuilder;
import static com.hybridbpm.core.HybridbpmCore.CONFIGURATION_DIRECTORY;
import com.hybridbpm.core.api.DashboardAPI;
import com.hybridbpm.core.api.DevelopmentAPI;
import com.hybridbpm.core.api.SystemAPI;
import com.hybridbpm.core.data.Parameter;
import com.hybridbpm.core.data.access.Group;
import com.hybridbpm.core.data.access.Role;
import com.hybridbpm.core.data.access.User;
import com.hybridbpm.core.data.bpm.Case;
import com.hybridbpm.core.data.dashboard.PanelDefinition;
import com.hybridbpm.core.data.dashboard.TabDefinition;
import com.hybridbpm.core.data.dashboard.ViewDefinition;
import com.hybridbpm.core.data.development.Module;
import com.hybridbpm.core.serializer.DocumentTypeSerializer;
import com.hybridbpm.core.serializer.FieldCollectionTypeSerializer;
import com.hybridbpm.core.serializer.FieldEditorTypeSerializer;
import com.hybridbpm.core.serializer.ModelStatusSerializer;
import com.hybridbpm.core.serializer.ModuleSubTypeSerializer;
import com.hybridbpm.core.serializer.ModuleTypeSerializer;
import com.hybridbpm.core.serializer.ParameterTypeSerializer;
import com.hybridbpm.core.serializer.PermissionSerializer;
import com.hybridbpm.core.serializer.CaseStatusSerializer;
import com.hybridbpm.core.serializer.CaseTypeSerializer;
import com.hybridbpm.core.serializer.TabLayoutTypeSerializer;
import com.hybridbpm.core.serializer.TaskModelStatusSerializer;
import com.hybridbpm.core.serializer.TaskModelGateTypeSerializer;
import com.hybridbpm.core.serializer.TaskModelOptionSerializer;
import com.hybridbpm.core.serializer.TaskModelTaskPrioritySerializer;
import com.hybridbpm.core.serializer.TranslatedSerializer;
import com.hybridbpm.core.serializer.UserStatusSerializer;
import com.orientechnologies.orient.client.db.ODatabaseHelper;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.orientechnologies.orient.object.serialization.OObjectSerializerContext;
import com.orientechnologies.orient.object.serialization.OObjectSerializerHelper;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;
import com.orientechnologies.orient.server.config.OServerUserConfiguration;
import com.tinkerpop.blueprints.impls.orient.OrientEdgeType;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marat Gubaidullin
 */
public class DatabaseServer {

    private static final Logger logger = Logger.getLogger(DatabaseServer.class.getSimpleName());
    public static final String HYBRIDBPM_DATABASE_URL = "plocal:hybridbpm/databases/hybridbpm";
    public static final String CONFIGURATION_FILENAME = "orientdb-server-config.xml";
    public static final String HYBRIDBPM_DATABASE_NAME = "hybridbpm";
    private OServer oServer;
    public static final String ORIENTDB_WWW_DIRECTORY = "www";
    private static final String rootName = "root";
    private static final String userName = "admin";
    private static String userPassword = "hybridbpm";
    private static String rootPassword = "root";

    private OObjectDatabaseTx objectDatabaseTx;
    private ODatabaseDocumentTx databaseDocumentTx;
    private OrientGraphNoTx graphNoTx;

    public void start() {
        try {
            logger.info("DatabaseServer starting");
            File configurationFile = new File(CONFIGURATION_DIRECTORY, CONFIGURATION_FILENAME);
            if (!configurationFile.exists()) {
                try (FileOutputStream fos = new FileOutputStream(configurationFile)) {
                    fos.write(HybridbpmCoreUtil.getDefaultDatabaseConfig().getBytes());
                }
            }
            configurationFile = new File(CONFIGURATION_DIRECTORY, CONFIGURATION_FILENAME);

            oServer = OServerMain.create();
            oServer.startup(configurationFile);
            oServer.activate();
            for (OServerUserConfiguration oServerUserConfiguration : oServer.getConfiguration().users) {
                if (oServerUserConfiguration.name.equals(rootName)) {
                    rootPassword = oServerUserConfiguration.password;
                }
                if (oServerUserConfiguration.name.equals(userName)) {
                    userPassword = oServerUserConfiguration.password;
                }
            }

            if (!ODatabaseHelper.existsDatabase(HYBRIDBPM_DATABASE_URL)) {
                OrientGraphFactory factory = new OrientGraphFactory(HYBRIDBPM_DATABASE_URL, userName, "admin");
                graphNoTx = factory.getNoTx();
                databaseDocumentTx = factory.getDatabase();
                databaseDocumentTx.command(new OCommandSQL("UPDATE ouser SET password = '" + userPassword + "' WHERE name = 'admin'")).execute();
                databaseDocumentTx.commit();
                objectDatabaseTx = new OObjectDatabaseTx(databaseDocumentTx);
                objectDatabaseTx.activateOnCurrentThread();
            } else {
                OrientGraphFactory factory = new OrientGraphFactory(HYBRIDBPM_DATABASE_URL, userName, userPassword);
                databaseDocumentTx = factory.getDatabase();
                objectDatabaseTx = new OObjectDatabaseTx(databaseDocumentTx);
                objectDatabaseTx.activateOnCurrentThread();
            }

            verifySchema();
            DevelopmentAPI.regenerateGroovySources();
            createOrientdbStudio();
            logger.info("DatabaseServer started");
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public static String getUsername() {
        return userName;
    }

    public static String getPassword() {
        return userPassword;
    }

    public void stop() {
        try {
            logger.info("DatabaseServer stopping");
            oServer.shutdown();
            logger.info("DatabaseServer stopped");
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private void verifySchema() {
        logger.log(Level.INFO, "DatabaseServer.verifySchema started");
        registerSerializers();
        if (!objectDatabaseTx.getMetadata().getSchema().existsClass(Case.class.getSimpleName())) {
            createSchema();
            registerEntityClasses();
            createDefaultConfig();
            createDefaultAccess();
            createDefaultData();
        } else {
            registerEntityClasses();
        }
        logger.log(Level.INFO, "DatabaseServer.verifySchema done");
    }

    private void registerSerializers() {
        logger.log(Level.INFO, "DatabaseServer.registerSerializers started");
        OObjectSerializerContext serializerContext = new OObjectSerializerContext();
        serializerContext.bind(new TranslatedSerializer());
        serializerContext.bind(new ModuleTypeSerializer());
        serializerContext.bind(new ModuleSubTypeSerializer());
        serializerContext.bind(new UserStatusSerializer());
        serializerContext.bind(new CaseStatusSerializer());
        serializerContext.bind(new CaseTypeSerializer());
        serializerContext.bind(new TaskModelStatusSerializer());
        serializerContext.bind(new TaskModelOptionSerializer());
        serializerContext.bind(new TaskModelGateTypeSerializer());
        serializerContext.bind(new TaskModelTaskPrioritySerializer());
        serializerContext.bind(new ParameterTypeSerializer());
        serializerContext.bind(new ModelStatusSerializer());
        serializerContext.bind(new FieldCollectionTypeSerializer());
        serializerContext.bind(new FieldEditorTypeSerializer());
        serializerContext.bind(new PermissionSerializer());
        serializerContext.bind(new DocumentTypeSerializer());
        serializerContext.bind(new TabLayoutTypeSerializer());
        OObjectSerializerHelper.bindSerializerContext(null, serializerContext);
        logger.log(Level.INFO, "DatabaseServer.registerSerializers done");
    }

    private void createDefaultConfig() {
        logger.log(Level.INFO, "DatabaseServer.createDefaultConfig started");
        SystemAPI.get(User.getSystemUser(), null).saveParameter(new Parameter(User.TOKEN_EXPIRE_PERIOD, "90", Parameter.PARAM_TYPE.SYSTEM));
        logger.log(Level.INFO, "DatabaseServer.createDefaultConfig done");
    }

    private void createDefaultData() {
        logger.log(Level.INFO, "DatabaseServer.createDefaultData started");
        // default module templates
        DevelopmentAPI.get(User.getSystemUser(), null).saveModule(HybridbpmDefaultBuilder.createModuleTemplate("SimpleFormTemplate", "Simple Form Template", "CODE", Module.MODULE_TYPE.FORM, Module.MODULE_SUBTYPE.TEMPLATED_FORM));
        DevelopmentAPI.get(User.getSystemUser(), null).saveModule(HybridbpmDefaultBuilder.createModuleTemplate("TaskFormTemplate", "Task Form Template", "CODE", Module.MODULE_TYPE.FORM, Module.MODULE_SUBTYPE.TASK_FORM));
        DevelopmentAPI.get(User.getSystemUser(), null).saveModule(HybridbpmDefaultBuilder.createModuleTemplate("SimpleConnectorTemplate", "Simple Connector Template", "CODE", Module.MODULE_TYPE.CONNECTOR, null));

        // Demo processes, forms and charts
        DevelopmentAPI.get(User.getSystemUser(), null).saveModule(HybridbpmDefaultBuilder.createDemoData());
        DevelopmentAPI.get(User.getSystemUser(), null).saveModule(HybridbpmDefaultBuilder.createDemoProcess());
        DevelopmentAPI.get(User.getSystemUser(), null).saveModule(HybridbpmDefaultBuilder.createDemoForm());
        DevelopmentAPI.get(User.getSystemUser(), null).saveModule(HybridbpmDefaultBuilder.createDemoConnector());
        Module chart1 = DevelopmentAPI.get(User.getSystemUser(), null).saveModule(HybridbpmDefaultBuilder.createDemoChart1());
        Module chart2 = DevelopmentAPI.get(User.getSystemUser(), null).saveModule(HybridbpmDefaultBuilder.createDemoChart2());

        // default dashboard views
        ViewDefinition viewDefinition = DashboardAPI.get(User.getSystemUser(), null).saveViewDefinition(HybridbpmDefaultBuilder.createViewDefinition("dashboard", "Dashboard", "DASHBOARD", 0), null, false);
        TabDefinition tabDefinition = DashboardAPI.get(User.getSystemUser(), null).saveTabDefinition(HybridbpmDefaultBuilder.createTabDefinition(viewDefinition, "BPM", "DASHBOARD", 0, TabDefinition.LAYOUT_TYPE.GRID), null, false);
        
        List<PanelDefinition> panels = DashboardAPI.get(User.getSystemUser(), null).getPanelDefinitionsByTab(tabDefinition.getId().toString());
        PanelDefinition panel = panels.get(0);
        panel.setDefaultTitle("Tasks by status");
        panel.setModuleType(Module.MODULE_TYPE.CHART);
        panel.setModuleName(chart1.getName());
        DashboardAPI.get(User.getSystemUser(), null).savePanelDefinition(panel);
        
        panel = panels.get(1);
        panel.setDefaultTitle("Process Instances");
        panel.setModuleType(Module.MODULE_TYPE.CHART);
        panel.setModuleName(chart2.getName());
        DashboardAPI.get(User.getSystemUser(), null).savePanelDefinition(panel);

        logger.log(Level.INFO, "DatabaseServer.createDefaultData done");
    }

    private void createDefaultAccess() {
        logger.log(Level.INFO, "DatabaseServer.createDefaultAccess started");
        User administrator = new User();
        administrator.setFirstName(User.ADMINISTRATOR_FIRSTNAME);
        administrator.setLastName(User.ADMINISTRATOR_LASTNAME);
        administrator.setUsername(User.ADMINISTRATOR);
        administrator.setEmail("administrator@hybridbpm.org");
        administrator.setStatus(User.STATUS.ENABLED);
        administrator.setFirstVisibleHourOfDay(0);
        administrator.setLastVisibleHourOfDay(23);
        administrator.setPassword(HybridbpmCoreUtil.hashPassword(User.ADMINISTRATOR));
        objectDatabaseTx.activateOnCurrentThread();
        administrator = objectDatabaseTx.save(administrator);

        User user = new User();
        user.setFirstName("John");
        user.setLastName("Smith");
        user.setUsername("jsmith");
        user.setEmail("john.smith@hybridbpm.org");
        user.setStatus(User.STATUS.ENABLED);
        user.setFirstVisibleHourOfDay(0);
        user.setLastVisibleHourOfDay(23);
        user.setPassword(HybridbpmCoreUtil.hashPassword(User.ADMINISTRATOR));
        user = objectDatabaseTx.save(user);

        Role administratorRole = objectDatabaseTx.save(Role.create(Role.ADMINISTRATOR, Role.ADMINISTRATOR, Role.ADMINISTRATOR));
        Role developerRole = objectDatabaseTx.save(Role.create(Role.DEVELOPER, Role.DEVELOPER, Role.DEVELOPER));
        Role userRole = objectDatabaseTx.save(Role.create(Role.USER, Role.USER, Role.USER));
        Role managerRole = objectDatabaseTx.save(Role.create(Role.MANAGER, Role.MANAGER, Role.MANAGER));

        Group administratorGroup = objectDatabaseTx.save(Group.create(Group.ADMINISTRATORS, Group.ADMINISTRATORS, Group.ADMINISTRATORS));
        Group developerGroup = objectDatabaseTx.save(Group.create(Group.DEVELOPERS, Group.DEVELOPERS, Group.DEVELOPERS));
        Group userGroup = objectDatabaseTx.save(Group.create(Group.USERS, Group.USERS, Group.USERS));
        Group managerGroup = objectDatabaseTx.save(Group.create(Group.MANAGERS, Group.MANAGERS, Group.MANAGERS));
        objectDatabaseTx.commit();

        databaseDocumentTx.command(new OCommandSQL("CREATE EDGE RoleGroup FROM " + administratorRole.getId() + " TO " + administratorGroup.getId())).execute();
        databaseDocumentTx.command(new OCommandSQL("CREATE EDGE RoleGroup FROM " + developerRole.getId() + " TO " + developerGroup.getId())).execute();
        databaseDocumentTx.command(new OCommandSQL("CREATE EDGE RoleGroup FROM " + userRole.getId() + " TO " + userGroup.getId())).execute();
        databaseDocumentTx.command(new OCommandSQL("CREATE EDGE RoleGroup FROM " + managerRole.getId() + " TO " + managerGroup.getId())).execute();

        databaseDocumentTx.command(new OCommandSQL("CREATE EDGE UserGroup FROM " + administrator.getId() + " TO " + administratorGroup.getId())).execute();
        databaseDocumentTx.command(new OCommandSQL("CREATE EDGE UserGroup FROM " + administrator.getId() + " TO " + developerGroup.getId())).execute();
        databaseDocumentTx.command(new OCommandSQL("CREATE EDGE UserGroup FROM " + administrator.getId() + " TO " + userGroup.getId())).execute();
        databaseDocumentTx.command(new OCommandSQL("CREATE EDGE UserGroup FROM " + administrator.getId() + " TO " + managerGroup.getId())).execute();
        databaseDocumentTx.command(new OCommandSQL("CREATE EDGE UserGroup FROM " + user.getId() + " TO " + userGroup.getId())).execute();
        databaseDocumentTx.commit();
        logger.log(Level.INFO, "DatabaseServer.createDefaultAccess done");
    }

    private void createSchema() {
        logger.log(Level.INFO, "DatabaseServer.createSchema started");
        // BPM schema
        OrientVertexType case1 = graphNoTx.createVertexType("Case");
        case1.createProperty("modelName", OType.STRING);
        case1.createProperty("code", OType.STRING);
        case1.createProperty("title", OType.STRING);
        case1.createProperty("description", OType.STRING);
        case1.createProperty("initiator", OType.STRING);
        case1.createProperty("type", OType.STRING);
        case1.createProperty("status", OType.STRING);
        case1.createProperty("template", OType.STRING);
        case1.createProperty("updateDate", OType.DATETIME);
        case1.createProperty("startDate", OType.DATETIME);
        case1.createProperty("finishDate", OType.DATETIME);
        case1.createIndex("caseModelNameIdx", OClass.INDEX_TYPE.NOTUNIQUE, "modelName");
        case1.createIndex("caseStatusIdx", OClass.INDEX_TYPE.NOTUNIQUE, "status");
        case1.createIndex("caseTypeIdx", OClass.INDEX_TYPE.NOTUNIQUE, "type");
        case1.createIndex("caseInitiatorIdx", OClass.INDEX_TYPE.NOTUNIQUE, "initiator");
        case1.createIndex("caseCodeIdx", OClass.INDEX_TYPE.UNIQUE, "code");

        OrientVertexType task = graphNoTx.createVertexType("Task");
        task.createProperty("taskName", OType.STRING);
        task.createProperty("description", OType.STRING);
        task.createProperty("status", OType.STRING);
        task.createProperty("option", OType.STRING);
        task.createProperty("initiator", OType.STRING);
        task.createProperty("executor", OType.STRING);
        task.createProperty("taskTitle", OType.STRING);
        task.createProperty("processModelName", OType.STRING);
        task.createProperty("taskType", OType.STRING);
        task.createProperty("taskPriority", OType.STRING);
        task.createProperty("iteration", OType.INTEGER);
        task.createProperty("initial", OType.BOOLEAN);
        task.createProperty("assigned", OType.BOOLEAN);
        task.createProperty("question", OType.BOOLEAN);
        task.createProperty("createDate", OType.DATETIME);
        task.createProperty("updateDate", OType.DATETIME);
        task.createProperty("startDate", OType.DATETIME);
        task.createProperty("finishDate", OType.DATE);
        task.createProperty("scheduleStartDate", OType.DATETIME);
        task.createProperty("scheduleEndDate", OType.DATETIME);
        task.createProperty("dueDate", OType.DATETIME);
        task.createIndex("taskNameIdx", OClass.INDEX_TYPE.NOTUNIQUE, "taskName");
        task.createIndex("taskExecutorIdx", OClass.INDEX_TYPE.NOTUNIQUE, "executor");
        task.createIndex("taskInitiatorIdx", OClass.INDEX_TYPE.NOTUNIQUE, "initiator");
        task.createIndex("taskStatusIdx", OClass.INDEX_TYPE.NOTUNIQUE, "status");

        OrientEdgeType userTaskList = graphNoTx.createEdgeType("UserTaskList");

        OrientEdgeType processTaskList = graphNoTx.createEdgeType("ProcessTaskList");

        OrientEdgeType linked = graphNoTx.createEdgeType("Linked");
        linked.createProperty("createDate", OType.DATETIME);
        linked.createProperty("creator", OType.STRING);

        OrientEdgeType variable = graphNoTx.createEdgeType("Variable");
        variable.createProperty("name", OType.STRING);
        variable.createProperty("className", OType.STRING);
        variable.createProperty("updateDate", OType.DATETIME);

        OClass join = databaseDocumentTx.getMetadata().getSchema().createClass("TaskJoin");
        join.createProperty("taskName", OType.STRING);
        join.createProperty("caseId", OType.STRING);
        join.createProperty("iteration", OType.INTEGER);
        join.createIndex("joinUniqueIdx", OClass.INDEX_TYPE.UNIQUE, "taskName", "caseId", "iteration");
        join.createIndex("joinCaseIdIdx", OClass.INDEX_TYPE.NOTUNIQUE, "caseId");

        // ACL schema
        OrientVertexType rolex = graphNoTx.createVertexType("Role");
        rolex.createProperty("name", OType.STRING);
        rolex.createProperty("title", OType.STRING);
        rolex.createProperty("description", OType.STRING);
        rolex.createIndex("roleNameIndex", OClass.INDEX_TYPE.UNIQUE, "name");

        OrientVertexType group = graphNoTx.createVertexType("Group");
        group.createProperty("name", OType.STRING);
        group.createProperty("title", OType.STRING);
        group.createProperty("description", OType.STRING);
        group.createIndex("groupNameIndex", OClass.INDEX_TYPE.UNIQUE, "name");

        OrientVertexType user = graphNoTx.createVertexType("User");
        user.createProperty("firstName", OType.STRING);
        user.createProperty("lastName", OType.STRING);
        user.createProperty("email", OType.STRING);
        user.createProperty("username", OType.STRING);
        user.createProperty("password", OType.STRING);
        user.createProperty("token", OType.STRING);
        user.createProperty("tokenExpireDate", OType.DATETIME);
        user.createProperty("manager", OType.LINK, user);
        user.createProperty("locale", OType.STRING);
        user.createProperty("status", OType.STRING);
        user.createProperty("firstVisibleHourOfDay", OType.INTEGER);
        user.createProperty("lastVisibleHourOfDay", OType.INTEGER);
        user.createIndex("userNameIndex", OClass.INDEX_TYPE.UNIQUE, "username");
        user.createIndex("userFirstNameIndex", OClass.INDEX_TYPE.NOTUNIQUE, "firstName");
        user.createIndex("userLastNameIndex", OClass.INDEX_TYPE.NOTUNIQUE, "lastName");
        user.createIndex("userEmailIndex", OClass.INDEX_TYPE.UNIQUE, "email");

        OrientEdgeType roleGroup = graphNoTx.createEdgeType("RoleGroup");
        OrientEdgeType userGroup = graphNoTx.createEdgeType("UserGroup");

        OrientEdgeType permission = graphNoTx.createEdgeType("Permission");
        permission.createProperty("parameter", OType.STRING);
        permission.createProperty("permissions", OType.EMBEDDEDLIST);

        // discussion schema
        OrientVertexType category = graphNoTx.createVertexType("Category");
        category.createProperty("createDate", OType.DATETIME);
        category.createProperty("creator", OType.STRING);
        category.createProperty("title", OType.STRING);
        category.createProperty("description", OType.STRING);

        OrientVertexType comment = graphNoTx.createVertexType("Comment");
        comment.createProperty("createDate", OType.DATETIME);
        comment.createProperty("creator", OType.LINK, user);
        comment.createProperty("body", OType.STRING);
        comment.createProperty("parent", OType.LINK, comment);
        comment.createProperty("task", OType.LINK, task);
        comment.createProperty("case", OType.LINK, case1);

        // file schema
        OrientVertexType file = graphNoTx.createVertexType("File");
        file.createProperty("createDate", OType.DATETIME);
        file.createProperty("creator", OType.STRING);
        file.createProperty("name", OType.STRING);
        file.createProperty("fileName", OType.STRING);
        file.createProperty("mime", OType.STRING);
        file.createProperty("size", OType.INTEGER);
        file.createProperty("scope", OType.STRING);
        file.createProperty("case", OType.LINK, case1);
        file.createProperty("task", OType.LINK, task);
        file.createProperty("comment", OType.LINK, comment);
        file.createIndex("fileNameIndex", OClass.INDEX_TYPE.NOTUNIQUE, "name");
        file.createIndex("fileCaseIndex", OClass.INDEX_TYPE.NOTUNIQUE, "case");
        file.createIndex("fileTaskIndex", OClass.INDEX_TYPE.NOTUNIQUE, "task");
        file.createIndex("fileCommentIndex", OClass.INDEX_TYPE.NOTUNIQUE, "comment");

        OrientVertexType fileBody = graphNoTx.createVertexType("FileBody");
        fileBody.createProperty("file", OType.LINK, file);

        // document schema
        OrientVertexType document = graphNoTx.createVertexType("Document");
        document.createProperty("createDate", OType.DATETIME);
        document.createProperty("updateDate", OType.DATETIME);
        document.createProperty("creator", OType.STRING);
        document.createProperty("description", OType.STRING);
        document.createProperty("name", OType.STRING);
        document.createProperty("path", OType.STRING);
        document.createProperty("mime", OType.STRING);
        document.createProperty("size", OType.INTEGER);
        document.createProperty("type", OType.STRING);
        document.createProperty("parent", OType.LINK, document);
        document.createProperty("case", OType.LINK, case1);
        document.createIndex("documentNameIndex", OClass.INDEX_TYPE.NOTUNIQUE, "name");
        document.createIndex("documentPathIndex", OClass.INDEX_TYPE.UNIQUE, "path");

        OrientVertexType documentVersion = graphNoTx.createVertexType("DocumentVersion");
        documentVersion.createProperty("createDate", OType.DATETIME);
        documentVersion.createProperty("updateDate", OType.DATETIME);
        documentVersion.createProperty("creator", OType.STRING);
        documentVersion.createProperty("description", OType.STRING);
        documentVersion.createProperty("name", OType.STRING);
        documentVersion.createProperty("mime", OType.STRING);
        documentVersion.createProperty("size", OType.INTEGER);
        documentVersion.createProperty("documentVersion", OType.INTEGER);
        documentVersion.createProperty("document", OType.LINK, document);

        OrientVertexType mod = graphNoTx.createVertexType("Module");
        mod.createProperty("updateDate", OType.DATETIME);
        mod.createProperty("title", OType.STRING);
        mod.createProperty("icon", OType.STRING);
        mod.createProperty("name", OType.STRING);
        mod.createProperty("code", OType.STRING);
        mod.createProperty("model", OType.STRING);
        mod.createProperty("design", OType.STRING);
        mod.createProperty("templateName", OType.STRING);
        mod.createProperty("processName", OType.STRING);
        mod.createProperty("subType", OType.STRING);
        mod.createProperty("type", OType.STRING);
        mod.createProperty("configurable", OType.BOOLEAN);
        mod.createProperty("system", OType.BOOLEAN);
        mod.createProperty("publishable", OType.BOOLEAN);
        mod.createProperty("template", OType.BOOLEAN);
        mod.createIndex("modNameIndex", OClass.INDEX_TYPE.UNIQUE, "name");

        OrientVertexType view = graphNoTx.createVertexType("ViewDefinition");
        view.createProperty("title", OType.STRING);
        view.createProperty("url", OType.STRING);
        view.createProperty("icon", OType.STRING);
        view.createProperty("order", OType.INTEGER);
        view.createIndex("viewUrlIndex", OClass.INDEX_TYPE.UNIQUE, "url");

        OrientVertexType tab = graphNoTx.createVertexType("TabDefinition");
        tab.createProperty("title", OType.STRING);
        tab.createProperty("viewId", OType.LINK, view);
        tab.createProperty("icon", OType.STRING);
        tab.createProperty("order", OType.INTEGER);
        tab.createProperty("rows", OType.INTEGER);
        tab.createProperty("columns", OType.INTEGER);

        OrientVertexType panel = graphNoTx.createVertexType("PanelDefinition");
        panel.createProperty("title", OType.STRING);
        panel.createProperty("tabId", OType.LINK, tab);
        panel.createProperty("column", OType.INTEGER);
        panel.createProperty("row", OType.INTEGER);
        panel.createProperty("columns", OType.INTEGER);
        panel.createProperty("order", OType.INTEGER);
        panel.createProperty("moduleType", OType.STRING);
        panel.createProperty("moduleName", OType.STRING);
        panel.createProperty("parameters", OType.STRING);

        graphNoTx.shutdown();
        logger.log(Level.INFO, "DatabaseServer.createSchema done");
    }

    private void registerEntityClasses() {
        logger.log(Level.INFO, "DatabaseServer.registerEntityClasses started");
        objectDatabaseTx.getEntityManager().registerEntityClass(com.hybridbpm.core.data.Parameter.class);
        objectDatabaseTx.getEntityManager().registerEntityClass(com.hybridbpm.core.data.document.Document.class);
        objectDatabaseTx.getEntityManager().registerEntityClass(com.hybridbpm.core.data.document.DocumentVersion.class);
        objectDatabaseTx.getEntityManager().registerEntityClass(com.hybridbpm.model.Translated.class);
        objectDatabaseTx.getEntityManager().registerEntityClass(com.hybridbpm.core.data.access.Group.class);
        objectDatabaseTx.getEntityManager().registerEntityClass(com.hybridbpm.core.data.access.Role.class);
        objectDatabaseTx.getEntityManager().registerEntityClass(com.hybridbpm.core.data.access.RoleGroup.class);
        objectDatabaseTx.getEntityManager().registerEntityClass(com.hybridbpm.core.data.access.UserGroup.class);
        objectDatabaseTx.getEntityManager().registerEntityClass(com.hybridbpm.core.data.access.User.class);
        objectDatabaseTx.getEntityManager().registerEntityClass(com.hybridbpm.core.data.access.Permission.class);
        objectDatabaseTx.getEntityManager().registerEntityClass(com.hybridbpm.core.data.dashboard.PanelDefinition.class);
        objectDatabaseTx.getEntityManager().registerEntityClass(com.hybridbpm.core.data.dashboard.TabDefinition.class);
        objectDatabaseTx.getEntityManager().registerEntityClass(com.hybridbpm.core.data.dashboard.ViewDefinition.class);
        objectDatabaseTx.getEntityManager().registerEntityClass(com.hybridbpm.core.data.bpm.Case.class);
        objectDatabaseTx.getEntityManager().registerEntityClass(com.hybridbpm.core.data.bpm.Task.class);
        objectDatabaseTx.getEntityManager().registerEntityClass(com.hybridbpm.core.data.bpm.Variable.class);
        objectDatabaseTx.getEntityManager().registerEntityClass(com.hybridbpm.core.data.bpm.Comment.class);
        objectDatabaseTx.getEntityManager().registerEntityClass(com.hybridbpm.core.data.bpm.File.class);
        objectDatabaseTx.getEntityManager().registerEntityClass(com.hybridbpm.core.data.bpm.FileBody.class);
        objectDatabaseTx.getEntityManager().registerEntityClass(com.hybridbpm.core.data.development.Module.class);
        objectDatabaseTx.getEntityManager().registerEntityClass(com.hybridbpm.core.event.BpmEvent.class);
//        objectDatabaseTx.getEntityManager().registerEntityClass(com.hybridbpm.model.FieldModel.class);
        objectDatabaseTx.setAutomaticSchemaGeneration(true);
        logger.log(Level.INFO, "DatabaseServer.registerEntityClasses done");
    }

    private void createOrientdbStudio() {
        try {
            File wwwFile = new File(ORIENTDB_WWW_DIRECTORY);
            if (!wwwFile.exists()) {
                logger.info("DatabaseServer createOrientdbStudio start");
                wwwFile.mkdirs();
                final File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
                try (JarFile jar = new JarFile(jarFile)) {
                    final Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
                    while (entries.hasMoreElements()) {
                        JarEntry jarEntry = entries.nextElement();
                        final String name = jarEntry.getName();
                        if (name.startsWith(ORIENTDB_WWW_DIRECTORY + "/")) { //filter according to the path
                            File newFile = new File(name);
                            if (jarEntry.isDirectory()) {
                                newFile.mkdirs();
                            } else {
                                try (FileOutputStream fos = new FileOutputStream(newFile)) {
                                    fos.write(HybridbpmCoreUtil.createBytesFromSource("/" + name));
                                }
                            }
                        }
                    }
                }
                logger.info("DatabaseServer createOrientdbStudio done");
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}
