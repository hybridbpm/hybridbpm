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

import com.hybridbpm.core.data.access.User;
import com.hybridbpm.core.data.bpm.Comment;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.component.UserImageSource;
import com.hybridbpm.ui.component.bpm.TaskCommentsLayout;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marat Gubaidullin
 */
@DesignRoot
@SuppressWarnings("serial")
public class CommentFormLayout extends VerticalLayout implements Button.ClickListener {

    private boolean deleteOnSave = false;
    private Comment comment;
    private Image userImage;
    private TextArea bodyField;
    private final BeanFieldGroup<Comment> binder = new BeanFieldGroup<>(Comment.class);
    private Button btnSubmit;

    public CommentFormLayout(Comment c, boolean deleteOnSave) {
        this.comment = c;
        this.deleteOnSave = deleteOnSave;
        Design.read(this);
        binder.setItemDataSource(this.comment);
        binder.bind(bodyField, "body");
        binder.setBuffered(true); // important
        btnSubmit.setIcon(FontAwesome.SEND);
        btnSubmit.addClickListener(this);
        setUserImage();
    }

    public void setUserImage() {
        User user = HybridbpmUI.getUser();
        if (user.getImage() != null) {
            StreamResource.StreamSource imagesource = new UserImageSource(user.getImage().toStream());
            StreamResource resource = new StreamResource(imagesource, UUID.randomUUID().toString());
            userImage.setSource(resource);
        } else {
            userImage.setSource(new ThemeResource("img/profile-pic-300px.jpg"));
        }
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton().equals(btnSubmit)) {
            try {
                bodyField.commit();
                binder.commit();
                comment = binder.getItemDataSource().getBean();
                Comment newComment = HybridbpmUI.getCommentAPI().saveComment(comment);
                ((AbstractOrderedLayout)getParent()).addComponent(new CommentViewLayout(newComment), ((AbstractOrderedLayout)getParent()).getComponentIndex(this));
                if (deleteOnSave){
                    ((AbstractOrderedLayout)getParent()).removeComponent(this);
                } else {
                    bodyField.setValue(null);
                }
            } catch (Exception ex) {
                Notification.show("Error", ex.getLocalizedMessage(), Notification.Type.ERROR_MESSAGE);
                Logger.getLogger(CommentFormLayout.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

}
