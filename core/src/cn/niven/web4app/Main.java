package cn.niven.web4app;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

public class Main {

	public static void main(String[] args) throws Exception {
		ServerConfiguration config = new ServerConfiguration();
		config.loadFromFile("config.ini");

		Server server = new Server();
		ServerConnector connector = new ServerConnector(server);
		connector.setPort(config.port);
		connector.setIdleTimeout(config.idletimeout);
		connector.setAcceptQueueSize(config.poolsize);
		server.setConnectors(new Connector[] { connector });

		HandlerList handlers = new HandlerList();
		handlers.addHandler(new ServiceHandler());

		ResourceHandler resHandler = new ResourceHandler();
		resHandler.setDirectoriesListed(true);
		resHandler.setResourceBase("res");
		handlers.addHandler(resHandler);

		server.setHandler(handlers);
		server.start();
		server.join();
		connector.close();
	}
}
