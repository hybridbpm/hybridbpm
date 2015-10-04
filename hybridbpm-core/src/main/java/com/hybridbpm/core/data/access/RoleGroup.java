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

import java.io.Serializable;
import javax.persistence.Id;

/**
 *
 * @author Marat Gubaidullin
 */
public class RoleGroup implements Serializable {

    @Id
    protected Object id;

    private Role out;
    private Group in;

    public RoleGroup() {
    }

    
    public Object getId() {
        return id;
    }
    
    public Role getOut() {
        return out;
    }

    public void setOut(Role role) {
        this.out = role;
    }

    public Group getIn() {
        return in;
    }

    public void setIn(Group group) {
        this.in = group;
    }

}
