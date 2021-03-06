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
package com.hybridbpm.core.data.bpm;

import com.orientechnologies.orient.core.record.impl.ORecordBytes;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 *
 * @author Marat Gubaidullin
 */
public class FileBody implements Serializable {

    @Id
    protected Object id;
    private File file;
    @OneToOne(orphanRemoval = true)
    private ORecordBytes body;

    public FileBody() {
    }

    public Object getId() {
        return id;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
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
        final FileBody other = (FileBody) obj;
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
