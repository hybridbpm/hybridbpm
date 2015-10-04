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
package com.hybridbpm.core.util;

import com.hybridbpm.core.api.DevelopmentAPI;
import com.hybridbpm.core.data.development.Module;
import com.hybridbpm.model.DataModel;
import com.hybridbpm.model.FieldModel;
import com.hybridbpm.model.FieldModel.CLASS;
import java.util.Objects;

/**
 *
 * @author Marat Gubaidullin
 */
public class FieldModelUtil {

    

    public static boolean isSimple(String className) {
        for (CLASS c : CLASS.values()) {
            if (c.getCanonicalName().equals(className)) {
                return true;
            }
        }
        return false;
    }

    public static CLASS getCLASSByCanonicalName(String className) {
        for (CLASS c : CLASS.values()) {
            if (c.getCanonicalName().equals(className)) {
                return c;
            }
        }
        return null;
    }

    private static String getBeanFieldGroupTemplate(String className, String name) {
        return "    private BeanFieldGroup<" + className + "> " + name + "BeanFieldGroup = new BeanFieldGroup<>(" + className + ".class);";
    }

    private static String getBeanItemContainerTemplate(String className, String name) {
        return "    private BeanItemContainer<" + className + "> " + name + "BeanItemContainer = new BeanItemContainer<>(" + className + ".class);";
    }

    private static String getObjectPropertyTemplate(String className, String name) {
        String simpleClassname = getCLASSByCanonicalName(className).getSimpleName();
        String value = getCLASSByCanonicalName(className).getDefaultValue();
        return "    private ObjectProperty<" + simpleClassname + "> " + name + "Property = new ObjectProperty<>(" + value + ");";
    }

    private static String getComponentTemplate(FieldModel fieldModel, String name) {
        String component = fieldModel.getEditor().getComponent();
        return "    private " + component + " " + name + component + ";";
    }

    private static String getTableTemplate(String name) {
        return "    private Table " + name + "Table;";
    }

    private static String getBeanComponentTemplate(FieldModel fieldModel, String name) {
        StringBuilder result = new StringBuilder();
        Module module = DevelopmentAPI.get(null, null).getModuleByName(fieldModel.getClassName());
        DataModel dataModel = HybridbpmCoreUtil.jsonToObject(module.getModel(), DataModel.class);
        for (FieldModel field : dataModel.getFields()) {
            String line = getFormComponentTemplate(field, name);
            result.append(line).append(line.isEmpty() ? "" : System.lineSeparator());
        }
        return result.toString().replace(System.lineSeparator() + System.lineSeparator(), System.lineSeparator());
    }

    private static String getBeanBindingTemplate(FieldModel fieldModel, String name) {
        StringBuilder result = new StringBuilder();
        Module module = DevelopmentAPI.get(null, null).getModuleByName(fieldModel.getClassName());
        DataModel dataModel = HybridbpmCoreUtil.jsonToObject(module.getModel(), DataModel.class);
        for (FieldModel field : dataModel.getFields()) {
            if (isSimple(field.getClassName())) {
                result.append("        ").append(name).append("BeanFieldGroup.bind(").append(createName(name, field.getName())).append(field.getEditor().getComponent()).append(", \"").append(field.getName()).append("\");").append(System.lineSeparator());
            }
        }
        return result.toString();
    }

    private static String getPropertyBindingTemplate(FieldModel fieldModel, String name) {
        String component = fieldModel.getEditor().getComponent();
        return "        " + name + component + ".setPropertyDataSource(" + name + "Property);";
    }

    private static String getTableBindingTemplate(String name) {
        StringBuilder result = new StringBuilder();
        result.append("        ").append(name).append("Table.setContainerDataSource(").append(name).append("BeanItemContainer);").append(System.lineSeparator());
        return result.toString();
    }

    private static String getPropertyCommitTemplate(FieldModel fieldModel, String name) {
        String component = fieldModel.getEditor().getComponent();
        return "        " + name + component + ".commit();";
    }

    private static String getBeanCommitTemplate(String name) {
        return "        " + name + "BeanFieldGroup.commit();";
    }

    private static String getPropertyDesignElement(FieldModel fieldModel, String name, String caption) {
        String design = fieldModel.getEditor().getDesign();
        String component = fieldModel.getEditor().getComponent();
        if (Objects.equals(fieldModel.getEditor(), FieldModel.EDITOR_TYPE.TEXT_FIELD)) {
            return "        <" + design + " _id=\"" + name + component + "\" caption=\"" + capitalize(caption) + "\" null-representation=\"\" />";
        } else {
            return "        <" + design + " _id=\"" + name + component + "\" caption=\"" + capitalize(caption) + "\" />";
        }
    }

    private static String getTableElement(String name) {
        return "        <v-table" + " _id=\"" + name + "Table \" caption=\"" + name + "\" />";
    }

