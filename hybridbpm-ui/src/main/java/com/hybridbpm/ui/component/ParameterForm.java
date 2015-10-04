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
package com.hybridbpm.ui.component;

import com.hybridbpm.model.FieldModel;
import com.hybridbpm.ui.component.bpm.window.VariableSuggester;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import java.util.Objects;
import java.util.logging.Logger;
import org.vaadin.aceeditor.AceEditor;
import org.vaadin.aceeditor.AceMode;
import org.vaadin.aceeditor.AceTheme;
import org.vaadin.aceeditor.Suggester;
import org.vaadin.aceeditor.SuggestionExtension;

/**
 *
 * @author Marat Gubaidullin
 */
public class ParameterForm extends HorizontalLayout {

    public static final Logger logger = Logger.getLogger(ParameterForm.class.getCanonicalName());

    private final FieldModel fieldModel;
    private final BeanFieldGroup fieldGroup = new BeanFieldGroup<>(FieldModel.class);
    private final Label name = new Label();
    private AceEditor valueEditor = new AceEditor();
    private final Suggester suggester;
    private final TYPE type;

    public enum TYPE {

        INPUT,
        OUTPUT;
    }

    public ParameterForm(FieldModel fieldModel, TYPE type, Suggester suggester, String value) {
        this.fieldModel = fieldModel;
        this.type = type;
        this.suggester = suggester;
        fieldGroup.setItemDataSource(new BeanItem<>(fieldModel));
        prepareComponents();
        setSpacing(true);
        setWidth(100, Unit.PERCENTAGE);
        switch (this.type) {
            case INPUT:
                addComponents(valueEditor, name);
                break;
            case OUTPUT:
                addComponents(name, valueEditor);
                name.addStyleName("label-arrow-right");
                break;
        }
        setExpandRatio(name, 1f);
        setExpandRatio(valueEditor, 2f);
        name.setCaption(fieldModel.getName() + " (" + fieldModel.getClassName() + ")");
        fieldGroup.bind(valueEditor, "defaultValue");
        if (value !=null && !value.isEmpty()){
            valueEditor.setValue(value);
        }
    }

    public void commit() throws FieldGroup.CommitException {
        fieldGroup.commit();
    }

    public FieldModel getFieldModel() {
        return fieldModel;
    }

    private void prepareComponents() {
        name.setIcon(FontAwesome.ARROW_RIGHT);
        name.setWidth(100, Unit.PERCENTAGE);

        ((AceEditor) valueEditor).setTheme(AceTheme.textmate);
        ((AceEditor) valueEditor).setShowGutter(true);
        ((AceEditor) valueEditor).setSizeFull();
        ((AceEditor) valueEditor).setHeight(150, Unit.PIXELS);
        if (Objects.equals(type, TYPE.INPUT)) {
            ((AceEditor) valueEditor).setMode(AceMode.groovy);
        } else {
            ((AceEditor) valueEditor).setMode(AceMode.json);
        }
        valueEditor.setWidth(100, Unit.PERCENTAGE);
        if (suggester != null) {
            SuggestionExtension extension = new SuggestionExtension(suggester);
            extension.setSuggestOnDot(false);
            extension.extend(valueEditor);
        }
    }

}
