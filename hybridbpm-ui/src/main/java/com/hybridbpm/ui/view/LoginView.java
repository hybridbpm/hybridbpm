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

import com.hybridbpm.core.data.access.User;
import com.hybridbpm.ui.CookieManager;
import com.hybridbpm.ui.HybridbpmUI;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 *
 * @author Marat Gubaidullin
 */
@SuppressWarnings("serial")
public class LoginView extends VerticalLayout {

    final Label welcome = new Label("Login");
    final Label title = new Label("HYBRIDBPM", ContentMode.HTML);
    final CssLayout labels = new CssLayout(welcome, title);

    final TextField username = new TextField("Username");
    final PasswordField password = new PasswordField("Password");
    final Button signin = new Button("Sign In");
    final HorizontalLayout fields = new HorizontalLayout(username, password, signin);

    final CheckBox rememberMe = new CheckBox("Remember me", false);

    final VerticalLayout loginPanel = new VerticalLayout(labels, fields, rememberMe);

    public LoginView() {
        setSizeFull();
        buildLoginForm();
        addComponent(loginPanel);
        setComponentAlignment(loginPanel, Alignment.MIDDLE_CENTER);
        CookieManager.getCookieValue(HybridbpmUI.COOKIENAME_USERNAME, new CookieManager.Callback() {

            @Override
            public void onValue(String value) {
                if (value != null && !value.isEmpty() && !value.equalsIgnoreCase("null")) {
                    username.setValue(value);
                }
            }
        });
    }

    //TODO:  delete this method after development
//    @Override
//    public boolean isAttached() {
//        boolean result = super.isAttached();
//        signin.click();
//        return result; 
//    }
    private void buildLoginForm() {
        Responsive.makeResponsive(loginPanel);
        loginPanel.setSizeUndefined();
        loginPanel.setSpacing(true);
        loginPanel.addStyleName("login-panel");

        labels.addStyleName("labels");
        welcome.setSizeUndefined();
        welcome.addStyleName(ValoTheme.LABEL_H4);
        welcome.addStyleName(ValoTheme.LABEL_LIGHT);
        title.setSizeUndefined();
        title.addStyleName(ValoTheme.LABEL_H3);
        title.addStyleName(ValoTheme.LABEL_COLORED);

        fields.setSpacing(true);
        fields.addStyleName("fields");

        username.setIcon(FontAwesome.USER);
        username.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
        username.setNullRepresentation("");

        password.setIcon(FontAwesome.LOCK);
        password.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);

        signin.addStyleName(ValoTheme.BUTTON_PRIMARY);
        signin.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        signin.focus();

        fields.setComponentAlignment(signin, Alignment.BOTTOM_LEFT);

        signin.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                HybridbpmUI.getCurrent().login(username.getValue(), password.getValue(), rememberMe.getValue());
            }
        });
    }

}
