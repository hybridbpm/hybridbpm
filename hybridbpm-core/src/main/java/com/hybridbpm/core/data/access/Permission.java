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
package com.hybridbpm.core.data.access;

import com.hybridbpm.core.data.dashboard.ViewDefinition;
import com.hybridbpm.core.data.development.Module;
import com.hybridbpm.core.data.document.Document;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.Id;

/**
 *
 * @author Marat Gubaidullin
 */
public class Permission  {

    @Id
    protected Object id;
    private String parameter;
    private Role out;
    private List<PERMISSION> permissions;

    public enum PERMISSION {

        START(Module.class),
        VIEW(Document.class, ViewDefinition.class),
        ADD_FILE(Document.class),
        ADD_FOLDER(Document.class),
        EDIT(Document.class),
        DELETE(Document.class),
        PERMISSIONS(Document.class);

        private final List<Class> classes = new ArrayList<>();

        private PERMISSION(Class... classes) {
            this.classes.addAll(Arrays.asList(classes));
        }

        public static List<PERMISSION> getPermissionsForClass(Class clazz) {
            List<PERMISSION> result = new ArrayList<>();
            for (PERMISSION p : PERMISSION.values()) {
                if (p.classes.contains(clazz)) {
                    result.add(p);
                }
            }
            return result;
        }
    };

    public Permission() {
    }
    
    public static Permission create(String parameter, Role role, PERMISSION... permissions){
        Permission permission = new Permission();
        permission.setOut(role);
        permission.addPermissions(permissions);
        permission.setParameter(parameter);
        return permission;
    }
    
    public Object getId() {
        return id;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public Role getOut() {
        return out;
    }

    public void setOut(Role role) {
        this.out = role;
    }

    public List<PERMISSION> getPermissions() {
        if (permissions == null) {
            permissions = new ArrayList<>();
        }
        return permissions;
    }

    public void setPermissions(List<PERMISSION> permissions) {
        this.permissions = permissions;
    }

    public void addPermission(PERMISSION permission) {
        this.getPermissions().add(permission);
    }

    public Permission add(PERMISSION permission) {
        this.getPermissions().add(permission);
        return this;
    }
    
    public void addPermissions(PERMISSION... permissions) {
        this.getPermissions().addAll(Arrays.asList(permissions));
    }

}
