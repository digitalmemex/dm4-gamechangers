package fi.aalto.gamechangers;

import static fi.aalto.gamechangers.DTOHelper.toBrand;
import static fi.aalto.gamechangers.DTOHelper.toEvent;
import static fi.aalto.gamechangers.DTOHelper.toGroup;
import static fi.aalto.gamechangers.DTOHelper.toInstitution;
import static fi.aalto.gamechangers.DTOHelper.toWork;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONException;

import de.deepamehta.accesscontrol.AccessControlService;
import de.deepamehta.contacts.ContactsService;
import de.deepamehta.core.Topic;
import de.deepamehta.core.osgi.PluginActivator;
import de.deepamehta.core.service.Inject;
import de.deepamehta.events.EventsService;
import de.deepamehta.workspaces.WorkspacesService;

@Path("/gamechangers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GamechangersPlugin extends PluginActivator implements GamechangersService {

	public static String NS(String suffix) {
		return "fi.aalto.gamechangers." + suffix;
	}

	// ----------------------------------------------------------------------------------------------
	// Instance Variables

	@Inject
	private ContactsService contactsService;

	@Inject
	private EventsService eventsService;

	@Inject
	private WorkspacesService wsService; // needed by migration 1

	@Inject
	private AccessControlService acService; // needed by migration 1

	@GET
	@Path("/v1/brands")
	@Override
	public List<Brand> getBrands() {
		List<Brand> results = new ArrayList<Brand>();
		
		for (Topic topic : dm4.getTopicsByType(NS("brand"))) {
			try {
				results.add(toBrand(topic));
			} catch (JSONException jsone) {
				// TODO: Log what object was dropped
			}
		}
		
		return results;
	}
	
	@GET
	@Path("/v1/brand/{id}")
	@Override
	public Brand getBrand(@PathParam("id") long topicId) {
		Topic topic = dm4.getTopic(topicId);
		try {
		
			if (topic != null) {
				return toBrand(topic);
			}
			
		} catch (JSONException jsone) {
			throw new RuntimeException(jsone);
		}

		return null;
	}

	@GET
	@Path("/v1/events")
	@Override
	public List<Event> getEvents() {
		List<Event> results = new ArrayList<Event>();
		
		for (Topic topic : dm4.getTopicsByType("dm4.events.event")) {
			try {
				results.add(toEvent(topic));
			} catch (JSONException jsone) {
				// TODO: Log what object was dropped
			}
		}
		
		return results;
	}
	
	@GET
	@Path("/v1/event/{id}")
	@Override
	public Event getEvent(@PathParam("id") long topicId) {
		Topic topic = dm4.getTopic(topicId);
		try {
		
			if (topic != null) {
				return toEvent(topic);
			}
			
		} catch (JSONException jsone) {
			throw new RuntimeException(jsone);
		}

		return null;
	}

	@GET
	@Path("/v1/groups")
	@Override
	public List<Group> getGroups() {
		List<Group> results = new ArrayList<Group>();
		
		for (Topic topic : dm4.getTopicsByType(NS("group"))) {
			try {
				results.add(toGroup(topic));
			} catch (JSONException jsone) {
				// TODO: Log what object was dropped
			}
		}
		
		return results;
	}
	
	@GET
	@Path("/v1/group/{id}")
	@Override
	public Group getGroup(@PathParam("id") long topicId) {
		Topic topic = dm4.getTopic(topicId);
		try {
		
			if (topic != null) {
				return toGroup(topic);
			}
			
		} catch (JSONException jsone) {
			throw new RuntimeException(jsone);
		}

		return null;
	}

	@GET
	@Path("/v1/institutions")
	@Override
	public List<Institution> getInstitutions() {
		List<Institution> results = new ArrayList<Institution>();
		
		for (Topic topic : dm4.getTopicsByType("dm4.contacts.institution")) {
			try {
				results.add(toInstitution(topic));
			} catch (JSONException jsone) {
				// TODO: Log what object was dropped
			}
		}
		
		return results;
	}
	
	@GET
	@Path("/v1/institution/{id}")
	@Override
	public Institution getInstitution(@PathParam("id") long topicId) {
		Topic topic = dm4.getTopic(topicId);
		try {
		
			if (topic != null) {
				return toInstitution(topic);
			}
			
		} catch (JSONException jsone) {
			throw new RuntimeException(jsone);
		}

		return null;
	}

	@GET
	@Path("/v1/works")
	@Override
	public List<Work> getWorks() {
		List<Work> results = new ArrayList<Work>();
		
		for (Topic topic : dm4.getTopicsByType(NS("work"))) {
			try {
				results.add(toWork(topic));
			} catch (JSONException jsone) {
				// TODO: Log what object was dropped
			}
		}
		
		return results;
	}
	
	@GET
	@Path("/v1/work/{id}")
	@Override
	public Work getWork(@PathParam("id") long topicId) {
		Topic topic = dm4.getTopic(topicId);
		try {
		
			if (topic != null) {
				return toWork(topic);
			}
			
		} catch (JSONException jsone) {
			throw new RuntimeException(jsone);
		}

		return null;
	}

	@Override
	public List<Person> getPersons() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Comment> getComments() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Proposal> getProposals() {
		// TODO Auto-generated method stub
		return null;
	}

	// --------------------------------------------------------------------------------------------------
	// Public Methods
}
