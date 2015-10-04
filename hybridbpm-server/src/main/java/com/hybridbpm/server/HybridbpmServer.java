package com.hybridbpm.server;

import com.hybridbpm.core.HybridbpmCore;
import com.hybridbpm.core.HybridbpmServletContextListener;
import com.vaadin.server.VaadinServlet;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.FileResourceManager;
import static io.undertow.servlet.Servlets.defaultContainer;
import static io.undertow.servlet.Servlets.deployment;
import static io.undertow.servlet.Servlets.servlet;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ListenerInfo;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mgubaidullin
 */
public class HybridbpmServer {

    private static final Logger logger = Logger.getLogger(HybridbpmServer.class.getSimpleName());
    private static Undertow undertow;
    private static final String PATH = "/hybridbpm";
    private static final HybridbpmCore hybridbpmServer = new HybridbpmCore();

    public static void main(String[] args) {
        logger.info("HybridbpmServer starting");
        
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() { 
                try {
                    logger.info("HybridbpmServer stopping");
                    hybridbpmServer.stop();
                    logger.info("HybridbpmServer stopped");
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        }));
        try {
            hybridbpmServer.start();
            DeploymentInfo servletBuilder = deployment()
                    .setClassLoader(HybridbpmServer.class.getClassLoader())
                    .setContextPath(PATH)
                    .setDeploymentName("hybridbpm.war")
                    .setDisplayName("HYBRIDBPM")
                    .setResourceManager(new ClassPathResourceManager(HybridbpmServer.class.getClassLoader()))
                    .addServlets(
                            servlet("VaadinServlet", VaadinServlet.class)
                            .setAsyncSupported(true)
                            .setLoadOnStartup(1)
                            .addInitParam("ui", "com.hybridbpm.ui.HybridbpmUI").addInitParam("widgetset", "com.hybridbpm.ui.HybridbpmWidgetSet")
                            .addMapping("/*").addMapping("/VAADIN")
                    );

            DeploymentManager manager = defaultContainer().addDeployment(servletBuilder);
            manager.deploy();

            PathHandler path = Handlers.path(Handlers.redirect(PATH)).addPrefixPath(PATH, manager.start());

            Undertow.Builder builder = Undertow.builder().addHttpListener(8080, "0.0.0.0").setHandler(path);

            undertow = builder.build();
            undertow.start();
            logger.info("HybridbpmServer started");
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

}
