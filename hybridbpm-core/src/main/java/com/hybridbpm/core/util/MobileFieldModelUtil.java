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
import com.hybridbpm.model.MobileFormComponent;
import com.hybridbpm.model.Translated;

/**
 *
 * @author Marat Gubaidullin
 */
public class MobileFieldModelUtil {

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

    private static MobileFormComponent getContainer(FieldModel fieldModel, String parentName) {
        MobileFormComponent mobileFormComponent = new MobileFormComponent();
        mobileFormComponent.setContainer(true);
        mobileFormComponent.setId(parentName);
        mobileFormComponent.setTitle(new Translated(fieldModel.getName()));
        Module module = DevelopmentAPI.get(null, null).getModuleByName(fieldModel.getClassName());
        DataModel dataModel = HybridbpmCoreUtil.jsonToObject(module.getModel(), DataModel.class);
        for (FieldModel field : dataModel.getFields()) {
            if (isSimple(field.getClassName())) {
                MobileFormComponent m = getSimpleComponent(field, createId(parentName, field.getName()), binding(parentName, field.getName()));
                mobileFormComponent.getComponents().add(m);
            } else {
                MobileFormComponent m = getContainer(field, createId(parentName, field.getName()));
                mobileFormComponent.getComponents().add(m);
            }
        }
        return mobileFormComponent;
    }

    private static MobileFormComponent getSimpleComponent(FieldModel fieldModel, String name, String binding) {
        MobileFormComponent mobileFormComponent = new MobileFormComponent();
        mobileFormComponent.setContainer(false);
        mobileFormComponent.setId(name);
        mobileFormComponent.setTitle(new Translated(fieldModel.getName()));
        mobileFormComponent.setFieldModel(fieldModel);
        mobileFormComponent.setValue(binding);
        return mobileFormComponent;
    }

    public static MobileFormComponent getMobileFormComponent(FieldModel fieldModel, String parentName) {
        if (isSimple(fieldModel.getClassName()) && fieldModel.getCollection().equals(FieldModel.COLLECTION_TYPE.NONE)) {
            return getSimpleComponent(fieldModel, createId(parentName, fieldModel.getName()), binding(parentName, fieldModel.getName()));
        } else if (!isSimple(fieldModel.getClassName()) && fieldModel.getCollection().equals(FieldModel.COLLECTION_TYPE.NONE)) {
            return getContainer(fieldModel, createId(parentName, fieldModel.getName()));
        } else {
            return null;
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

    private static String createId(String prefix, String name) {
        return prefix == null ? name : prefix + capitalize(name);
    }
    
    private static String binding(String prefix, String name) {
        StringBuilder builder = new StringBuilder();
        builder.append("${").append(prefix == null ? name : prefix + "." + name).append("}");
        return builder.toString();
    }

}
