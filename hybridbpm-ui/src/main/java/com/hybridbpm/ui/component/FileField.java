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
import com.hybridbpm.ui.HybridbpmUI;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.declarative.Design;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Marat Gubaidullin
 */
@DesignRoot
@SuppressWarnings("serial")
public class FileField extends HorizontalLayout implements Button.ClickListener {

    private File file;

    private Button btnDownload;
    private Button btnDelete;

    private final OnDemandStreamResource demandStreamResource = new OnDemandStreamResource();
    private final OnDemandFileDownloader demandFileDownloader = new OnDemandFileDownloader(demandStreamResource);

    public ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private byte[] image = null;

    public FileField(File file) {
        this.file = file;
         Design.read(this);
        btnDelete.setIcon(FontAwesome.TIMES);
        btnDelete.addClickListener(this);
        demandFileDownloader.extend(btnDownload);
        checkVisibility();
    }

    private void checkVisibility() {
        if (file.getSize() == 0) {
            btnDownload.setVisible(false);
            btnDelete.setVisible(false);
        } else {
            btnDelete.setVisible(true);
            btnDownload.setVisible(true);
            btnDownload.setCaption(file.getFileName());
        }
    }

    public File getFile() {
        return file;
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton().equals(btnDelete)){
            ((FileManager)getParent()).remove(this);
        }
    }

    class OnDemandStreamResource extends StreamResource {

        public OnDemandStreamResource() {
            super(new StreamSource() {

                @Override
                public InputStream getStream() {
                    return new ByteArrayInputStream(new byte[1]);
                }
            }, "");
        }

    }

    class OnDemandFileDownloader extends FileDownloader {

        public OnDemandFileDownloader(Resource resource) {
            super(resource);
        }

        @Override
        public boolean handleConnectorRequest(VaadinRequest request, VaadinResponse response, String path) throws IOException {
            if (image == null && file.getId() != null) {
                StreamResource resource = new StreamResource(new StreamSource() {

                    @Override
                    public InputStream getStream() {
                        File doc = HybridbpmUI.getBpmAPI().getFileById(file.getId(), true);
                        return new ByteArrayInputStream(doc.getBody());
                    }
                }, file.getFileName());
                this.setResource("dl", resource);
            } else {
                StreamResource resource = new StreamResource(new StreamSource() {

                    @Override
                    public InputStream getStream() {
                        return new ByteArrayInputStream(image);
                    }
                }, file.getFileName());
                this.setResource("dl", resource);
            }

            return super.handleConnectorRequest(request, response, path);
        }
    }

}
