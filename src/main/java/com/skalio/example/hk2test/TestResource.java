package com.skalio.example.hk2test;


import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author hank
 */
@Path("test")
public class TestResource {
	
	@Inject
	EchoService echoService;
	
	@Inject
	EntityManager em;
	
	@GET
	@Path("service")
	public Response doServiceGet(@QueryParam("in") @DefaultValue("Esel") String in) {
		
		String out = echoService.doIt(in);
		Response r = Response
				.ok()
				.entity(out)
				.build();
		return r;
	}
	
	@GET
	@Path("static")
	public Response doStaticGet(@QueryParam("in") @DefaultValue("Esel") String in) {
		
		String out = in;
		Response r = Response
				.ok()
				.entity(out)
				.build();
		return r;
	}
	
	@GET
	@Path("direct")
	public Response doDirectGet() {
		String out = (em == null ? "EM is NULL" : "EM is NOT NULL");
		Response r = Response
				.ok()
				.entity(out)
				.build();
		return r;
	}
	
}