    private static String getBeanDesignTemplate(FieldModel fieldModel, String name) {
        StringBuilder result = new StringBuilder();
        result.append("         <v-vertical-layout caption=\"").append(capitalize(name)).append("\" style-name=\"card\" margin=\"\" spacing=\"\">").append(System.lineSeparator());
        Module module = DevelopmentAPI.get(null, null).getModuleByName(fieldModel.getClassName());
        DataModel dataModel = HybridbpmCoreUtil.jsonToObject(module.getModel(), DataModel.class);
        for (FieldModel field : dataModel.getFields()) {
            String line = getFormDesignElement(field, name);
            if (!line.isEmpty()) {
                result.append(line).append(System.lineSeparator());
            }
        }
        return result.append("        </v-vertical-layout>").toString();
    }

    public static String getConnectorParameterTemplate(FieldModel fieldModel) {
        if (fieldModel.getCollection().equals(FieldModel.COLLECTION_TYPE.NONE)) {
            return "    private " + fieldModel.getClassName() + " " + fieldModel.getName() + ";";
        } else if (fieldModel.getCollection().equals(FieldModel.COLLECTION_TYPE.LIST)) {
            return "    private List<" + fieldModel.getClassName() + "> " + fieldModel.getName() + ";";
        } else {
            return "";
        }
    }

    public static String getFormDatasourceTemplate(FieldModel fieldModel, String parentName) {
        if (isSimple(fieldModel.getClassName()) && fieldModel.getCollection().equals(FieldModel.COLLECTION_TYPE.NONE)) {
            return getObjectPropertyTemplate(fieldModel.getClassName(), createName(parentName, fieldModel.getName()));
        } else if (!isSimple(fieldModel.getClassName()) && fieldModel.getCollection().equals(FieldModel.COLLECTION_TYPE.NONE)) {
            return getBeanFieldGroupTemplate(fieldModel.getClassName(), createName(parentName, fieldModel.getName()));
        } else {
            return getBeanItemContainerTemplate(fieldModel.getClassName(), createName(parentName, fieldModel.getName()));
        }
    }

    public static String getFormComponentTemplate(FieldModel fieldModel, String parentName) {
        if (isSimple(fieldModel.getClassName()) && fieldModel.getCollection().equals(FieldModel.COLLECTION_TYPE.NONE)) {
            return getComponentTemplate(fieldModel, createName(parentName, fieldModel.getName()));
        } else if (!isSimple(fieldModel.getClassName()) && fieldModel.getCollection().equals(FieldModel.COLLECTION_TYPE.NONE)) {
            return getBeanComponentTemplate(fieldModel, createName(parentName, fieldModel.getName()));
        } else {
            return getTableTemplate(createName(parentName, createName(parentName, fieldModel.getName())));
        }
    }

    public static String getFormBindingTemplate(FieldModel fieldModel, String parentName) {
        if (isSimple(fieldModel.getClassName()) && fieldModel.getCollection().equals(FieldModel.COLLECTION_TYPE.NONE)) {
            return getPropertyBindingTemplate(fieldModel, createName(parentName, fieldModel.getName()));
        } else if (!isSimple(fieldModel.getClassName()) && fieldModel.getCollection().equals(FieldModel.COLLECTION_TYPE.NONE)) {
            return getBeanBindingTemplate(fieldModel, createName(parentName, fieldModel.getName()));
        } else {
            return getTableBindingTemplate(createName(parentName, fieldModel.getName()));
        }
    }

    public static String getFormCommitTemplate(FieldModel fieldModel, String parentName) {
        if (isSimple(fieldModel.getClassName()) && fieldModel.getCollection().equals(FieldModel.COLLECTION_TYPE.NONE)) {
            return getPropertyCommitTemplate(fieldModel, createName(parentName, fieldModel.getName()));
        } else if (!isSimple(fieldModel.getClassName()) && fieldModel.getCollection().equals(FieldModel.COLLECTION_TYPE.NONE)) {
            return getBeanCommitTemplate(createName(parentName, fieldModel.getName()));
        } else {
            return "";
        }
    }

    public static String getFormLoadTemplate(FieldModel fieldModel, String parentName) {
        if (isSimple(fieldModel.getClassName()) && fieldModel.getCollection().equals(FieldModel.COLLECTION_TYPE.NONE)) {
            return "";
        } else if (!isSimple(fieldModel.getClassName()) && fieldModel.getCollection().equals(FieldModel.COLLECTION_TYPE.NONE)) {
            return "";
        } else {
            return "";
        }
    }

    public static String getFormDesignElement(FieldModel fieldModel, String parentName) {
        if (isSimple(fieldModel.getClassName()) && fieldModel.getCollection().equals(FieldModel.COLLECTION_TYPE.NONE)) {
            return getPropertyDesignElement(fieldModel, createName(parentName, fieldModel.getName()), fieldModel.getName());
        } else if (!isSimple(fieldModel.getClassName()) && fieldModel.getCollection().equals(FieldModel.COLLECTION_TYPE.NONE)) {
            return getBeanDesignTemplate(fieldModel, createName(parentName, fieldModel.getName()));
        } else {
            return getTableElement(createName(parentName, fieldModel.getName()));
        }
    }

    private static String capitalize(String s) {
        if (s.length() == 0) {
            return s;
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private static String decapitalize(String s) {
        if (s.length() == 0) {
            return s;
        }
        return s.substring(0, 1).toLowerCase() + s.substring(1);
    }

    private static String createName(String prefix, String name) {
        return prefix == null ? name : prefix + capitalize(name);
    }

}
