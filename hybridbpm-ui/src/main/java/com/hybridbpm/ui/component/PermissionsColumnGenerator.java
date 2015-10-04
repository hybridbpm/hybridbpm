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
package com.hybridbpm.ui.component;

import com.hybridbpm.core.data.access.Permission;
import com.vaadin.ui.Table;
import java.util.List;

/**
 *
 * @author Marat Gubaidullin
 */
public final class PermissionsColumnGenerator implements Table.ColumnGenerator {
    
    @Override
    public Object generateCell(Table source, Object itemId, Object columnId) {
        List<Permission.PERMISSION> permissions = (List<Permission.PERMISSION>) source.getItem(itemId).getItemProperty(columnId).getValue();
        if (permissions == null) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            for (Permission.PERMISSION permission : permissions) {
                sb.append(permission.name()).append(", ");
            }
            return sb.substring(0, sb.length() - 2);
        }
    }
    
}
