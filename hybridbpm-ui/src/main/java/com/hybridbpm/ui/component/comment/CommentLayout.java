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
package com.hybridbpm.ui.component.comment;

import com.hybridbpm.core.data.bpm.Comment;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;

/**
 *
 * @author Marat Gubaidullin
 */
@DesignRoot
@SuppressWarnings("serial")
public class CommentLayout extends VerticalLayout implements Button.ClickListener {

    private VerticalLayout mainPanel;

    public CommentLayout(Comment comment) {
        Design.read(this);
        Responsive.makeResponsive(this);
        setMargin(false);
        CommentViewLayout commentViewLayout = new CommentViewLayout(comment);
        mainPanel.addComponent(commentViewLayout);
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
//        if (event.getButton().equals(btnSave)) {
//        }
    }

}
