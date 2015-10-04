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
package com.hybridbpm.ui;

import com.hybridbpm.core.data.access.User;
import com.hybridbpm.ui.component.ValoMenuItemButton;
import com.hybridbpm.ui.component.ValoMenuAddViewButton;
import com.hybridbpm.core.data.dashboard.ViewDefinition;
import com.hybridbpm.ui.component.UserImageSource;
import com.hybridbpm.ui.component.access.UsersLayout;
import com.hybridbpm.ui.view.AccessView;
import com.hybridbpm.ui.view.AdministrationView;
import com.hybridbpm.ui.view.CalendarView;
import com.hybridbpm.ui.view.CaseView;
import com.hybridbpm.ui.view.DashboardView;
import com.hybridbpm.ui.view.DevelopmentView;
import com.hybridbpm.ui.view.DocumentView;
import com.hybridbpm.ui.view.TaskListView;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Sizeable;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Window;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.themes.ValoTheme;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * A responsive menu component providing user information and the controls for primary navigation between the views.
 */
@SuppressWarnings({"serial", "unchecked"})
@DesignRoot
public final class MainMenu extends CssLayout implements Button.ClickListener, MenuBar.Command {

    private static final String STYLE_VISIBLE = "valo-menu-visible";
    private Image userImage;
    private MenuBar settings;
    private MenuItem settingsItem;
    private MenuItem profileItem;
    private MenuItem signOutItem;
    private CssLayout menuItemsLayout;
    private Button valoMenuToggleButton;
    private Button developerButton;

    public MainMenu() {
        setSizeUndefined();
        Design.read(this);
        settingsItem = settings.addItem("", null);
        profileItem = settingsItem.addItem("Profile", this);
        settingsItem.addSeparator();
        signOutItem = settingsItem.addItem("Sign Out", this);
        valoMenuToggleButton.addClickListener(this);
        valoMenuToggleButton.setIcon(FontAwesome.LIST);
        developerButton.setIcon(FontAwesome.EYE_SLASH);
        developerButton.setDescription("Turn on developer mode");
        developerButton.addClickListener(this);
    }

    public void buildMenu(String viewUrl, boolean navigate, String selectedUrl) {
        cleanMenu();
        setUserImage();
        // add build-in views
        if (HybridbpmUI.getAccessAPI().isUser()) {

            ViewDefinition vd = new ViewDefinition(TaskListView.ORDER, TaskListView.VIEW_URL, TaskListView.TITLE, TaskListView.ICON);
            addMenuItemComponent(vd, null);
            HybridbpmUI.registerView(vd, new TaskListView());

            vd = new ViewDefinition(CaseView.ORDER, CaseView.VIEW_URL, CaseView.TITLE, CaseView.ICON);
            addMenuItemComponent(vd, null);
            HybridbpmUI.registerView(vd, new CaseView());

            vd = new ViewDefinition(CalendarView.ORDER, CalendarView.VIEW_URL, CalendarView.TITLE, CalendarView.ICON);
            addMenuItemComponent(vd, null);
            HybridbpmUI.registerView(vd, new CalendarView());

            vd = new ViewDefinition(DocumentView.ORDER, DocumentView.VIEW_URL, DocumentView.TITLE, DocumentView.ICON);
            addMenuItemComponent(vd, null);
            HybridbpmUI.registerView(vd, new DocumentView());

        }

        if (HybridbpmUI.getAccessAPI().isAdministrator()) {
            ViewDefinition vd = new ViewDefinition(AdministrationView.ORDER, AdministrationView.VIEW_URL, AdministrationView.TITLE, AdministrationView.ICON);
            addMenuItemComponent(vd, null);
            HybridbpmUI.registerView(vd, new AdministrationView());

//            vd = new ViewDefinition(MobileView.ORDER, MobileView.VIEW_URL, MobileView.TITLE, MobileView.ICON);
//            addMenuItemComponent(vd, null);
//            HybridbpmUI.registerView(vd, new MobileView());
            
            vd = new ViewDefinition(AccessView.ORDER, AccessView.VIEW_URL, AccessView.TITLE, AccessView.ICON);
            addMenuItemComponent(vd, null);
            HybridbpmUI.registerView(vd, new AccessView());
        }

        if (HybridbpmUI.getAccessAPI().isDeveloper()) {
            ViewDefinition vd = new ViewDefinition(DevelopmentView.ORDER, DevelopmentView.VIEW_URL, DevelopmentView.TITLE, DevelopmentView.ICON);
            addMenuItemComponent(vd, null);
            HybridbpmUI.registerView(vd, new DevelopmentView());
        }

        List<ViewDefinition> definitions = HybridbpmUI.getDashboardAPI().getViewDefinitions();
        for (ViewDefinition vd : definitions) {
            addMenuItemComponent(vd, null);
            HybridbpmUI.registerView(vd, new DashboardView(vd));
        }

        if (HybridbpmUI.getAccessAPI().isDeveloper()) {
            developerButton.setVisible(true);
            developerButton.setEnabled(true);
            // new view menu
            addMenuItemComponent(null, null);
        } else {
            developerButton.setVisible(false);
            developerButton.setEnabled(false);
        }
//        if (getAccessAPI().isManager()) {
//            ViewDefinition vd = new ViewDefinition(DashboardView.ORDER, DashboardView.VIEW_URL, DashboardView.TITLE, DashboardView.ICON);
//            addMenuItemComponent(vd, null);
//            HybridbpmUI.registerView(vd, new DashboardView());
//        }

        // navigate to view
        if (navigate) {
            if (viewUrl != null) {
                HybridbpmUI.getCurrent().getNavigator().navigateTo(viewUrl);
            } else {
                HybridbpmUI.getCurrent().getNavigator().navigateTo(TaskListView.VIEW_URL);
            }
        } else if (selectedUrl != null) {
            setSelection(selectedUrl, viewUrl);
        }
    }

