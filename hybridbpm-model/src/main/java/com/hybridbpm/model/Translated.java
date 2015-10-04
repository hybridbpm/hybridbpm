/* 
 * Copyright (c) 2011-2015 Marat Gubaidullin. 
 * 
 * This file is part of HYBRIDBPM.
 * 
 * HybridBPM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * HybridBPM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with HybridBPM.  If not, see <http ://www.gnu.org/licenses/>.
 */
package com.hybridbpm.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author Marat Gubaidullin
 */
public class Translated implements Serializable {
    
    private Map<Locale, String> values;
    private Locale defaultLocale = Locale.US;
    
    public Translated() {
    }
    
    public Translated(String defaultValue) {
        this.addValue(defaultLocale, defaultValue);
    }
    
    public String getValue(Locale locale) {
        return values.containsKey(locale) ? values.get(locale) : values.get(defaultLocale);
    }
    
    public String getValue() {
        return getValues().get(defaultLocale);
    }
    
    public Map<Locale, String> getValues() {
        if (values == null) {
            values = new HashMap<>();
        }
        return values;
    }
    
    public void setValues(Map<Locale, String> value) {
        this.values = value;
        if (this.values != null && !this.values.containsKey(defaultLocale)) {
            this.values.put(defaultLocale, value.values().iterator().next());
        }
    }
    
    public void addValue(Locale locale, String val) {
        getValues().put(locale, val);
        if (!getValues().containsKey(defaultLocale)) {
            getValues().put(defaultLocale, val);
        }
    }
    
    public Locale getDefaultLocale() {
        return defaultLocale;
    }
    
    public void setDefaultLocale(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }
    
}
