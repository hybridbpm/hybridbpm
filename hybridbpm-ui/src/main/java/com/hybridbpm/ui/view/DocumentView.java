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
package com.hybridbpm.ui.view;

import com.hybridbpm.core.data.access.Permission;
import com.hybridbpm.ui.component.TableButtonBar;
import com.hybridbpm.core.data.document.Document;
import com.hybridbpm.core.util.DashboardConstant;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.component.ConfigureWindow;
import com.hybridbpm.ui.component.TableButton;
import com.hybridbpm.ui.component.bpm.DateColumnGenerator;
import com.hybridbpm.ui.component.document.DocumentBreadcrumbButton;
import com.hybridbpm.ui.component.document.DocumentFormLayout;
import com.hybridbpm.ui.component.document.DocumentLayout;
import com.hybridbpm.ui.component.document.DocumentColumnGenerator;
import com.hybridbpm.ui.util.Translate;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.data.Item;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.declarative.Design;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.vaadin.dialogs.ConfirmDialog;

@DesignRoot
@SuppressWarnings("serial")
public final class DocumentView extends AbstractView implements View, Button.ClickListener, Window.CloseListener, TabSheet.SelectedTabChangeListener {

    public static final String VIEW_URL = DashboardConstant.VIEW_URL_DOCUMENT;
    public static final String TITLE = Translate.getMessage("titleDocuments");
    public static final String ICON = FontAwesome.FILES_O.name();
    public static final Integer ORDER = Integer.MAX_VALUE - 1;

    private VerticalLayout panelLayout;
    private TabSheet tabSheet;
    private VerticalLayout documentsLayout;
    private HorizontalLayout breadcrumbs;
    private TextField textFieldSearch;
    private Button btnSearch;
    private Button btnAddFolder;
    private Button btnAddFile;
    private Button btnRefresh;
    private Table documentTable;
    private Document parent;
    private String parentId;

    public DocumentView() {
        Design.read(this);
        tabSheet.getTab(documentsLayout).setCaption(Translate.getMessage("Documents"));
        btnSearch.setCaption(Translate.getMessage("btnSearch"));
        btnRefresh.setCaption(Translate.getMessage("btnRefresh"));
        btnAddFile.setCaption(Translate.getMessage("btnAddFile"));
        btnAddFolder.setCaption(Translate.getMessage("btnAddFolder"));
        textFieldSearch.setCaption(Translate.getMessage("textFieldSearch"));
        
        Responsive.makeResponsive(panelLayout);
        btnAddFolder.setIcon(FontAwesome.FOLDER_O);
        btnAddFolder.addClickListener(this);
        btnAddFile.setIcon(FontAwesome.FILE_O);
        btnAddFile.addClickListener(this);

        btnRefresh.setIcon(FontAwesome.REFRESH);
        btnRefresh.addClickListener(this);

        textFieldSearch.setIcon(FontAwesome.SEARCH);

        documentsLayout.setMargin(new MarginInfo(true, false, false, false));
        documentsLayout.setExpandRatio(documentTable, 1f);

        documentTable.addContainerProperty("name", String.class, null, Translate.getMessage("tableDocumentsName"), null, Table.Align.LEFT);
        documentTable.setColumnExpandRatio("name", 1f);
        documentTable.addContainerProperty("description", String.class, null, Translate.getMessage("tableDocumentsTitle"), null, Table.Align.LEFT);
        documentTable.addContainerProperty("creator", String.class, null, Translate.getMessage("tableDocumentsCreator"), null, Table.Align.LEFT);
        documentTable.addContainerProperty("createDate", Date.class, null, Translate.getMessage("tableDocumentsCreateDate"), null, Table.Align.LEFT);
        documentTable.addContainerProperty("updateDate", Date.class, null, Translate.getMessage("tableDocumentsUpdateDate"), null, Table.Align.LEFT);
        documentTable.addContainerProperty("actions", TableButtonBar.class, null, Translate.getMessage("tableDocumentsActions"), null, Table.Align.LEFT);
        documentTable.setColumnWidth("createDate", 150);
        documentTable.setColumnWidth("updateDate", 150);
        documentTable.setColumnWidth("actions", 55);
        documentTable.addGeneratedColumn("name", new DocumentColumnGenerator(this));
        documentTable.addGeneratedColumn("createDate", new DateColumnGenerator());
        documentTable.addGeneratedColumn("updateDate", new DateColumnGenerator());
        documentTable.setVisibleColumns("name", "description", "creator", "createDate", "updateDate", "actions");
        tabSheet.addSelectedTabChangeListener(this);
    }

