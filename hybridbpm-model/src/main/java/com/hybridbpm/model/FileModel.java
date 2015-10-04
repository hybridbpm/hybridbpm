/* 
 * Copyright (c) 2011-2015 Marat Gubaidullin. 
 * 
 * This file is part of HYBRIDBPM.
 * 
 * HybridBPM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * HybridBPM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with HybridBPM.  If not, see <http ://www.gnu.org/licenses/>.
 */
package com.hybridbpm.model;

import java.io.Serializable;

/**
 *
 * @author Marat Gubaidullin
 */
public class FileModel implements Serializable {

    private String name;
    private String description;
    private Boolean multiple;

    public FileModel() {
    }

    public FileModel(String name, String description) {
        this(name, description, false);
    }
    
    public FileModel(String name, String description, boolean multiple) {
        this.name = name;
        this.description = description;
        this.multiple = multiple;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getMultiple() {
        return multiple;
    }

    public void setMultiple(Boolean multiple) {
        this.multiple = multiple;
    }
}
