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

import java.util.*;

/**
 *
 * @author Marat Gubaidullin
 */
public class ConnectorModel extends AbstractModel {

    private List<FieldModel> inParameters;
    private List<FieldModel> outParameters;

    public ConnectorModel() {
    }
    
    public static ConnectorModel createDefault(){
        ConnectorModel connectorModel = new ConnectorModel();
        connectorModel.getInParameters().add(new FieldModel("inParam", "inParam", String.class.getCanonicalName(), null, FieldModel.COLLECTION_TYPE.NONE, FieldModel.EDITOR_TYPE.TEXT_FIELD));
        connectorModel.getOutParameters().add(new FieldModel("outParam", "outParam", String.class.getCanonicalName(), null, FieldModel.COLLECTION_TYPE.NONE, FieldModel.EDITOR_TYPE.TEXT_FIELD));
        return connectorModel;
    }

    public List<FieldModel> getInParameters() {
        if (inParameters == null){
            inParameters = new ArrayList<>();
        }
        return inParameters;
    }
    
    public void setInParameters(List<FieldModel> inParameters) {
        this.inParameters = inParameters;
    }
    
    public void addInParameter(FieldModel parameter){
        getOutParameters().add(parameter);
    }

    public List<FieldModel> getOutParameters() {
        if (outParameters == null){
            outParameters = new ArrayList<>();
        }
        return outParameters;
    }

    public void setOutParameters(List<FieldModel> outParameters) {
        this.outParameters = outParameters;
    }
    
    public void addOutParameter(FieldModel parameter){
        getOutParameters().add(parameter);
    }
    
}
