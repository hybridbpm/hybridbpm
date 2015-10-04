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
package com.hybridbpm.ui.component.dashboard.tab;

import com.hybridbpm.ui.HybridbpmStyle;
import com.hybridbpm.ui.component.dashboard.panel.DashboardPanel;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class DashboardPanelContainer extends VerticalLayout {

    protected Layout root;

    public DashboardPanelContainer() {
        addStyleName(HybridbpmStyle.OVERFLOW_HIDDEN);
        addStyleName(HybridbpmStyle.LAYOUT_PADDING8);
        setMargin(new MarginInfo(true, false, false, false));
    }
    
    public void setRoot(Layout root) {
        this.root = root;
        this.root.setSizeFull();
        removeAllComponents();
        addComponent(this.root);
        setExpandRatio(this.root, 1);
    }

    public void addPanel(DashboardPanel dashboardPanel) {
        root.addComponent(dashboardPanel);
    }

}
