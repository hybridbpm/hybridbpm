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
package com.hybridbpm.ui.component.document;

import com.hybridbpm.core.data.document.Document;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;

/**
 *
 * @author Marat Gubaidullin
 */
public final class DocumentBreadcrumbButton extends Button {

    private final Document document;

    public DocumentBreadcrumbButton(Document document, ClickListener clickListener) {
        this.document = document;
        setStyleName(ValoTheme.BUTTON_LINK);
        addStyleName("breadcrumbs");
        addStyleName(ValoTheme.BUTTON_SMALL);
        if (document != null) {
            setIcon(FontAwesome.ANGLE_RIGHT);
            setCaption(document.getName());
        } else {
            setCaption("Documents");
        }
        if (clickListener != null) {
            addClickListener(clickListener);
        }
    }

    public Document getDocument() {
        return document;
    }

}
