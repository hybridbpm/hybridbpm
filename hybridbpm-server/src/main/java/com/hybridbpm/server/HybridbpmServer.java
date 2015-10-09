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
package com.hybridbpm.server;

import com.hybridbpm.core.HybridbpmCore;
import com.hybridbpm.rest.HybridbpmRestApplication;
import com.vaadin.server.VaadinServlet;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import static io.undertow.servlet.Servlets.defaultContainer;
import static io.undertow.servlet.Servlets.deployment;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletInfo;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;

/**
 *
 * @author Marat Gubaidullin
 */
public class HybridbpmServer {

    private static final Logger logger = Logger.getLogger(HybridbpmServer.class.getSimpleName());
    private static Undertow undertow;
    private static UndertowJaxrsServer undertowJaxrsServer;
    private static final String PATH = "/hybridbpm";
    private static final HybridbpmCore hybridbpmServer = new HybridbpmCore();

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        logger.info("HybridbpmServer starting");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                logger.info("HybridbpmServer stopping");
                hybridbpmServer.stop();
                undertowJaxrsServer.stop();
                undertow.stop();
                logger.info("HybridbpmServer stopped");
            } catch (Exception ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }));
        try {
            hybridbpmServer.start();

            ServletInfo servletInfo = new ServletInfo("VaadinServlet", VaadinServlet.class)
                    .setAsyncSupported(true)
                    .setLoadOnStartup(1)
                    .addInitParam("ui", "com.hybridbpm.ui.HybridbpmUI").addInitParam("widgetset", "com.hybridbpm.ui.HybridbpmWidgetSet")
                    .addMapping("/*").addMapping("/VAADIN");

            DeploymentInfo servletBuilder = deployment()
                    .setClassLoader(HybridbpmServer.class.getClassLoader())
                    .setContextPath(PATH)
                    .setDeploymentName("hybridbpm.war")
                    .setDisplayName("HYBRIDBPM")
                    .setResourceManager(new ClassPathResourceManager(HybridbpmServer.class.getClassLoader()))
                    .addServlets(servletInfo)
                    .addServletContextAttribute(WebSocketDeploymentInfo.ATTRIBUTE_NAME, new WebSocketDeploymentInfo());

            DeploymentManager manager = defaultContainer().addDeployment(servletBuilder);
            manager.deploy();

            PathHandler path = Handlers.path(Handlers.redirect(PATH)).addPrefixPath(PATH, manager.start());

            Undertow.Builder builder = Undertow.builder().addHttpListener(8080, "0.0.0.0").setHandler(path);

            undertow = builder.build();
            undertow.start();
            logger.info("HybridbpmServer UI started");

            Undertow.Builder builderJaxrs = Undertow.builder().addHttpListener(8081, "0.0.0.0");
            undertowJaxrsServer = new UndertowJaxrsServer().start(builderJaxrs);
            undertowJaxrsServer.deploy(HybridbpmRestApplication.class);

            logger.info("HybridbpmServer REST started");

            logger.info("HybridbpmServer started");
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

}
