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
import com.hybridbpm.ui.component.TableButtonBar;
import com.hybridbpm.core.data.document.Document;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.component.ConfigureWindow;
import com.hybridbpm.ui.component.PermissionsColumnGenerator;
import com.hybridbpm.ui.component.TableButton;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.declarative.Design;
import java.util.List;
import org.vaadin.dialogs.ConfirmDialog;

@DesignRoot
@SuppressWarnings("serial")
public final class DocumentAccessLayout extends VerticalLayout implements Button.ClickListener, Window.CloseListener {

    public Table accessTable;
    private Document document;
    private boolean editable = false;

    public DocumentAccessLayout() {
        Design.read(this);
        accessTable.addContainerProperty("role", String.class, null, "Role", null, Table.Align.LEFT);
        accessTable.addContainerProperty("permission", List.class, null, "Permission", null, Table.Align.LEFT);
        accessTable.addContainerProperty("actions", TableButtonBar.class, null, "Actions", null, Table.Align.LEFT);
        accessTable.addGeneratedColumn("permission", new PermissionsColumnGenerator());
        accessTable.setColumnWidth("actions", 55);
        accessTable.setVisibleColumns("role", "permission", "actions");
    }

    public void setDocument(Document document) {
        this.document = document;
        List<Permission.PERMISSION> perms = HybridbpmUI.getDocumentAPI().getMyDocumentPermissions(document);
        editable = perms.contains(Permission.PERMISSION.PERMISSIONS);
    }

    public void refreshTable() {
        accessTable.removeAllItems();
        for (Permission permission : HybridbpmUI.getDocumentAPI().getDocumentPermissions(document)) {
            addToTable(permission);
        }
        accessTable.sort(new Object[]{ "role", "permission"}, new boolean[]{true, true, true});
    }

    private void addToTable(Permission permission) {
        Item item = accessTable.addItem(permission);
        item.getItemProperty("role").setValue(permission.getOut().getTitle().getValue(HybridbpmUI.getCurrent().getLocale()));
        item.getItemProperty("permission").setValue(permission.getPermissions());
        item.getItemProperty("actions").setValue(getTableButtonBar(permission));
    }

    private Object getTableButtonBar(Permission permission) {
        if (editable) {
            TableButton editButton = TableButton.createEdit(permission, this);
            TableButton deleteButton = TableButton.createDelete(permission, this);
            return new TableButtonBar(editButton, deleteButton);
        } else {
            return new TableButtonBar();
        }
    }

    @Override
    public void buttonClick(final Button.ClickEvent event) {
        if (event.getButton() instanceof TableButton && ((TableButton) event.getButton()).getType().equals(TableButton.TYPE.EDIT)) {
            addPermission(document, ((TableButton<Permission>) event.getButton()).getCustomData());
        } else  if (event.getButton() instanceof TableButton && ((TableButton) event.getButton()).getType().equals(TableButton.TYPE.DELETE)) {
            ConfirmDialog.show(UI.getCurrent(), "Please Confirm:", "Delete permission?", "OK", "Cancel", new ConfirmDialog.Listener() {

                @Override
                public void onClose(ConfirmDialog dialog) {
                    if (dialog.isConfirmed()) {
                        Permission permission = ((TableButton<Permission>) event.getButton()).getCustomData();
                        HybridbpmUI.getDocumentAPI().removeDocumentPermission(permission.getId().toString());
                        refreshTable();
                    }
                }
            });
        }
    }

    protected void addPermission(Document document, Permission permission) {
        final DocumentPermissionLayout documentPermissionLayout = new DocumentPermissionLayout(document, permission);
        final ConfigureWindow configureWindow = new ConfigureWindow(documentPermissionLayout, "Permission");
        Button.ClickListener clickListener = new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (event.getButton().equals(configureWindow.btnClose)) {

                } else if (event.getButton().equals(configureWindow.btnOk)) {
                    documentPermissionLayout.save();
                }
                configureWindow.close();
            }
        };
        configureWindow.setClickListener(clickListener);
        configureWindow.addCloseListener(this);
        configureWindow.setSizeUndefined();
        HybridbpmUI.getCurrent().addWindow(configureWindow);
    }

    @Override
    public void windowClose(Window.CloseEvent e) {
        refreshTable();
    }

}
