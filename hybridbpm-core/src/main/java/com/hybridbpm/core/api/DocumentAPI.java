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

import com.hybridbpm.core.data.access.Permission;
import com.hybridbpm.core.data.access.Role;
import com.hybridbpm.core.data.access.User;
import com.hybridbpm.core.data.document.Document;
import com.hybridbpm.core.data.document.DocumentVersion;
import com.hybridbpm.core.util.HybridbpmCoreUtil;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.record.impl.ORecordBytes;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

/**
 *
 * @author Marat Gubaidullin
 */
public class DocumentAPI extends AbstractAPI {

    private static final Logger logger = Logger.getLogger(DocumentAPI.class.getSimpleName());

    private DocumentAPI(User user, String sessionId) {
        super(user, sessionId);
    }

    public static DocumentAPI get(User user, String sessionId) {
        return new DocumentAPI(user, sessionId);
    }

    public Map<Document, List<Permission.PERMISSION>> getMyDocuments(String parentId) {
        String documentRequest = parentId != null
                ? "SELECT FROM Document WHERE @rid IN "
                + "(SELECT in FROM PERMISSION WHERE in.@class = 'Document' AND permissions IN ('VIEW') "
                + "AND out in (SELECT FROM Role WHERE @rid IN (SELECT out('UserGroup').in('RoleGroup') FROM User WHERE @rid = " + user.getId() + "))) "
                + " AND parent = " + parentId
                : "SELECT FROM Document WHERE @rid IN "
                + "(SELECT in FROM PERMISSION WHERE in.@class = 'Document' AND permissions IN ('VIEW') "
                + "AND out in (SELECT FROM Role WHERE @rid IN (SELECT out('UserGroup').in('RoleGroup') FROM User WHERE @rid = " + user.getId() + ")))"
                 + " AND parent IS NULL ";
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            List<Document> documents = database.query(new OSQLSynchQuery<Document>(documentRequest));
            documents = detachList(documents);
            Map<Document, List<Permission.PERMISSION>> result = new HashMap<>(documents.size());
            for (Document doc : documents) {
                Edge edge = getOrientGraph().getVertex(doc.getId()).getEdges(Direction.IN, "Permission").iterator().next();
                Permission p = getOObjectDatabaseTx().load(new ORecordId(edge.getId().toString()));
                result.put(doc, p.getPermissions());
            }
            return result;
        }
    }

    public List<Document> getMyDocumentBreadcumbs(String documentId) {
        List<Document> result = new ArrayList<>();
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            Document doc = database.load(new ORecordId(documentId));
            result.add(detach(doc));
            while (doc.getParent() != null){
                doc = database.load(new ORecordId(doc.getParent().getId().toString()));
                result.add(result.size()-1, detach(doc));
            }
        }
        return detachList(result);
    }

    public List<Permission.PERMISSION> getMyDocumentPermissions(Document document) {
        return getMyDocumentPermissions(document.getId().toString());
    }

    public List<Permission.PERMISSION> getMyDocumentPermissions(String documentId) {
        String request = "SELECT FROM Permission WHERE in = ? AND out IN (SELECT out('UserGroup').in('RoleGroup') from User WHERE username = ?)";
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            List<Permission> list = database.query(new OSQLSynchQuery<Permission>(request), documentId, user.getUsername());
            list = detachList(list);
            List<Permission.PERMISSION> result = new ArrayList<>(list.size());
            for (Permission pi : list) {
                for (Permission.PERMISSION p : pi.getPermissions()) {
                    result.add(p);
                }
            }
            return result;
        }
    }

    public List<Permission> getDocumentPermissions(Document document) {
        return getDocumentPermissions(document.getId().toString());
    }

    public List<Permission> getDocumentPermissions(String documentId) {
        String request = "SELECT FROM Permission WHERE in = ?";
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            List<Permission> list = database.query(new OSQLSynchQuery<Permission>(request), documentId);
            return detachList(list);
        }
    }

    public List<DocumentVersion> getDocumentVersions(String documentId) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            List<DocumentVersion> list = database.query(new OSQLSynchQuery<DocumentVersion>("SELECT  FROM DocumentVersion WHERE document = " + documentId));
            return detachList(list);
        }
    }

    public Document getDocumentById(Object id, boolean withBody) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            Document result = database.load(new ORecordId(id.toString()));
            result = detach(result);
            if (withBody) {
                List<DocumentVersion> list = database.query(new OSQLSynchQuery<DocumentVersion>("SELECT FROM DocumentVersion WHERE document = " + id + " ORDER BY documentVersion DESC", 1));
                DocumentVersion documentVersion = list.get(0);
                result.setBody(documentVersion.getBody().toStream());
            }
            return result;
        }
    }

    public Document getDocumentByVersion(Object id, int v) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            Document result = database.load(new ORecordId(id.toString()));
            result = detach(result);
            List<DocumentVersion> list = database.query(new OSQLSynchQuery<DocumentVersion>("SELECT  FROM DocumentVersion WHERE document = " + id + " AND documentVersion = " + v, 1));
            DocumentVersion documentVersion = list.get(0);
            result.setBody(documentVersion.getBody().toStream());
            return result;
        }
    }

    public byte[] getDocumentBodyByVersionId(Object id) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            DocumentVersion documentVersion = database.load(new ORecordId(id.toString()));
            return documentVersion.getBody().toStream();
        }
    }

    public void removeDocument(String id) throws RuntimeException {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            Document doc = database.load(new ORecordId(id));
            removeOneDocument(doc);
            database.commit();
        }
    }

    private void removeOneDocument(Document doc) throws RuntimeException {
        if (Objects.equals(doc.getType(), Document.TYPE.FOLDER)) {
            removeSubfolders(doc);
        }
        getOObjectDatabaseTx().command(new OCommandSQL("DELETE VERTEX DocumentVersion WHERE document = " + doc.getId().toString())).execute();
        getOObjectDatabaseTx().command(new OCommandSQL("DELETE VERTEX Document WHERE @rid = " + doc.getId().toString())).execute();
        getOObjectDatabaseTx().commit();
    }

    private void removeSubfolders(Document doc) throws RuntimeException {
        List<Document> docs = getOObjectDatabaseTx().query(new OSQLSynchQuery<Document>("SELECT  FROM Document WHERE parent = " + doc.getId()));
        for (Document d : docs) {
            removeOneDocument(d);
        }
    }

    public void removeDocumentPermission(String id) throws RuntimeException {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            database.delete(new ORecordId(id));
            database.commit();
        }
    }

    public void saveDocumentPermission(Document document, Permission permission) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            if (permission.getId() == null) {
                Vertex doc = getOrientGraph().getVertex(document.getId());
                Vertex role = getOrientGraph().getVertex(permission.getOut().getId());
                Edge perm = getOrientGraph().addEdge("class:Permission", role, doc, null);
                perm.setProperty("permissions", permission.getPermissions());
            } else {
                getOrientGraph().getEdge(permission.getId()).setProperty("permissions", permission.getPermissions());
            }
        }
    }

    public void saveDocuments(List<Document> documents) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            for (Document document : documents) {
                internalSaveDocument(document);
            }
            database.commit();
        }
    }

    public Document saveDocument(Document document) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            document = internalSaveDocument(document);
            database.commit();
            return detach(document);
        }
    }

    private Document internalSaveDocument(Document document) {
        boolean newDocument = false;
        ODocument file;
        if (document.getId() == null) {
            newDocument = true;
            file = new ODocument("Document");
            file.field("name", document.getName());
            file.field("description", HybridbpmCoreUtil.objectToJson(document.getDescription()));
            file.field("createDate", new Date());
            file.field("updateDate", new Date());
            file.field("creator", user.getUsername());
            file.field("mime", document.getMime());
            file.field("size", document.getSize());
            file.field("type", document.getType().name());
            if (document.getParent() != null) {
                ODocument parent = getODatabaseDocumentTx().getRecord(new ORecordId(document.getParent().getId().toString()).getRecord());
                file.field("parent", parent);
                file.field("path", parent.field("path") + System.getProperty("file.separator") + file.field("name"));
                file.save();
                inheritPermissions(parent, file);
            } else {
                file.field("path", System.getProperty("file.separator") + document.getName());
                file.save();
                defaultPermissions(file);
            }
        } else {
            file = getODatabaseDocumentTx().getRecord(new ORecordId(document.getId().toString()).getRecord());
        }

        if (Objects.equals(document.getType(), Document.TYPE.FILE) && document.getBody() != null) {
            List<ODocument> docs = getODatabaseDocumentTx().query(new OSQLSynchQuery<ODocument>("SELECT COUNT(1) FROM DocumentVersion WHERE document = '" + file.getIdentity() + "' ", 1));
            Integer documentVersion = docs.size() > 0 ? Integer.parseInt(docs.get(0).field("COUNT").toString()) + 1 : 1;
            byte[] body = document.getBody();
            ODocument fileVersion = new ODocument("DocumentVersion");
            fileVersion.field("name", document.getName());
            fileVersion.field("description", HybridbpmCoreUtil.objectToJson(document.getDescription()));
            fileVersion.field("document", file);
            fileVersion.field("createDate", new Date());
            fileVersion.field("updateDate", new Date());
            fileVersion.field("creator", user.getUsername());
            fileVersion.field("mime", document.getMime());
            fileVersion.field("size", document.getSize());
            fileVersion.field("documentVersion", documentVersion);
            ORecordBytes record = new ORecordBytes(body);
            fileVersion.field("body", record);
            fileVersion.save();
        }

        document = getOObjectDatabaseTx().load(file.getIdentity());
        return detach(document);
    }

    private void inheritPermissions(ODocument parent, ODocument doc) {
        Vertex parentV = getOrientGraph().getVertex(parent.getIdentity());
        Vertex docV = getOrientGraph().getVertex(doc.getIdentity());
        Iterable<Edge> permissions = parentV.getEdges(Direction.IN, "Permission");
        for (Edge permission : permissions) {
            Vertex role = permission.getVertex(Direction.OUT);
            Edge perm = getOrientGraph().addEdge("class:Permission", role, docV, null);
            perm.setProperty("permissions", permission.getProperty("permissions"));
        }
    }

    private void defaultPermissions(ODocument doc) {
        List<Permission.PERMISSION> permissions = new ArrayList<>();
        for (Permission.PERMISSION permission : Permission.PERMISSION.getPermissionsForClass(Document.class)) {
            permissions.add(permission);
        }
        Vertex role = getOrientGraph().getVertex(getRole(Role.ADMINISTRATOR).getId());
        Vertex docV = getOrientGraph().getVertex(doc.getIdentity());
        Edge perm = getOrientGraph().addEdge("class:Permission", role, docV, null);
        perm.setProperty("permissions", permissions);
    }

    protected Map<String, Document> createSubfolders(Document parent, List<String> subFolders) {
        Map<String, Document> result = new HashMap<>(subFolders.size());
        for (String subFolder : subFolders) {
            Document folder = internalSaveDocument(Document.createFolder(subFolder, "", parent));
            result.put(subFolder, folder);
            parent = folder.getParent();
        }
        return detachMap(result);
    }

}
