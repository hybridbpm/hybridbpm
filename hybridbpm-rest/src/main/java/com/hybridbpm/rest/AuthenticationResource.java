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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jboss.resteasy.util.Base64;

/**
 *
 * @author Marat Gubaidullin
 */
@Path("/authentication")
public class AuthenticationResource {

    @GET
    @Path("/token")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getToken(@Context HttpHeaders headers) {
        try {
            if (headers != null) {
                String header = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
                String[] tokens = header.split("\\s+", 2);
                String decoded = new String(Base64.decode(tokens[1]));
                String[] credentials = decoded.split(":", 2);
                String username = credentials[0];
                String password = credentials[1];
                User user = AccessAPI.get(null, null).login(username, password);
                
                return Response.ok(user.getToken()).build();
            }
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
}
