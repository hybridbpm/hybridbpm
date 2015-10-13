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

import com.hybridbpm.ui.component.TableButtonBar;
import com.hybridbpm.core.data.document.Document;
import com.hybridbpm.core.data.document.DocumentVersion;
import com.hybridbpm.ui.HybridbpmUI;
import com.hybridbpm.ui.component.TableButton;
import com.hybridbpm.ui.component.bpm.DateColumnGenerator;
import com.hybridbpm.ui.util.Translate;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.data.Item;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Objects;

@DesignRoot
@SuppressWarnings("serial")
public final class DocumentHistoryLayout extends VerticalLayout  {

    public Table historyTable;
    private Document document;

    public DocumentHistoryLayout() {
        Design.read(this);

        historyTable.addContainerProperty("documentVersion", Integer.class, null, Translate.getMessage("documentHistoryVersion"), null, Table.Align.RIGHT);
        historyTable.addContainerProperty("size", Integer.class, null, Translate.getMessage("documentHistorySize"), null, Table.Align.RIGHT);
        historyTable.addContainerProperty("creator", String.class, null, Translate.getMessage("documentHistoryCreator"), null, Table.Align.LEFT);
        historyTable.addContainerProperty("createDate", Date.class, null, Translate.getMessage("documentHistoryCreateDate"), null, Table.Align.LEFT);
        historyTable.addContainerProperty("actions", TableButtonBar.class, null, Translate.getMessage("documentHistoryActions"), null, Table.Align.LEFT);
        historyTable.setColumnWidth("createDate", 150);
        historyTable.setColumnWidth("actions", 55);
        historyTable.addGeneratedColumn("createDate", new DateColumnGenerator());
        historyTable.setVisibleColumns("documentVersion", "creator", "createDate", "size", "actions");

    }
    
    public void setDocument(Document document) {
        this.document = document;
    }
    
    public void refreshTable() {    
        historyTable.removeAllItems();
        for (DocumentVersion documentVersion : HybridbpmUI.getDocumentAPI().getDocumentVersions(document.getId().toString())) {
            addToTable(documentVersion);
        }
        historyTable.sort(new Object[]{"documentVersion"}, new boolean[]{false});
    }

    private void addToTable(DocumentVersion documentVersion) {
        Item item = historyTable.addItem(documentVersion);
        item.getItemProperty("documentVersion").setValue(documentVersion.getDocumentVersion());
        item.getItemProperty("size").setValue(documentVersion.getSize());
        item.getItemProperty("creator").setValue(documentVersion.getCreator());
        item.getItemProperty("createDate").setValue(documentVersion.getCreateDate());
        if (Objects.equals(document.getType(), Document.TYPE.FOLDER)) {
        } else {
            item.getItemProperty("actions").setValue(getTableButtonBar(documentVersion));
        }
    }

    private Object getTableButtonBar(DocumentVersion documentVersion) {
        if (Objects.equals(document.getType(), Document.TYPE.FILE)) {
            TableButton downloadButton = TableButton.createDownload(document, null);
            OnDemandFileDownloader onDemandFileDownloader = new OnDemandFileDownloader(documentVersion.getId().toString(), document.getName());
            onDemandFileDownloader.extend(downloadButton);
            return new TableButtonBar(downloadButton);
        } else {
            return new TableButtonBar();
        }
    }

    class OnDemandStreamResource extends StreamResource {

        public OnDemandStreamResource() {
            super(new StreamResource.StreamSource() {

                @Override
                public InputStream getStream() {
                    return new ByteArrayInputStream(new byte[1]);
                }
            }, "");
        }

    }

    class OnDemandFileDownloader extends FileDownloader {

        private final String documentVersionId;
        private final String name;

        public OnDemandFileDownloader(String documentVersionId, String name) {
            super(new OnDemandStreamResource());
            this.documentVersionId = documentVersionId;
            this.name = name;
        }

        @Override
        public boolean handleConnectorRequest(VaadinRequest request, VaadinResponse response, String path) throws IOException {
            StreamResource resource = new StreamResource(new StreamResource.StreamSource() {

                @Override
                public InputStream getStream() {
                    byte[] doc = HybridbpmUI.getDocumentAPI().getDocumentBodyByVersionId(documentVersionId);
                    return new ByteArrayInputStream(doc);
                }
            }, name);
            this.setResource("dl", resource);
            return super.handleConnectorRequest(request, response, path);
        }
    }

}
