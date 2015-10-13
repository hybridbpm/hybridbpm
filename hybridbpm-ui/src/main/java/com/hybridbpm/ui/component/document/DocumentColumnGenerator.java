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
import com.hybridbpm.ui.util.Translate;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.themes.ValoTheme;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Objects;

/**
 *
 * @author Marat Gubaidullin
 */
public final class DocumentColumnGenerator implements Table.ColumnGenerator {

    private final ClickListener clickListener;

    public DocumentColumnGenerator(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public Object generateCell(Table source, Object itemId, Object columnId) {
        Document document = (Document) itemId;
        Button button = new Button(document.getName());
        button.setData(document);
        button.addStyleName(ValoTheme.BUTTON_LINK);
        if (Objects.equals(document.getType(), Document.TYPE.FILE)){
            OnDemandFileDownloader onDemandFileDownloader = new OnDemandFileDownloader(document.getId().toString(), document.getName());
            onDemandFileDownloader.extend(button);
            button.setDescription(Translate.getMessage("btnDownload"));
        } else {
            button.setDescription(Translate.getMessage("btnOpen"));
            button.addClickListener(clickListener);
        }
        return button;
    }
    
    class OnDemandStreamResource extends StreamResource {

        public OnDemandStreamResource() {
            super(() -> new ByteArrayInputStream(new byte[1]), "");
        }

    }

    class OnDemandFileDownloader extends FileDownloader {

        private final String documentId;
        private final String name;

        public OnDemandFileDownloader(String documentId, String name) {
            super(new OnDemandStreamResource());
            this.documentId = documentId;
            this.name = name;
        }

        @Override
        public boolean handleConnectorRequest(VaadinRequest request, VaadinResponse response, String path) throws IOException {
            StreamResource resource = new StreamResource(() -> {
                Document doc = HybridbpmUI.getDocumentAPI().getDocumentById(documentId, true);
                return new ByteArrayInputStream(doc.getBody());
            }, name);
            this.setResource("dl", resource);
            return super.handleConnectorRequest(request, response, path);
        }
    }

}
