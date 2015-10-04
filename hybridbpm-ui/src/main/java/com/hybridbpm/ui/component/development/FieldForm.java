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

import com.hybridbpm.model.FieldModel;
import com.hybridbpm.model.FieldModel.COLLECTION_TYPE;
import com.hybridbpm.core.data.development.Module;
import com.hybridbpm.core.util.FieldModelUtil;
import com.hybridbpm.model.FieldModel.EDITOR_TYPE;
import com.hybridbpm.ui.HybridbpmUI;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marat Gubaidullin
 */
public class FieldForm extends Panel implements Button.ClickListener {

    public static final Logger logger = Logger.getLogger(FieldForm.class.getCanonicalName());

    private FieldModel fieldModel;
    private final BeanFieldGroup fieldGroup = new BeanFieldGroup<>(FieldModel.class);
    private final TextField name = new TextField();
    private final TextField description = new TextField();
    private final TextField defaultValue = new TextField();
    private final ComboBox className = new ComboBox();
    private final ComboBox collection = new ComboBox();
    private final ComboBox editor = new ComboBox();
    private final Button btnShowMore = new Button(null, this);
    private final Button btnRemove = new Button(null, this);
    private final Button btnUp = new Button(null, this);
    private final Button btnDown = new Button(null, this);
    private final HorizontalLayout firstLayout = new HorizontalLayout(name, className, collection, editor);
    private final HorizontalLayout secondLayout = new HorizontalLayout(description, defaultValue);
    private final VerticalLayout elementsLayout = new VerticalLayout(firstLayout, secondLayout);
    private final HorizontalLayout buttonsLayout = new HorizontalLayout(btnShowMore, btnUp, btnDown, btnRemove);
    private final HorizontalLayout layout = new HorizontalLayout(elementsLayout, buttonsLayout);
    private final CLASS_LIST_TYPE classListType;

    private static final String NAME = "NAME";

    public enum CLASS_LIST_TYPE {

        SIMPLE_DATA,
        COMPLEX_DATA,
        BOTH;
    }

    public FieldForm(CLASS_LIST_TYPE classListType) {
        this.classListType = classListType;
        setContent(layout);
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setWidth(100, Unit.PERCENTAGE);
        layout.setExpandRatio(elementsLayout, 1f);

        firstLayout.setSpacing(true);
        firstLayout.setWidth(100, Unit.PERCENTAGE);

        secondLayout.setSpacing(true);
        secondLayout.setWidth(100, Unit.PERCENTAGE);

        firstLayout.setExpandRatio(name, 1f);
        firstLayout.setExpandRatio(className, 1f);
        firstLayout.setExpandRatio(collection, 1f);
        firstLayout.setExpandRatio(editor, 1f);

        secondLayout.setExpandRatio(description, 1f);
        secondLayout.setExpandRatio(defaultValue, 1f);
        secondLayout.setVisible(false);

        buttonsLayout.setComponentAlignment(btnShowMore, Alignment.MIDDLE_CENTER);
        buttonsLayout.setComponentAlignment(btnRemove, Alignment.MIDDLE_CENTER);
        buttonsLayout.setComponentAlignment(btnUp, Alignment.MIDDLE_CENTER);
        buttonsLayout.setComponentAlignment(btnDown, Alignment.MIDDLE_CENTER);
        buttonsLayout.setHeight(100, Unit.PERCENTAGE);
//        buttonsLayout.setSpacing(true);

        prepareComponents();
        fieldGroup.bind(name, "name");
        fieldGroup.bind(description, "description");
        fieldGroup.bind(defaultValue, "defaultValue");
        fieldGroup.bind(className, "className");
        fieldGroup.bind(collection, "collection");
        fieldGroup.bind(editor, "editor");
    }

    public void setFieldModel(FieldModel fieldModel) {
        this.fieldModel = fieldModel;
        fieldGroup.setItemDataSource(new BeanItem<>(fieldModel));
    }

    public void commit() throws FieldGroup.CommitException {
        fieldGroup.commit();
    }

