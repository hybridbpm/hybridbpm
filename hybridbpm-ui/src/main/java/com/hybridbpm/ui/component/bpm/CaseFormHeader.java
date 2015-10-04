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
package com.hybridbpm.ui.component.bpm;

import com.hybridbpm.core.data.bpm.Case;
import com.vaadin.ui.*;
import com.vaadin.ui.declarative.*;
import com.vaadin.annotations.DesignRoot;

@DesignRoot
public class CaseFormHeader extends HorizontalLayout {
    
    private TextField caseName;
    private TextField caseInitiator;
    private DateField startDate;
    private DateField finishDate;
    private DateField updateDate;
    private TextField status;

    public CaseFormHeader() {
        Design.read(this);
    }

    public void setData(Case hCase){
        this.caseName.setReadOnly(false);
        this.caseInitiator.setReadOnly(false);
        this.startDate.setReadOnly(false);
        this.finishDate.setReadOnly(false);
        this.updateDate.setReadOnly(false);
        this.status.setReadOnly(false);
        
        this.caseName.setValue(hCase.getModelName());
        this.caseInitiator.setValue(hCase.getInitiator());
        this.startDate.setValue(hCase.getStartDate());
        this.finishDate.setValue(hCase.getFinishDate());
        this.updateDate.setValue(hCase.getUpdateDate());
        this.status.setValue(hCase.getStatus().toString());
        
        this.caseName.setReadOnly(true);
        this.caseInitiator.setReadOnly(true);
        this.startDate.setReadOnly(true);
        this.finishDate.setReadOnly(true);
        this.updateDate.setReadOnly(true);
        this.status.setReadOnly(true);
        
    }
}
