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
package com.hybridbpm.ui.component;

import com.vaadin.annotations.JavaScript;
import com.vaadin.server.AbstractJavaScriptExtension;
import com.vaadin.ui.TextArea;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Marat Gubaidullin
 */
@JavaScript({"htextcomplete.js", "jquery.min.js", "jquery.overlay.js", "jquery.textcomplete.js", "jquery.textcomplete.js"})
public class TextComplete extends AbstractJavaScriptExtension {

    public void extend(final TextArea textArea, List<String> users, List<String> projects) {
        if (textArea.getId() == null) {
            textArea.setId(UUID.randomUUID().toString().replace("-", ""));
        }
        StringBuilder execute = new StringBuilder("htextcomplete('#");
        execute.append(textArea.getId());
        execute.append("', [");
        for (String user : users) {
            execute.append("'").append(user).append("',");
        }
        execute.deleteCharAt(execute.length() - 1);
        execute.append("], [");
        for (String project : projects) {
            execute.append("'").append(project).append("',");
        }
        execute.deleteCharAt(execute.length() - 1);
        execute.append("])");
        super.extend(textArea);
        com.vaadin.ui.JavaScript.getCurrent().execute(execute.toString());
    }

}
