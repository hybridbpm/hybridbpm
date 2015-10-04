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
import com.hybridbpm.core.data.access.User;
import com.hybridbpm.core.data.bpm.Comment;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Marat Gubaidullin
 */
public class CommentAPI extends AbstractAPI {

    private static final Logger logger = Logger.getLogger(CommentAPI.class.getSimpleName());

    private CommentAPI(User user, String sessionId) {
        super(user, sessionId);
    }

    public static CommentAPI get(User user, String sessionId) {
        return new CommentAPI(user, sessionId);
    }

    public List<Comment> getMyComments(String categoryId) {
        String request = "SELECT FROM Comment WHERE category.@rid = " + categoryId;
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            List<Comment> posts = database.query(new OSQLSynchQuery<Comment>(request));
            return detachList(posts);
        }
    }

    public List<Comment> getMyCommentsByParent(String parentId) {
        String request = "SELECT * FROM Comment WHERE parent = ? ORDER BY createDate asc";
        try (ODatabaseDocumentTx database = getODatabaseDocumentTx()) {
            List<ODocument> docs = database.query(new OSQLSynchQuery<Comment>(request), parentId);
            List<Comment> comments = new ArrayList<>(docs.size());
            for (ODocument doc : docs) {
                Comment comment = oDocumentToComment(doc);
                comments.add(comment);
            }
            return comments;
        }
    }

    public List<Comment> getCommentsByCaseId(String caseId) {
        String request = "SELECT FROM Comment WHERE case = ? ORDER BY createDate asc";
        try (ODatabaseDocumentTx database = getODatabaseDocumentTx()) {
            List<ODocument> docs = database.query(new OSQLSynchQuery<Comment>(request), caseId);
            List<Comment> comments = new ArrayList<>(docs.size());
            for (ODocument doc : docs) {
                Comment comment = oDocumentToComment(doc);
                comments.add(comment);
            }
            return comments;
        }
    }

    public List<Comment> getRootCommentsByCaseId(String caseId) {
        String request = "SELECT FROM Comment WHERE case = ? AND parent IS NULL ORDER BY createDate asc";
        try (ODatabaseDocumentTx database = getODatabaseDocumentTx()) {
            List<ODocument> docs = database.query(new OSQLSynchQuery<Comment>(request), caseId);
            List<Comment> comments = new ArrayList<>(docs.size());
            for (ODocument doc : docs) {
                Comment comment = oDocumentToComment(doc);
                comments.add(comment);
            }
            return comments;
        }
    }

    private List<Permission.PERMISSION> getPermissions(List<Permission> permissions, String categoryId) {
        List<Permission.PERMISSION> result = new ArrayList<>();
        for (Permission dp : permissions) {
//            if (dp.getClassId().equals(categoryId)) {
            result.addAll(dp.getPermissions());
//            }
        }
        return result;
    }

    private Comment oDocumentToComment(ODocument doc) {
        Comment comment = new Comment();
        comment.setId(doc.getIdentity().toString());
        comment.setBody(doc.field("body").toString());
        comment.setParent(doc.field("parent") != null ? ((OIdentifiable) doc.field("parent")).getIdentity().toString() : null);
        comment.setTask(doc.field("task") != null ? ((OIdentifiable) doc.field("task")).getIdentity().toString() : null);
        comment.setCase(doc.field("case") != null ? ((OIdentifiable) doc.field("case")).getIdentity().toString() : null);
        comment.setCreator(doc.field("creator") != null ? ((OIdentifiable) doc.field("creator")).getIdentity().toString() : null);
        comment.setCreateDate(doc.field("createDate") != null ? (Date) doc.field("createDate", Date.class) : null);
        return comment;
    }

    public Comment getCommentById(String id) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            OIdentifiable rec = database.load(new ORecordId(id));
            ODocument doc = rec.getRecord();
            return oDocumentToComment(doc);
        }
    }

    public Comment saveComment(Comment comment) {
        try (ODatabaseDocumentTx database = getODatabaseDocumentTx()) {
            ODocument doc = new ODocument("Comment");
            doc.field("body", comment.getBody());
            doc.field("parent", comment.getParent() != null ? new ORecordId(comment.getParent()) : null);
            doc.field("case", comment.getCase() != null ? new ORecordId(comment.getCase()) : null);
            doc.field("task", comment.getTask() != null ? new ORecordId(comment.getTask()) : null);
            doc.field("createDate", new Date());
            doc.field("creator", user.getId());
            doc = doc.save();
            database.commit();
            return oDocumentToComment(doc);
        }
    }
}
