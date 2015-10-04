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
public class DataModel extends AbstractModel {

    private List<FieldModel> fields;
    
    public DataModel() {
    }
    
    public static DataModel createDefault(){
        DataModel dataModel = new DataModel();
        dataModel.getFields().add(new FieldModel("field1", "Field", String.class.getCanonicalName(), null, FieldModel.COLLECTION_TYPE.NONE, FieldModel.EDITOR_TYPE.TEXT_FIELD));
        return dataModel;
    }

    public List<FieldModel> getFields() {
        if (fields == null){
            fields = new ArrayList<>();
        }
        return fields;
    }

    public void setFields(List<FieldModel> fields) {
        this.fields = fields;
    }

    public void addField(FieldModel fieldModel){
        getFields().add(fieldModel);
    }
    
    public boolean containsField(String name){
        for (FieldModel fieldModel : fields){
            if (fieldModel.getName().equals(name)){
                return true;
            }
        }
        return false;
    }
}
