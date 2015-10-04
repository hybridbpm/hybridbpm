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

import com.hybridbpm.core.*;
import com.hybridbpm.core.data.access.Permission;
import com.hybridbpm.core.data.access.Role;
import com.hybridbpm.core.data.dashboard.PanelDefinition;
import com.hybridbpm.core.data.access.User;
import com.hybridbpm.core.data.dashboard.TabDefinition;
import com.hybridbpm.core.data.dashboard.ViewDefinition;
import com.hybridbpm.core.event.DashboardEvent;
import com.hybridbpm.core.util.HybridbpmCoreUtil;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marat Gubaidullin
 */
public class DashboardAPI extends AbstractAPI {

    private static final Logger logger = Logger.getLogger(DashboardAPI.class.getSimpleName());
    private String sessionId;

    private DashboardAPI(User user, String sessionId) {
        super(user, sessionId);
    }

    public static DashboardAPI get(User user, String sessionId) {
        return new DashboardAPI(user, sessionId);
    }

    public List<ViewDefinition> getViewDefinitions() throws RuntimeException {
        String request = "SELECT FROM ViewDefinition WHERE @rid IN "
                + "(SELECT in FROM PERMISSION WHERE in.@class = 'ViewDefinition' AND permissions IN ('VIEW') "
                + "AND out in (SELECT FROM Role WHERE @rid IN (SELECT out('UserGroup').in('RoleGroup') FROM User WHERE @rid = ?)))"
                + " ORDER BY order ";
        logger.log(Level.FINEST, "getViewDefinitions");
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            List<ViewDefinition> result = database.query(new OSQLSynchQuery<ViewDefinition>(request), user.getId());
            return detachList(result);
        }
    }

    public ViewDefinition getViewDefinitionByUrl(String url) throws RuntimeException {
        logger.log(Level.FINEST, "getViewDefinitionByUrl");
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            List<ViewDefinition> list = database.query(new OSQLSynchQuery<ViewDefinition>("SELECT * FROM ViewDefinition WHERE url = ?", 1), url);
            if (!list.isEmpty()) {
                ViewDefinition result = list.get(0);
                result = detach(result);
                return result;
            } else {
                return null;
            }
        }
    }

    public ViewDefinition getViewDefinitionById(Object id) throws RuntimeException {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            ViewDefinition result = database.load(new ORecordId(id.toString()));
            return detach(result);
        }
    }

    public TabDefinition getTabDefinitionById(Object id) throws RuntimeException {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            TabDefinition result = database.load(new ORecordId(id.toString()));
            return detach(result);
        }
    }

    public void deleteViewDefinition(Object id, boolean notify) throws RuntimeException {
        ViewDefinition vd = null;
        String viewUrl = null;
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            vd = database.load(new ORecordId(id.toString()));
            if (vd != null) {
                viewUrl = vd.getUrl();
                List<TabDefinition> tabs = database.query(new OSQLSynchQuery<TabDefinition>("SELECT * FROM TabDefinition WHERE viewId = ?"), id);
                for (TabDefinition td : tabs) {
                    database.command(new OCommandSQL("DELETE VERTEX PanelDefinition WHERE tabId = " + td.getId())).execute();
                }
                database.command(new OCommandSQL("DELETE VERTEX TabDefinition WHERE viewId = " + id)).execute();
            }
            database.delete(new ORecordId(id.toString()));
            database.commit();
        }
        if (notify && vd != null && vd instanceof ViewDefinition) {
            HazelcastServer.getDashboardEventTopic().publish(DashboardEvent.createViewDeleteEvent(sessionId, viewUrl));
        }
    }

    public void deleteTabDefinition(Object id, boolean notify) throws RuntimeException {
        TabDefinition tab = null;
        String viewUrl = null;
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            tab = database.load(new ORecordId(id.toString()));
            if (tab != null) {
                ViewDefinition view = database.load(new ORecordId(tab.getViewId().getId().toString()));
                viewUrl = view.getUrl();
                database.command(new OCommandSQL("DELETE VERTEX PanelDefinition WHERE tabId = " + id)).execute();
            }
            database.delete(new ORecordId(id.toString()));
            database.commit();
        }
        if (notify && tab != null && tab instanceof TabDefinition) {
            HazelcastServer.getDashboardEventTopic().publish(DashboardEvent.createViewChangeEvent(sessionId, viewUrl));
        }
    }

    public ViewDefinition saveViewDefinition(ViewDefinition viewDefinition, List<Permission> permissions, boolean notify) {
        String viewUrl = null;
        boolean newView = viewDefinition.getId() == null;
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            viewDefinition = database.save(viewDefinition);
            viewUrl = viewDefinition.getUrl();
            database.command(new OCommandSQL("DELETE EDGE Permission WHERE in = " + viewDefinition.getId().toString())).execute();
            database.commit();
            viewDefinition = detach(viewDefinition);
        }

        if (permissions == null || permissions.isEmpty()) {
            permissions = getDefaultPermissions();
        }
        for (Permission permission : permissions) {
            savePermission(viewDefinition.getId(), permission);
        }
