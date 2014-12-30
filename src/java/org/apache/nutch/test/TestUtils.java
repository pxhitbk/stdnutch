package org.apache.nutch.test;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.handler.ResourceHandler;

public class TestUtils {
	
	public static void printCrawlDb(List<String> crawlUrls) throws IOException {
		for(String s: crawlUrls) {
			System.out.println(s);
		}
	}
	
	public static void printGeneratedSegment(List<URLCrawlDatum> contents) throws IOException {
		for(URLCrawlDatum c: contents) {
			System.out.println(c.toString());
		}
	}
	
	  /**
	   * Creates a new JettyServer with one static root context
	   * 
	   * @param port port to listen to
	   * @param staticContent folder where static content lives
	   * @throws UnknownHostException 
	   */
	  public static Server getServer(int port, String staticContent) throws UnknownHostException{
	    Server webServer = new org.mortbay.jetty.Server();
	    SocketConnector listener = new SocketConnector();
	    listener.setPort(port);
	    listener.setHost("127.0.0.1");
	    webServer.addConnector(listener);
	    ContextHandler staticContext = new ContextHandler();
	    staticContext.setContextPath("/");
	    staticContext.setResourceBase(staticContent);
	    staticContext.addHandler(new ResourceHandler());
	    webServer.addHandler(staticContext);
	    return webServer;
	  }
}
