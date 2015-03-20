package nl.tudelft.ewi.sorcerers.resources;

import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import nl.tudelft.ewi.sorcerers.core.FooManager;
import nl.tudelft.ewi.sorcerers.github.LineMapService;
import nl.tudelft.ewi.sorcerers.model.Warning;
import nl.tudelft.ewi.sorcerers.usecases.GetWarningsForCommit;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

@Path("/foo")
public class FooResource {
	private final FooManager manager;
	private EntityManager em;
	private GetWarningsForCommit gwfc;
	private LineMapService lms;
	
	@Inject
	public FooResource(FooManager manager, EntityManager em, GetWarningsForCommit gwfc, LineMapService lms) {
		this.manager = manager;
		this.em = em;
		this.gwfc = gwfc;
		this.lms = lms;
	}

	@SuppressWarnings("unchecked")
	@GET
	@Produces("application/json")
	@Transactional
	public List<Warning> retrieve() {
		Warning w = new Warning("rmhartog/bugfree-octo-wookie", "ca41fa4d04525071e4522084b2744c508f1f68bd", "README.md", 1, "test message");
		em.persist(w);
		Warning w2 = new Warning("rmhartog/bugfree-octo-wookie", "4b1fc0be3a772e0736dd0b485e7b6b8bd50f13d8", "README.md", 3, "test message");
		em.persist(w2);
		return (List<Warning>) em.createQuery("SELECT w FROM Warning w").getResultList();
	}
	
	@GET
	@Path("{repo: ([-a-zA-Z_0-9]+\\/[-a-zA-Z_0-9]+)}/{commit}")
	@Produces("application/json")
	@Transactional
	public List<Warning> retrieve(@PathParam("repo") String repo, @PathParam("commit") String commit) {
		return gwfc.execute(repo, commit);
	}
	
	@GET
	@Path("{repo: ([-a-zA-Z_0-9]+\\/[-a-zA-Z_0-9]+)}/{base}/{head}")
	@Produces("application/json")
	@Transactional
	public List<Warning> retrieve(@PathParam("repo") String repo, @PathParam("base") String base, @PathParam("head") String head) {
		lms.createLineMap(repo, base, head);
		return null;
	}

	@PUT
	@Consumes("application/json")
	public void update(String foo) {
		manager.setFoo(foo);
	}
}