//        if (newView) { // create empty tab
//            TabDefinition tabDefinition = TabDefinition.createDefaultVertical();
//            tabDefinition.setViewId(viewDefinition);
//            saveTabDefinition(tabDefinition, permissions, false);
//        }
        if (notify) {
            HazelcastServer.getDashboardEventTopic().publish(DashboardEvent.createViewChangeEvent(sessionId, viewUrl));
        }
        return viewDefinition;
    }

    public void savePermission(Object id, Permission permission) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            if (permission.getId() == null) {
                Vertex doc = getOrientGraph().getVertex(id);
                Vertex role = getOrientGraph().getVertex(permission.getOut().getId());
                Edge perm = getOrientGraph().addEdge("class:Permission", role, doc, null);
                perm.setProperty("permissions", permission.getPermissions());
            } else {
                getOrientGraph().getEdge(permission.getId()).setProperty("permissions", permission.getPermissions());
            }
        }
    }

    public List<Permission> getViewPermissions(String viewId) {
        String request = "SELECT FROM Permission WHERE in = ?";
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            List<Permission> list = database.query(new OSQLSynchQuery<>(request), viewId);
            return detachList(list);
        }
    }

    public TabDefinition saveTabDefinition(TabDefinition tabDefinition, List<Permission> permissions, boolean notify) {
        String tabId = null;
        String viewUrl = null;
        boolean newTab = tabDefinition.getId() == null;
        try (ODatabaseDocumentTx database = getODatabaseDocumentTx()) {
            ODocument view = database.getRecord(new ORecordId(tabDefinition.getViewId().getId().toString()).getRecord());
            ODocument tab;
            if (tabDefinition.getId() == null) {
                tab = new ODocument("TabDefinition");
                tab.field("viewId", view);
                switch (tabDefinition.getLayout()) {
                    case HORIZONTAL:
                        tab.field("columns", 1);
                        tab.field("rows", 1);
                        database.save(tab);
                        break;
                    case VERTICAL:
                        tab.field("columns", 1);
                        tab.field("rows", 1);
                        database.save(tab);
                        break;
                    case GRID:
                        tab.field("columns", 2);
                        tab.field("rows", 2);
                        break;
                }
            } else {
                tab = database.getRecord(new ORecordId(tabDefinition.getId().toString()).getRecord());
            }
            tab.field("title", HybridbpmCoreUtil.objectToJson(tabDefinition.getTitle()));
            tab.field("icon", tabDefinition.getIcon());
            tab.field("layout", tabDefinition.getLayout());
            tab.field("order", tabDefinition.getOrder());

            tab = database.save(tab);
            viewUrl = view.field("url");
            database.command(new OCommandSQL("DELETE EDGE Permission WHERE in = " + tab.getIdentity().toString())).execute();
            database.commit();
            tabId = tab.getIdentity().toString();
            tabDefinition = getOObjectDatabaseTx().load(new ORecordId(tabId));
            tabDefinition = detach(tabDefinition);
        }

        if (permissions == null || permissions.isEmpty()) {
            permissions = getDefaultPermissions();
        }

        for (Permission permission : permissions) {
            savePermission(tabId, permission);
        }
        if (newTab) { // create empty tab panels
            if (Objects.equals(tabDefinition.getLayout(), TabDefinition.LAYOUT_TYPE.GRID)) {
                List<PanelDefinition> list = new ArrayList<>(4);
                PanelDefinition panelDefinition = PanelDefinition.createDefault(0, 0);
                panelDefinition.setTabId(tabDefinition);
                list.add(panelDefinition);
                panelDefinition = PanelDefinition.createDefault(0, 1);
                panelDefinition.setTabId(tabDefinition);
                list.add(panelDefinition);
                panelDefinition = PanelDefinition.createDefault(1, 0);
                panelDefinition.setTabId(tabDefinition);
                list.add(panelDefinition);
                panelDefinition = PanelDefinition.createDefault(1, 1);
                panelDefinition.setTabId(tabDefinition);
                list.add(panelDefinition);
                savePanelDefinitions(list);
            } else {
                PanelDefinition panelDefinition = PanelDefinition.createDefault();
                panelDefinition.setTabId(tabDefinition);
                savePanelDefinition(panelDefinition);
            }
        }
        if (notify) {
            HazelcastServer.getDashboardEventTopic().publish(DashboardEvent.createViewChangeEvent(sessionId, viewUrl));
        }
        return tabDefinition;
    }

    public List<Permission> getTabPermissions(String viewId) {
        String request = "SELECT FROM Permission WHERE in = ?";
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            List<Permission> list = database.query(new OSQLSynchQuery<Permission>(request), viewId);
            return detachList(list);
        }
    }

    public List<Permission> getDefaultPermissions() {
        List<Permission> list = new ArrayList<>();
        list.add(Permission.create(null, AccessAPI.get(user, sessionId).getRole(Role.ADMINISTRATOR), Permission.PERMISSION.VIEW));
        list.add(Permission.create(null, AccessAPI.get(user, sessionId).getRole(Role.DEVELOPER), Permission.PERMISSION.VIEW));
        return list;
    }

    public Integer getNextViewOrder() {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            List<ODocument> maxOrder = database.query(new OSQLSynchQuery<ODocument>("SELECT MAX(order) FROM ViewDefinition", 1));
            if (!maxOrder.isEmpty()) {
                return Integer.parseInt(maxOrder.get(0).field("MAX").toString()) + 1;
            } else {
                return 0;
            }
        }
    }

    public Integer getNextTabOrder(String viewId) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            List<ODocument> maxOrder = database.query(new OSQLSynchQuery<ODocument>("SELECT MAX(order) FROM TabDefinition WHERE viewId = ?", 1), viewId);
            if (!maxOrder.isEmpty()) {
                return Integer.parseInt(maxOrder.get(0).field("MAX").toString()) + 1;
            } else {
                return 0;
            }
        }
    }

