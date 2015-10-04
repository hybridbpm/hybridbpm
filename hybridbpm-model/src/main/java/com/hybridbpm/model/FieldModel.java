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
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Marat Gubaidullin
 */
public class FieldModel implements Serializable {

    private String name;
    private String description;
    private String className;
    private String defaultValue;
    private COLLECTION_TYPE collection;
    private EDITOR_TYPE editor;

    public enum COLLECTION_TYPE {

        NONE, LIST //, MAP
    };

    public enum EDITOR_TYPE {

        TEXT_FIELD("TextField", "v-text-field"),
        TEXT_AREA("TextArea", "v-text-area"),
        COMBOBOX("ComboBox", "v-combo-box"),
        DATE_FIELD("PopupDateField", "v-popup-date-field"),
        OPTION_GROUP("OptionGroup", "v-option-group"),
        CHECK_BOX("CheckBox", "v-check-box");

        private final String component;
        private final String design;

        private EDITOR_TYPE(String component, String design) {
            this.component = component;
            this.design = design;
        }

        public String getComponent() {
            return component;
        }

        public String getDesign() {
            return design;
        }

    };
    
    public static enum CLASS {

        STRING(String.class.getCanonicalName(), String.class.getSimpleName(), "new String()"),
        INTEGER(Integer.class.getCanonicalName(), Integer.class.getSimpleName(), "0" ),
        BIG_DECIMAL(BigDecimal.class.getCanonicalName(), BigDecimal.class.getSimpleName(), "BigDecimal.ZERO"),
        DATE(Date.class.getCanonicalName(), Date.class.getSimpleName(), "new Date()"),
        BOOLEAN(Boolean.class.getCanonicalName(), Boolean.class.getSimpleName(), "Boolean.FALSE");

        private final String canonicalName;
        private final String simpleName;
        private final String defaultValue;

        private CLASS(String canonicalName, String simpleName, String defaultValue) {
            this.canonicalName = canonicalName;
            this.simpleName = simpleName;
            this.defaultValue = defaultValue;
        }

        public String getCanonicalName() {
            return canonicalName;
        }

        public String getSimpleName() {
            return simpleName;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

    }

    public FieldModel() {
    }

    public FieldModel(String name, String description, String className, String defaultValue, COLLECTION_TYPE collection, EDITOR_TYPE editor) {
        this.name = name;
        this.description = description;
        this.className = className;
        this.collection = collection;
        this.defaultValue = defaultValue;
        this.editor = editor;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
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

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public COLLECTION_TYPE getCollection() {
        return collection;
    }

    public void setCollection(COLLECTION_TYPE collection) {
        this.collection = collection;
    }

    public EDITOR_TYPE getEditor() {
        return editor;
    }

    public void setEditor(EDITOR_TYPE editor) {
        this.editor = editor;
    }

}
