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
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Id;

/**
 *
 * @author Marat Gubaidullin
 */
public class Document implements Serializable {

    @Id
    protected Object id;
    protected Date createDate;
    protected Date updateDate;
    protected Translated description;
    protected String creator;
    private String name;
    private String path;
    private Document parent;
    private String mime;
    private int size;
    transient private byte[] body;

    private TYPE type;

    public enum TYPE {

        FILE, FOLDER
    };

    public Document() {
    }
    
    public static Document createFolder(String name, String description, Document parent) {
        Document document = createFolder(parent);
        document.setName(name);
        document.setDescription(new Translated(description));
        return document;
    }

    public static Document createFolder(Document parent) {
        Document document = new Document();
        document.setType(TYPE.FOLDER);
        if (parent != null) {
            document.setParent(parent);
        }
        document.setDescription(new Translated(""));
        return document;
    }

    public static Document createFile(Document parent) {
        Document document = new Document();
        document.setType(TYPE.FILE);
        document.setDescription(new Translated(""));
        document.setParent(parent);
        return document;
    }

    public Object getId() {
        return id;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Translated getDescription() {
        return description;
    }

    public void setDescription(Translated description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Document getParent() {
        return parent;
    }

    public void setParent(Document parent) {
        this.parent = parent;
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

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
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
        final Document other = (Document) obj;
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
