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
package com.hybridbpm.ui.component.development;

import com.hybridbpm.core.util.HybridbpmCoreUtil;
import com.hybridbpm.model.DataModel;
import com.hybridbpm.model.FieldModel;
import com.hybridbpm.core.data.development.Module;
import com.hybridbpm.ui.HybridbpmUI;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.vaadin.aceeditor.AceEditor;
import org.vaadin.aceeditor.AceMode;
import org.vaadin.aceeditor.AceTheme;

/**
 *
 * @author Marat Gubaidullin
 */
public class DataEditor extends AbstractEditor {

    private static final Logger logger = Logger.getLogger(DataEditor.class.getCanonicalName());

    private DataModel dataModel;
    private Module Module;
    private final Button btnAdd = new Button(null, this);
    private final VerticalLayout modelerLayout = new VerticalLayout();
    private final AceEditor codeEditor = new AceEditor();
    private final BeanFieldGroup<Module> binder = new BeanFieldGroup<>(Module.class);

    @Override
    public Module getModule() {
        return Module;
    }

    public DataEditor(Module Module) {
        super();
        this.Module = HybridbpmUI.getDevelopmentAPI().getModuleById(Module.getId());
        this.dataModel = HybridbpmCoreUtil.jsonToObject(this.Module.getModel(), DataModel.class);
        prepareModeler();
        horizontalSplitPanel.addComponents(modelerLayout, codeEditor);
        horizontalSplitPanel.setSplitPosition(100, Sizeable.Unit.PERCENTAGE);

        codeEditor.setMode(AceMode.groovy);
        codeEditor.setTheme(AceTheme.textmate);
        codeEditor.setShowGutter(true);
        codeEditor.setSizeFull();
        binder.setItemDataSource(this.Module);
        binder.bind(codeEditor, "code");
    }

    private void prepareModeler() {
        btnAdd.setCaption("Add field");
        btnAdd.setIcon(FontAwesome.PLUS_CIRCLE);
        btnAdd.setStyleName(ValoTheme.BUTTON_LINK);
        btnAdd.addStyleName(ValoTheme.BUTTON_SMALL);

        modelerLayout.setMargin(true);
        modelerLayout.setSpacing(true);
        modelerLayout.setWidth(100, Sizeable.Unit.PERCENTAGE);
        for (FieldModel fieldModel : dataModel.getFields()) {
            FieldForm fieldForm = new FieldForm(FieldForm.CLASS_LIST_TYPE.BOTH);
            fieldForm.setFieldModel(fieldModel);
            modelerLayout.addComponent(fieldForm);
        }
        modelerLayout.addComponent(btnAdd);
        modelerLayout.setComponentAlignment(btnAdd, Alignment.MIDDLE_RIGHT);
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        try {
            if (event.getButton().equals(btnSave)) {
                binder.commit();
                dataModel.getFields().clear();
                for (Component comp : modelerLayout) {
                    if (comp instanceof FieldForm) {
                        FieldForm fieldForm = (FieldForm) comp;
                        fieldForm.commit();
                        dataModel.getFields().add(fieldForm.getFieldModel());
                    }
                }
                Module.setModel(HybridbpmCoreUtil.objectToJson(dataModel));
                Module = HybridbpmUI.getDevelopmentAPI().saveModule(Module);
                binder.setItemDataSource(Module);
            } else if (event.getButton().equals(btnAdd)) {
                FieldForm fieldForm = new FieldForm(FieldForm.CLASS_LIST_TYPE.BOTH);
                fieldForm.setFieldModel(new FieldModel("field", "", String.class.getCanonicalName(), null, FieldModel.COLLECTION_TYPE.NONE, FieldModel.EDITOR_TYPE.TEXT_FIELD));
                modelerLayout.addComponent(fieldForm, modelerLayout.getComponentIndex(btnAdd));
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}
