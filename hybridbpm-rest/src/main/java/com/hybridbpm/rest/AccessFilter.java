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

import com.hybridbpm.core.api.AccessAPI;
import com.hybridbpm.core.data.access.User;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Marat Gubaidullin
 */
public class AccessFilter implements Filter {

    private static final Logger logger = Logger.getLogger(AccessFilter.class.getSimpleName());

    private final AccessAPI accessAPI = AccessAPI.get(null, null);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.log(Level.INFO, "init {0}", filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        Map<String, String> headers = new HashMap<>();
        if (Objects.equals(httpServletRequest.getPathInfo(), RestConstant.PATH_AUTHENTICATION + RestConstant.PATH_TOKEN)) {
            // basic authentication to get token
            String[] credentials = HybridbpmRestUtil.getBasicAuthorizationCredentials(httpServletRequest);
            if (credentials != null && credentials.length == 2) {
                String username = credentials[0];
                String password = credentials[1];
                try {
                    User user = accessAPI.login(username, password);
                    headers.put(RestConstant.HEADER_LOCALE, httpServletRequest.getLocale().toString());
                    headers.put(RestConstant.HEADER_USERNAME, username);
                    headers.put(RestConstant.HEADER_PASSWORD, password);
                    headers.put(RestConstant.HEADER_ACCESS, Boolean.TRUE.toString());
                } catch (RuntimeException re) {
                    headers.put(RestConstant.HEADER_LOCALE, httpServletRequest.getLocale().toString());
                    headers.put(RestConstant.HEADER_ERROR, re.getLocalizedMessage());
                    headers.put(RestConstant.HEADER_ACCESS, Boolean.FALSE.toString());
                }
            }
        } else {
            // token authentication for api
            try {
                String token = HybridbpmRestUtil.getAuthorizationToken(httpServletRequest);
                User user = accessAPI.getUserByUserToken(token);
                headers.put(RestConstant.HEADER_LOCALE, httpServletRequest.getLocale().toString());
                headers.put(RestConstant.HEADER_USERNAME, user.getUsername());
                headers.put(RestConstant.HEADER_ACCESS, Boolean.TRUE.toString());
            } catch (RuntimeException re) {
                headers.put(RestConstant.HEADER_LOCALE, httpServletRequest.getLocale().toString());
                headers.put(RestConstant.HEADER_ERROR, re.getLocalizedMessage());
                headers.put(RestConstant.HEADER_ACCESS, Boolean.FALSE.toString());
            }
        }
        chain.doFilter(new SecurityHttpServletRequest(httpServletRequest, headers), response);
    }

    @Override
    public void destroy() {
    }

}
