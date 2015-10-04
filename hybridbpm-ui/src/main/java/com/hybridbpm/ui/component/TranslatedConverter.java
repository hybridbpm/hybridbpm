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
import com.vaadin.data.util.converter.Converter;
import java.util.Locale;

/**
 *
 * @author Marat Gubaidullin
 */
public class TranslatedConverter implements Converter<String, Translated>{

    @Override
    public Translated convertToModel(String value, Class<? extends Translated> targetType, Locale locale) throws ConversionException {
        Translated translated = new Translated();
        translated.addValue(locale, value);
        return translated; 
    }

    @Override
    public String convertToPresentation(Translated value, Class<? extends String> targetType, Locale locale) throws ConversionException {
        return value.getValue(locale);
    }

    @Override
    public Class<Translated> getModelType() {
        return Translated.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
    
}
