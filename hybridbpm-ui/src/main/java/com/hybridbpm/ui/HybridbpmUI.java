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

import com.hybridbpm.core.HazelcastServer;
import com.hybridbpm.core.api.AccessAPI;
import com.hybridbpm.core.api.BpmAPI;
import com.hybridbpm.core.api.CrudAPI;
import com.hybridbpm.core.api.DashboardAPI;
import com.hybridbpm.core.api.DevelopmentAPI;
import com.hybridbpm.core.api.CommentAPI;
import com.hybridbpm.core.api.DocumentAPI;
import com.hybridbpm.core.api.SyncAPI;
import com.hybridbpm.core.api.SystemAPI;
import com.hybridbpm.core.data.access.User;
import com.hybridbpm.core.data.dashboard.ViewDefinition;
import com.hybridbpm.ui.dashboard.DashboardMessage;
import com.hybridbpm.ui.view.DashboardView;
import com.hybridbpm.ui.view.LoginView;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;

/**
 *
 * @author Marat Gubaidullin
 */
@PreserveOnRefresh
@Push(PushMode.MANUAL)
@Theme("hybridbpm")
@Title("HybridBPM")
@SuppressWarnings("serial")
@Widgetset("com.hybridbpm.ui.HybridbpmWidgetSet")
public class HybridbpmUI extends UI {

    private static final Logger logger = Logger.getLogger(HybridbpmUI.class.getSimpleName());
    public static final String COOKIENAME_USERNAME = "hybridbpm-username";

    @SuppressWarnings("serial")
    @WebServlet(name = "Hybridbpm", value = {"/*", "/VAADIN/*"}, asyncSupported = true, loadOnStartup = 1)
    @VaadinServletConfiguration(productionMode = true, ui = HybridbpmUI.class, closeIdleSessions = true)
    public static class Servlet extends VaadinServlet {
    }

    protected final UsersMenu usersMenu = new UsersMenu();
    protected final MainMenu mainMenu = new MainMenu();
    private final CssLayout viewContent = new CssLayout();
    private HybridbpmNavigator navigator = new HybridbpmNavigator(this, viewContent);
    private final HorizontalLayout mainLayout = new HorizontalLayout(mainMenu, viewContent, usersMenu);
    private final CssLayout rootLayout = new CssLayout(mainLayout);
    private User user;
    private String dashboardListenerId = null;
    private String notificationListenerId = null;
    private View currentView;
    private String redirectView;
    private ResourceBundle messages = null;
    private Boolean developerMode = Boolean.FALSE;

    @Override
    protected void init(VaadinRequest request) {
        Responsive.makeResponsive(this);
        setLocale(request.getLocale());
        prepareMessages();
        dashboardListenerId = HazelcastServer.getDashboardEventTopic().addMessageListener(new DashBoardMessageListener(this));
        addStyleName(ValoTheme.UI_WITH_MENU);

        mainLayout.setExpandRatio(viewContent, 1f);
        mainLayout.setSizeFull();
        mainLayout.addStyleName("mainview");
        viewContent.setSizeFull();
        viewContent.addStyleName("view-content");
        rootLayout.setSizeFull();
        updateContent();
    }

    private void updateContent() {
        if (user != null) {
//         Authenticated user
            setLocale(user.getLocale()!=null ? Locale.forLanguageTag(user.getLocale()) : getLocale());
            prepareMessages();
            setContent(rootLayout);
            removeStyleName("loginview");
            buildMenu(redirectView, true, null);
            usersMenu.search(null);
        } else {
            setContent(new LoginView());
            addStyleName("loginview");
        }
    }

    @Override
    protected void refresh(VaadinRequest request) {
        super.refresh(request);
        getNavigator().navigateTo(getNavigator().getState());
    }

    public static HybridbpmUI getCurrent() {
        return (HybridbpmUI) UI.getCurrent();
    }

    public static User getUser() {
        return getCurrent().user;
    }

    public static boolean isAuthenticated() {
        return getCurrent().user != null;
    }

    public static DevelopmentAPI getDevelopmentAPI() {
        return DevelopmentAPI.get(getCurrent().user, VaadinSession.getCurrent().getSession().getId());
    }

    public static DocumentAPI getDocumentAPI() {
        return DocumentAPI.get(getCurrent().user, VaadinSession.getCurrent().getSession().getId());
    }

    public static CommentAPI getCommentAPI() {
        return CommentAPI.get(getCurrent().user, VaadinSession.getCurrent().getSession().getId());
    }

    public static DashboardAPI getDashboardAPI() {
        return DashboardAPI.get(getCurrent().user, VaadinSession.getCurrent().getSession().getId());
    }

