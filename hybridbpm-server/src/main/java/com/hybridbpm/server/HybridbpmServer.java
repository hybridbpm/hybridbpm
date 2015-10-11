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

import com.hybridbpm.rest.AccessFilter;
import com.hybridbpm.core.HybridbpmCore;
import com.hybridbpm.rest.HybridbpmRestApplication;
import com.hybridbpm.rest.RestConstant;
import com.vaadin.server.VaadinServlet;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.servlet.ServletExtension;
import static io.undertow.servlet.Servlets.defaultContainer;
import static io.undertow.servlet.Servlets.deployment;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletInfo;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;

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
            startUndertow();
            startUndertowJaxrsServer();
            logger.info("HybridbpmServer started");
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private static void startUndertow() throws ServletException {
        ServletInfo servletInfo = new ServletInfo(VaadinServlet.class.getName(), VaadinServlet.class)
                .setAsyncSupported(true)
                .setLoadOnStartup(1)
                .addInitParam("ui", "com.hybridbpm.ui.HybridbpmUI").addInitParam("widgetset", "com.hybridbpm.ui.HybridbpmWidgetSet")
                .addMapping("/*").addMapping("/VAADIN");

        DeploymentInfo deploymentInfo = deployment()
                .setClassLoader(HybridbpmServer.class.getClassLoader())
                .setContextPath(PATH)
                .setDeploymentName("hybridbpm.war")
                .setDisplayName("HYBRIDBPM")
                .setResourceManager(new ClassPathResourceManager(HybridbpmServer.class.getClassLoader()))
                .addServlets(servletInfo)
                .addServletContextAttribute(WebSocketDeploymentInfo.ATTRIBUTE_NAME, new WebSocketDeploymentInfo());

        DeploymentManager manager = defaultContainer().addDeployment(deploymentInfo);
        manager.deploy();

        PathHandler path = Handlers.path(Handlers.redirect(PATH)).addPrefixPath(PATH, manager.start());

        Undertow.Builder builder = Undertow.builder().addHttpListener(8080, "0.0.0.0").setHandler(path);

        undertow = builder.build();
        undertow.start();
        logger.info("HybridbpmServer UI started");
    }

    private static void startUndertowJaxrsServer() {
        Undertow.Builder builderJaxrs = Undertow.builder().addHttpListener(8081, "0.0.0.0");
        undertowJaxrsServer = new UndertowJaxrsServer().start(builderJaxrs);

        ServletExtension servletExtension = (DeploymentInfo deploymentInfo, ServletContext servletContext) -> {
            servletContext
                    .addFilter("MyFilter1", AccessFilter.class)
                    .addMappingForServletNames(null, false, "ResteasyServlet");
            
            servletContext
                    .addFilter("MyFilter2", AccessFilter.class)
                    .addMappingForUrlPatterns(null, false, "/");
        };

        ResteasyDeployment resteasyDeployment = new ResteasyDeployment();
        resteasyDeployment.setApplicationClass(HybridbpmRestApplication.class.getName());
        DeploymentInfo deploymentInfo = undertowJaxrsServer.undertowDeployment(resteasyDeployment);
        deploymentInfo.setClassLoader(Thread.currentThread().getContextClassLoader());
        deploymentInfo.setContextPath(RestConstant.PATH_API);
        deploymentInfo.setDeploymentName("HybridbpmRestApplication");
        deploymentInfo.addServletExtension(servletExtension);
        undertowJaxrsServer.deploy(deploymentInfo);

        logger.info("HybridbpmServer REST started");

    }

}
