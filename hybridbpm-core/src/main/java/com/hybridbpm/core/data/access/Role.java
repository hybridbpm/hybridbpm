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
import java.util.Objects;
import javax.persistence.Id;

/**
 *
 * @author Marat Gubaidullin
 */
public class Role implements Serializable {
    
    public static final String ADMINISTRATOR = "Administrator";
    public static final String DEVELOPER = "Developer";
    public static final String USER = "User";
    public static final String MANAGER = "Manager";

    @Id
    protected Object id;
    private String name;
    private Translated title;
    private Translated description;

    public Role() {
    }
    
    public static Role create(String name, String title, String description) {
        Role role = new Role();
        role.setName(name);
        role.setTitle(new Translated(title));
        role.setDescription(new Translated(description));
        return role;
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
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Role other = (Role) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
    
    

}