    public static AccessAPI getAccessAPI() {
        return AccessAPI.get(getCurrent().user, VaadinSession.getCurrent().getSession().getId());
    }

    public static SystemAPI getSystemAPI() {
        return SystemAPI.get(getCurrent().user, VaadinSession.getCurrent().getSession().getId());
    }

    public static BpmAPI getBpmAPI() {
        return BpmAPI.get(getCurrent().user, VaadinSession.getCurrent().getSession().getId());
    }

    public static SyncAPI getSyncAPI() {
        return SyncAPI.get(getCurrent().user, VaadinSession.getCurrent().getSession().getId());
    }

    public static CrudAPI getCrudAPI() {
        return CrudAPI.get(getCurrent().user, VaadinSession.getCurrent().getSession().getId());
    }

    public static void sendMessage(DashboardMessage dashboardMessage) {
        if (getCurrent().currentView instanceof DashboardView) {
//            ((AbstractConfigurableView) getCurrent().currentView).sendMessage(dashboardMessage);
        }
    }

    protected void buildMenu(String viewUrl, boolean navigate, String selectedUrl) {
        usersMenu.search(null);
        navigator = new HybridbpmNavigator(this, viewContent);
        mainMenu.cleanMenu();
        mainMenu.buildMenu(viewUrl, navigate, selectedUrl);
        mainMenu.setUserImage();
    }

    protected void reloadUser() {
        user = getAccessAPI().getUserById(user.getId().toString());
        mainMenu.setUserImage();
    }

    public static void navigateTo(String navigationState) {
        getCurrent().getNavigator().navigateTo(navigationState);
    }

    public void login(String username, String password, boolean rememberMe) {
        try {
            user = AccessAPI.get(null, null).login(username, password);
            updateContent();
            subscribe();
            getBpmAPI().notifyTaskList();
            if (rememberMe) {
                CookieManager.setCookie(COOKIENAME_USERNAME, user.getUsername(), VaadinService.getCurrentRequest().getContextPath());
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            Notification.show("Error", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
        }
    }

    public void logout() {
        HazelcastServer.removeDashboardNotificationEventTopic(user.getId().toString(), notificationListenerId);
        HazelcastServer.getDashboardEventTopic().removeMessageListener(dashboardListenerId);
        user = null;
        CookieManager.setCookie(COOKIENAME_USERNAME, null, VaadinService.getCurrentRequest().getContextPath());
        VaadinSession.getCurrent().close();
        getUI().getPage().setLocation(VaadinService.getCurrentRequest().getContextPath());
        VaadinService.getCurrentRequest().getWrappedSession().invalidate();
    }

    public HybridbpmNavigator getHybridbpmNavigator() {
        return navigator;
    }

    public void registerView(ViewDefinition viewDefinition, Class<? extends View> viewClass) {
        ViewProvider viewProvider = new Navigator.ClassBasedViewProvider(viewDefinition.getUrl(), viewClass);
        ((HybridbpmUI) UI.getCurrent()).getHybridbpmNavigator().addProvider(viewProvider);
    }

    public static void registerView(ViewDefinition viewDefinition, View view) {
        ViewProvider viewProvider = new Navigator.StaticViewProvider(viewDefinition.getUrl(), view);
        ((HybridbpmUI) UI.getCurrent()).getHybridbpmNavigator().addProvider(viewProvider);
    }

    private void subscribe() {
        notificationListenerId = HazelcastServer.getDashboardNotificationEventTopic(user.getId().toString()).addMessageListener(new DashBoardNotificationMessageListener(this));
    }

    public static View getCurrentView() {
        return getCurrent().currentView;
    }

    public static void setCurrentView(View currentView) {
        getCurrent().currentView = currentView;
    }

    public void setRedirectView(String redirectView) {
        this.redirectView = redirectView;
    }

    public UsersMenu getUsersMenu() {
        return usersMenu;
    }
    
    public ResourceBundle getMessages() {
        return messages;
    }

    public void setMessages(ResourceBundle messages) {
        this.messages = messages;
    }

    public String getMessage(String key) {
        if (messages.containsKey(key)) {
            return messages.getString(key);
        } else {
            return key;
        }
    }
    
    public static String getText(String key) {
        return HybridbpmUI.getCurrent().getMessage(key);
    }

    private void prepareMessages() {
       try {
           messages = ResourceBundle.getBundle("MessageBundle", getLocale());
       } catch (Exception e){
           messages = ResourceBundle.getBundle("MessageBundle", Locale.US);
       }
    }

    public static Boolean getDeveloperMode() {
        return HybridbpmUI.getCurrent().developerMode;
    }

    public static void setDeveloperMode(Boolean developerMode) {
        HybridbpmUI.getCurrent().developerMode = developerMode;
    }
    
    
}
