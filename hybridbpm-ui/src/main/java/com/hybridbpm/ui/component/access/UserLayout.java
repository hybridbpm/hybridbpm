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
package com.hybridbpm.ui.component.access;

import com.hybridbpm.core.data.access.User;
import com.hybridbpm.core.util.HybridbpmCoreUtil;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.component.UserImageSource;
import static com.hybridbpm.ui.component.access.RoleGroupLayout.NAME;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.declarative.Design;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@DesignRoot
@SuppressWarnings("serial")
public final class UserLayout extends HorizontalLayout {

    private TextField username;
    private PasswordField password1;
    private PasswordField password2;
    private Label errorLabel;
    private TextField firstName;
    private TextField lastName;
    private TextField email;
    private TextField firstVisibleHourOfDay;
    private TextField lastVisibleHourOfDay;
    private ComboBox manager;
    private ComboBox userLocale;
    private Image userImage;
    private Upload imageUpload;
    private ImageUploader imageUploader = new ImageUploader();
    public ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private BeanFieldGroup<User> binder = new BeanFieldGroup<>(User.class);
    private boolean readOnly = true;
    private byte[] image = null;

    public UserLayout(User user) {
        Design.read(this);
        if (user == null) {
            user = new User();
            user.setLocale(HybridbpmUI.getCurrent().getLocale().toString());
        }
        binder.setItemDataSource(user);
        binder.bind(username, "username");
        binder.bind(firstName, "firstName");
        binder.bind(lastName, "lastName");
        binder.bind(email, "email");
        binder.bind(manager, "manager");
        binder.bind(userLocale, "locale");
        binder.bind(firstVisibleHourOfDay, "firstVisibleHourOfDay");
        binder.bind(lastVisibleHourOfDay, "lastVisibleHourOfDay");
        binder.setBuffered(true);

        if (user.getId() != null) {
            username.setReadOnly(true);
        }
        if (user.getImage() != null) {
            image = user.getImage().toStream();
            StreamResource.StreamSource imagesource = new UserImageSource(image);
            StreamResource resource = new StreamResource(imagesource, UUID.randomUUID().toString());
            userImage.setSource(resource);
        } else {
            userImage.setSource(new ThemeResource("img/profile-pic-300px.jpg"));
        }
        imageUpload.setImmediate(true);
        imageUpload.addSucceededListener(imageUploader);
        imageUpload.setReceiver(imageUploader);
//        password1.addValueChangeListener(new PasswordChangeListener());
        password1.setImmediate(true);
        password2.setImmediate(true);
//        password2.addValueChangeListener(new PasswordChangeListener());

        manager.addContainerProperty(NAME, String.class, null);
        manager.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        manager.setItemCaptionPropertyId(NAME);
        for (User u : HybridbpmUI.getAccessAPI().getAllUsers()) {
            Item item = manager.addItem(u);
            item.getItemProperty(NAME).setValue(u.getFullName() + " (" + u.getEmail() + ")");
        }
        for (Locale l : Locale.getAvailableLocales()) {
            Item item = userLocale.addItem(l.toString());
            userLocale.setItemCaptionMode(AbstractSelect.ItemCaptionMode.EXPLICIT);
            userLocale.setItemCaption(l.toString(), l.getDisplayName());
        }
        

        if (HybridbpmUI.getAccessAPI().isAdministrator()) {
            readOnly = false;
            errorLabel.setVisible(false);
            manager.setEnabled(true);
        } else if (Objects.equals(user.getUsername(), HybridbpmUI.getUser().getUsername())) {
            readOnly = false;
            errorLabel.setVisible(false);
            manager.setEnabled(false);
            manager.setReadOnly(true);
        } else {
            readOnly = true;
            password1.setVisible(false);
            password2.setVisible(false);
            imageUpload.setVisible(false);
            errorLabel.setVisible(false);
            manager.setEnabled(false);
            manager.setReadOnly(true);
        }
    }

    public void save() {
        if (!readOnly) {
            try {
                binder.commit();
                User ui = binder.getItemDataSource().getBean();
                if (!Objects.equals(password1.getValue(), password2.getValue())) {
                    errorLabel.setValue("Passwords should be the same!");
                    errorLabel.setVisible(true);
                    throw new RuntimeException("Passwords should be the same!");
                } else if (password1.getValue() !=null && !password1.getValue().isEmpty()) {
                    ui.setPassword(HybridbpmCoreUtil.hashPassword(password1.getValue()));
                }
                ui = HybridbpmUI.getAccessAPI().saveUser(ui, image);
                binder.setItemDataSource(ui);
            } catch (FieldGroup.CommitException ex) {
                Logger.getLogger(UserLayout.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            } catch (RuntimeException ex) {
                Notification.show("Error", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
            }
        }
    }

    class ImageUploader implements Receiver, SucceededListener {

        private String filename;

        @Override
        public OutputStream receiveUpload(String filename, String mimeType) {
            this.filename = filename;
            baos.reset();
            return baos;
        }

        @Override
        public void uploadSucceeded(SucceededEvent event) {
            image = baos.toByteArray();
            StreamResource.StreamSource imagesource = new UserImageSource(image);
            StreamResource resource = new StreamResource(imagesource, filename);
            userImage.setSource(resource);
        }
    };

    class PasswordChangeListener implements Property.ValueChangeListener {

        @Override
        public void valueChange(Property.ValueChangeEvent event) {
            if (!Objects.equals(password1.getValue(), password2.getValue())) {
                errorLabel.setValue("Passwords should be the same!");
                errorLabel.setVisible(true);
            } else {
                errorLabel.setVisible(false);
            }
        }

    }
}