    @Override
    public void enter(ViewChangeEvent event) {
        if (event.getParameters() != null && !event.getParameters().trim().isEmpty()) {
            openDocumentTab(event.getParameters());
        } else if (documentTable.getItemIds().isEmpty()) {
            parentId = null;
            refreshData();
        }
    }

    private void refreshData() {
        checkBreadcrumbs();
        checkButtonBar();
        documentTable.removeAllItems();
        Map<Document, List<Permission.PERMISSION>> documents = HybridbpmUI.getDocumentAPI().getMyDocuments(parentId);
        for (Document document : documents.keySet()) {
            List<Permission.PERMISSION> perms = documents.get(document);
            addDocumentToTable(document, null, perms);
        }
        documentTable.sort(new Object[]{"name"}, new boolean[]{true});
    }

    private void checkBreadcrumbs() {
        if (parentId != null) {
            parent = HybridbpmUI.getDocumentAPI().getDocumentById(parentId, false);
            List<Document> parents = HybridbpmUI.getDocumentAPI().getMyDocumentBreadcumbs(parentId);
            breadcrumbs.setVisible(true);
            breadcrumbs.removeAllComponents();
            breadcrumbs.addComponents(new DocumentBreadcrumbButton(null, this));
            for (Document document : parents) {
                breadcrumbs.addComponent(new DocumentBreadcrumbButton(document, this));
            }
        } else {
            breadcrumbs.setVisible(false);
        }
    }

    private void checkButtonBar() {
        if (parent == null) {
            if (HybridbpmUI.getAccessAPI().isAdministrator()) {
                btnAddFolder.setVisible(true);
                btnAddFile.setVisible(false);
            } else {
                btnAddFolder.setVisible(false);
                btnAddFile.setVisible(false);
            }
        } else {
            btnAddFile.setVisible(false);
            btnAddFolder.setVisible(false);
            List<Permission.PERMISSION> permissions = HybridbpmUI.getDocumentAPI().getMyDocumentPermissions(parentId);
            if (permissions.contains(Permission.PERMISSION.ADD_FILE)) {
                btnAddFile.setVisible(true);
            }
            if (permissions.contains(Permission.PERMISSION.ADD_FOLDER)) {
                btnAddFolder.setVisible(true);
            }
        }
    }

    private void addDocumentToTable(Document document, Document parent, List<Permission.PERMISSION> permissions) {
        Item item = documentTable.addItem(document);
        item.getItemProperty("name").setValue(document.getName());
        item.getItemProperty("description").setValue(document.getDescription().getValue(HybridbpmUI.getCurrent().getLocale()));
        item.getItemProperty("creator").setValue(document.getCreator());
        item.getItemProperty("createDate").setValue(document.getCreateDate());
        item.getItemProperty("updateDate").setValue(document.getUpdateDate());
        item.getItemProperty("actions").setValue(getTableButtonBar(document, permissions));
    }

    @Override
    public void buttonClick(final Button.ClickEvent event) {
        if (event.getButton().equals(btnAddFolder)) {
            addFolder();
        } else if (event.getButton().equals(btnAddFile)) {
            addFile();
        } else if (event.getButton().equals(btnRefresh)) {
            refreshData();
        } else if (event.getButton().getData() != null && event.getButton().getData() instanceof Document) {
            Document document = (Document) event.getButton().getData();
            parentId = document.getId().toString();
            refreshData();
        } else if (event.getButton() instanceof DocumentBreadcrumbButton) {
            Document document = ((DocumentBreadcrumbButton) event.getButton()).getDocument();
            parentId = document !=null ? document.getId().toString() : null;
            refreshData();
        } else if (event.getButton() instanceof TableButton && ((TableButton) event.getButton()).getType().equals(TableButton.TYPE.EDIT)) {
            Document document = ((TableButton<Document>) event.getButton()).getCustomData();
            openDocumentTab(document.getId().toString());
        } else if (event.getButton() instanceof TableButton && ((TableButton) event.getButton()).getType().equals(TableButton.TYPE.DELETE)) {
            ConfirmDialog.show(UI.getCurrent(), 
                    Translate.getMessage("windowTitleConfirm"), 
                    Translate.getMessage("deleteFileQuestion"), 
                    Translate.getMessage("btnOK"), 
                    Translate.getMessage("btnCancel"), (ConfirmDialog dialog) -> {
                        if (dialog.isConfirmed()) {
                            Document document = ((TableButton<Document>) event.getButton()).getCustomData();
                            HybridbpmUI.getDocumentAPI().removeDocument(document.getId().toString());
                            documentTable.removeItem(document);
                        }
            });
        }
    }