//    public List<PanelDefinition> getPanelDefinitionList() {
//        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
//            List<PanelDefinition> result = database.query(new OSQLSynchQuery<PanelDefinition>("SELECT * FROM PanelDefinition"));
//            return detachList(result);
//        }
//    }
    public List<TabDefinition> getTabDefinitionByView(String viewId) {
        String request = "SELECT FROM TabDefinition WHERE viewId = ? AND @rid IN "
                + "(SELECT in FROM PERMISSION WHERE in.@class = 'TabDefinition' AND permissions IN ('VIEW') "
                + "AND out in (SELECT FROM Role WHERE @rid IN (SELECT out('UserGroup').in('RoleGroup') FROM User WHERE @rid = ?)))"
                + " ORDER BY order ";
        logger.log(Level.FINEST, "getTabDefinitionByView");
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            List<TabDefinition> result = database.query(new OSQLSynchQuery<>(request), viewId, user.getId());
            return detachList(result);
        }
    }

    public List<PanelDefinition> getPanelDefinitionsByTab(Object tabId) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            List<PanelDefinition> result = database.query(new OSQLSynchQuery<>("SELECT * FROM PanelDefinition WHERE tabId = ? ORDER BY order"), tabId);
            return detachList(result);
        }
    }

    public PanelDefinition getPanelDefinitionsById(Object id) throws RuntimeException {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            PanelDefinition result = database.load(new ORecordId(id.toString()));
            return detach(result);
        }
    }

    public void savePanelDefinition(PanelDefinition panelDefinition) {
        try (ODatabaseDocumentTx database = getODatabaseDocumentTx()) {
            savePanelDefinitionInternal(panelDefinition, database);
            database.commit();
        }
    }

    public void savePanelDefinitions(List<PanelDefinition> panelDefinitions) {
        try (ODatabaseDocumentTx database = getODatabaseDocumentTx()) {
            if (panelDefinitions != null && !panelDefinitions.isEmpty()) {
                for (PanelDefinition panelDefinition : panelDefinitions) {
                    savePanelDefinitionInternal(panelDefinition, database);
                }
                TabDefinition tabDefinition = panelDefinitions.get(0).getTabId();
                ODocument tab = database.getRecord(new ORecordId(tabDefinition.getId().toString()).getRecord());
                switch (tabDefinition.getLayout()) {
                    case HORIZONTAL:
                        tab.field("columns", panelDefinitions.size());
                        tab.field("rows", 0);
                        database.save(tab);
                        break;
                    case VERTICAL:
                        tab.field("columns", 0);
                        tab.field("rows", panelDefinitions.size());
                        database.save(tab);
                        break;
                    case GRID:
                        break;
                }
                database.commit();
            }
        }
    }

    private void savePanelDefinitionInternal(PanelDefinition panelDefinition, ODatabaseDocumentTx database) {
        ODocument panel;
        if (panelDefinition.getId() == null) {
            ODocument tab = database.getRecord(new ORecordId(panelDefinition.getTabId().getId().toString()).getRecord());
            panel = new ODocument("PanelDefinition");
            panel.field("tabId", tab);
            panel.field("title", HybridbpmCoreUtil.objectToJson(panelDefinition.getTitle()));
            panel.field("column", panelDefinition.getColumn());
            panel.field("row", panelDefinition.getRow());
            panel.field("moduleType", panelDefinition.getModuleType());
            panel.field("moduleName", panelDefinition.getModuleName());
            panel.field("order", panelDefinition.getOrder());
        } else {
            panel = database.getRecord(new ORecordId(panelDefinition.getId().toString()).getRecord());
            panel.field("title", HybridbpmCoreUtil.objectToJson(panelDefinition.getTitle()));
            panel.field("column", panelDefinition.getColumn());
            panel.field("row", panelDefinition.getRow());
            panel.field("moduleType", panelDefinition.getModuleType());
            panel.field("moduleName", panelDefinition.getModuleName());
            panel.field("order", panelDefinition.getOrder());
        }
        database.save(panel);
    }

    public void deletePanelDefinition(Object id, boolean notify) throws RuntimeException {
        PanelDefinition panel = null;
        String viewUrl = null;
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            panel = database.load(new ORecordId(id.toString()));
            if (panel != null) {
                TabDefinition tab = detach(database.load(new ORecordId(panel.getTabId().getId().toString())));
                ViewDefinition view = database.load(new ORecordId(tab.getViewId().getId().toString()));
                viewUrl = view.getUrl();
                if (Objects.equals(tab.getLayout(), TabDefinition.LAYOUT_TYPE.VERTICAL) || Objects.equals(tab.getLayout(), TabDefinition.LAYOUT_TYPE.HORIZONTAL)){
                    database.command(new OCommandSQL("UPDATE PanelDefinition INCREMENT order = -1 WHERE tabId = " + tab.getId() + " AND order > " + panel.getOrder())).execute();
                }
            }
            database.delete(new ORecordId(id.toString()));
            database.commit();
        }
        if (notify && viewUrl != null) {
            HazelcastServer.getDashboardEventTopic().publish(DashboardEvent.createViewChangeEvent(sessionId, viewUrl));
        }
    }

    public TabDefinition addToVerticalTabDefinition(TabDefinition tabDefinition, int order) throws RuntimeException {
        try (ODatabaseDocumentTx database = getODatabaseDocumentTx()) {
            database.command(new OCommandSQL("UPDATE PanelDefinition INCREMENT order = 1 WHERE tabId = " + tabDefinition.getId() + " AND order >= " + order)).execute();

            PanelDefinition newPanelDef = PanelDefinition.createDefault();
            newPanelDef.setTabId(tabDefinition);
            newPanelDef.setOrder(order);
            savePanelDefinitionInternal(newPanelDef, database);

            database.commit();
            tabDefinition = getOObjectDatabaseTx().load(new ORecordId(tabDefinition.getId().toString()));
            return detach(tabDefinition);
        }
    }

    public TabDefinition addToHorizontalTabDefinition(TabDefinition tabDefinition, int order) throws RuntimeException {
        try (ODatabaseDocumentTx database = getODatabaseDocumentTx()) {
            database.command(new OCommandSQL("UPDATE PanelDefinition INCREMENT order = 1 WHERE tabId = " + tabDefinition.getId() + " AND order >= " + order)).execute();

            PanelDefinition newPanelDef = PanelDefinition.createDefault();
            newPanelDef.setTabId(tabDefinition);
            newPanelDef.setOrder(order);
            savePanelDefinitionInternal(newPanelDef, database);

            database.commit();
            tabDefinition = getOObjectDatabaseTx().load(new ORecordId(tabDefinition.getId().toString()));
            return detach(tabDefinition);
        }
    }

    public TabDefinition addRowToGridTabDefinition(TabDefinition tabDefinition, int row) throws RuntimeException {
        try (ODatabaseDocumentTx database = getODatabaseDocumentTx()) {
            database.command(new OCommandSQL("UPDATE TabDefinition INCREMENT rows = 1 WHERE @rid = " + tabDefinition.getId())).execute();
            database.command(new OCommandSQL("UPDATE PanelDefinition INCREMENT row = 1 WHERE tabId = " + tabDefinition.getId() + " AND row >= " + row)).execute();

            for (int i = 0; i < tabDefinition.getColumns(); i++) {
                PanelDefinition newPanelDef = PanelDefinition.createDefault();
                newPanelDef.setTabId(tabDefinition);
                newPanelDef.setRow(row);
                newPanelDef.setColumn(i);
                savePanelDefinitionInternal(newPanelDef, database);
            }
            database.commit();
            tabDefinition = getOObjectDatabaseTx().load(new ORecordId(tabDefinition.getId().toString()));
            return detach(tabDefinition);
        }
    }

    public TabDefinition addColumnToGridTabDefinition(TabDefinition tabDefinition, int column) throws RuntimeException {
        try (ODatabaseDocumentTx database = getODatabaseDocumentTx()) {
            database.command(new OCommandSQL("UPDATE TabDefinition INCREMENT columns = 1 WHERE @rid = " + tabDefinition.getId())).execute();
            database.command(new OCommandSQL("UPDATE PanelDefinition INCREMENT column = 1 WHERE tabId = " + tabDefinition.getId() + " AND column >= " + column)).execute();

            for (int i = 0; i < tabDefinition.getRows(); i++) {
                PanelDefinition newPanelDef = PanelDefinition.createDefault();
                newPanelDef.setTabId(tabDefinition);
                newPanelDef.setColumn(column);
                newPanelDef.setRow(i);
                savePanelDefinitionInternal(newPanelDef, database);
            }
            database.commit();
            tabDefinition = getOObjectDatabaseTx().load(new ORecordId(tabDefinition.getId().toString()));
            return detach(tabDefinition);
        }
    }

}
