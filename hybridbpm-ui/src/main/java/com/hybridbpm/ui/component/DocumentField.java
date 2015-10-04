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

import com.hybridbpm.core.data.document.Document;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Upload;
import com.vaadin.ui.themes.ValoTheme;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Date;

/**
 *
 * @author Marat Gubaidullin
 */
public class DocumentField extends CustomField<Document> implements Button.ClickListener {

    private final Button btnDownload = new Button(null, this);
    private final ImageUploader imageUploader = new ImageUploader();
    private final Upload fileUpload = new Upload(null, imageUploader);
    private final Button btnRemove = new Button(null, this);
    private final HorizontalLayout form = new HorizontalLayout(btnDownload, fileUpload, btnRemove);
    private Document document;
    public ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private byte[] image = null;
    
    public DocumentField() {
        form.setSpacing(true);
        form.addStyleName("file-field");
        btnDownload.addStyleName(ValoTheme.BUTTON_LINK);
        btnRemove.addStyleName(ValoTheme.BUTTON_LINK);
        btnRemove.setIcon(FontAwesome.TIMES_CIRCLE);
        fileUpload.setImmediate(true);
        fileUpload.addSucceededListener(imageUploader);
        checkVisibility();
    }
    
    private void checkVisibility(){
        if (document.getBody() == null){
            btnDownload.setVisible(false);
            btnRemove.setVisible(false);
            fileUpload.setVisible(true);
        } else {
            fileUpload.setVisible(false);
            btnDownload.setVisible(true);
            btnRemove.setVisible(true);
        }
    }

    @Override
    protected Component initContent() {
        return form;
    }

    @Override
    public Class<? extends Document> getType() {
        return Document.class;
    }

    @Override
    protected Document getInternalValue() {
        return document;
    }

    @Override
    protected void setInternalValue(Document document) {
        this.document = document;
        checkVisibility();
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton().equals(btnDownload)){
            
        } else if (event.getButton().equals(btnRemove)){
            document.setBody(null);
            document.setSize(0);
            document.setMime(null);
            checkVisibility();
        }
    }
    
    class ImageUploader implements Upload.Receiver, Upload.SucceededListener {

        private String filename;

        @Override
        public OutputStream receiveUpload(String filename, String mimeType) {
            this.filename = filename;
            btnDownload.setCaption(filename);
            document.setMime(mimeType);
            baos.reset();
            return baos;
        }

        @Override
        public void uploadSucceeded(Upload.SucceededEvent event) {
            image = baos.toByteArray();
            document.setCreateDate(new Date());
            document.setBody(image);
            document.setSize(image.length);
            checkVisibility();
        }
    };


}
