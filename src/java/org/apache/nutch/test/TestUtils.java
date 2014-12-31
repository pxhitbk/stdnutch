package org.apache.nutch.test;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.handler.ResourceHandler;

public class TestUtils {
	
	public static void printListString(List<String> list) throws IOException {
		System.err.println("----------------------");
		for(String s: list) {
			System.out.println(s);
		}
	}
	
	public static void printGeneratedSegment(List<URLCrawlDatum> contents) throws IOException {
		System.err.println("Generated segment:");
		for(URLCrawlDatum c: contents) {
			System.out.println(c.toString());
		}
	}
	
	public static void printFetchedData(List<FetchedData> data) throws IOException {
		System.err.println("Fetch data:");
		for(FetchedData d: data) {
			System.out.println(d.toString());
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
