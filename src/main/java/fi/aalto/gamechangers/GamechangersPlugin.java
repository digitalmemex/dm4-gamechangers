package fi.aalto.gamechangers;

import static fi.aalto.gamechangers.DTOHelper.toBrand;
import static fi.aalto.gamechangers.DTOHelper.toComment;
import static fi.aalto.gamechangers.DTOHelper.toCommentOrNull;
import static fi.aalto.gamechangers.DTOHelper.toCommentTopic;
import static fi.aalto.gamechangers.DTOHelper.toEventOrNull;
import static fi.aalto.gamechangers.DTOHelper.toGroup;
import static fi.aalto.gamechangers.DTOHelper.toInstitution;
import static fi.aalto.gamechangers.DTOHelper.toPersonOrNull;
import static fi.aalto.gamechangers.DTOHelper.toProposal;
import static fi.aalto.gamechangers.DTOHelper.toProposalTopic;
import static fi.aalto.gamechangers.DTOHelper.toWork;
import static fi.aalto.gamechangers.GamechangersPlugin.NS;

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
import de.deepamehta.core.model.AssociationModel;
import de.deepamehta.core.osgi.PluginActivator;
import de.deepamehta.core.service.Inject;
import de.deepamehta.core.service.Transactional;
import de.deepamehta.core.service.event.PreCreateAssociationListener;
import de.deepamehta.core.util.DeepaMehtaUtils;
import de.deepamehta.events.EventsService;
import de.deepamehta.time.TimeService;
import de.deepamehta.workspaces.WorkspacesService;

