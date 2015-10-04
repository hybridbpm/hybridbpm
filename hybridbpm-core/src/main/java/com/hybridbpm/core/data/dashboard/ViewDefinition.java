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
package com.hybridbpm.core.data.dashboard;

import com.hybridbpm.model.Translated;
import java.io.Serializable;
import javax.persistence.Id;


/**
 *
 * @author Marat Gubaidullin
 */
public class ViewDefinition implements Serializable {

    @Id
    protected Object id;
    protected Translated title;

    private String url;
    private String icon;
    private Integer order;

    public ViewDefinition() {
    }

    public ViewDefinition(Integer order, String url, String title, String icon) {
        this.order = order;
        this.url = url;
        this.icon = icon;
        this.title = new Translated(title);
    }

    public Object getId() {
        return id;
    }

    public Translated getTitle() {
        return title;
    }

    public void setTitle(Translated title) {
        this.title = title;
    }
    
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
    
}