    public FieldModel getFieldModel() {
        return fieldModel;
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton().equals(btnShowMore)) {
            if (secondLayout.isVisible()) {
                btnShowMore.setIcon(FontAwesome.PLUS_SQUARE_O);
                btnShowMore.setDescription("Show details");
                secondLayout.setVisible(false);
            } else {
                btnShowMore.setIcon(FontAwesome.MINUS_SQUARE_O);
                btnShowMore.setDescription("Hide details");
                secondLayout.setVisible(true);
            }
        } else if (event.getButton().equals(btnRemove)) {
            ((VerticalLayout) getParent()).removeComponent(this);
        } else if (event.getButton().equals(btnUp)) {
            int index = ((VerticalLayout) getParent()).getComponentIndex(this);
            if (index != 0) {
                Component upComponent = ((VerticalLayout) getParent()).getComponent(index - 1);
                ((VerticalLayout) getParent()).replaceComponent(upComponent, this);
            }
        }
        if (event.getButton().equals(btnDown)) {
            int index = ((VerticalLayout) getParent()).getComponentIndex(this);
            if (((VerticalLayout) getParent()).getComponentCount() > 2
                    && index < ((VerticalLayout) getParent()).getComponentCount() - 1) {
                Component downComponent = ((VerticalLayout) getParent()).getComponent(index + 1);
                if (downComponent instanceof FieldForm) {
                    ((VerticalLayout) getParent()).replaceComponent(downComponent, this);
                }
            }
        }
    }

    private void prepareComponents() {
        className.setCaption("Data type");
        className.setContainerDataSource(getClassesContainer());
        className.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        className.setItemCaptionPropertyId(NAME);
        className.setWidth(100, Unit.PERCENTAGE);
        className.addValueChangeListener(new ClassChangeListener());

        name.setCaption("Name");
        name.setWidth(100, Unit.PERCENTAGE);
        name.setNullRepresentation("");
        name.setRequired(true);
        name.setImmediate(true);
        name.addValueChangeListener(new NameChangeListener());

        description.setCaption("Description");
        description.setWidth(100, Unit.PERCENTAGE);
        description.setNullRepresentation("");

        defaultValue.setCaption("Default Value");
        defaultValue.setWidth(100, Unit.PERCENTAGE);
        defaultValue.setNullRepresentation("");

        collection.setCaption("Collection");
        collection.setContainerDataSource(getCollectionContainer());
        collection.setNewItemsAllowed(false);
        collection.setNullSelectionAllowed(false);
        collection.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        collection.setItemCaptionPropertyId(NAME);
        collection.setWidth(100, Unit.PERCENTAGE);

        editor.setCaption("Editor");
        editor.setContainerDataSource(getEditorContainer());
        editor.setNewItemsAllowed(false);
        editor.setNullSelectionAllowed(false);
        editor.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        editor.setItemCaptionPropertyId(NAME);
        editor.setWidth(100, Unit.PERCENTAGE);
        editor.setVisible(!Objects.equals(classListType, CLASS_LIST_TYPE.COMPLEX_DATA));

        btnShowMore.setHeight(100, Unit.PERCENTAGE);
        btnShowMore.setIcon(FontAwesome.PLUS_SQUARE_O);
        btnShowMore.setDescription("Show more details");
        btnShowMore.addStyleName(ValoTheme.BUTTON_LINK);

        btnRemove.setHeight(100, Unit.PERCENTAGE);
        btnRemove.setIcon(FontAwesome.TIMES_CIRCLE);
        btnRemove.addStyleName(ValoTheme.BUTTON_LINK);

        btnUp.setHeight(100, Unit.PERCENTAGE);
        btnUp.setIcon(FontAwesome.ARROW_CIRCLE_UP);
        btnUp.addStyleName(ValoTheme.BUTTON_LINK);

        btnDown.setHeight(100, Unit.PERCENTAGE);
        btnDown.setIcon(FontAwesome.ARROW_CIRCLE_DOWN);
        btnDown.addStyleName(ValoTheme.BUTTON_LINK);
    }

    private IndexedContainer getCollectionContainer() {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty(NAME, String.class, null);

        for (COLLECTION_TYPE collection_type : FieldModel.COLLECTION_TYPE.values()) {
            addItem(container, collection_type, collection_type.name());
        }
        container.sort(new Object[]{NAME}, new boolean[]{true});
        return container;
    }

    private IndexedContainer getEditorContainer() {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty(NAME, String.class, null);

        for (EDITOR_TYPE type : FieldModel.EDITOR_TYPE.values()) {
            addItem(container, type, type.getComponent());
        }
        container.sort(new Object[]{NAME}, new boolean[]{true});
        return container;
    }

    private IndexedContainer getClassesContainer() {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty(NAME, String.class, null);

        if (classListType.equals(CLASS_LIST_TYPE.SIMPLE_DATA) || classListType.equals(CLASS_LIST_TYPE.BOTH)) {
            for (FieldModel.CLASS c : FieldModel.CLASS.values()) {
                addItem(container, c.getCanonicalName(), c.getSimpleName());
            }
        }

        if (classListType.equals(CLASS_LIST_TYPE.COMPLEX_DATA) || classListType.equals(CLASS_LIST_TYPE.BOTH)) {
            try {
                for (Module data : HybridbpmUI.getDevelopmentAPI().getModuleListByType(Module.MODULE_TYPE.DATA, false)) {
                    addItem(container, data.getName(), data.getName());
                }
            } catch (Exception ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        container.sort(new Object[]{NAME}, new boolean[]{true});
        return container;
    }

    private void addItem(Container container, Object id, String caption) {
        Item item = container.addItem(id);
        item.getItemProperty(NAME).setValue(caption);
    }

    public class ClassChangeListener implements Property.ValueChangeListener {

        @Override
        public void valueChange(Property.ValueChangeEvent event) {
            String className = (String) event.getProperty().getValue();
            if (FieldModelUtil.isSimple(className)) {
                switch (FieldModelUtil.getCLASSByCanonicalName(className)) {
                    case STRING:
                        editor.setValue(EDITOR_TYPE.TEXT_FIELD);
                        break;
                    case DATE:
                        editor.setValue(EDITOR_TYPE.DATE_FIELD);
                        break;
                    case BIG_DECIMAL:
                        editor.setValue(EDITOR_TYPE.TEXT_FIELD);
                        break;
                    case BOOLEAN:
                        editor.setValue(EDITOR_TYPE.CHECK_BOX);
                        break;
                    case INTEGER:
                        editor.setValue(EDITOR_TYPE.DATE_FIELD);
                        break;
                    default:
                        editor.setValue(null);
                        break;
                }
            }
        }
    }

    public class NameChangeListener implements Property.ValueChangeListener {

        @Override
        public void valueChange(Property.ValueChangeEvent event) {

        }
    }
}
