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
import com.vaadin.annotations.DesignRoot;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import java.util.List;
import java.util.UUID;
import org.ocpsoft.prettytime.PrettyTime;

/**
 *
 * @author Marat Gubaidullin
 */
@DesignRoot
@SuppressWarnings("serial")
public class CommentViewLayout extends VerticalLayout implements Button.ClickListener {

    private final Comment comment;
    private Image userImage;
    private Button senderUsername;
    private Button btnReply;
    private Label bodyField;
    private Label senderName;
    private Label createDate;
    private final PrettyTime prettyTime = new PrettyTime();

    public CommentViewLayout(Comment c) {
        this.comment = c;
        Design.read(this);
        bodyField.setContentMode(ContentMode.HTML);
        bodyField.setValue(comment.getBody());
        prettyTime.setLocale(HybridbpmUI.getCurrent().getLocale());
        createDate.setValue(prettyTime.format(comment.getCreateDate()));
        createDate.setSizeUndefined();
        btnReply.setIcon(FontAwesome.REPLY);
        btnReply.addClickListener(this);
        setUserImage();
        addChildren();
    }

    public void setUserImage() {
        User user = HybridbpmUI.getAccessAPI().getUserById(comment.getCreator());
        if (user.getImage() != null) {
            StreamResource.StreamSource imagesource = new UserImageSource(user.getImage().toStream());
            StreamResource resource = new StreamResource(imagesource, UUID.randomUUID().toString());
            userImage.setSource(resource);
        } else {
            userImage.setSource(new ThemeResource("img/profile-pic-300px.jpg"));
        }
        senderUsername.setCaption("@" + user.getUsername());
        senderName.setValue(user.getFullName());
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton().equals(btnReply)) {
            addComment();
        }
    }

    public void addComment() {
        Comment replyComment = new Comment();
        replyComment.setParent(comment.getId());
        replyComment.setCase(comment.getCase());
        replyComment.setTask(comment.getTask());
        CommentFormLayout commentFormLayout = new CommentFormLayout(replyComment, true);
        this.addComponent(commentFormLayout);
    }

    private void addChildren() {
        List<Comment> comments = HybridbpmUI.getCommentAPI().getMyCommentsByParent(comment.getId());
        for (Comment c : comments) {
            CommentViewLayout child = new CommentViewLayout(c);
            addComponent(child);
        }
    }

}
