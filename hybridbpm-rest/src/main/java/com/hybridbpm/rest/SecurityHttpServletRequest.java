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
package com.hybridbpm.rest;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 *
 * @author Marat Gubaidullin
 */
public class SecurityHttpServletRequest extends HttpServletRequestWrapper {

    private final Map<String, String> headers;

    public SecurityHttpServletRequest(HttpServletRequest request, Map<String, String> headers) {
        super(request);
        this.headers = headers;
    }

    @Override
    public String getHeader(String name) {
        String value = headers.get(name);
        if (value != null) {
            return value;
        }
        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        String value = headers.get(name);
        if (value != null) {
            return Collections.enumeration(Collections.singletonList(value));
        }
        return super.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        List<String> headerNames = Collections.list(super.getHeaderNames());
        headers.keySet().stream().filter((headerName) -> (!headerNames.contains(headerName))).forEach((headerName) -> {
            headerNames.add(headerName);
        });
        return Collections.enumeration(headerNames);
    }

}
