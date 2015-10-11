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
package com.hybridbpm.ui.util;

import com.vaadin.server.FontIcon;

/**
 *
 * @author Marat Gubaidullin
 */
@SuppressWarnings("serial")
public enum IcoMoon implements FontIcon {

    PIE(0xe93a),
    DONUT(0xe702),
    BAR(0xf036),
    COLUMN(0xe93b),
    LINE(0xe939),
    AREA(0xe93c),
    
    DATABASE(0xe666),
    FILE(0xf016),
    REST(0xe840),
    GRID(0xe65a),
    NO_SQL(0xe8bb),
    CLOUD(0xf0c2); 

    // You can see the codepoints in the IcoMoon app, or in the demo.html
    private final int codepoint;
    // This must match (S)CSS
    private final String fontFamily = "IcoMoon";

    IcoMoon(int codepoint) {
        this.codepoint = codepoint;
    }

    @Override
    public String getFontFamily() {
        return fontFamily;
    }

    @Override
    public int getCodepoint() {
        return codepoint;
    }

    @Override
    public String getHtml() {
        return "<span class=\"v-icon IcoMoon\">&#x"
                + Integer.toHexString(codepoint) + ";</span>";
    }

    @Override
    public String getMIMEType() {
        // Font icons are not real resources
        throw new UnsupportedOperationException(
                FontIcon.class.getSimpleName()
                + " should not be used where a MIME type is needed.");
    }

}

