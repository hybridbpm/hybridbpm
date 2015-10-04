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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Marat Gubaidullin
 */
public class MobileFormComponent extends AbstractModel {

    private FieldModel fieldModel;
    private List<MobileFormComponent> components;
    private boolean container;
    private String value;
    private String id;

    public MobileFormComponent() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public FieldModel getFieldModel() {
        return fieldModel;
    }

    public void setFieldModel(FieldModel fieldModel) {
        this.fieldModel = fieldModel;
    }
    
    public List<MobileFormComponent> getComponents() {
        if (components == null) {
            components = new ArrayList<>();
        }
        return components;
    }

    public void setComponents(List<MobileFormComponent> components) {
        this.components = components;
    }

    public boolean isContainer() {
        return container;
    }

    public void setContainer(boolean container) {
        this.container = container;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
