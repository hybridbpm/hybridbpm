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
package com.hybridbpm.ui.component.document;

import com.hybridbpm.core.data.access.Permission;
import com.hybridbpm.core.data.document.Document;
import com.hybridbpm.ui.HybridbpmUI;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import java.util.Objects;

/**
 *
 * @author Marat Gubaidullin
 */
@DesignRoot
@SuppressWarnings("serial")
public class DocumentLayout extends VerticalLayout implements Button.ClickListener, TabSheet.SelectedTabChangeListener {

    private String documentId;
    private final Document document;
    private TabSheet tabSheet;
    private DocumentFormLayout documentFormLayout;
    private DocumentHistoryLayout documentHistoryLayout;
    private DocumentAccessLayout documentAccessLayout;
    private Button btnSave;
    private Button btnPermission;
    private HorizontalLayout buttonBar;

    public DocumentLayout(String documentId) {
        this.documentId = documentId;
        document = HybridbpmUI.getDocumentAPI().getDocumentById(documentId, false);
        setCaption(document.getName());
        Design.read(this);
        documentFormLayout.initUI(document);
        documentHistoryLayout.setDocument(document);
        documentHistoryLayout.refreshTable();
        Responsive.makeResponsive(this);
        btnSave.setIcon(FontAwesome.SAVE);
        btnSave.addClickListener(this);
        btnPermission.setIcon(FontAwesome.PLUS);
        btnPermission.addClickListener(this);
        btnPermission.setVisible(false);
        tabSheet.addSelectedTabChangeListener(this);
        if (Objects.equals(Document.TYPE.FOLDER, document.getType())) {
            tabSheet.getTab(documentFormLayout).setCaption("Folder");
            tabSheet.getTab(documentHistoryLayout).setVisible(false);
        } else if (Objects.equals(Document.TYPE.FILE, document.getType())) {
            tabSheet.getTab(documentFormLayout).setCaption("File");
            tabSheet.getTab(documentHistoryLayout).setVisible(true);
        }
        if (HybridbpmUI.getDocumentAPI().getMyDocumentPermissions(document).contains(Permission.PERMISSION.PERMISSIONS)){
            documentAccessLayout.setDocument(document);
            documentAccessLayout.refreshTable();
            tabSheet.getTab(documentAccessLayout).setVisible(true);
        } else {
            tabSheet.getTab(documentAccessLayout).setVisible(false);
        }
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton().equals(btnSave)) {
            documentFormLayout.save();
            documentHistoryLayout.refreshTable();
        }
        if (event.getButton().equals(btnPermission)) {
            documentAccessLayout.addPermission(document, null);
        }
    }

    public String getDocumentId() {
        return documentId;
    }

    @Override
    public void selectedTabChange(TabSheet.SelectedTabChangeEvent event) {
        if (event.getTabSheet().getSelectedTab().equals(documentAccessLayout)){
            btnPermission.setVisible(true);
        } else {
            btnPermission.setVisible(false);
        }
    }

}
