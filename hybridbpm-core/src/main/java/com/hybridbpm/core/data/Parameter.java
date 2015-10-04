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
package com.hybridbpm.core.data;

import java.util.Date;
import javax.persistence.Id;

/**
 *
 * @author Marat Gubaidullin
 */
public class Parameter  {

    @Id
    protected Object id;
    protected Date updateDate;

    private String name;
    private String value;
    private PARAM_TYPE type;

    public enum PARAM_TYPE {

        SYSTEM, CONTEXT
    };

    public Parameter() {
    }

    public Parameter(String name, String value, PARAM_TYPE type) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.updateDate = new Date();
    }

    public Object getId() {
        return id;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public PARAM_TYPE getType() {
        return type;
    }

    public void setType(PARAM_TYPE type) {
        this.type = type;
    }
    
}
