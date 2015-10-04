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

import com.hybridbpm.model.Translated;
import java.io.Serializable;
import javax.persistence.Id;

/**
 *
 * @author Marat Gubaidullin
 */
public class Group implements Serializable {

    @Id
    protected Object id;

    public static final String ADMINISTRATORS = "Administrators";
    public static final String DEVELOPERS = "Developers";
    public static final String USERS = "Users";
    public static final String MANAGERS = "Managers";

    private String name;
    private Translated title;
    private Translated description;

    public Group() {
    }

    public static Group create(String name, String title, String description) {
        Group group = new Group();
        group.setName(name);
        group.setTitle(new Translated(title));
        group.setDescription(new Translated(description));
        return group;
    }
    
    public Object getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Translated getTitle() {
        return title;
    }

    public void setTitle(Translated title) {
        this.title = title;
    }

    public Translated getDescription() {
        return description;
    }

    public void setDescription(Translated description) {
        this.description = description;
    }

}
