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

import com.hybridbpm.ui.component.development.FormEditor;
import com.hybridbpm.ui.component.development.ModuleLinkButton;
import com.hybridbpm.ui.component.TableButtonBar;
import com.hybridbpm.core.data.development.Module;
import com.hybridbpm.core.util.DashboardConstant;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.component.ConfigureWindow;
import com.hybridbpm.ui.component.TableButton;
import com.hybridbpm.ui.component.bpm.DateColumnGenerator;
import com.hybridbpm.ui.component.bpm.designer.ProcessEditor;
import com.hybridbpm.ui.component.development.AbstractEditor;
import com.hybridbpm.ui.component.chart.ChartEditor;
import com.hybridbpm.ui.component.development.ConnectorEditor;
import com.hybridbpm.ui.component.development.DataEditor;
import com.hybridbpm.ui.component.development.ModuleLayout;
import com.hybridbpm.ui.util.Translate;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.Table;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.declarative.Design;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.vaadin.dialogs.ConfirmDialog;

@DesignRoot
@SuppressWarnings("serial")
public final class DevelopmentView extends AbstractView implements View, Button.ClickListener, Window.CloseListener, Property.ValueChangeListener {

    private static final Logger logger = Logger.getLogger(DevelopmentView.class.getSimpleName());
    public static final String VIEW_URL = DashboardConstant.VIEW_URL_DEVELOPMENT;
    public static final String TITLE = "Development";
    public static final String ICON = FontAwesome.COG.name();
    public static final Integer ORDER = Integer.MAX_VALUE - 1;

    public VerticalLayout panelLayout;
    public TabSheet tabSheet;
    public VerticalLayout modulesLayout;
    private Button btnAdd;
    private Button btnRefresh;
    private Button btnExport;
    private Button btnImport;
    private Button btnRegenerate;
    private OptionGroup moduleType;
    public TreeTable modulesTable;

    public DevelopmentView() {
        Design.read(this);
        Responsive.makeResponsive(panelLayout);

        moduleType.addContainerProperty("NAME", String.class, null);
        moduleType.addItem(Boolean.FALSE).getItemProperty("NAME").setValue("Module");
        moduleType.addItem(Boolean.TRUE).getItemProperty("NAME").setValue("Template");
        moduleType.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        moduleType.setItemCaptionPropertyId("NAME");
        moduleType.setValue(Boolean.FALSE);
        moduleType.addValueChangeListener(this);

        btnAdd.setIcon(FontAwesome.PLUS_CIRCLE);
        btnAdd.addClickListener(this);

        btnRefresh.setIcon(FontAwesome.REFRESH);
        btnRefresh.addClickListener(this);

        btnExport.setIcon(FontAwesome.CLOUD_UPLOAD);
        btnExport.addClickListener(this);

        btnImport.setIcon(FontAwesome.CLOUD_DOWNLOAD);
        btnImport.addClickListener(this);
        
        btnRegenerate.setIcon(FontAwesome.WRENCH);
        btnRegenerate.addClickListener(this);

        modulesLayout.setMargin(new MarginInfo(true, false, false, false));
        modulesLayout.setExpandRatio(modulesTable, 1f);

        modulesTable.addContainerProperty("title", Component.class, null, "Title", null, Table.Align.LEFT);
        modulesTable.setColumnExpandRatio("title", 1f);
        modulesTable.addContainerProperty("updateDate", Date.class, null, "Update Date", null, Table.Align.LEFT);
        modulesTable.addContainerProperty("actions", TableButtonBar.class, null, "Actions", null, Table.Align.LEFT);
        modulesTable.setColumnWidth("updateDate", 150);
        modulesTable.setColumnWidth("actions", 80);
        modulesTable.addGeneratedColumn("updateDate", new DateColumnGenerator());
        modulesTable.setVisibleColumns("title", "updateDate", "actions");
    }

    @Override
    public void enter(ViewChangeEvent event) {
        refreshData();
    }

    @Override
    public void buttonClick(final Button.ClickEvent event) {
        if (event.getButton().equals(btnAdd)) {
            openModule(null, this);
        } else if (event.getButton().equals(btnRefresh)) {
            refreshData();
        } else if (event.getButton().equals(btnExport)) {
            HybridbpmUI.getDevelopmentAPI().saveScriptsToFileSystem(true);
        } else if (event.getButton().equals(btnRegenerate)) {
            HybridbpmUI.getDevelopmentAPI().regenerateGroovySources();
        } else if (event.getButton().equals(btnImport)) {
            HybridbpmUI.getDevelopmentAPI().updateScriptsFromFileSystem();
            refreshData();
        } else if (event.getButton() instanceof ModuleLinkButton) {
            openModuleTab((ModuleLinkButton) event.getButton());
        } else if (event.getButton() instanceof TableButton && ((TableButton) event.getButton()).getType().equals(TableButton.TYPE.EDIT)) {
            Module Module = ((TableButton<Module>) event.getButton()).getCustomData();
            Module = HybridbpmUI.getDevelopmentAPI().getModuleById(Module.getId());
            openModule(Module, this);
        } else if (event.getButton() instanceof TableButton && ((TableButton) event.getButton()).getType().equals(TableButton.TYPE.DELETE)) {
            ConfirmDialog.show(UI.getCurrent(), Translate.getMessage("windowTitleConfirm"), "Delete module?", Translate.getMessage("btnOK"), Translate.getMessage("btnCancel"), new ConfirmDialog.Listener() {

                @Override
                public void onClose(ConfirmDialog dialog) {
                    if (dialog.isConfirmed()) {
                        Module Module = ((TableButton<Module>) event.getButton()).getCustomData();
                        HybridbpmUI.getDevelopmentAPI().deleteDefinition(Module.getId(), false);
                        refreshData();
                    }
                }
            });
        }
    }

