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

import com.hybridbpm.core.data.dashboard.TabDefinition;
import com.hybridbpm.core.data.dashboard.ViewDefinition;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.component.AbstractTableLayout;
import com.hybridbpm.ui.component.dashboard.ViewManager;
import com.hybridbpm.ui.component.dashboard.tab.DashboardTab;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.vaadin.dialogs.ConfirmDialog;

@DesignRoot
@SuppressWarnings("serial")
public final class DashboardView extends AbstractView implements View, Button.ClickListener, TabSheet.SelectedTabChangeListener, TabSheet.CloseHandler {

    private static final Logger logger = Logger.getLogger(DashboardView.class.getSimpleName());
    protected ViewDefinition viewDefinition;
    protected String viewUrl;
    protected String viewUrlParameters;
    protected CssLayout root;
    protected CssLayout floatToolbar;
    protected Button btnAdd;
    protected Button btnEdit;
    protected Button btnDelete;
    protected VerticalLayout tabSheetLayout;
    protected TabSheet tabSheet;
    public VerticalLayout panelLayout;

    public DashboardView(ViewDefinition vd) {
        this.viewDefinition = HybridbpmUI.getDashboardAPI().getViewDefinitionById(vd.getId().toString());
        Design.read(this);
        Responsive.makeResponsive(this);

        btnAdd.addClickListener(this);
        btnAdd.setIcon(FontAwesome.PLUS_CIRCLE);
        btnAdd.setCaption("Add tab");

        btnEdit.addClickListener(this);
        btnEdit.setIcon(FontAwesome.EDIT);
        btnEdit.setCaption("Edit view");

        btnDelete.addClickListener(this);
        btnDelete.setIcon(FontAwesome.TIMES_CIRCLE);
        btnDelete.setCaption("Delete view");

        createTabs();
        checkDeveloperMode();

        tabSheet.addSelectedTabChangeListener(this);
        tabSheet.setCloseHandler(this);
    }

    private void createTabs() {
        tabSheet.removeAllComponents();
        if (this.viewDefinition != null) {
            for (TabDefinition tabDefinition : HybridbpmUI.getDashboardAPI().getTabDefinitionByView(this.viewDefinition.getId().toString())) {
                TabSheet.Tab tab = tabSheet.addTab(new DashboardTab(tabDefinition, this.viewDefinition), tabDefinition.getTitle().getValue(HybridbpmUI.getCurrent().getLocale()), FontAwesome.valueOf(tabDefinition.getIcon()));
                tab.setClosable(HybridbpmUI.getDeveloperMode());
            }
        }
    }

    private void checkDeveloperMode() {
        if (HybridbpmUI.getAccessAPI().isDeveloper() && HybridbpmUI.getDeveloperMode()) {
            floatToolbar.setEnabled(true);
            floatToolbar.setVisible(true);
            tabSheetLayout.setMargin(new MarginInfo(true, false, false, false));
        } else {
            floatToolbar.setEnabled(false);
            floatToolbar.setVisible(false);
            tabSheetLayout.setMargin(false);
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        checkDeveloperMode();
        try {
            if (!Objects.equals(event.getOldView(), event.getNewView())) {
                viewUrl = event.getViewName();
                viewDefinition = HybridbpmUI.getDashboardAPI().getViewDefinitionByUrl(viewUrl);
                tabSheet.removeSelectedTabChangeListener(this);
                createTabs();
                tabSheet.addSelectedTabChangeListener(this);
            }
            if (event.getParameters() != null && !event.getParameters().trim().isEmpty()) {
                openDashboardTab(event.getParameters());
            } else {
                TabDefinition td = ((DashboardTab) tabSheet.getTab(0).getComponent()).getTabDefinition();
                HybridbpmUI.getCurrent().getHybridbpmNavigator().navigateTo(viewUrl + "/" + td.getId());
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private void openDashboardTab(String tabId) {
        Tab tab = getTab(tabId);
        if (tab != null) {
            tabSheet.removeSelectedTabChangeListener(this);
            tabSheet.setSelectedTab(tab);
            tabSheet.addSelectedTabChangeListener(this);
        }

    }

    private Tab getTab(String tabid) {
        for (Component component : tabSheet) {
            if (component instanceof DashboardTab && Objects.equals(((DashboardTab) component).getTabDefinition().getId().toString(), tabid)) {
                return tabSheet.getTab(component);
            }
        }
        return null;
    }

    @Override
    public void selectedTabChange(TabSheet.SelectedTabChangeEvent event) {
        try {
            if (event.getTabSheet().getSelectedTab() instanceof DashboardTab) {
                TabDefinition td = ((DashboardTab) event.getTabSheet().getSelectedTab()).getTabDefinition();
                HybridbpmUI.getCurrent().getHybridbpmNavigator().navigateTo(viewUrl + "/" + td.getId());
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton().equals(btnAdd)) {
            ViewManager.editTabDefinitionWindow(null, viewDefinition);
        } else if (event.getButton().equals(btnEdit)) {
            ViewManager.editViewDefinitionWindow(viewDefinition);
        } else if (event.getButton().equals(btnDelete)) {
            ViewManager.deleteViewDefinitionWindow(viewDefinition);
        }
    }

    @Override
    public void onTabClose(final TabSheet tabsheet, final Component tabContent) {
        if (tabContent instanceof DashboardTab) {
            final DashboardTab dashboardTab = (DashboardTab) tabContent;

            ConfirmDialog.show(UI.getCurrent(), "Please Confirm:", "Delete tab?", "OK", "Cancel", new ConfirmDialog.Listener() {

                @Override
                public void onClose(ConfirmDialog dialog) {
                    if (dialog.isConfirmed()) {
                        HybridbpmUI.getDashboardAPI().deleteTabDefinition(dashboardTab.getTabDefinition().getId(), true);

                        tabsheet.removeComponent(tabContent);
                        tabsheet.setSelectedTab(0);
                    } else {
//                                this.close();
                    }
                }
            });
        }
    }

}
