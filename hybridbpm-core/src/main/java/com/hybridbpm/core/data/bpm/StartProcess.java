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

import com.hybridbpm.model.ProcessModel;
import java.util.Objects;

/**
 *
 * @author Marat Gubaidullin
 */
public class StartProcess {

    private ProcessModel processModel;
    private String taskName;
    private String icon;

    public StartProcess(ProcessModel processModel, String taskName, String icon) {
        this.processModel = processModel;
        this.taskName = taskName;
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public ProcessModel getProcessModel() {
        return processModel;
    }

    public void setProcessModel(ProcessModel processModel) {
        this.processModel = processModel;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StartProcess other = (StartProcess) obj;
        if (!Objects.equals(this.processModel.getName(), other.processModel.getName())) {
            return false;
        }
        return Objects.equals(this.taskName, other.taskName);
    }

    
}
