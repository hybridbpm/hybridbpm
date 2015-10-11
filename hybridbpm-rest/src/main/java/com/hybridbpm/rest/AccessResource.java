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
import com.hybridbpm.core.util.HybridbpmCoreUtil;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Marat Gubaidullin
 */
@Path(RestConstant.PATH_AUTHENTICATION)
public class AccessResource {

    private static final Logger logger = Logger.getLogger(AccessResource.class.getSimpleName());

    @GET
    @POST
    @Path(RestConstant.PATH_TOKEN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response getToken(@Context HttpHeaders headers) {
        try {
            String access = headers.getHeaderString(RestConstant.HEADER_ACCESS);
            String username = headers.getHeaderString(RestConstant.HEADER_USERNAME);
            String password = headers.getHeaderString(RestConstant.HEADER_PASSWORD);
            String error = headers.getHeaderString(RestConstant.HEADER_ERROR);
//            System.out.println(access != null);
//            System.out.println(Boolean.parseBoolean(access));
            if (access != null && Boolean.parseBoolean(access)) {
                AccessAPI accessAPI = AccessAPI.get(null, null);
                User user = accessAPI.login(username, password);
                String token = HybridbpmCoreUtil.generateToken(username);
                accessAPI.setUserToken(user, token);
                return Response.ok(token).build();
            } else {
                return Response.status(Response.Status.FORBIDDEN).entity(error).build();
            }
        } catch (RuntimeException | NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(Response.Status.FORBIDDEN).entity(ex.getMessage()).build();
        }
    }

}
