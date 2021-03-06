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
package com.hybridbpm.ui.component.bpm;

import com.hybridbpm.core.data.bpm.StartProcess;
import com.hybridbpm.ui.HybridbpmUI;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.themes.ValoTheme;

/**
 *
 * @author Marat Gubaidullin
 */
public final class StartProcessColumnGenerator implements Table.ColumnGenerator {

    private final ClickListener clickListener;

    public StartProcessColumnGenerator(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public Object generateCell(Table source, Object itemId, Object columnId) {
        StartProcess spd = (StartProcess) itemId;
        Button button = new Button(spd.getProcessModel().getTitle().getValue(HybridbpmUI.getCurrent().getLocale()), clickListener);
        button.setData(spd);
        button.addStyleName(ValoTheme.BUTTON_LINK);
        button.setIcon(FontAwesome.valueOf(spd.getIcon()));
        button.setDescription("Start case");
        return button;
    }

}
