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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import org.jboss.resteasy.util.Base64;

/**
 *
 * @author Marat Gubaidullin
 */
public class HybridbpmRestUtil {

    private static final Logger logger = Logger.getLogger(HybridbpmRestUtil.class.getSimpleName());

    public static String[] getBasicAuthorizationCredentials(HttpServletRequest request) {
        try {
            if (request != null) {
                String header = request.getHeader(HttpHeaders.AUTHORIZATION);
                String[] tokens = header.split("\\s+", 2);
                String decoded = new String(Base64.decode(tokens[1]));
                String[] credentials = decoded.split(":", 2);
                return credentials;
            } else {
                return null;
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

    public static String getAuthorizationToken(HttpServletRequest request) {
        try {
            if (request != null) {
                String header = request.getHeader(HttpHeaders.AUTHORIZATION);
                String[] tokens = header.split("\\s+", 2);
                return tokens[1];
            } else {
                return null;
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }
    
}
