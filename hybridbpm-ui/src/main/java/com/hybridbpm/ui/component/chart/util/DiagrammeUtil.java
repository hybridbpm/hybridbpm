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
package com.hybridbpm.ui.component.chart.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hybridbpm.core.data.chart.DiagrammePreference;
import com.vaadin.data.fieldgroup.BeanFieldGroup;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mgubaidullin
 */
public class DiagrammeUtil {

    public static final Logger LOG = Logger.getLogger(DiagrammeUtil.class.getCanonicalName());

    public static <T> T stringToObject(String json, Class<T> clazz) {
        T result = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
//            mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
            mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            result = (T) mapper.readValue(json, clazz);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return result;
    }

    public static String objectToString(Object object) {
        try {
            ObjectMapper mapper = new ObjectMapper();
//            mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
            mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return new String();
    }
    
    public static byte[] readFromSource(String path) {
        InputStream streamSource = DiagrammeUtil.class.getResourceAsStream(path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        try {
            while ((length = streamSource.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
        } catch (IOException ex) {
            Logger.getLogger(DiagrammeUtil.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return baos.toByteArray();
    }

    public static <X> X getPreferenceValue(String propertyName, BeanFieldGroup<DiagrammePreference> diagrammePreference) {
        return (X) diagrammePreference.getItemDataSource().getItemProperty(propertyName).getValue();
    }

    public static String[] getCategoriesNames(Set columnValues) {
        String[] categoriesNames = new String[columnValues.size()];
        Iterator it = columnValues.iterator();
        int i = 0;
        while (it.hasNext()) {
            categoriesNames[i] = it.next().toString();
            i++;
        }
        return categoriesNames;
    }

    public static <T> T checkNotEmpty(T field, String errorMessage) {
        if (field == null) {
            throw new NullPointerException(errorMessage);
        } else {
            return field;
        }
    }
}
