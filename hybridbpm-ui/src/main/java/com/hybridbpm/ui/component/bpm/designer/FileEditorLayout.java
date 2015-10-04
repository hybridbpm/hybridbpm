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
package com.hybridbpm.ui.component.bpm.designer;

import com.hybridbpm.model.FileModel;
import com.hybridbpm.model.ProcessModel;
import com.hybridbpm.ui.component.development.FileForm;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 *
 * @author Marat Gubaidullin
 */
public class FileEditorLayout extends VerticalLayout implements Button.ClickListener {

    private ProcessModel processModel;
    private Button btnAdd = new Button(null, this);

    public FileEditorLayout() {
    }

    public void initUI() {
        removeAllComponents();
        btnAdd.setCaption(("Add file"));
        btnAdd.setStyleName(ValoTheme.BUTTON_LINK);
        btnAdd.setIcon(FontAwesome.PLUS_CIRCLE);
        setMargin(true);
        setSpacing(true);
        setWidth(100, Sizeable.Unit.PERCENTAGE);
        for (FileModel fileModel : processModel.getFileModels()) {
            FileForm fileForm = new FileForm();
            fileForm.setFileModel(fileModel);
            addComponent(fileForm);
        }
        addComponent(btnAdd);
        setComponentAlignment(btnAdd, Alignment.MIDDLE_RIGHT);
    }

    public ProcessModel getProcessModel() {
        return processModel;
    }

    public void setProcessModel(ProcessModel processModel) {
        this.processModel = processModel;
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton().equals(btnAdd)) {
            FileForm fileForm = new FileForm();
            fileForm.setFileModel(new FileModel("file", ""));
            addComponent(fileForm, getComponentIndex(btnAdd));
        }
    }
}
