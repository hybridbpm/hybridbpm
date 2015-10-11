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

import com.hybridbpm.core.data.bpm.File;
import com.hybridbpm.model.FileModel;
import com.hybridbpm.ui.util.HybridbpmStyle;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Component;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marat Gubaidullin
 */
@DesignRoot
@SuppressWarnings("serial")
public class FileManager extends VerticalLayout {

    private FileModel fileModel;
    private Upload fileUpload;

    private final ImageUploader imageUploader = new ImageUploader();

    public ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private byte[] image = null;
    private final List<String> idsToRemove = new ArrayList<>();

    public FileManager() {
        Design.read(this);
        fileUpload.setImmediate(true);
        fileUpload.addSucceededListener(imageUploader);
        fileUpload.setReceiver(imageUploader);
        setStyleName(HybridbpmStyle.LAYOUT_PADDING16);
        addStyleName("card");
    }

    public void initUI(FileModel fileModel) {
        this.fileModel = fileModel;
        checkVisibility();
    }

    private void checkVisibility() {
        if (fileModel.getMultiple()) {
            fileUpload.setVisible(true);
        } else if (!fileModel.getMultiple() && getComponentCount() == 1) {
            fileUpload.setVisible(true);
        } else {
            fileUpload.setVisible(false);
        }
    }

    protected void remove(FileField fileField) {
        if (fileField.getFile() != null && fileField.getFile().getId() != null) {
            idsToRemove.add(fileField.getFile().getId().toString());
        }
        removeComponent(fileField);
        checkVisibility();
    }

    public List<File> getFileList() {
        List<File> result = new ArrayList<>();
        for (Component comp : this) {
            if (comp instanceof FileField) {
                try {
                    File document = ((FileField) comp).getFile();
                    result.add(document);
                } catch (Exception ex) {
                    Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        }
        return result;
    }

    public void setFileList(List<File> files) {
        for (File document : files) {
            FileField fileField = new FileField(document);
            addComponent(fileField, getComponentIndex(fileUpload));
        }
        checkVisibility();
    }

    public List<String> getIdsToRemove() {
        return idsToRemove;
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
                File file = new File();
                file.setBody(image);
                file.setScope(File.SCOPE.CASE);
                file.setFileName(filename);
                file.setMime(file.getId() != null ? file.getMime() : mimeType);
                file.setSize(image.length);
                FileField fileField = new FileField(file);
                addComponent(fileField, getComponentIndex(fileUpload));
                checkVisibility();
            } catch (Exception ex) {
                Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

}
