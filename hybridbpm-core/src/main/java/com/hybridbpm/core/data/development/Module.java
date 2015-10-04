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
package com.hybridbpm.core.data.development;

import com.hybridbpm.model.Translated;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Id;

/**
 *
 * @author Marat Gubaidullin
 */
public class Module implements Serializable {

    @Id
    protected Object id;
    protected Date updateDate;
    protected Translated title;
    private String icon;
    private String name;
    private String code;
    private String model;
    private String design;
    private Boolean configurable;
    private Boolean system;
    private Boolean publishable;
    private Boolean template = false;
    private String templateName;
    private String processName;
    private MODULE_SUBTYPE subType;
    private MODULE_TYPE type;
    public static final String DEFAULT_ICON = "CODE";

    public Module() {
    }

    public Module(String name, String title, MODULE_TYPE type) {
        this.name = name;
        this.title = new Translated(title);
        this.icon = DEFAULT_ICON;
        this.type = type;
        this.configurable = true;
        this.system = false;
        this.publishable = type.equals(MODULE_TYPE.FORM);
    }

    public enum MODULE_TYPE {

        FORM("DESKTOP", "groovy", "html"),
        MOBILE("MOBILE", null, "template"),
        CHART("PIE", null, "template"),
        PROCESS("GEARS", null, null),
        SCSS("CSS3", null, "scss"),
        DATA("DATABASE", "groovy", null),
        CONNECTOR("EXCHANGE", "groovy", null);

        private final String codeExt;
        private final String designExt;
        private final String icon;
        private final String modelExt = "json";

        private MODULE_TYPE(String icon, String codeExt, String designExt) {
            this.icon = icon;
            this.codeExt = codeExt;
            this.designExt = designExt;
        }

        public String getIcon() {
            return icon;
        }

        public String getCodeExt() {
            return codeExt;
        }

        public String getDesignExt() {
            return designExt;
        }

        public String getModelExt() {
            return modelExt;
        }

    }

    public enum MODULE_SUBTYPE {

        TASK_FORM(MODULE_TYPE.FORM),
        TEMPLATED_FORM(MODULE_TYPE.FORM);

        private final MODULE_TYPE type;

        private MODULE_SUBTYPE(MODULE_TYPE type) {
            this.type = type;
        }

        public MODULE_TYPE getType() {
            return type;
        }
        
        public static List<MODULE_SUBTYPE> getSubtypes(MODULE_TYPE moduleType){
            List<MODULE_SUBTYPE> result = new ArrayList<>();
            for (MODULE_SUBTYPE moduleSubtype : values()){
                if (moduleSubtype.getType().equals(moduleType)){
                    result.add(moduleSubtype);
                }
            }
            return result;
        }
        
    }

    public String getIcon() {
        return icon != null ? icon : DEFAULT_ICON;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getConfigurable() {
        return configurable;
    }

    public void setConfigurable(Boolean configurable) {
        this.configurable = configurable;
    }

    public Boolean getSystem() {
        return system;
    }

    public void setSystem(Boolean system) {
        this.system = system;
    }

    public MODULE_TYPE getType() {
        return type;
    }

    public void setType(MODULE_TYPE type) {
        this.type = type;
    }

    public Boolean getPublishable() {
        return publishable;
    }

    public void setPublishable(Boolean publishable) {
        this.publishable = publishable;
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

    public Translated getTitle() {
        return title;
    }

    public void setTitle(Translated title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDesign() {
        return design;
    }

    public void setDesign(String design) {
        this.design = design;
    }

    public Boolean getTemplate() {
        return template;
    }

    public void setTemplate(Boolean template) {
        this.template = template;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public MODULE_SUBTYPE getSubType() {
        return subType;
    }

    public void setSubType(MODULE_SUBTYPE subType) {
        this.subType = subType;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }
    
}