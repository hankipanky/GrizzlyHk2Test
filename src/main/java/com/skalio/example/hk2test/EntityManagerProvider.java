/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.skalio.example.hk2test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

/**
 *
 * @author hank
 */
public class EntityManagerProvider extends AbstractBinder implements Factory<EntityManager> {
	private final EntityManagerFactory emf;

	public EntityManagerProvider() {
		emf = Persistence.createEntityManagerFactory("derbypu");
	}

	@Override
	protected void configure() {
		bindFactory(this).to(EntityManager.class);
		System.out.println("EntityManager binding done");
	}

	@Override
	public EntityManager provide() {
		EntityManager em = emf.createEntityManager();
		System.out.println("New EntityManager created");
		return em;
	}

	@Override
	public void dispose(EntityManager em) {
		em.close();
	}
}
