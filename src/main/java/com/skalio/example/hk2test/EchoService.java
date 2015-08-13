package com.skalio.example.hk2test;


import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.jvnet.hk2.annotations.Service;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author hank
 */
@Service
public class EchoService {
	
	@Inject
	EntityManager em;
	
	public String doIt(String in) {
		
		if (em == null) {
			in = "EM is null";
		}
		
		return "echo: "+ in;
	}
	
}