    private void openDocumentTab(String documentId) {
        Tab tab = getTab(documentId);
        if (tab == null) {
            if (HybridbpmUI.getDocumentAPI().getMyDocumentPermissions(documentId).contains(Permission.PERMISSION.VIEW)) {
                tab = tabSheet.addTab(new DocumentLayout(documentId));
                tab.setClosable(true);
                tabSheet.setSelectedTab(tab);
            }
        } else {
            tabSheet.setSelectedTab(tab);
        }

    }

    private Tab getTab(String documentId) {
        for (Component component : tabSheet) {
            if (component instanceof DocumentLayout && Objects.equals(((DocumentLayout) component).getDocumentId(), documentId)) {
                return tabSheet.getTab(component);
            }
        }
        return null;
    }

    @Override
    public void windowClose(Window.CloseEvent e) {
        refreshData();
    }

    private Object getTableButtonBar(Document document, List<Permission.PERMISSION> permissions) {
        TableButtonBar buttonBar = new TableButtonBar();
        if (permissions.contains(Permission.PERMISSION.EDIT)) {
            buttonBar.addComponent(TableButton.createEdit(document, this));
        }
        if (permissions.contains(Permission.PERMISSION.DELETE)) {
            buttonBar.addComponent(TableButton.createDelete(document, this));
        }
        return buttonBar;
    }

    public void addFolder() {
        final DocumentFormLayout documentFormLayout = new DocumentFormLayout();
        documentFormLayout.initUI(Document.createFolder(parent));
        final ConfigureWindow configureWindow = new ConfigureWindow(documentFormLayout, Translate.getMessage("windowNewFolder"));
        Button.ClickListener clickListener = new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (event.getButton().equals(configureWindow.btnClose)) {

                } else if (event.getButton().equals(configureWindow.btnOk)) {
                    documentFormLayout.save();
                }
                configureWindow.close();
            }
        };
        configureWindow.setClickListener(clickListener);
        configureWindow.addCloseListener(this);
        HybridbpmUI.getCurrent().addWindow(configureWindow);
    }

    public void addFile() {
        final DocumentFormLayout documentFormLayout = new DocumentFormLayout();
        documentFormLayout.initUI(Document.createFile(parent));
        final ConfigureWindow configureWindow = new ConfigureWindow(documentFormLayout, Translate.getMessage("windowNewFile"));
        Button.ClickListener clickListener = new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (event.getButton().equals(configureWindow.btnClose)) {

                } else if (event.getButton().equals(configureWindow.btnOk)) {
                    documentFormLayout.save();
                }
                configureWindow.close();
            }
        };
        configureWindow.setClickListener(clickListener);
        configureWindow.addCloseListener(this);
        HybridbpmUI.getCurrent().addWindow(configureWindow);
    }

    @Override
    public void selectedTabChange(TabSheet.SelectedTabChangeEvent event) {
        if (event.getTabSheet().getSelectedTab().equals(documentsLayout)) {
            HybridbpmUI.getCurrent().getHybridbpmNavigator().navigateTo(DocumentView.VIEW_URL);
        } else if (event.getTabSheet().getSelectedTab() instanceof DocumentLayout) {
            HybridbpmUI.getCurrent().getHybridbpmNavigator().navigateTo(DocumentView.VIEW_URL + "/" + ((DocumentLayout) event.getTabSheet().getSelectedTab()).getDocumentId());
        }
    }
}
