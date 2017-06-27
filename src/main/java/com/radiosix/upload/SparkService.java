package com.radiosix.upload;

import freemarker.template.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Service;
import spark.embeddedserver.EmbeddedServers;
import spark.embeddedserver.jetty.EmbeddedJettyFactory;
import spark.template.freemarker.FreeMarkerEngine;
import com.radiosix.upload.embeddedserver.COCDJettyServer;
import com.radiosix.upload.routes.CommonRoutes;

public class SparkService {

    private static final Logger LOG = LoggerFactory.getLogger(SparkService.class);
    public static Service service;

    public static void main(String... args) {

        EmbeddedServers.add(EmbeddedServers.Identifiers.JETTY, new EmbeddedJettyFactory((i, j, k) -> {
            // use a custom Jetty server implementation so we can customise Jetty behaviours
            return new COCDJettyServer();
        }));
        service = Service.ignite();
		
		// Get port config of heroku on environment variable
        ProcessBuilder process = new ProcessBuilder();
        Integer port;
        if (process.environment().get("PORT") != null) {
            port = Integer.parseInt(process.environment().get("PORT"));
        } else {
            port = 8080;
        }
        service.port(port);


        FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine();
        Configuration configuration = new Configuration();
        configuration.setClassForTemplateLoading(FreeMarkerEngine.class, "");
        freeMarkerEngine.setConfiguration(configuration);

        service.init();

        CommonRoutes.setUpRoutes(freeMarkerEngine);
    }
}
