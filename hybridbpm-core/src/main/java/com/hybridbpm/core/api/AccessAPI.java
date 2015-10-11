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
import com.hybridbpm.core.data.access.Group;
import com.hybridbpm.core.data.access.RoleGroup;
import com.hybridbpm.core.data.access.Role;
import com.hybridbpm.core.data.access.UserGroup;
import com.hybridbpm.core.data.access.User;
import com.hybridbpm.core.data.dashboard.PanelDefinition;
import com.hybridbpm.core.data.dashboard.TabDefinition;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.record.impl.ORecordBytes;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.orientechnologies.orient.object.iterator.OObjectIteratorClass;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marat Gubaidullin
 */
public class AccessAPI extends AbstractAPI {

    private static final Logger logger = Logger.getLogger(AccessAPI.class.getSimpleName());

    public User login(String username, String password) throws RuntimeException {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            List<User> list = database.query(new OSQLSynchQuery<User>(
                    "SELECT * FROM User WHERE username ='" + username + "' AND password = '" + HybridbpmCoreUtil.hashPassword(password) + "'", 1));
            if (!list.isEmpty()) {
                User result = list.get(0);
                result = database.detachAll(result, true);
                return result;
            } else {
                throw new RuntimeException("Username and/or password incorrect!");
            }
        }
    }

    private AccessAPI(User user, String sessionId) {
        super(user, sessionId);
    }

    public static AccessAPI get(User user, String sessionId) {
        return new AccessAPI(user, sessionId);
    }

    public List<Role> getAllRoles() {
        List<Role> result = new ArrayList<>();
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            OObjectIteratorClass<Role> list = database.browseClass(Role.class);
            for (Role role : list) {
                role = database.detachAll(role, true);
                result.add(role);
            }
        }
        return result;
    }

    public List<Group> getAllGroups() {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            OObjectIteratorClass<Group> list = database.browseClass(Group.class);
            List<Group> result = new ArrayList<>();
            for (Group group : list) {
                group = database.detachAll(group, true);
                result.add(group);
            }
            return result;
        }
    }

    public List<User> getAllUsers() {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            OObjectIteratorClass<User> list = database.browseClass(User.class);
            List<User> result = new ArrayList<>();
            for (User u : list) {
                u = database.detachAll(u, true);
                result.add(u);
            }
            return result;
        }
    }

    public List<User> findUsersByName(String name) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            Map<String, Object> params = new HashMap<>();
            params.put("param", "%" + (name != null ? name.toLowerCase() : "") + "%");
            List<User> list = database.query(
                    new OSQLSynchQuery<>("SELECT * FROM User WHERE  username.toLowerCase() LIKE :param OR firstName.toLowerCase() LIKE :param OR lastName.toLowerCase() LIKE :param ORDER BY username "),
                    params);
            return detachList(list);
        }
    }

    public User getUserByUserName(String userName) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            List<User> list = database.query(new OSQLSynchQuery<>("SELECT * FROM User WHERE  username = ? "), userName);
            return (list != null && !list.isEmpty()) ? detach(list.get(0)) : null;
        }
    }

    public User getUserByUserToken(String token) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            List<User> list = database.query(new OSQLSynchQuery<>("SELECT * FROM User WHERE  token = ? "), token);
            return (list != null && !list.isEmpty()) ? detach(list.get(0)) : null;
        }
    }

    public List<String> findUserNamesByName(String name) {
        List<User> list = findUsersByName(name);
        List<String> result = new ArrayList<>(list.size());
        for (User u : list) {
            result.add(u.getUsername());
        }
        return result;
    }

    public List<UserGroup> getAllUserGroups() {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            OObjectIteratorClass<UserGroup> list = database.browseClass(UserGroup.class);
            List<UserGroup> result = new ArrayList<>();
            for (UserGroup userGroup : list) {
                userGroup = database.detachAll(userGroup, true);
                result.add(userGroup);
            }
            return result;
        }
    }

    public List<RoleGroup> getAllRoleGroups() {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            OObjectIteratorClass<RoleGroup> list = database.browseClass(RoleGroup.class);
            List<RoleGroup> result = new ArrayList<>();
            for (RoleGroup roleGroup : list) {
                roleGroup = database.detachAll(roleGroup, true);
                result.add(roleGroup);
            }
            return result;
        }
    }

    public User getUserById(String id) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            User result = database.load(new ORecordId(id));
            return detach(result);
        }
    }

    public void removeInstance(String id) throws RuntimeException {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            database.delete(new ORecordId((String) id));
            database.commit();
        }
    }

    public boolean isAdministrator() {
        return hasRole(Role.ADMINISTRATOR);
    }

    public boolean isDeveloper() {
        return hasRole(Role.DEVELOPER);
    }

    public boolean isManager() {
        return hasRole(Role.MANAGER);
    }

    public boolean isUser() {
        return hasRole(Role.USER);
    }

    private boolean hasRole(String role) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            List<ODocument> docs = database.query(new OSQLSynchQuery<ODocument>(
                    "SELECT COUNT(1) FROM User WHERE username = '" + user.getUsername() + "' AND out('UserGroup').in('RoleGroup').name CONTAINS '" + role + "'", 1));
            Integer count = docs.size() > 0 ? Integer.parseInt(docs.get(0).field("COUNT").toString()) : 0;
            return (count > 0 ? Boolean.TRUE : Boolean.FALSE);
        }
    }

    public Role saveRole(Role role) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            role = database.save(role);
            database.commit();
            return detach(role);
        }
    }

    public User saveUser(User user, byte[] image) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            if (image != null) {
                ORecordBytes record = new ORecordBytes(image);
                record.save();
                user.setImage(record);
            }
            user = database.save(user);
            database.commit();
            return detach(user);
        }
    }

    public void setUserToken(User user, String token) throws RuntimeException {
        String period = SystemAPI.get(null, null).getSystemParameter(User.TOKEN_EXPIRE_PERIOD).getValue();
        try (ODatabaseDocumentTx database = getODatabaseDocumentTx()) {
            LocalDateTime tokenExpireDate = LocalDateTime.now();
            tokenExpireDate = tokenExpireDate.plusDays(Long.parseLong(period));

            database.command(
                    new OCommandSQL(
                            "UPDATE User SET token = ?, tokenExpireDate = ?  WHERE @rid = ?"))
                    .execute(token, HybridbpmCoreUtil.toDate(tokenExpireDate), user.getId());
        }
    }

    public Group saveGroup(Group group) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            group = database.save(group);
            database.commit();
            return detach(group);
        }
    }

    public void addRoleGroup(String roleId, String groupId) {
        try (ODatabaseDocumentTx database = getODatabaseDocumentTx()) {
            database.command(new OCommandSQL("CREATE EDGE RoleGroup FROM " + roleId + " TO " + groupId)).execute();
            database.commit();
        }
    }

    public void addUserGroup(String userId, String groupId) {
        try (ODatabaseDocumentTx database = getODatabaseDocumentTx()) {
            database.command(new OCommandSQL("CREATE EDGE UserGroup FROM " + userId + " TO " + groupId)).execute();
            database.commit();
        }
    }

    public UserGroup saveUserGroup(UserGroup userGroup) {
        try (OObjectDatabaseTx database = getOObjectDatabaseTx()) {
            userGroup = database.save(userGroup);
            database.commit();
            return detach(userGroup);
        }
    }

    public List<String> getUserRoles(String username) {
        List<String> result = new ArrayList<>();
        try (ODatabaseDocumentTx database = getODatabaseDocumentTx()) {
            List<ODocument> roles = database.query(new OSQLSynchQuery<ODocument>(
                    "SELECT FROM Role WHERE @rid IN (SELECT out('UserGroup').in('RoleGroup') FROM User WHERE username = '" + username + "')"));
            for (ODocument role : roles) {
                result.add(role.field("name").toString());
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
        return result;
    }

}
