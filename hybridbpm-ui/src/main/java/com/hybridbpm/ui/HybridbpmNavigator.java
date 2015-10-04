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

import com.hybridbpm.ui.view.ErrorView;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
public class HybridbpmNavigator extends Navigator {

    public HybridbpmNavigator(UI ui, final ComponentContainer container) {
        super(ui, container);
        // set error view 
        setErrorView(ErrorView.class);

        addViewChangeListener(new ViewChangeListener() {

            @Override
            public boolean beforeViewChange(final ViewChangeEvent event) {
                // Since there's no conditions in switching between the views
                // we can always return true.
                if (!HybridbpmUI.isAuthenticated() && event.getViewName()!=null && !event.getViewName().isEmpty()) {
                    HybridbpmUI.getCurrent().setRedirectView(event.getViewName());
                }
                return true;
            }

            @Override
            public void afterViewChange(final ViewChangeEvent event) {
                HybridbpmUI.getCurrent().mainMenu.setSelection(event.getViewName(), event.getParameters());
                HybridbpmUI.setCurrentView(event.getNewView());
            }
        });
    }

    @Override
    protected void navigateTo(View view, String viewName, String parameters) {
        super.navigateTo(view, viewName, parameters); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void navigateTo(String navigationState) {
        super.navigateTo(navigationState); //To change body of generated methods, choose Tools | Templates.
    }

}
