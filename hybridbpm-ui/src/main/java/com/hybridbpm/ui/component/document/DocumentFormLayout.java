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
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.component.TranslatedField;
import com.hybridbpm.ui.util.Translate;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marat Gubaidullin
 */
@DesignRoot
@SuppressWarnings("serial")
public class DocumentFormLayout extends VerticalLayout {

    private Document document;

    private TextField textFieldName;
    private TranslatedField fieldDescription;
    private TextField textFieldCreator;
    private TextField textFieldMime;
    private TextField textFieldSize;
    private PopupDateField dateFieldCreate;
    private PopupDateField dateFieldUpdate;

    private HorizontalLayout fileLayout;
    private Button btnDownload;
    private Upload fileUpload;

    private final OnDemandStreamResource demandStreamResource = new OnDemandStreamResource();
    private final OnDemandFileDownloader demandFileDownloader = new OnDemandFileDownloader(demandStreamResource);
    private final ImageUploader imageUploader = new ImageUploader();

    public ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private byte[] image = null;

    private BeanFieldGroup<Document> binder = new BeanFieldGroup<>(Document.class);

    public DocumentFormLayout() {

    }

    public void initUI(Document document) {
        this.document = document;
        Design.read(this);
        fileLayout.setCaption(Translate.getMessage("fileLayoutCaption"));
        textFieldName.setCaption(Translate.getMessage("textFieldName"));
        fieldDescription.setCaption(Translate.getMessage("fieldDescription"));
        textFieldCreator.setCaption(Translate.getMessage("textFieldCreator"));
        textFieldMime.setCaption(Translate.getMessage("textFieldMime"));
        textFieldSize.setCaption(Translate.getMessage("textFieldSize"));
        dateFieldCreate.setCaption(Translate.getMessage("dateFieldCreate"));
        dateFieldUpdate.setCaption(Translate.getMessage("dateFieldUpdate"));

        binder.setItemDataSource(this.document);
        binder.bind(textFieldName, "name");
        binder.bind(fieldDescription, "description");
        binder.bind(textFieldCreator, "creator");
        binder.bind(dateFieldCreate, "createDate");
        binder.bind(dateFieldUpdate, "updateDate");
        binder.bind(textFieldMime, "mime");
        binder.bind(textFieldSize, "size");
        binder.setBuffered(true); // important

        fileUpload.setImmediate(true);
        fileUpload.addSucceededListener(imageUploader);
        fileUpload.setReceiver(imageUploader);
        demandFileDownloader.extend(btnDownload);

        checkVisibility();
    }

    private void checkVisibility() {
        if (Objects.equals(Document.TYPE.FILE, document.getType())) {
            fileLayout.setVisible(true);
            textFieldName.setVisible(false);
            textFieldMime.setVisible(true);
            textFieldSize.setVisible(true);
            textFieldMime.setReadOnly(true);
            textFieldSize.setReadOnly(true);
            if (document.getSize() == 0) {
                btnDownload.setVisible(false);
                fileUpload.setVisible(true);
            } else {
                fileUpload.setVisible(true);
                btnDownload.setVisible(true);
                btnDownload.setCaption(document.getName());
            }
        } else if (Objects.equals(Document.TYPE.FOLDER, document.getType())) {
            fileLayout.setVisible(false);
            textFieldMime.setVisible(false);
            textFieldSize.setVisible(false);
            if (this.document.getId() != null) {
                textFieldName.setReadOnly(true);
            } else {
                textFieldName.setReadOnly(false);
            }
        }

        if (this.document.getId() != null) {
            textFieldCreator.setReadOnly(true);
            dateFieldCreate.setReadOnly(true);
            dateFieldUpdate.setReadOnly(true);

            textFieldCreator.setVisible(true);
            dateFieldCreate.setVisible(true);
            dateFieldUpdate.setVisible(true);
        } else {
            textFieldCreator.setVisible(false);
            dateFieldCreate.setVisible(false);
            dateFieldUpdate.setVisible(false);
        }
    }

    public void save() {
        try {
            binder.commit();
            document = binder.getItemDataSource().getBean();
            document = HybridbpmUI.getDocumentAPI().saveDocument(document);
            binder.setItemDataSource(document);
            checkVisibility();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class ImageUploader implements Upload.Receiver, Upload.SucceededListener {

        private String filename;
        private String mimeType;

        @Override
        public OutputStream receiveUpload(String filename, String mimeType) {
            this.filename = filename;
            this.mimeType = mimeType;
            baos.reset();
            return baos;
        }

        @Override
        public void uploadSucceeded(Upload.SucceededEvent event) {
            try {
                image = baos.toByteArray();
                binder.commit();
                document = binder.getItemDataSource().getBean();
                document.setUpdateDate(new Date());
                document.setName(document.getId() != null ? document.getName() : filename);
                document.setBody(image);
                document.setMime(document.getId() != null ? document.getMime() : mimeType);
                document.setSize(image.length);
                binder.setItemDataSource(document);
                checkVisibility();
            } catch (FieldGroup.CommitException ex) {
                Logger.getLogger(DocumentFormLayout.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    class OnDemandStreamResource extends StreamResource {

        public OnDemandStreamResource() {
            super(() -> new ByteArrayInputStream(new byte[1]), "");
        }

    }

    class OnDemandFileDownloader extends FileDownloader {

        public OnDemandFileDownloader(Resource resource) {
            super(resource);
        }

        @Override
        public boolean handleConnectorRequest(VaadinRequest request, VaadinResponse response, String path) throws IOException {
            if (image == null && document.getId() != null) {
                StreamResource resource = new StreamResource(() -> {
                    Document doc = HybridbpmUI.getDocumentAPI().getDocumentById(document.getId(), true);
                    return new ByteArrayInputStream(doc.getBody());
                }, textFieldName.getValue());
                this.setResource("dl", resource);
            } else {
                StreamResource resource = new StreamResource(() -> new ByteArrayInputStream(image), textFieldName.getValue());
                this.setResource("dl", resource);
            }

            return super.handleConnectorRequest(request, response, path);
        }
    }

}
