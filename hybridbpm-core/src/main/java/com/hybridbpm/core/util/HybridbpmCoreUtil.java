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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hybridbpm.core.data.development.Module;
import static com.hybridbpm.core.util.DevelopmentConstant.DATA_TEMPLATE_SCRIPT;
import static com.hybridbpm.core.util.DevelopmentConstant.FIELD_TEMPLATE_SCRIPT;
import static com.hybridbpm.core.util.DevelopmentConstant.LIST_TEMPLATE_SCRIPT;
import static com.hybridbpm.core.util.DevelopmentConstant.MAP_TEMPLATE_SCRIPT;
import static com.hybridbpm.core.util.DevelopmentConstant.METHOD_FIELD_TEMPLATE_SCRIPT;
import static com.hybridbpm.core.util.DevelopmentConstant.METHOD_LIST_TEMPLATE_SCRIPT;
import static com.hybridbpm.core.util.DevelopmentConstant.METHOD_MAP_TEMPLATE_SCRIPT;
import com.hybridbpm.model.ConnectorModel;
import com.hybridbpm.model.DataModel;
import com.hybridbpm.model.FieldModel;
import com.hybridbpm.model.FileModel;
import com.hybridbpm.model.FormModel;
import com.hybridbpm.model.MobileFormComponent;
import groovy.lang.GroovyClassLoader;
import groovy.lang.Writable;
import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import org.codehaus.groovy.control.CompilationFailedException;

/**
 *
 * @author Marat Gubaidullin
 */
public class HybridbpmCoreUtil {

