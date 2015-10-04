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
package com.hybridbpm.ui.component.sync;

import com.hybridbpm.core.data.sync.MobileTask;
import com.hybridbpm.ui.component.AbstractTabLayout;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class MobileAdministrationLayout extends AbstractTabLayout {

    private static final Logger logger = Logger.getLogger(MobileFormListLayout.class.getSimpleName());

    private TabSheet tabSheet;

    public MobileAdministrationLayout() {
        super();
        btnAdd.setVisible(false);
        setMargin(new MarginInfo(true, false, false, false));
    }
    
    
    @Override
    public void addNew() {

    }

    public void setTabSheet(TabSheet tabSheet) {
        this.tabSheet = tabSheet;
    }

    @Override
    public void prepareUI() {
    }

    @Override
    public void refreshData() {
    }

    @Override
    public void buttonClick(final Button.ClickEvent event) {
        super.buttonClick(event);
        try {
            if (event.getButton().getData() != null && event.getButton().getData() instanceof MobileTask) {
//                Case case1 = (Case) event.getButton().getData();
//                TabSheet.Tab tab = tabSheet.addTab(new CaseLayout(case1.getId().toString()), "Process " + case1.getId().toString());
//                tab.setClosable(true);
//                tabSheet.setSelectedTab(tab);
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            Notification.show("Error", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
        }
    }


}
