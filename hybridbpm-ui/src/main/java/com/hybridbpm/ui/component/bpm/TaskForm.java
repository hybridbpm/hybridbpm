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
package com.hybridbpm.ui.component.bpm;

import com.hybridbpm.core.data.bpm.File;
import com.hybridbpm.core.data.bpm.Variable;
import com.hybridbpm.core.util.FieldModelUtil;
import com.hybridbpm.model.FieldModel;
import com.hybridbpm.model.FileModel;
import com.hybridbpm.model.ProcessModel;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.component.FileManager;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.VerticalLayout;
import groovy.lang.GroovyClassLoader;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;

/**
 *
 * @author Marat Gubaidullin
 */
@SuppressWarnings("serial")
public abstract class TaskForm extends VerticalLayout {

    private static final Logger logger = Logger.getLogger(TaskForm.class.getSimpleName());

    protected String caseId;
    protected ProcessModel processModel;

    public void setProcessData(String caseId, ProcessModel processModel) {
        this.caseId = caseId;
        this.processModel = processModel;

        bindProcessVariables();
        bindProcessFiles();
    }

    public abstract void commit();

    protected Map<String, Object> getProcessVariablesValues() {
        Map<String, Object> variables = new HashMap<>();
        for (Field f : this.getClass().getDeclaredFields()) {
            try {
                if (f.getType().equals(ObjectProperty.class) && f.getName().endsWith("Property")) {
                    String name = f.getName().substring(0, f.getName().lastIndexOf("Property"));
                    f.setAccessible(true);
                    ObjectProperty objectProperty = (ObjectProperty) f.get(this);
                    if (processModel.getVariableModelByName(name) != null) {
                        variables.put(name, objectProperty.getValue());
                    }
                } else if (f.getType().equals(BeanFieldGroup.class) && f.getName().endsWith("BeanFieldGroup")) {
                    String name = f.getName().substring(0, f.getName().lastIndexOf("BeanFieldGroup"));
                    f.setAccessible(true);
                    BeanFieldGroup beanFieldGroup = (BeanFieldGroup) f.get(this);
                    if (processModel.getFieldModelByName(name) != null && beanFieldGroup.getItemDataSource() != null) {
                        variables.put(name, beanFieldGroup.getItemDataSource().getBean());
                    }
                }
            } catch (SecurityException | IllegalArgumentException | IllegalAccessException nsfe) {
                logger.log(Level.SEVERE, nsfe.getMessage(), nsfe);
            }
        }
        return variables;
    }

    protected Map<String, List<File>> getProcessFiles() {
        Map<String, List<File>> files = new HashMap<>();
        for (Field f : this.getClass().getDeclaredFields()) {
            try {
                if (f.getType().equals(FileManager.class) && f.getName().endsWith("FileManager")) {
                    String name = f.getName().substring(0, f.getName().lastIndexOf("FileManager"));
                    f.setAccessible(true);
                    FileManager fileManager = (FileManager) f.get(this);
                    if (processModel.getFileModelByName(name) != null) {
                        files.put(name, fileManager.getFileList());
                    }
                }
            } catch (SecurityException | IllegalArgumentException | IllegalAccessException nsfe) {
                logger.log(Level.SEVERE, nsfe.getMessage(), nsfe);
            }
        }
        return files;
    }
    
    protected List<String> getFilesIdToDelete() {
        List<String> filesIdToDelete = new ArrayList<>();
        for (Field f : this.getClass().getDeclaredFields()) {
            try {
                if (f.getType().equals(FileManager.class) && f.getName().endsWith("FileManager")) {
                    String name = f.getName().substring(0, f.getName().lastIndexOf("FileManager"));
                    f.setAccessible(true);
                    FileManager fileManager = (FileManager) f.get(this);
                    filesIdToDelete.addAll(fileManager.getIdsToRemove());
                }
            } catch (SecurityException | IllegalArgumentException | IllegalAccessException nsfe) {
                logger.log(Level.SEVERE, nsfe.getMessage(), nsfe);
            }
        }
        return filesIdToDelete;
    }

    public void bindProcessVariables() {
        Map<String, Object> variables;
        if (this.caseId != null) {
            variables = HybridbpmUI.getBpmAPI().getVariableValues(caseId);
        } else {
            variables = generateDefaultVariableValues();
        }

        for (String name : variables.keySet()) {
            try {
                FieldModel field = processModel.getFieldModelByName(name);
                if (field != null && FieldModelUtil.isSimple(field.getClassName())) {
                    Field f = this.getClass().getDeclaredField(name + "Property");
                    f.setAccessible(true);
                    if (f.getType().equals(ObjectProperty.class)) {
                        ObjectProperty objectProperty = (ObjectProperty) f.get(this);
                        objectProperty.setValue(variables.get(name));
                    }
                } else {
                    Field f = this.getClass().getDeclaredField(name + "BeanFieldGroup");
                    f.setAccessible(true);
                    if (f.getType().equals(BeanFieldGroup.class)) {
                        BeanFieldGroup beanFieldGroup = (BeanFieldGroup) f.get(this);
//                        System.out.println("bind " + name + " " + variables.get(name));
                        beanFieldGroup.setItemDataSource(variables.get(name));
                    }
                }
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException nsfe) {
                logger.log(Level.SEVERE, nsfe.getMessage(), nsfe);
            }
        }
    }

    public void bindProcessFiles() {
        Map<String, List<File>> files = new HashMap<>();
        if (this.caseId != null) {
            files = HybridbpmUI.getBpmAPI().getCaseFiles(caseId);
        }
        for (FileModel fileModel : processModel.getFileModels()) {
            try {
                Field f = this.getClass().getDeclaredField(fileModel.getName() + "FileManager");
                f.setAccessible(true);
                if (f.getType().equals(FileManager.class)) {
                    FileManager fileManager = (FileManager) f.get(this);
                    fileManager.initUI(fileModel);
                    if (files.containsKey(fileModel.getName())){
                        fileManager.setFileList(files.get(fileModel.getName()));
                    }
                }
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException nsfe) {
                logger.log(Level.SEVERE, nsfe.getMessage(), nsfe);
            }
        }
    }

    protected HashMap<String, Object> generateDefaultVariableValues() {
        HashMap<String, Object> result = new HashMap<>();
        try {
            Map<String, Variable> variableInstances = HybridbpmUI.getBpmAPI().createFirstVariables(processModel);
            for (String name : variableInstances.keySet()) {
                Variable vi = variableInstances.get(name);
                if (FieldModelUtil.isSimple(vi.getClassName())) {

                } else {
                    GroovyClassLoader groovyClassLoader = HybridbpmUI.getDevelopmentAPI().getGroovyClassLoader();
                    Class clazz = groovyClassLoader.loadClass(vi.getClassName());
                    Object object = clazz.newInstance();
                    System.out.println("object = " + object);
                    result.put(name, object);
                }
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return result;
    }

}