    private static final Logger logger = Logger.getLogger(HybridbpmCoreUtil.class.getSimpleName());

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(password.getBytes("UTF-8"));
            byte[] bytes = md.digest();
            return (new HexBinaryAdapter()).marshal(bytes);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            logger.severe(ex.getMessage());
        }
        return null;
    }

    public static String generateToken() throws NoSuchAlgorithmException {
        SecureRandom random = new SecureRandom();
        return UUID.randomUUID().toString() + new BigInteger(32, random).toString(32);
    }

    public static boolean isInteger(String str) {
        try {
            int d = Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static String objectToJson(Object object) {
        try {
            return new String(objectToJsonByteArray(object), "UTF-8");
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

    private static byte[] objectToJsonByteArray(Object object) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.writeValue(baos, object);
            return baos.toByteArray();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

    public static <T> T jsonToObject(String json, Class<T> clazz) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return objectMapper.readValue(json, clazz);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

    public static Object jsonByteArrayObject(byte[] json, Class clazz) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return objectMapper.readValue(json, clazz);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

    public static String streamToString(InputStream inputStream) throws IOException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            return new String(baos.toByteArray());
        } finally {
            inputStream.close();
        }
    }
    
    public static byte[] streamToBytes(InputStream inputStream) throws IOException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            return baos.toByteArray();
        } finally {
            inputStream.close();
        }
    }

    public static String generateModuleCodeFromTemplate(String moduleName, String codeTemplate) {
        try {
            Map bindings = new HashMap();
            bindings.put("moduleName", moduleName);
            SimpleTemplateEngine simpleTemplateEngine = new SimpleTemplateEngine();
            Template template = simpleTemplateEngine.createTemplate(codeTemplate);
            Writable writable = template.make(bindings);
            String finalScript = writable.toString();
            return finalScript;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    public static String getDefaultHazelcastConfig() {
        try {
            return createFromSource("/config/hazelcast.xml");
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    public static String getDefaultDatabaseConfig() {
        try {
            return createFromSource("/config/orientdb-server-config.xml");
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    public static String getScriptTemplateByName(String name) {
        try {
            return createFromSource("/template/" + name);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    public static String createFromSource(String path) throws Exception {
        InputStream streamSource = HybridbpmCoreUtil.class.getResourceAsStream(path);
        String source = streamToString(streamSource);
        return source;
    }
    
    public static byte[] createBytesFromSource(String path) throws Exception {
        InputStream streamSource = HybridbpmCoreUtil.class.getResourceAsStream(path);
        return streamToBytes(streamSource);
    }

    public static String fillTemplate(String templateString, Map bindings) throws RuntimeException {
        try {
            SimpleTemplateEngine simpleTemplateEngine = new SimpleTemplateEngine();
            Template template = simpleTemplateEngine.createTemplate(templateString);
            Writable writable = template.make(bindings);
            String finalScript = writable.toString();
            return finalScript;
        } catch (CompilationFailedException | ClassNotFoundException | IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        }
    }

    public static String updateMobileFormDesign(FormModel formModel) {
        MobileFormComponent mobileFormComponent = new MobileFormComponent();
        mobileFormComponent.setId(formModel.getName());
        mobileFormComponent.setTitle(formModel.getTitle());
        mobileFormComponent.setContainer(true);
        try {
            for (FieldModel field : formModel.getParameters()) {
                MobileFormComponent m = MobileFieldModelUtil.getMobileFormComponent(field, null);
                mobileFormComponent.getComponents().add(m);
            }
//            for (FileModel file : formModel.getFiles()) {
//                String line = FileModelUtil.getFormDesignElement(file);
//                if (!line.isEmpty()) designBuilder.append(line).append(System.lineSeparator());
//            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(Include.NON_NULL);
            return mapper.writeValueAsString(mobileFormComponent);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    public static String updateFormCodeWithDatasources(FormModel formModel, String code) {
        return updateFormCode(formModel, code, "getFormDatasourceTemplate", SyntaxConstant.FORM_DATASOURCES_START, SyntaxConstant.FORM_DATASOURCES_END);
    }

    public static String updateFormCodeWithComponents(FormModel formModel, String code) {
        return updateFormCode(formModel, code, "getFormComponentTemplate", SyntaxConstant.FORM_COMPONENTS_START, SyntaxConstant.FORM_COMPONENTS_END);
    }

    public static String updateFormCodeWithBindings(FormModel formModel, String code) {
        return updateFormCode(formModel, code, "getFormBindingTemplate", SyntaxConstant.FORM_BINDINGS_START, SyntaxConstant.FORM_BINDINGS_END);
    }

    public static String updateFormCodeWithCommits(FormModel formModel, String code) {
        return updateFormCode(formModel, code, "getFormCommitTemplate", SyntaxConstant.FORM_COMMITS_START, SyntaxConstant.FORM_COMMITS_END);
    }

    public static String updateFormCodeWithLoads(FormModel formModel, String code) {
        return updateFormCode(formModel, code, "getFormLoadTemplate", SyntaxConstant.FORM_LOADS_START, SyntaxConstant.FORM_LOADS_END);
    }

    public static String updateFormCode(FormModel formModel, String code, String methodName, String start, String end) {
        try {
            StringBuilder parametersBuilder = new StringBuilder();
            Method method = FieldModelUtil.class.getMethod(methodName, FieldModel.class, String.class);
            for (FieldModel field : formModel.getParameters()) {
                String line = (String) method.invoke(null, field, null);
                if (!line.isEmpty()) {
                    parametersBuilder.append(line).append(System.lineSeparator());
                }
            }
            return replaceGeneratedCode(code, parametersBuilder.toString(), start, end);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            return code;
        }
    }

    public static String updateFormCodeWithFiles(FormModel formModel, String code) {
        try {
            StringBuilder designBuilder = new StringBuilder();
            for (FileModel file : formModel.getFiles()) {
                String line = FileModelUtil.getFileComponent(file);
                if (!line.isEmpty()) {
                    designBuilder.append(line).append(System.lineSeparator());
                }
            }
            return replaceGeneratedCode(code, designBuilder.toString(), SyntaxConstant.FORM_FILES_START, SyntaxConstant.FORM_FILES_END);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            return code;
        }
    }

    public static String updateFormDesign(FormModel formModel, String design) {
        try {
            StringBuilder designBuilder = new StringBuilder();
            for (FieldModel field : formModel.getParameters()) {
                String line = FieldModelUtil.getFormDesignElement(field, null);
                if (!line.isEmpty()) {
                    designBuilder.append(line).append(System.lineSeparator());
                }
            }
            for (FileModel file : formModel.getFiles()) {
                String line = FileModelUtil.getFormDesignElement(file);
                if (!line.isEmpty()) {
                    designBuilder.append(line).append(System.lineSeparator());
                }
            }
            return replaceGeneratedCode(design, designBuilder.toString(), SyntaxConstant.FORM_DESIGN_START, SyntaxConstant.FORM_DESIGN_END);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            return design;
        }
    }

    public static String updateConnectorCodeInParameters(Module Module) {
        ConnectorModel connectorModel = HybridbpmCoreUtil.jsonToObject(Module.getModel(), ConnectorModel.class);
        String code = Module.getCode();
        try {
            StringBuilder parametersBuilder = new StringBuilder();
            for (FieldModel field : connectorModel.getInParameters()) {
                parametersBuilder.append(FieldModelUtil.getConnectorParameterTemplate(field)).append(System.lineSeparator());
            }
            return replaceGeneratedCode(code, parametersBuilder.toString(), SyntaxConstant.CONNECTOR_IN_PARAMETERS_START, SyntaxConstant.CONNECTOR_IN_PARAMETERS_END);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            return code;
        }
    }

    public static String updateConnectorCodeOutParameters(Module Module) {
        ConnectorModel connectorModel = HybridbpmCoreUtil.jsonToObject(Module.getModel(), ConnectorModel.class);
        String code = Module.getCode();
        try {
            StringBuilder parametersBuilder = new StringBuilder();
            for (FieldModel field : connectorModel.getOutParameters()) {
                parametersBuilder.append(FieldModelUtil.getConnectorParameterTemplate(field)).append(System.lineSeparator());
            }
            return replaceGeneratedCode(code, parametersBuilder.toString(), SyntaxConstant.CONNECTOR_OUT_PARAMETERS_START, SyntaxConstant.CONNECTOR_OUT_PARAMETERS_END);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            return code;
        }
    }

    private static String replaceGeneratedCode(String code, String generatedCode, String BEGIN, String END) {
        StringBuilder codeBuilder = new StringBuilder();
        if (code.contains(BEGIN) && code.contains(END)) {
            int start = code.indexOf(BEGIN);
            int end = code.indexOf(END);

            codeBuilder.append(code.substring(0, start));
            codeBuilder.append(BEGIN).append(System.lineSeparator());
            codeBuilder.append(generatedCode.replaceAll(System.lineSeparator() + System.lineSeparator(), System.lineSeparator()));
            codeBuilder.append("    ").append(code.substring(end, code.length()));
            return codeBuilder.toString();
        } else {
            return code;
        }
    }

    public static String generateClassName(String name, String version) {
        StringBuilder nameValue = new StringBuilder();
        nameValue.append(name != null ? name : "").append("_").append(version != null ? version : "_");

        StringBuilder result = new StringBuilder();
        for (Character c : nameValue.toString().toCharArray()) {
            if (Character.isJavaIdentifierPart(c)) {
                result.append(c);
            } else {
                result.append("_");
            }
        }
        while (!Character.isJavaIdentifierStart(result.charAt(0))) {
            result.deleteCharAt(0);
        }

        return Character.toUpperCase(result.charAt(0)) + result.substring(1);
    }

    public static String generateFieldName(String name) {
        String nameValue = name;
        StringBuilder result = new StringBuilder();
        for (Character c : nameValue.toCharArray()) {
            if (Character.isJavaIdentifierPart(c)) {
                result.append(c);
            } else {
                result.append("");
            }
        }
        while (!Character.isJavaIdentifierStart(result.charAt(0))) {
            result.deleteCharAt(0);
        }
        return Character.toLowerCase(result.charAt(0)) + result.substring(1);
    }

    public static void deleteDirectory(String path) {
        try {
            Path directory = Paths.get(path);
            Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }

            });
        } catch (IOException ex) {
            logger.severe(ex.getMessage());
        }
    }

    public static String createDataCode(DataModel dataModel) {
        try {
            // get all data templates
            String data = getScriptTemplateByName(DATA_TEMPLATE_SCRIPT);
            String field = getScriptTemplateByName(FIELD_TEMPLATE_SCRIPT);
            String list = getScriptTemplateByName(LIST_TEMPLATE_SCRIPT);
            String map = getScriptTemplateByName(MAP_TEMPLATE_SCRIPT);
            String methodField = getScriptTemplateByName(METHOD_FIELD_TEMPLATE_SCRIPT);
            String methodList = getScriptTemplateByName(METHOD_LIST_TEMPLATE_SCRIPT);
            String methodMap = getScriptTemplateByName(METHOD_MAP_TEMPLATE_SCRIPT);
            // prepare fields and methods
            StringBuilder f = new StringBuilder();
            StringBuilder m = new StringBuilder();
            for (FieldModel fieldModel : dataModel.getFields()) {
                Map<String, Object> bindings = new HashMap<>();
                bindings.put("fieldName", fieldModel.getName());
                bindings.put("className", fieldModel.getClassName());
                if (fieldModel.getCollection().equals(FieldModel.COLLECTION_TYPE.NONE)) {
                    f.append(fillTemplate(field, bindings)).append(System.lineSeparator());
                    m.append(fillTemplate(methodField, bindings)).append(System.lineSeparator());
                } else if (fieldModel.getCollection().equals(FieldModel.COLLECTION_TYPE.LIST)) {
                    f.append(fillTemplate(list, bindings)).append(System.lineSeparator());
                    m.append(fillTemplate(methodList, bindings)).append(System.lineSeparator());
//                } else if (fieldModel.getCollection().equals(COLLECTION_TYPE.MAP)) {
//                    f.append(fillTemplate(map.getSource(), bindings)).append(System.lineSeparator());
//                    m.append(fillTemplate(methodMap.getSource(), bindings)).append(System.lineSeparator());
                }
            }
            // create final script
            Map<String, Object> bindings = new HashMap<>();
            bindings.put("className", dataModel.getName());
            bindings.put("fields", f.toString());
            bindings.put("methods", m.toString());
            String source = fillTemplate(data, bindings);
            return source;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            return null;
        }
    }

    public GroovyClassLoader getGroovyClassLoader() {
        return new GroovyClassLoader(Thread.currentThread().getContextClassLoader());
    }

    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameDay(cal1, cal2);
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)
                && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    public static boolean isToday(Date date) {
        return isSameDay(date, Calendar.getInstance().getTime());
    }

}
