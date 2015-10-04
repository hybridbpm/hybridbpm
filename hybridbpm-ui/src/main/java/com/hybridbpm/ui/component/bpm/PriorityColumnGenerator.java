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

import com.hybridbpm.model.TaskModel;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

/**
 *
 * @author Marat Gubaidullin
 */
public final class PriorityColumnGenerator implements Table.ColumnGenerator {

    @Override
    public Object generateCell(Table source, Object itemId, Object columnId) {
        TaskModel.TASK_PRIORITY priority = (TaskModel.TASK_PRIORITY) source.getItem(itemId).getItemProperty(columnId).getValue();
        if (priority != null && priority.getIcon() != null) {
            return new Label(FontAwesome.valueOf(priority.getIcon()).getHtml(), ContentMode.HTML);
        } else {
            return new Label("", ContentMode.HTML);
        }
    }

}
