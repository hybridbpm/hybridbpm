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
package com.hybridbpm.core.data.sync;

import com.hybridbpm.core.data.access.Role;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mgubaidullin
 */
public class CouchbaseUser {

    private String name;
    private String password;
    private List<String> admin_channels = new ArrayList<>(1);
    private List<String> admin_roles = new ArrayList<>(1);

    public CouchbaseUser() {
    }

    public CouchbaseUser(String name, String password) {
        this.name = name;
        this.password = password;
        this.admin_channels.add(name);
        this.admin_roles.add(Role.USER);
    }

    public CouchbaseUser(String name) {
        this.name = name;
        this.admin_channels.add(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getAdmin_channels() {
        return admin_channels;
    }

    public void setAdmin_channels(List<String> admin_channels) {
        this.admin_channels = admin_channels;
    }

    public List<String> getAdmin_roles() {
        return admin_roles;
    }

    public void setAdmin_roles(List<String> admin_roles) {
        this.admin_roles = admin_roles;
    }

}

