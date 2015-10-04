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

import java.io.Serializable;
import java.util.UUID;

/**
 *
 * @author Marat Gubaidullin
 */
public class TransitionModel implements Serializable {

    private String id;
    private String name;
    private String beginElementModel;
    private String endElementModel;
    private String expression;
    private Boolean defaultTransition;
    private Float x;
    private Float y;

    public TransitionModel() {
        this.id = UUID.randomUUID().toString();
    }

    public TransitionModel(String name, String beginElementModel, String endElementModel, Boolean defaultTransition, String expression, Float x, Float y) {
        this();
        this.name = name;
        this.beginElementModel = beginElementModel;
        this.endElementModel = endElementModel;
        this.defaultTransition = defaultTransition;
        this.expression = expression;
        this.x = x;
        this.y = y;
    }
    
    static TransitionModel createConditionalTransitionModel(String name, String beginElement, String endElement, String expression, Float x, Float y) {
        return new TransitionModel(name, beginElement, endElement, Boolean.FALSE, expression, x, y);
    }

    static TransitionModel createDefaultTransitionModel(String name, String beginElement, String endElement, Float x, Float y) {
        return new TransitionModel(name, beginElement, endElement, Boolean.TRUE, null, x, y);
    }

    static TransitionModel createTransitionModel(String name, String beginElement, String endElement, Float x, Float y) {
        return new TransitionModel(name, beginElement, endElement, Boolean.FALSE, null, x, y);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBeginTaskModel() {
        return beginElementModel;
    }

    public void setBeginTaskModel(String beginElementModel) {
        this.beginElementModel = beginElementModel;
    }

    public String getEndTaskModel() {
        return endElementModel;
    }

    public void setEndTaskModel(String endElementModel) {
        this.endElementModel = endElementModel;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public Boolean getDefaultTransition() {
        return defaultTransition;
    }

    public void setDefaultTransition(Boolean defaultTransition) {
        this.defaultTransition = defaultTransition;
    }

    public Float getX() {
        return x;
    }

    public void setX(Float x) {
        this.x = x;
    }

    public Float getY() {
        return y;
    }

    public void setY(Float y) {
        this.y = y;
    }
}
