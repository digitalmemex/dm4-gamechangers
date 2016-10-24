package fi.aalto.gamechangers;

import static fi.aalto.gamechangers.DTOHelper.toBrand;
import static fi.aalto.gamechangers.DTOHelper.toComment;
import static fi.aalto.gamechangers.DTOHelper.toCommentOrNull;
import static fi.aalto.gamechangers.DTOHelper.toCommentTopic;
import static fi.aalto.gamechangers.DTOHelper.toEvent;
import static fi.aalto.gamechangers.DTOHelper.toGroup;
import static fi.aalto.gamechangers.DTOHelper.toInstitution;
import static fi.aalto.gamechangers.DTOHelper.toPerson;
import static fi.aalto.gamechangers.DTOHelper.toProposal;
import static fi.aalto.gamechangers.DTOHelper.toProposalTopic;
import static fi.aalto.gamechangers.DTOHelper.toWork;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONException;

import de.deepamehta.accesscontrol.AccessControlService;
import de.deepamehta.contacts.ContactsService;
import de.deepamehta.core.RelatedTopic;
import de.deepamehta.core.Topic;
import de.deepamehta.core.osgi.PluginActivator;
import de.deepamehta.core.service.Inject;
import de.deepamehta.core.service.Transactional;
import de.deepamehta.events.EventsService;
import de.deepamehta.workspaces.WorkspacesService;

@Path("/gamechangers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GamechangersPlugin extends PluginActivator implements GamechangersService {

	public static String NS(String suffix) {
		return "fi.aalto.gamechangers." + suffix;
	}

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
	@Path("/v1/comments_of_item/{id}")
	@Override
	public List<Comment> getCommentsOfItem(@PathParam("id") long topicId) {
		Topic topic = dm4.getTopic(topicId);

		if (topic == null) {
			return null;
		}

		List<Comment> results = new ArrayList<Comment>();
		
		List<RelatedTopic> comments = topic.getRelatedTopics("dm4.core.association", null, null, NS("comment"));
		if (comments == null) {
			return results;
		}
		
		for (RelatedTopic commentTopic : comments) {
			try {
				Comment comment = toCommentOrNull(commentTopic);
				if (comment != null)
					results.add(comment);
			} catch (JSONException jsone) {
				// TODO: Log what object was dropped
			}
		}
		
		return results;
	}

	@GET
	@Path("/v1/comments")
	@Override
	public List<Comment> getComments() {
		List<Comment> results = new ArrayList<Comment>();
		
		for (Topic topic : dm4.getTopicsByType(NS("comment"))) {
			try {
				Comment comment = toCommentOrNull(topic);
				if (comment != null)
					results.add(comment);
			} catch (JSONException jsone) {
				// TODO: Log what object was dropped
			}
		}
		
		return results;
	}
	
	@GET
	@Path("/v1/comment/{id}")
	@Override
	public Comment getComment(@PathParam("id") long topicId) {
		Topic topic = dm4.getTopic(topicId);
		try {
		
			if (topic != null) {
				return toCommentOrNull(topic);
			}
			
		} catch (JSONException jsone) {
			throw new RuntimeException(jsone);
		}

		return null;
	}
	
	@PUT
	@Path("/v1/comment")
	@Transactional
	@Override
	public Comment createComment(CommentBean comment) {
		Topic commentedOnTopic = dm4.getTopic(comment.commentedItemId);
		if (commentedOnTopic == null)
			throw new IllegalArgumentException("commentedItemId is invalid.");

		// TODO: Aggressive validation of the CommentBean instance!
		// Required values, maximum lengths, ...
		
		try {
			
			Topic topic = toCommentTopic(dm4, mf, comment, commentedOnTopic);
			
			// Assigns the new value to the 'data' workspace
			long wsId = wsService.getWorkspace(NS("comments")).getId();
			wsService.assignToWorkspace(topic, wsId);
			
			return toComment(topic);
		} catch (JSONException jsone) {
			throw new RuntimeException(jsone);
		}
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
	@Path("/v1/persons")
	@Override
	public List<Person> getPersons() {
		List<Person> results = new ArrayList<Person>();
		
		for (Topic topic : dm4.getTopicsByType("dm4.contacts.person")) {
			try {
				results.add(toPerson(topic));
			} catch (JSONException jsone) {
				// TODO: Log what object was dropped
			}
		}
		
		return results;
	}
	
	@GET
	@Path("/v1/person/{id}")
	@Override
	public Person getPerson(@PathParam("id") long topicId) {
		Topic topic = dm4.getTopic(topicId);
		try {
		
			if (topic != null) {
				return toPerson(topic);
			}
			
		} catch (JSONException jsone) {
			throw new RuntimeException(jsone);
		}

		return null;
	}

	@GET
	@Path("/v1/proposals")
	@Override
	public List<Proposal> getProposals() {
		List<Proposal> results = new ArrayList<Proposal>();
		
		for (Topic topic : dm4.getTopicsByType(NS("proposal"))) {
			try {
				results.add(toProposal(topic));
			} catch (JSONException jsone) {
				// TODO: Log what object was dropped
			}
		}
		
		return results;
	}
	
	@GET
	@Path("/v1/proposal/{id}")
	@Override
	public Proposal getProposal(@PathParam("id") long topicId) {
		Topic topic = dm4.getTopic(topicId);
		try {
		
			if (topic != null) {
				return toProposal(topic);
			}
			
		} catch (JSONException jsone) {
			throw new RuntimeException(jsone);
		}

		return null;
	}

	@PUT
	@Path("/v1/proposal")
	@Transactional
	@Override
	public Proposal createProposal(ProposalBean proposal) {
		// Required values, maximum lengths, ...
		
		try {
			
			Topic topic = toProposalTopic(dm4, mf, proposal);
			
			// Assigns the new value to the 'data' workspace
			long wsId = wsService.getWorkspace(NS("comments")).getId();
			wsService.assignToWorkspace(topic, wsId);
			
			return toProposal(topic);
		} catch (JSONException jsone) {
			throw new RuntimeException(jsone);
		}
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

}
