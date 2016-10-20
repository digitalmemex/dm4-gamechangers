package fi.aalto.gamechangers;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.deepamehta.accesscontrol.AccessControlService;
import de.deepamehta.contacts.ContactsService;
import de.deepamehta.core.model.AssociationModel;
import de.deepamehta.core.osgi.PluginActivator;
import de.deepamehta.core.service.Inject;
import de.deepamehta.core.service.event.PreCreateAssociationListener;
import de.deepamehta.events.EventsService;
import de.deepamehta.workspaces.WorkspacesService;

@Path("/gamechangers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GamechangersPlugin extends PluginActivator implements GamechangersService, PreCreateAssociationListener {

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
	public void preCreateAssociation(AssociationModel model) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Event> getEvents() {
		// TODO Auto-generated method stub
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
