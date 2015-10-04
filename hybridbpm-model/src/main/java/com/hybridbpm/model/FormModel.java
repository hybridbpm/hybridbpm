/* 
 * Copyright (c) 2011-2015 Marat Gubaidullin. 
 * 
 * This file is part of HYBRIDBPM.
 * 
 * HybridBPM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * HybridBPM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with HybridBPM.  If not, see <http ://www.gnu.org/licenses/>.
 */
package com.hybridbpm.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Marat Gubaidullin
 */
public class FormModel extends AbstractModel {

    private List<FieldModel> parameters;
    private List<FileModel> files;

    public FormModel() {
    }
    
    public static FormModel createDefault(){
        FormModel formModel = new FormModel();
        formModel.getParameters().add(new FieldModel("field1", "Field", String.class.getCanonicalName(), null, FieldModel.COLLECTION_TYPE.NONE, FieldModel.EDITOR_TYPE.TEXT_FIELD));
        return formModel;
    }
    
    public static FormModel create(String name){
        FormModel formModel = new FormModel();
        formModel.setName(name);
        return formModel;
    }

    public List<FieldModel> getParameters() {
        if (parameters == null){
            parameters = new ArrayList<>();
        }
        return parameters;
    }

    public void setParameters(List<FieldModel> parameter) {
        this.parameters = parameter;
    }

    public void addParameter(FieldModel fieldModel){
        getParameters().add(fieldModel);
    }
    
    public List<FileModel> getFiles() {
        if (files == null){
            files = new ArrayList<>();
        }
        return files;
    }

    public void setFiles(List<FileModel> files) {
        this.files = files;
    }

    public void addFile(FileModel fileModel){
        getFiles().add(fileModel);
    }

    
}
