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

import com.hybridbpm.model.FileModel;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.logging.Logger;

/**
 *
 * @author Marat Gubaidullin
 */
public class FileForm extends Panel implements Button.ClickListener {

    public static final Logger logger = Logger.getLogger(FileForm.class.getCanonicalName());

    private FileModel fileModel;
    private final BeanFieldGroup fieldGroup = new BeanFieldGroup<>(FileModel.class);
    private final TextField name = new TextField();
    private final TextField description = new TextField();
    private final CheckBox multiple = new CheckBox();
    private final Button btnRemove = new Button(null, this);
    private final Button btnUp = new Button(null, this);
    private final Button btnDown = new Button(null, this);
    private final HorizontalLayout elementsLayout = new HorizontalLayout(name, description, multiple);
    private final HorizontalLayout buttonsLayout = new HorizontalLayout(btnUp, btnDown, btnRemove);
    private final HorizontalLayout layout = new HorizontalLayout(elementsLayout, buttonsLayout);

    private static final String NAME = "NAME";

    public FileForm() {
        setContent(layout);
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setWidth(100, Unit.PERCENTAGE);
        layout.setExpandRatio(elementsLayout, 1f);
        
        elementsLayout.setWidth(100, Unit.PERCENTAGE);
        elementsLayout.setSpacing(true);
        elementsLayout.setComponentAlignment(multiple, Alignment.MIDDLE_CENTER);
        elementsLayout.setExpandRatio(name, 3f);
        elementsLayout.setExpandRatio(description, 3f);
        elementsLayout.setExpandRatio(multiple, 1f);
        description.setWidth(100, Unit.PERCENTAGE);
        name.setWidth(100, Unit.PERCENTAGE);

        buttonsLayout.setComponentAlignment(btnRemove, Alignment.MIDDLE_CENTER);
        buttonsLayout.setComponentAlignment(btnUp, Alignment.MIDDLE_CENTER);
        buttonsLayout.setComponentAlignment(btnDown, Alignment.MIDDLE_CENTER);
        buttonsLayout.setHeight(100, Unit.PERCENTAGE);
//        buttonsLayout.setSpacing(true);

        prepareComponents();
        fieldGroup.bind(name, "name");
        fieldGroup.bind(description, "description");
        fieldGroup.bind(multiple, "multiple");
    }

    public void setFileModel(FileModel fileModel) {
        this.fileModel = fileModel;
        fieldGroup.setItemDataSource(new BeanItem<>(fileModel));
    }

    public void commit() throws FieldGroup.CommitException {
        fieldGroup.commit();
    }

    public FileModel getFileModel() {
        return fileModel;
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton().equals(btnRemove)) {
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
                if (downComponent instanceof FileForm) {
                    ((VerticalLayout) getParent()).replaceComponent(downComponent, this);
                }
            }
        }
    }

    private void prepareComponents() {
        name.setCaption("Name");
        name.setWidth(100, Unit.PERCENTAGE);
        name.setNullRepresentation("");
        name.setRequired(true);
        name.setImmediate(true);
        name.addValueChangeListener(new NameChangeListener());

        description.setCaption("Description");
        description.setWidth(100, Unit.PERCENTAGE);
        description.setNullRepresentation("");

        multiple.setCaption("Multiple");
        multiple.setWidth(100, Unit.PERCENTAGE);

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

    public class NameChangeListener implements Property.ValueChangeListener {

        @Override
        public void valueChange(Property.ValueChangeEvent event) {

        }
    }
}
