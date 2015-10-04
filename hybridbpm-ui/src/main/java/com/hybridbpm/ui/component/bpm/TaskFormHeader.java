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

import com.hybridbpm.core.data.access.User;
import com.hybridbpm.core.data.bpm.Task;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.component.UserImageSource;
import com.vaadin.ui.*;
import com.vaadin.ui.declarative.*;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import java.util.UUID;
import org.ocpsoft.prettytime.PrettyTime;

@DesignRoot
public class TaskFormHeader extends VerticalLayout {
    
    private Task task;
    private Image userImage;
    private Label senderName;
    private Button senderUsername;
    private Button btnCaseCode;
    private Label processTitle;
    private Label taskTitle;
    private HorizontalLayout toolbar;
    private final PrettyTime prettyTime = new PrettyTime();

    public TaskFormHeader() {
        Design.read(this);
        prettyTime.setLocale(HybridbpmUI.getCurrent().getLocale());
    }

    public void initUI(Task task) {
        this.task = task;
        senderName.setValue("<b>"+HybridbpmUI.getAccessAPI().getUserByUserName(task.getInitiator()).getFullName()+"</b>  " 
                + (task.getUpdateDate()!=null ?prettyTime.format(task.getUpdateDate()) :""));
        senderUsername.setCaption("@" + task.getInitiator());
        btnCaseCode.setCaption(task.getCaseCode());
        processTitle.setValue(task.getCaseTitle());
        taskTitle.setContentMode(ContentMode.HTML);
        taskTitle.setValue(task.getTaskTitle());
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
}