    private void refreshData() {
        modulesTable.removeAllItems();
        modulesTable.addItem(Module.MODULE_TYPE.DATA).getItemProperty("title").setValue(new Label(Module.MODULE_TYPE.DATA.name()));
        modulesTable.addItem(Module.MODULE_TYPE.FORM).getItemProperty("title").setValue(new Label(Module.MODULE_TYPE.FORM.name()));
//        modulesTable.addItem(Module.MODULE_TYPE.MOBILE).getItemProperty("title").setValue(new Label(Module.MODULE_TYPE.MOBILE.name()));
        modulesTable.addItem(Module.MODULE_TYPE.PROCESS).getItemProperty("title").setValue(new Label(Module.MODULE_TYPE.PROCESS.name()));
        modulesTable.addItem(Module.MODULE_TYPE.CONNECTOR).getItemProperty("title").setValue(new Label(Module.MODULE_TYPE.CONNECTOR.name()));
        modulesTable.addItem(Module.MODULE_TYPE.CHART).getItemProperty("title").setValue(new Label(Module.MODULE_TYPE.CHART.name()));

        for (Module Module : HybridbpmUI.getDevelopmentAPI().getModuleList((Boolean) moduleType.getValue())) {
            Item item = modulesTable.addItem(Module.getId());
            modulesTable.setParent(Module.getId(), Module.getType());
            modulesTable.setChildrenAllowed(Module.getId(), false);
            item.getItemProperty("title").setValue(new ModuleLinkButton(Module, this));
            item.getItemProperty("updateDate").setValue(Module.getUpdateDate());
            item.getItemProperty("actions").setValue(getTableButtonBar(Module));
        }
        modulesTable.sort(new Object[]{"title"}, new boolean[]{false});
    }

    private void openModuleTab(ModuleLinkButton moduleLinkButton) {
        Tab tab = getModuleTab(moduleLinkButton.getModule());
        if (tab == null) {
            switch (moduleLinkButton.getModule().getType()) {
                case FORM:
                    tab = tabSheet.addTab(new FormEditor(moduleLinkButton.getModule()), moduleLinkButton.getCaption());
                    tab.setClosable(true);
                    break;
                case CONNECTOR:
                    tab = tabSheet.addTab(new ConnectorEditor(moduleLinkButton.getModule()), moduleLinkButton.getCaption());
                    tab.setClosable(true);
                    break;
                case DATA:
                    tab = tabSheet.addTab(new DataEditor(moduleLinkButton.getModule()), moduleLinkButton.getCaption());
                    tab.setClosable(true);
                    break;
                case PROCESS:
                    tab = tabSheet.addTab(new ProcessEditor(moduleLinkButton.getModule()), moduleLinkButton.getCaption());
                    tab.setClosable(true);
                    break;
                case CHART:
                    tab = tabSheet.addTab(new ChartEditor(moduleLinkButton.getModule()), moduleLinkButton.getCaption());
                    tab.setClosable(true);
                    break;
            }

        }
        tabSheet.setSelectedTab(tab);
    }

    public void openTab(Component component, Module Module) {
        Tab tab = getTestTab(Module);
        if (tab != null) {
            tabSheet.removeTab(tab);
        }
        tab = tabSheet.addTab(component, Module.getName());
        tab.setClosable(true);
        tabSheet.setSelectedTab(tab);
    }

    private Tab getModuleTab(Module Module) {
        for (Component component : tabSheet) {
            if (component instanceof AbstractEditor && Objects.equals(((AbstractEditor) component).getModule().getId(), Module.getId())) {
                return tabSheet.getTab(component);
            }
        }
        return null;
    }
    
     private Tab getTestTab(Module module) {
        for (Component component : tabSheet) {
            if (Objects.equals(module.getType(), Module.MODULE_TYPE.FORM)
                    && Objects.equals(module.getSubType(), Module.MODULE_SUBTYPE.TASK_FORM)
                    && tabSheet.getTab(component).getCaption().equals(module.getName())) {
                return tabSheet.getTab(component);
            }
        }
        return null;
    }

    @Override
    public void windowClose(Window.CloseEvent e) {
        refreshData();
    }

    private Object getTableButtonBar(Module Module) {
        TableButton editButton = TableButton.createEdit(Module, this);
        editButton.setVisible(!Module.getSystem());
        TableButton deleteButton = TableButton.createDelete(Module, this);
        deleteButton.setVisible(!Module.getSystem());
        return new TableButtonBar(editButton, deleteButton);
    }

    public static void openModule(Module Module, Window.CloseListener closeListener) {
        final ModuleLayout moduleLayout = new ModuleLayout(Module);
        final ConfigureWindow configureWindow = new ConfigureWindow(moduleLayout, "New Module");
        Button.ClickListener clickListener = new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                if (event.getButton().equals(configureWindow.btnClose)) {

                } else if (event.getButton().equals(configureWindow.btnOk)) {
                    moduleLayout.save();
                }
                configureWindow.close();
                } catch (RuntimeException re){
                    logger.log(Level.SEVERE, re.getMessage(), re);
                    Notification.show("Error", re.getMessage(), Notification.Type.ERROR_MESSAGE);
                }
            }
        };
        configureWindow.setClickListener(clickListener);
        configureWindow.addCloseListener(closeListener);
        HybridbpmUI.getCurrent().addWindow(configureWindow);
    }

    @Override
    public void valueChange(Property.ValueChangeEvent event) {
        refreshData();
    }

}
