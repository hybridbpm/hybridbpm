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

import com.hybridbpm.model.Translated;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.Locale;

/**
 *
 * @author Marat Gubaidullin
 */
public class TranslatedField extends CustomField<Translated> implements Button.ClickListener {

    private final VerticalLayout form = new VerticalLayout();
    private final Button btnAdd = new Button("Add locale", this);

    public TranslatedField(String caption) {
        this();
        setCaption(caption);
    }
    
    public TranslatedField() {
        form.setSpacing(true);
        form.setMargin(false);
        form.addStyleName(ValoTheme.LAYOUT_CARD);
        form.addStyleName("transacted-field");

        btnAdd.addStyleName(ValoTheme.BUTTON_LINK);
        btnAdd.setIcon(FontAwesome.PLUS_CIRCLE);
        form.addComponent(btnAdd);
    }

    @Override
    protected Component initContent() {
        return form;
    }

    @Override
    public Class<? extends Translated> getType() {
        return Translated.class;
    }

    @Override
    protected Translated getInternalValue() {
        Translated translated = new Translated();
        for (Component component : form) {
            if (component instanceof TranslatedFieldValue) {
                TranslatedFieldValue tfv = (TranslatedFieldValue) component;
                translated.addValue(tfv.getLocaleValue(), tfv.getTextValue());
            }
        }
        return translated;
    }

    @Override
    protected void setInternalValue(Translated translated) {
        form.removeAllComponents();
        form.addComponent(btnAdd);
        if (translated != null) {
            for (Locale locale : translated.getValues().keySet()) {
                form.addComponent(new TranslatedFieldValue(locale, translated.getValue(locale)), form.getComponentIndex(btnAdd));
            }
        } else {

        }
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        form.addComponent(new TranslatedFieldValue(null, getInternalValue().getValue()), form.getComponentIndex(btnAdd));
    }

}
