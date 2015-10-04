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
package com.hybridbpm.ui.component.bpm.window;

import com.hybridbpm.model.FieldModel;
import com.hybridbpm.model.ProcessModel;
import java.util.LinkedList;
import java.util.List;
import org.vaadin.aceeditor.Suggester;
import org.vaadin.aceeditor.Suggestion;

/**
 *
 * @author Marat Gubaidullin
 */
public class VariableSuggester implements Suggester {
    
    private final ProcessModel processModel;

    public VariableSuggester(ProcessModel processModel) {
        this.processModel = processModel;
    }
    
    @Override
    public List<Suggestion> getSuggestions(String string, int i) {
        LinkedList<Suggestion> suggs = new LinkedList<>();
        suggs.add(new Suggestion("currentCase (Case)", null, "currentCase"));
        for (FieldModel fieldModel : processModel.getVariableModels()){
            suggs.add(new Suggestion(fieldModel.getName() + " ("+fieldModel.getClassName()+")", null, fieldModel.getName()));
        }
        return suggs;
    }

    @Override
    public String applySuggestion(Suggestion sugg, String text, int cursor) {
        String ins = sugg.getSuggestionText();
        String s1 = text.substring(0, cursor) + ins + text.substring(cursor);
        return s1;
    }

}
