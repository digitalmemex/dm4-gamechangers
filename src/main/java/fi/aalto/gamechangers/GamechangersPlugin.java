package fi.aalto.gamechangers;

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
import de.deepamehta.core.ChildTopics;
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

	@Override
	public List<Event> getEvents() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@GET
	@Path("/v1/event/{id}")
	@Override
	public Event getEvent(@PathParam("id") long topicId) {
		Topic topic = dm4.getTopic(topicId);
		try {
		
			if (topic != null) {
				ChildTopics childs = topic.getChildTopics();
				
				// Fix
				Event event = new Event();
				event.put("_type", "event");
				event.put("id", topicId);
				event.put("name", childs.getStringOrNull("dm4.events.title"));
				event.put("typeOfEvent", childs.getStringOrNull(NS("event.type")));
				event.put("from", "dm4.datetime#dm4.events.from");
				event.put("to", "dm4.datetime#dm4.events.to");
				return event;
			}
			
		} catch (JSONException jsone) {
			throw new RuntimeException(jsone);
		}

		return null;
	}

	@Override
	public List<Work> getWorks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Brand> getBrands() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Group> getGroups() {
		// TODO Auto-generated method stub
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
