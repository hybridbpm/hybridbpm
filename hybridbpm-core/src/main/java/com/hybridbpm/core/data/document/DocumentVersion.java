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
package com.hybridbpm.core.data.document;

import com.hybridbpm.model.Translated;
import com.orientechnologies.orient.core.record.impl.ORecordBytes;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 *
 * @author Marat Gubaidullin
 */
public class DocumentVersion implements Serializable {

    @Id
    protected Object id;
    protected Date createDate;
    protected Translated description;
    protected String creator;
    private String name;
    private Document document;
    private String mime;
    private int size;
    private int documentVersion;
    @OneToOne(orphanRemoval = true)
    private ORecordBytes body;

    public DocumentVersion() {
    }

    public Object getId() {
        return id;
    }

    public int getDocumentVersion() {
        return documentVersion;
    }

    public void setDocumentVersion(int documentVersion) {
        this.documentVersion = documentVersion;
    }
    
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Translated getDescription() {
        return description;
    }

    public void setDescription(Translated description) {
        this.description = description;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocumen(Document document) {
        this.document = document;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public ORecordBytes getBody() {
        return body;
    }

    public void setBody(ORecordBytes body) {
        this.body = body;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DocumentVersion other = (DocumentVersion) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.id);
        return hash;
    }

}
