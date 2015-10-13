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
package com.hybridbpm.ui.component.dashboard;

import com.hybridbpm.core.data.access.Permission;
import com.hybridbpm.core.data.dashboard.TabDefinition;
import com.hybridbpm.ui.component.*;
import com.hybridbpm.core.data.dashboard.ViewDefinition;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.component.dashboard.tab.TabConfigurationLayout;
import com.hybridbpm.ui.util.Translate;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.vaadin.dialogs.ConfirmDialog;

/**
 *
 * @author Marat Gubaidullin
 */
public final class ViewManager {
    
    public static void editViewDefinitionWindow(ViewDefinition definition) {
        final ViewConfigurationLayout viewLayout = new ViewConfigurationLayout(definition);
        final ConfigureWindow configureWindow = new ConfigureWindow(viewLayout, definition == null ? "Add view" : "Edit view");
        Button.ClickListener clickListener = new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (event.getButton().equals(configureWindow.btnClose)) {
                    configureWindow.close();
                } else if (event.getButton().equals(configureWindow.btnOk)) {
                    try {
                        ViewDefinition viewDefinition = viewLayout.getViewDefinition();
                        List<Permission> permissions = viewLayout.getViewPermissions();
                        HybridbpmUI.getDashboardAPI().saveViewDefinition(viewDefinition, permissions, true);
                        configureWindow.close();
                        HybridbpmUI.navigateTo(viewDefinition.getUrl());
                    } catch (Exception ex) {
                        Logger.getLogger(ViewsLayout.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                    }
                }
            }
        };
        configureWindow.setClickListener(clickListener);
        HybridbpmUI.getCurrent().addWindow(configureWindow);
    }
    
    public static void deleteViewDefinitionWindow(final ViewDefinition vd) {
        final ViewDefinition viewDefinition = vd;
        ConfirmDialog.show(UI.getCurrent(), Translate.getMessage("windowTitleConfirm"), "Delete view?", Translate.getMessage("btnOK"), Translate.getMessage("btnCancel"), new ConfirmDialog.Listener() {

                @Override
                public void onClose(ConfirmDialog dialog) {
                    if (dialog.isConfirmed()) {
                        HybridbpmUI.getDashboardAPI().deleteViewDefinition(viewDefinition.getId(), true);
                        HybridbpmUI.navigateTo(viewDefinition.getUrl());
                    } else {
//                                this.close();
                    }
                }
            });
    }
    
    public static void editTabDefinitionWindow(TabDefinition tabDefinition, ViewDefinition viewDefinition) {
        final TabConfigurationLayout tabConfigurationLayout = new TabConfigurationLayout(tabDefinition, viewDefinition);
        final ConfigureWindow configureWindow = new ConfigureWindow(tabConfigurationLayout, viewDefinition == null ? "Add tab" : "Edit tab");
        Button.ClickListener clickListener = new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (event.getButton().equals(configureWindow.btnClose)) {
                    configureWindow.close();
                } else if (event.getButton().equals(configureWindow.btnOk)) {
                    try {
                        TabDefinition tabDefinition = tabConfigurationLayout.getTabDefinition();
                        List<Permission> permissions = tabConfigurationLayout.getViewPermissions();
                        HybridbpmUI.getDashboardAPI().saveTabDefinition(tabDefinition, permissions, true);
                        configureWindow.close();
                    } catch (Exception ex) {
                        Logger.getLogger(ViewsLayout.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                    }
                }
            }
        };
        configureWindow.setClickListener(clickListener);
        HybridbpmUI.getCurrent().addWindow(configureWindow);
    }
    
}
