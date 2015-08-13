package com.skalio.example.hk2test;


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
public class ApplicationConfig extends ResourceConfig {
	public ApplicationConfig() {
		
		// register for @Inject EntityManager
		// register(new EntityManagerProvider());
		
		// where to search for JAX-RS annotated classes
		packages(this.getClass().getPackage().getName());

		// ServiceLocator locator = ServiceLocatorUtilities.createAndPopulateServiceLocator();

	}
}
