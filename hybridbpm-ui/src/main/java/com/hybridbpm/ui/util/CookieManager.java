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
package com.hybridbpm.ui.util;

import com.vaadin.ui.JavaScript;
import com.vaadin.ui.JavaScriptFunction;
import elemental.json.JsonArray;
import java.util.UUID;

/**
 *
 * @author Marat Gubaidullin
 */
public class CookieManager {

    public interface Callback {

        void onValue(String value);
    }

    public static void setCookie(String key, String value, String path) {
        JavaScript.getCurrent().execute(String.format(
                "document.cookie = \"%s=%s; path=%s\";", key, value, path
        ));
    }

    public static void getCookieValue(String key, final Callback callback) {
        final String callbackid = "hybridbpmcookie"+UUID.randomUUID().toString().substring(0,8);
        JavaScript.getCurrent().addFunction(callbackid, new JavaScriptFunction() {

            @Override
            public void call(JsonArray arguments) {
                JavaScript.getCurrent().removeFunction(callbackid);
                if(arguments.length() == 0) {
                    callback.onValue(null);
                } else {
                    callback.onValue(arguments.getString(0));
                }
            }
        });
        JavaScript.getCurrent().execute(String.format(
                "var nameEQ = \"%2$s=\";var ca = document.cookie.split(';');for(var i=0;i < ca.length;i++) {var c = ca[i];while (c.charAt(0)==' ') c = c.substring(1,c.length); if (c.indexOf(nameEQ) == 0) {%1$s( c.substring(nameEQ.length,c.length)); return;};} %1$s();", 
                callbackid,key
        ));

    }
}
