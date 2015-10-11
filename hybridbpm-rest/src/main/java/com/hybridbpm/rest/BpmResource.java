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
import com.hybridbpm.core.api.RestAPI;
import com.hybridbpm.core.data.access.User;
import com.hybridbpm.model.mobile.MobileTask;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Marat Gubaidullin
 */
@Path(RestConstant.PATH_BPM)
public class BpmResource {

    private static final Logger logger = Logger.getLogger(BpmResource.class.getSimpleName());

    @GET
    @Path(RestConstant.PATH_TASKS)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMyTasks(@Context HttpHeaders headers) {
        try {
            String username = headers.getHeaderString(RestConstant.HEADER_USERNAME);
            String locale = headers.getHeaderString(RestConstant.HEADER_LOCALE);
            if (username != null) {
                AccessAPI accessAPI = AccessAPI.get(null, null);
                User user = accessAPI.getUserByUserName(username);
                RestAPI restAPI = RestAPI.get(user, null);
                List<MobileTask> tasks = restAPI.getMyTasksToDo(new Locale(locale));
                GenericEntity<List<MobileTask>> list = new GenericEntity<List<MobileTask>>(tasks) {
                };
                return Response.ok(list).build();
            } else {
                return Response.status(Response.Status.FORBIDDEN).build();
            }
        } catch (RuntimeException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(ex.getMessage()).build();
        }
    }

}
