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
package com.hybridbpm.ui.component.bpm;

import com.hybridbpm.core.data.bpm.Comment;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.component.comment.CommentFormLayout;
import com.hybridbpm.ui.component.comment.CommentViewLayout;
import com.vaadin.ui.VerticalLayout;
import java.util.List;

/**
 *
 * @author Marat Gubaidullin
 */
@SuppressWarnings("serial")
public class TaskCommentsLayout extends VerticalLayout {

    private final String caseId;
    private final String taskId;
    private CommentFormLayout postFormLayout;

    public TaskCommentsLayout(String caseId, String taskId) {
        this.caseId = caseId;
        this.taskId = taskId;
    }

    public void initUI() {
        setMargin(true);
        removeAllComponents();
        List<Comment> comments = HybridbpmUI.getCommentAPI().getRootCommentsByCaseId(caseId);
        if (comments != null && !comments.isEmpty()) {
            for (Comment comment : comments) {
                CommentViewLayout commentViewLayout = new CommentViewLayout(comment);
                addComponent(commentViewLayout);
            }
        }
        Comment comment = new Comment();
        comment.setCase(caseId);
        comment.setTask(taskId);
        postFormLayout = new CommentFormLayout(comment, false);
        addComponent(postFormLayout);
    }

}
