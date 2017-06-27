package com.radiosix.upload.embeddedserver;

import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;


/**
 * Created by leo on 05/05/17.
 */
public class COCDJettyServer extends Server {


    public COCDJettyServer() {}

    @Override
    public void setConnectors(Connector[] connectors) {
        disableServerHeaderOnAllHttpConnectors(connectors);
        super.setConnectors(connectors);
    }

    /**
     * Prevents information leakage by disabling the 'Server' header in all HTTP responses
     * By default Jetty adds the build number into an http header.
     * This is undesirable because it is information that could be useful to attackers
     *
     * @param connectors the Connectors being set on this Jetty instance
     */
    private static void disableServerHeaderOnAllHttpConnectors(Connector[] connectors) {
        for(Connector connector : connectors) {
            for(ConnectionFactory connectionFactory  : connector.getConnectionFactories()) {
                if(connectionFactory instanceof HttpConnectionFactory) {
                    ((HttpConnectionFactory)connectionFactory).getHttpConfiguration().setSendServerVersion(false);
                }
            }
        }
    }

}
