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
package com.hybridbpm.ui.component.configuration;

import com.hybridbpm.core.data.Parameter;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.component.AbstractTreeTableLayout;
import com.hybridbpm.ui.component.ConfigureWindow;
import com.hybridbpm.ui.component.TableButton;
import com.hybridbpm.ui.component.TableButtonBar;
import com.vaadin.data.Item;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.vaadin.dialogs.ConfirmDialog;

@SuppressWarnings("serial")
public class SystemParametersLayout extends AbstractTreeTableLayout {

    public SystemParametersLayout() {
        super();
        btnAdd.setDescription("Add parameter");
        setMargin(new MarginInfo(true, false, false, false));
    }

    @Override
    public void buttonClick(final Button.ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton() instanceof TableButton && ((TableButton)event.getButton()).getType().equals(TableButton.TYPE.EDIT)) {
            Parameter parameterInstance = ((TableButton<Parameter>) event.getButton()).getCustomData();
            editParameterWindow(parameterInstance);
        } else if (event.getButton() instanceof TableButton && ((TableButton)event.getButton()).getType().equals(TableButton.TYPE.DELETE)) {
            Parameter parameterInstance = ((TableButton<Parameter>) event.getButton()).getCustomData();
            deleteParameterWindow(parameterInstance);
        }
    }

    @Override
    public void prepareTable() {
        iTable.addContainerProperty("name", String.class, null, "Name", null, Table.Align.LEFT);
        iTable.addContainerProperty("value", String.class, null, "Value", null, Table.Align.LEFT);
        iTable.addContainerProperty("actions", TableButtonBar.class, null, "Actions", null, Table.Align.LEFT);
        iTable.setColumnWidth("actions", 80);
        iTable.setVisibleColumns("name", "value", "actions");
    }

    @Override
    public void refreshTable() {
        iTable.removeAllItems();

        iTable.addItem(Parameter.PARAM_TYPE.SYSTEM).getItemProperty("name").setValue(Parameter.PARAM_TYPE.SYSTEM.name());
        iTable.addItem(Parameter.PARAM_TYPE.CONTEXT).getItemProperty("name").setValue(Parameter.PARAM_TYPE.CONTEXT.name());

        for (Parameter parameterInstance : HybridbpmUI.getSystemAPI().getParameters()) {
            Item item = iTable.addItem(parameterInstance.getId());
            iTable.setParent(parameterInstance.getId(), parameterInstance.getType());
            iTable.setChildrenAllowed(parameterInstance.getId(), false);
            item.getItemProperty("name").setValue(parameterInstance.getName());
            item.getItemProperty("value").setValue(parameterInstance.getValue());
            item.getItemProperty("actions").setValue(getTableButtonBar(parameterInstance));
        }
        iTable.sort(new Object[]{"order"}, new boolean[]{true});
    }

    private Object getTableButtonBar(Parameter parameterInstance) {
        TableButton editButton = TableButton.createEdit(parameterInstance, this);
        TableButton deleteButton = TableButton.createDelete(parameterInstance, this);
        deleteButton.setVisible(parameterInstance.getType().equals(Parameter.PARAM_TYPE.CONTEXT));
        return new TableButtonBar(editButton, deleteButton);
    }

    @Override
    public void addNew() {
        editParameterWindow(null);
    }

    public void editParameterWindow(Parameter parameterInstance) {
        final SystemParameterLayout parameterLayout = new SystemParameterLayout(parameterInstance);
        final ConfigureWindow configureWindow = new ConfigureWindow(parameterLayout, parameterInstance == null ? "Add parameter" : "Edit parameter");
        Button.ClickListener clickListener = new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (event.getButton().equals(configureWindow.btnClose)) {
                    configureWindow.close();
                } else if (event.getButton().equals(configureWindow.btnOk)) {
                    try {
                        Parameter parameterInstance = parameterLayout.getParameter();
                        HybridbpmUI.getSystemAPI().saveParameter(parameterInstance);
                        configureWindow.close();
                        refreshTable();
                    } catch (Exception ex) {
                        Logger.getLogger(SystemParametersLayout.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                    }
                }
            }
        };
        configureWindow.setClickListener(clickListener);
        configureWindow.setWidth(50, Unit.PERCENTAGE);
        configureWindow.setHeight(70, Unit.PERCENTAGE);
        HybridbpmUI.getCurrent().addWindow(configureWindow);
    }

    public void deleteParameterWindow(final Parameter pi) {
        ConfirmDialog.show(UI.getCurrent(), "Please Confirm:", "Delete parameter?", "OK", "Cancel", new ConfirmDialog.Listener() {

            @Override
            public void onClose(ConfirmDialog dialog) {
                if (dialog.isConfirmed()) {
                    HybridbpmUI.getSystemAPI().deleteContextParameter(pi.getId());
                    refreshTable();
                } else {
//                                this.close();
                }
            }
        });
    }

}
