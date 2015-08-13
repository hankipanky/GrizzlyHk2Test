package com.skalio.example.hk2test;


import java.net.URI;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author hank
 */
public class Main {
	public final static String BASE_URI = "http://localhost:8888/";
	
	public static void main(String[] args) throws Exception {
		
		ServiceLocator locator = ServiceLocatorUtilities.createAndPopulateServiceLocator();
		ServiceLocatorUtilities.bind(locator, new EntityManagerProvider());
		ServiceLocatorUtilities.dumpAllDescriptors(locator);
		
		ResourceConfig config = new ApplicationConfig();
		
		HttpServer server = GrizzlyHttpServerFactory.createHttpServer(
				URI.create(BASE_URI),
				config,
				locator
		);
		
		System.out.println("Server started at "+ BASE_URI);
		System.out.println("Press Enter to stop");
		System.in.read();
		
		server.shutdownNow();
		
	}
}
