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
package com.hybridbpm.ui.component.access;

import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.component.AbstractTableLayout;
import com.hybridbpm.ui.component.TableButton;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import org.vaadin.dialogs.ConfirmDialog;

@SuppressWarnings("serial")
public abstract class AbstractAccessLayout extends AbstractTableLayout {

    public AbstractAccessLayout() {
        super();
        setMargin(new MarginInfo(true, false, false, false));
    }
    
    @Override
    public void buttonClick(final Button.ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton() instanceof TableButton && ((TableButton)event.getButton()).getType().equals(TableButton.TYPE.DELETE)) {
            ConfirmDialog.show(UI.getCurrent(), "Please Confirm:", "Delete?", "OK", "Cancel", new ConfirmDialog.Listener() {
                @Override
                public void onClose(ConfirmDialog dialog) {
                    if (dialog.isConfirmed()) {
                        HybridbpmUI.getAccessAPI().removeInstance(((TableButton) event.getButton()).getCustomData().toString());
                        refreshTable();
                    }
                }
            });
        }
    }
    
}
