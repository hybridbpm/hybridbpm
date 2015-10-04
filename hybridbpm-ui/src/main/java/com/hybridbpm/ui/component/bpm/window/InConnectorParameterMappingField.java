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
package com.hybridbpm.ui.component.bpm.window;

import com.hybridbpm.core.data.access.Permission;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Marat Gubaidullin
 */
public class InConnectorParameterMappingField extends CustomField<Map> {

    private final OptionGroup permissionsOptionGroup = new OptionGroup();
    private final VerticalLayout form = new VerticalLayout(permissionsOptionGroup);

    public InConnectorParameterMappingField(String caption) {
        this();
        setCaption(caption);
    }

    public InConnectorParameterMappingField() {
        permissionsOptionGroup.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
        permissionsOptionGroup.setNullSelectionAllowed(false);
        permissionsOptionGroup.setMultiSelect(true);
    }

    public void setPermissionsClass(Class clazz) {
        for (Permission.PERMISSION permission : Permission.PERMISSION.getPermissionsForClass(clazz)) {
            permissionsOptionGroup.addItem(permission);
        }
    }

    @Override
    protected Component initContent() {
        return form;
    }

    @Override
    protected Map<String, String> getInternalValue() {
        Map<String, String> result = new HashMap<>();
//        if (permissionsOptionGroup.getValue() != null && permissionsOptionGroup.getValue() instanceof Collection) {
//            for (Permission.PERMISSION object : ((Collection<Permission.PERMISSION>) permissionsOptionGroup.getValue())) {
//                result.add(object);
//            }
//        } else if (permissionsOptionGroup.getValue() != null) {
//            result.add((Permission.PERMISSION) permissionsOptionGroup.getValue());
//        }
        return result;
    }

    @Override
    protected void setInternalValue(Map parameters) {
//        if (permissions != null) {
//            List<Permission.PERMISSION> data = new ArrayList<>();
//            data.addAll(permissions);
//            permissionsOptionGroup.setValue(data);
//        }
    }

    @Override
    public Class<? extends Map> getType() {
        return Map.class;
    }

}
