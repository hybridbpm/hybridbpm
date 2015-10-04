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

import com.vaadin.annotations.DesignRoot;
import com.vaadin.data.Item;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.declarative.Design;
import java.util.Locale;

/**
 *
 * @author Marat Gubaidullin
 */
@DesignRoot
public class TranslatedFieldValue extends HorizontalLayout implements Button.ClickListener {

    private Locale locale;
    private String value;

    private TextField valueField;
    private ComboBox localeComboBox;
    private Button btnDelete;

    public TranslatedFieldValue(Locale locale, String value) {
        Design.read(this);

        for (Locale l : Locale.getAvailableLocales()) {
            Item item = localeComboBox.addItem(l);
            localeComboBox.setItemCaptionMode(AbstractSelect.ItemCaptionMode.EXPLICIT);
            localeComboBox.setItemCaption(l, l.getDisplayName());
        }
        valueField.setValue(value);
        localeComboBox.setValue(locale);
        btnDelete.setIcon(FontAwesome.TIMES_CIRCLE);
        btnDelete.setVisible(!Locale.US.equals(locale));
        btnDelete.addClickListener(this);
        localeComboBox.setEnabled(!Locale.US.equals(locale));
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        ((Layout) this.getParent()).removeComponent(this);
    }

    public Locale getLocaleValue() {
        return (Locale) localeComboBox.getValue();
    }
    
    public String getTextValue() {
        return  valueField.getValue();
    }

}