@Path("/gamechangers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GamechangersPlugin extends PluginActivator implements GamechangersService, PreCreateAssociationListener {

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
	
	@Inject
	private TimeService timeService;
	
	@Override
	public void init() {
		DTOHelper.wsService = wsService;
		DTOHelper.timeService = timeService;
		DTOHelper.dm4 = dm4;
	}
	
	@GET
	@Path("/v1/featured_video")
	@Override
	public long getFeaturedVideo() {
		return DTOHelper.toFeaturedVideoId(dm4.getTopicByUri(NS("featured_video")));
	}
	
	@GET
	@Path("/v1/brands")
	@Override
	public List<Brand> getBrands() {
		return getBrands(null);
	}
	
	@GET
	@Path("/v2/{languageCode}/brands")
	@Override
	public List<Brand> getBrands(@PathParam("languageCode") String languageCode) {
		List<Brand> results = new ArrayList<Brand>();
		
		for (Topic topic : dm4.getTopicsByType(NS("brand"))) {
			try {
				results.add(toBrand(languageCode, topic));
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
		return getBrand(null, topicId);
	}
	
	@GET
	@Path("/v2/{languageCode}/brand/{id}")
	@Override
	public Brand getBrand(@PathParam("languageCode") String languageCode, @PathParam("id") long topicId) {
		Topic topic = dm4.getTopic(topicId);
		try {
		
			if (topic != null) {
				return toBrand(languageCode, topic);
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
		if (ValidationHelper.isValid(comment)
				&& ValidationHelper.isValidCommentedOnTopic(commentedOnTopic = dm4.getTopic(comment.commentedItemId))) {
			
			try {
				
				Topic topic = toCommentTopic(dm4, mf, comment, commentedOnTopic);
				
				// Assigns the new value to the 'data' workspace
				long wsId = wsService.getWorkspace(NS("workspace.comments")).getId();
				wsService.assignToWorkspace(topic, wsId);
				
				return toComment(topic);
			} catch (JSONException jsone) {
				throw new RuntimeException(jsone);
			}
		}
		
		throw new RuntimeException("Validation failed.");
	}

	@GET
	@Path("/v1/events")
	@Override
	public List<Event> getEvents() {
		return getEvents(null);
	}
	
	@GET
	@Path("/v2/{languageCode}/events")
	@Override
	public List<Event> getEvents(@PathParam("languageCode") String languageCode) {
		List<Event> results = new ArrayList<Event>();
		
		for (Topic topic : dm4.getTopicsByType("dm4.events.event")) {
			try {
				Event event = toEventOrNull(languageCode, topic);
				if(event != null) {
					results.add(event);
				}
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
		return getEvent(null, topicId);
	}
	
	@GET
	@Path("/v2/{languageCode}/event/{id}")
	@Override
	public Event getEvent(@PathParam("languageCode") String languageCode, @PathParam("id") long topicId) {
		Topic topic = dm4.getTopic(topicId);
		try {
		
			if (topic != null) {
				return toEventOrNull(languageCode, topic);
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
		return getGroups(null);
	}
	
	@GET
	@Path("/v2/{languageCode}/groups")
	@Override
	public List<Group> getGroups(@PathParam("languageCode") String languageCode) {
		List<Group> results = new ArrayList<Group>();
		
		for (Topic topic : dm4.getTopicsByType(NS("group"))) {
			try {
				results.add(toGroup(languageCode, topic));
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
		return getGroup(null, topicId);
	}
	
	@GET
	@Path("/v2/{languageCode}/group/{id}")
	@Override
	public Group getGroup(@PathParam("languageCode") String languageCode, @PathParam("id") long topicId) {
		Topic topic = dm4.getTopic(topicId);
		try {
		
			if (topic != null) {
				return toGroup(languageCode, topic);
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
		return getPersons(null);
	}
	
	@GET
	@Path("/v2/{languageCode}/persons")
	@Override
	public List<Person> getPersons(@PathParam("languageCode") String languageCode) {
		List<Person> results = new ArrayList<Person>();
		
		for (Topic topic : dm4.getTopicsByType("dm4.contacts.person")) {
			try {
				Person person = toPersonOrNull(languageCode, topic);
				if(person != null) {
					results.add(person);
				}
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
		return getPerson(null, topicId);
	}
	
	@GET
	@Path("/v2/{languageCode}/person/{id}")
	@Override
	public Person getPerson(@PathParam("languageCode") String languageCode, @PathParam("id") long topicId) {
		Topic topic = dm4.getTopic(topicId);
		try {
		
			if (topic != null) {
				return toPersonOrNull(languageCode, topic);
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
		if (ValidationHelper.isValid(proposal)) {
			
			try {
				
				Topic topic = toProposalTopic(dm4, mf, proposal);
				
				// Assigns the new value to the 'data' workspace
				long wsId = wsService.getWorkspace(NS("workspace.comments")).getId();
				wsService.assignToWorkspace(topic, wsId);
				
				return toProposal(topic);
			} catch (JSONException jsone) {
				throw new RuntimeException(jsone);
			}
			
		}
		
		throw new RuntimeException("Validation failed.");
	}

	@GET
	@Path("/v1/institutions")
	@Override
	public List<Institution> getInstitutions() {
		return getInstitutions(null);
	}
	
	@GET
	@Path("/v2/{languageCode}/institutions")
	@Override
	public List<Institution> getInstitutions(@PathParam("languageCode") String languageCode) {
		List<Institution> results = new ArrayList<Institution>();
		
		for (Topic topic : dm4.getTopicsByType("dm4.contacts.institution")) {
			try {
				results.add(toInstitution(languageCode, topic));
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
		return getInstitution(null, topicId);
	}
	
	@GET
	@Path("/v2/{languageCode}/institution/{id}")
	@Override
	public Institution getInstitution(@PathParam("languageCode") String languageCode, @PathParam("id") long topicId) {
		Topic topic = dm4.getTopic(topicId);
		try {
		
			if (topic != null) {
				return toInstitution(languageCode, topic);
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
		return getWorks(null);
	}
	
	@GET
	@Path("/v2/{languageCode}/works")
	@Override
	public List<Work> getWorks(@PathParam("languageCode") String languageCode) {
		List<Work> results = new ArrayList<Work>();
		
		for (Topic topic : dm4.getTopicsByType(NS("work"))) {
			try {
				results.add(toWork(languageCode, topic));
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
		return getWork(null, topicId);
	}

	@GET
	@Path("/v2/{languageCode}/work/{id}")
	@Override
	public Work getWork(@PathParam("languageCode") String languageCode, @PathParam("id") long topicId) {
		Topic topic = dm4.getTopic(topicId);
		try {
		
			if (topic != null) {
				return toWork(languageCode, topic);
			}
			
		} catch (JSONException jsone) {
			throw new RuntimeException(jsone);
		}

		return null;
	}

	@GET
	@Path("/v2/{languageCode}/eras")
	@Override
	public List<Era> getEras(@PathParam("languageCode") String languageCode) {
		try {
			return DTOHelper.toEraList(languageCode, dm4.getTopicsByType(NS("era")));
		} catch (JSONException jsone) {
			// TODO: Log what object was dropped
		}
		return null;
	}
	
    @Override
    public void preCreateAssociation(AssociationModel assoc) {
    	// Translation autotypings:
    	autotypeTranslations(
    			assoc,
    			"dm4.events.title",
    			"dm4.contacts.institution_name",
    			"dm4.contacts.person_name",
    			NS("group.name"),
    			NS("brand.name"),
    			NS("work.label"),
    			NS("era.name")
		);
    }
    
    private void autotypeTranslations(AssociationModel assoc, String... typeUris) {
    	for (String typeUri : typeUris) {
	        DeepaMehtaUtils.associationAutoTyping(assoc, typeUri, NS("translatedtext"),
	                NS("translation"), "dm4.core.default", "dm4.core.default", dm4);
    	}
    	
    }

}
