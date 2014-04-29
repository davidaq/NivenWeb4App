package cn.niven.web4app;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

import cn.niven.web4app.webpage.WebPageHandler;
import cn.niven.web4app.ws.ServiceHandler;

public class Main {

	public static void main(String[] args) throws Exception {
		ServerConfiguration.config.loadFromFile("config.ini");

		Util.touchURL("http://localhost:"
				+ ServerConfiguration.get("server_listen_port", "8080")
				+ "/shutdown!");

		Server server = new Server();
		ServerConnector connector = new ServerConnector(server);
		connector.setPort(Integer.valueOf(ServerConfiguration.get(
				"server_listen_port", "8080")));
		server.setConnectors(new Connector[] { connector });

		HandlerList handlers = new HandlerList();

		handlers.addHandler(new ServiceHandler());
		handlers.addHandler(new WebPageHandler());

		ResourceHandler resHandler = new ResourceHandler();
		resHandler.setDirectoriesListed(true);
		resHandler.setResourceBase("res");
		handlers.addHandler(resHandler);

		server.setHandler(handlers);
		server.start();
		Components.getComponent("");
		server.join();
		connector.close();
	}
}
