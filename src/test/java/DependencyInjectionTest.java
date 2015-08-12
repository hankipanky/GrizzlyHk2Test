

import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import junit.framework.Assert;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author hank
 */
public class DependencyInjectionTest {
	private final static String BASE_URI = "http://localhost:8888/";
	private final static String OK = "OK";
	private HttpServer server;
	private ExecutorService backgroundService;
	
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

	public class BackgroundTask implements Callable<String> {
		@Inject
		EntityManager em;

		@Override
		public String call() throws Exception {
			System.out.println("Background task started");
			Assert.assertNotNull(em);	// will throw exception
			
			System.out.println("EntityManager is not null");
			return OK;
		}
	}
	
	@Path("/test")
	public class JerseyResource {
		@Inject
		EntityManager em;
		
		@GET
		@Produces(MediaType.TEXT_PLAIN)
		public Response doGet() {
			System.out.println("GET request received");
			Assert.assertNotNull(em);
			
			System.out.println("EntityManager is not null");
			return Response.ok()
					.entity(OK)
					.build();
		}
	}
	
	@Before
	public void setUp() {
		System.out.println("Setting up");
		ResourceConfig config = new ResourceConfig();
		config.register(new EntityManagerProvider());
		config.registerInstances(new JerseyResource());	// can't find a better way to register the resource
		
		server = GrizzlyHttpServerFactory.createHttpServer(
				URI.create(BASE_URI),
				config
		);
		
		backgroundService = Executors.newSingleThreadScheduledExecutor();
	}
	
	@After
	public void tearDown() {
		System.out.println("Shutting down");
		server.shutdownNow();
		backgroundService.shutdownNow();
	}

	@Test
	public void testScheduledBackgroundTask() throws Exception {
		Assert.assertTrue(server.isStarted());
		
		Future<String> f = backgroundService.submit(new BackgroundTask());
		System.out.println("Background task submitted");

		try {
			Assert.assertEquals(OK, f.get());	// forces Exception
		} catch (ExecutionException | InterruptedException ex) {
			System.out.println("Caught exception "+ ex.getMessage());
			ex.printStackTrace();
			
			Assert.fail();
		}
	}
	
	@Test
	public void testBackgroundTask() throws Exception {
		Assert.assertTrue(server.isStarted());
		
		BackgroundTask task = new BackgroundTask();
		System.out.println("Background task instantiated");

		Assert.assertEquals(OK, task.call());
	}
	
	@Test
	public void testResource() {
		Assert.assertTrue(server.isStarted());
		
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(BASE_URI);
		
		Response r = target.path("test")
				.request()
				.get();
		Assert.assertEquals(200, r.getStatus());
		Assert.assertEquals(OK, r.readEntity(String.class));
	}
}