    protected void addMenuItemComponent(final ViewDefinition viewDefinition, String parameters) {
        CssLayout dashboardWrapper = new CssLayout();
        dashboardWrapper.addStyleName("badgewrapper");
        dashboardWrapper.addStyleName(ValoTheme.MENU_ITEM);
        dashboardWrapper.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);

        Label notificationsBadge = new Label();
        notificationsBadge.addStyleName(ValoTheme.MENU_BADGE);
        notificationsBadge.setWidthUndefined();
        notificationsBadge.setVisible(false);

        if (viewDefinition != null) {
            dashboardWrapper.addComponents(new ValoMenuItemButton(viewDefinition, parameters), notificationsBadge);
            menuItemsLayout.addComponent(dashboardWrapper);
        } else if (HybridbpmUI.getDeveloperMode()) {
            dashboardWrapper.addComponents(new ValoMenuAddViewButton(), notificationsBadge);
            menuItemsLayout.addComponent(dashboardWrapper);
        }
    }

    public void cleanMenu() {
        menuItemsLayout.removeAllComponents();
    }

    public void setSelection(String viewUrl, String parameters) {
        for (Component comp : menuItemsLayout) {
            if (comp instanceof CssLayout) {
                CssLayout dashboardWrapper = (CssLayout) comp;
                if (dashboardWrapper.getComponent(0) instanceof ValoMenuItemButton) {
                    ValoMenuItemButton menuItemButton = (ValoMenuItemButton) dashboardWrapper.getComponent(0);
                    menuItemButton.removeStyleName("selected");
                    if (menuItemButton.getView().getUrl().equals(viewUrl) && menuItemButton.getParameters() == null && (parameters == null || parameters.isEmpty())) {
                        menuItemButton.addStyleName("selected");
                    } else if (menuItemButton.getView().getUrl().equals(viewUrl) && parameters != null && Objects.equals(menuItemButton.getParameters(), parameters)) {
                        menuItemButton.addStyleName("selected");
                    }
                }
            }
        }
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton().equals(valoMenuToggleButton)) {
            if (getStyleName().contains(STYLE_VISIBLE)) {
                removeStyleName(STYLE_VISIBLE);
            } else {
                addStyleName(STYLE_VISIBLE);
            }
        } else if (event.getButton().equals(developerButton)) {
            HybridbpmUI.setDeveloperMode(!HybridbpmUI.getDeveloperMode());
            developerButton.setIcon(HybridbpmUI.getDeveloperMode() ? FontAwesome.EYE : FontAwesome.EYE_SLASH);
            developerButton.setDescription(HybridbpmUI.getDeveloperMode() ? "Turn off developer mode" : "Turn on developer mode");
            HybridbpmUI.getCurrent().buildMenu(HybridbpmUI.getCurrent().getNavigator().getState(), true, HybridbpmUI.getCurrent().getNavigator().getState());
        }
    }

    @Override
    public void menuSelected(MenuItem selectedItem) {
        if (selectedItem.equals(signOutItem)) {
            HybridbpmUI.getCurrent().logout();
        } else if (selectedItem.equals(profileItem)) {
            UsersLayout.openUserEditor(HybridbpmUI.getUser(), new Window.CloseListener() {

                @Override
                public void windowClose(Window.CloseEvent e) {
                    HybridbpmUI.getCurrent().reloadUser();
                }
            });
        }
    }

    public void changeNotification(String viewUrl, Boolean visible, String message) {
        for (Component component : menuItemsLayout) {
            if (component instanceof CssLayout) {
                Component button = ((CssLayout) component).getComponent(0);
                if (button instanceof ValoMenuItemButton) {
                    ValoMenuItemButton valoMenuItemButton = (ValoMenuItemButton) button;
                    if (valoMenuItemButton.getView().getUrl().equals(viewUrl)) {
                        Label label = (Label) ((CssLayout) component).getComponent(1);
                        label.setValue(message);
                        label.setVisible(visible);
                    }
                }
            }
        }
    }

    public void setUserImage() {
        User user = HybridbpmUI.getUser();
        settingsItem.setText(user.getUsername());
        if (user.getImage() != null) {
            StreamResource.StreamSource imagesource = new UserImageSource(user.getImage().toStream());
            StreamResource resource = new StreamResource(imagesource, UUID.randomUUID().toString());
            userImage.setSource(resource);
        } else {
            userImage.setSource(new ThemeResource("img/profile-pic-300px.jpg"));
        }
    }

}
