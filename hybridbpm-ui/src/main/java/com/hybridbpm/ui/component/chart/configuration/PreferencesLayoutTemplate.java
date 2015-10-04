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
package com.hybridbpm.ui.component.chart.configuration;

import com.hybridbpm.core.data.chart.DiagrammePreference;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.ui.*;

import java.util.logging.Logger;


@SuppressWarnings("serial")
public abstract class PreferencesLayoutTemplate extends VerticalLayout {

    private static final Logger LOG = Logger.getLogger(PreferencesLayoutTemplate.class.getName());

    protected final GridLayout gridLayout = new GridLayout();
    
    protected BeanFieldGroup<DiagrammePreference> preferences;

    public PreferencesLayoutTemplate(BeanFieldGroup<DiagrammePreference> preferences) {
        this.preferences = preferences;
        gridLayout.setSizeFull();
        gridLayout.setMargin(false);
        gridLayout.setSpacing(true);
        setSpacing(true);
        setSizeFull();
    }

    public void commit() {}

    public void bindConfigurationValues() {}

    public void unbindConfigurationValues() {}

    public void updateComboboxes() {}

    protected void bindField(Field<?> field, String bindName, BeanFieldGroup<DiagrammePreference> preferences) {
        unbindField(bindName, preferences);
        preferences.bind(field, bindName);
    }

    protected void unbindField(String bindName, BeanFieldGroup<DiagrammePreference> preferences) {
        Field existing = preferences.getField(bindName);
        if (existing != null) {
            preferences.unbind(existing);
        }
    }

   
}
