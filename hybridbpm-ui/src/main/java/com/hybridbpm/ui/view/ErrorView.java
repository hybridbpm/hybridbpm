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

import com.hybridbpm.core.util.DashboardConstant;
import com.hybridbpm.ui.util.Translate;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Label;
import com.vaadin.ui.declarative.Design;

@DesignRoot
@SuppressWarnings("serial")
public final class ErrorView extends AbstractView implements View{
    
    public static final String VIEW_URL = DashboardConstant.VIEW_URL_ERROR;
    public static final String TITLE = Translate.getMessage("page-not-found");
    public static final String ICON = FontAwesome.EXCLAMATION.name();
    private Label label;

    public ErrorView() {
        Design.read(this);
        label.setValue(Translate.getMessage("page-not-found"));
    }

    @Override
    public void enter(ViewChangeEvent event) {
    }
    
}
