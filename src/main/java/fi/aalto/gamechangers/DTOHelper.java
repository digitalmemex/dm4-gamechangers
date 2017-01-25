package fi.aalto.gamechangers;

import static fi.aalto.gamechangers.GamechangersPlugin.NS;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import de.deepamehta.core.Association;
import de.deepamehta.core.ChildTopics;
import de.deepamehta.core.DeepaMehtaObject;
import de.deepamehta.core.JSONEnabled;
import de.deepamehta.core.RelatedTopic;
import de.deepamehta.core.Topic;
import de.deepamehta.core.model.ChildTopicsModel;
import de.deepamehta.core.model.SimpleValue;
import de.deepamehta.core.model.TopicModel;
import de.deepamehta.core.service.CoreService;
import de.deepamehta.core.service.ModelFactory;
import de.deepamehta.time.TimeService;
import de.deepamehta.workspaces.WorkspacesService;
import fi.aalto.gamechangers.GamechangersService.Brand;
import fi.aalto.gamechangers.GamechangersService.Comment;
import fi.aalto.gamechangers.GamechangersService.CommentBean;
import fi.aalto.gamechangers.GamechangersService.Era;
import fi.aalto.gamechangers.GamechangersService.Event;
import fi.aalto.gamechangers.GamechangersService.Group;
import fi.aalto.gamechangers.GamechangersService.Institution;
import fi.aalto.gamechangers.GamechangersService.Person;
import fi.aalto.gamechangers.GamechangersService.Proposal;
import fi.aalto.gamechangers.GamechangersService.ProposalBean;
import fi.aalto.gamechangers.GamechangersService.Work;

public class DTOHelper {

	static WorkspacesService wsService;

	static TimeService timeService;
	
	static CoreService dm4;

	private static <T> T selfOrDefault(T instance, T defaultValue) {
		return (instance != null) ? instance : defaultValue;
	}

	public static Event toEventOrNull(String languageCode, Topic eventTopic) throws JSONException {
		return toEventOrNull(languageCode, eventTopic, new HashSet<Long>());
	}
	
	private static EventImpl toEventOrNull(String languageCode, Topic eventTopic, Set<Long> alreadyVisitedTopics) throws JSONException {
		ChildTopics childs = eventTopic.getChildTopics();

		String name = getTranslatedStringOrNull(childs, languageCode, "dm4.events.title");
		if (!selfOrDefault(childs.getBooleanOrNull(NS("event.hidden")), false)
				&& name != null) {
			String html = childs.getStringOrNull("dm4.events.notes");
			EventImpl dto = new EventImpl();
			dto.put("_type", "event");
			dto.put("id", eventTopic.getId());
			dto.put("name", name);
			dto.put("type", childs.getStringOrNull(NS("event.type")));
			dto.put("address", toAddressOrNull(childs.getTopicOrNull("dm4.contacts.address")));
			dto.put("from", toMillisSinceEpochOrNull(childs.getTopicOrNull("dm4.datetime#dm4.events.from")));
			dto.put("to", toMillisSinceEpochOrNull(childs.getTopicOrNull("dm4.datetime#dm4.events.to")));
			dto.put("notes", stripHtml(languageCode, html));
			dto.put("url", childs.getStringOrNull("dm4.webbrowser.url"));
			dto.put("images", toImageList(html));
			dto.put("latestPublicComments", toLatestPublicComments(eventTopic));
			dto.put("associatedItems", toAssociatedItems(languageCode, eventTopic, alreadyVisitedTopics));

			dto.put("vimeoVideoId", toVimeoVideoId(eventTopic));

			return dto;
		} else {
			return null;
		}
	}

	public static Institution toInstitution(String languageCode, Topic instTopic) throws JSONException {
		ChildTopics childs = instTopic.getChildTopics();
		String html = childs.getStringOrNull("dm4.contacts.notes");
		InstitutionImpl dto = new InstitutionImpl();
		dto.put("_type", "institution");
		dto.put("id", instTopic.getId());
		dto.put("name", getTranslatedStringOrNull(childs, languageCode, "dm4.contacts.institution_name"));
		dto.put("type", childs.getStringOrNull(NS("institution.type")));
//		dto.put("address", toAddressOrNull(childs.getTopicOrNull("dm4.contacts.address")));
		dto.put("urls", toStringListOrNull(childs.getTopicsOrNull("dm4.webbrowser.url")));
		dto.put("notes", stripHtml(languageCode, html));
		dto.put("from", toMillisSinceEpochOrNull(childs.getTopicOrNull("dm4.datetime.date#dm4.events.from")));
		dto.put("to", toMillisSinceEpochOrNull(childs.getTopicOrNull("dm4.datetime.date#dm4.events.to")));
		dto.put("images", toImageList(html));

		dto.put("vimeoVideoId", toVimeoVideoId(instTopic));

		return dto;
	}


	public static Group toGroup(String languageCode, Topic groupTopic) throws JSONException {
		ChildTopics childs = groupTopic.getChildTopics();

		String html = childs.getStringOrNull("dm4.notes.text");
		GroupImpl dto = new GroupImpl();
		dto.put("_type", "group");
		dto.put("id", groupTopic.getId());
		dto.put("name", getTranslatedStringOrNull(childs, languageCode, NS("group.name")));
		dto.put("notes", stripHtml(languageCode, html));
		dto.put("from", toMillisSinceEpochOrNull(childs.getTopicOrNull("dm4.datetime.date#dm4.events.from")));
		dto.put("to", toMillisSinceEpochOrNull(childs.getTopicOrNull("dm4.datetime.date#dm4.events.to")));
		dto.put("images", toImageList(html));

		dto.put("vimeoVideoId", toVimeoVideoId(groupTopic));

		return dto;
	}

	public static Brand toBrand(String languageCode, Topic brandTopic) throws JSONException {
		ChildTopics childs = brandTopic.getChildTopics();

		String html = childs.getStringOrNull("dm4.notes.text");
		BrandImpl dto = new BrandImpl();
		dto.put("_type", "brand");
		dto.put("id", brandTopic.getId());
		dto.put("name", getTranslatedStringOrNull(childs, languageCode, NS("brand.name")));
		dto.put("notes", stripHtml(languageCode, html));
		dto.put("from", toMillisSinceEpochOrNull(childs.getTopicOrNull("dm4.datetime.date#dm4.events.from")));
		dto.put("to", toMillisSinceEpochOrNull(childs.getTopicOrNull("dm4.datetime.date#dm4.events.to")));
		dto.put("images", toImageList(html));

		dto.put("vimeoVideoId", toVimeoVideoId(brandTopic));

		return dto;
	}

	public static Comment toCommentOrNull(Topic commentTopic) throws JSONException {
		return toCommentImpl(commentTopic, false);
	}

	public static Comment toComment(Topic commentTopic) throws JSONException {
		return toCommentImpl(commentTopic, true);
	}

	private static CommentImpl toCommentImpl(Topic commentTopic, boolean alwaysCreate) throws JSONException {
		ChildTopics childs = commentTopic.getChildTopics();

		Topic commentedTopic = getCommentedTopicOrNull(commentTopic);

		if (commentedTopic != null && (alwaysCreate || selfOrDefault(childs.getBooleanOrNull(NS("comment.public")), false))) {
			String html = childs.getStringOrNull("dm4.notes.text");
			CommentImpl dto = new CommentImpl();
			dto.put("_type", "comment");
			dto.put("id", commentTopic.getId());
			dto.put("name", childs.getStringOrNull("dm4.contacts.person_name"));
			dto.put("notes", stripHtmlSimple(html));
			dto.put("commentedItemId", commentedTopic.getId());
			addCreationTimestamp(dto, commentTopic);
			dto.put("images", toImageList(html));

			return dto;
		} else {
			return null;
		}
	}

	/**
	 * Strips the text out of h1, h2, h3 and p text and concatenates that as a text.
	 *
	 * @param html
	 * @return
	 */
	private static String stripHtmlSimple(String html) {
		if (html == null)
			return null;

		StringBuilder sb = new StringBuilder();
        Document doc = Jsoup.parse(html);
        
        for(Element elem : doc.select("h1, h2, h3, p")) {
        	if (elem.hasText()) {
        		// If there is a text already, prepend a whitespace.
        		if (sb.length() > 0) {
        			sb.append(" ");
        		}
    			sb.append(elem.text());
        	}
        }

		return sb.toString();
	}
	/**
	 * Strips the text out of h1, h2, h3 and p text and concatenates that as a text.
	 *
	 * @param html
	 * @return
	 */
	private static String stripHtml(String languageCode, String html) {
		if (html == null)
			return null;

		StringBuilder sb = new StringBuilder();
        Document doc = Jsoup.parse(html);
        
        boolean ourLanguageTagSeen = false;
        String ourLanguageTag = "//"+ languageCode;

        for(Element elem : doc.select("h1, h2, h3, p")) {
        	if (elem.hasText()) {
        		String text = elem.text().trim().replaceAll("\u00a0","");
        		if (!ourLanguageTagSeen) {
        			ourLanguageTagSeen = text.equals(ourLanguageTag);
        			continue;
        		} else {
        			// If we see our language tag again, just ignore it.
        			if (text.equals(ourLanguageTag)) {
        				continue;
        			}
        			
        			// If we see any other language tag, abort finding text.
        			if (text.matches("//[a-z][a-z]")) {
        				break;
        			}
        			
        			// Handle text that applies to our language tag
        			
            		// Only append if there is actual content.
            		if (text.length() > 0) {
            			
                		// If there is a text already, prepend a whitespace.
                		if (sb.length() > 0) {
                			sb.append(" ");
                		}
                		
            			sb.append(text);
            		}
        		}
        	}
        }

		return sb.toString();
	}
	
	static Long toFeaturedVideoId(Topic featuredVideoTopic) {
		if (featuredVideoTopic == null) {
			return null;
		} else {
			String url = featuredVideoTopic.getChildTopics().getStringOrNull("dm4.webbrowser.url");

			return parseVimeoVideoId(url);
		}
	}

	/**
	 * Finds a web_resource topic associated with the given event, parses the web resource's URL and
	 * return the id part of a vimeo URL, e.g. "https://vimeo.com/90647039" is turned into 90647039.
	 *
	 * @param eventTopic
	 * @return
	 * @throws JSONException
	 */
	private static Long toVimeoVideoId(Topic eventTopic) throws JSONException {
		RelatedTopic webResourceTopic = eventTopic.getRelatedTopic(
				"dm4.core.association", "dm4.core.default", "dm4.core.default", "dm4.webbrowser.web_resource");

		if (webResourceTopic == null) {
			return null;
		}

		String url = webResourceTopic.getChildTopics().getStringOrNull("dm4.webbrowser.url");

		return parseVimeoVideoId(url);
	}
	
	private static Long parseVimeoVideoId(String urlString) {
		Long result;
		
		if (urlString == null) {
			return null;
		}

		URL videoUrl;
		try {
			videoUrl = new URL(urlString);
			result = Long.parseLong(videoUrl.getPath().substring(1));
		} catch (MalformedURLException e) {
			return null;
		} catch (NumberFormatException e) {
			return null;
		} catch (StringIndexOutOfBoundsException e) {
			return null;
		}
		
		return result;
	}

	private static List<CommentImpl> toLatestPublicComments(Topic eventTopic) throws JSONException {
		ArrayList<CommentImpl> result = new ArrayList<CommentImpl>();

		for (RelatedTopic topic : getDefaultRelatedTopics(eventTopic, NS("comment"))) {
			CommentImpl dto = toCommentImpl(topic, false);
			if (dto != null) {
				result.add(dto);
			}
		}

		// Sorts in a way that the most recently modified comments shows up first
		result.sort(new Comparator<CommentImpl>() {

			@Override
			public int compare(CommentImpl _this, CommentImpl _that) {
				try {
					long thisModTime = timeService.getModificationTime(_this.getLong("id"));
					long thatModTime = timeService.getModificationTime(_that.getLong("id"));

					if (thisModTime < thatModTime) {
						return 1;
					} else if (thisModTime > thatModTime) {
						return -1;
					} else {
						return 0;
					}
				} catch (JSONException e) {
					// Will not happen as all objects have the 'id' set.
					throw new RuntimeException("Hell frozen.");
				}

			}

		});

		// Allow only 10 comments to be left.
		int size;
		while ( (size = result.size()) > 10) {
			result.remove(size - 1);
		}

		return result;
	}

	private static List<JSONEnabled> toAssociatedItems(String languageCode, Topic origin, Set<Long> alreadyVisitedTopics) throws JSONException {
		ArrayList<JSONEnabled> result = new ArrayList<JSONEnabled>();

		result.addAll(toGroups(languageCode, getDefaultRelatedTopics(origin, NS("group"))));
		result.addAll(toBrands(languageCode, getDefaultRelatedTopics(origin, NS("brand"))));
		result.addAll(toWorks(languageCode, getDefaultRelatedTopics(origin, NS("work"))));
		result.addAll(toInstitutions(languageCode, getDefaultRelatedTopics(origin, "dm4.contacts.institution")));
		result.addAll(toPersons(languageCode, getDefaultRelatedTopics(origin, "dm4.contacts.person")));

		// We don't filter out the origin topic here (even though we could) because we specifically want
		// it to show up again in the associated items.
		//alreadyVisitedTopics.add(origin.getId());
		result.addAll(toEvents(languageCode, getDefaultRelatedTopics(origin, "dm4.events.event", alreadyVisitedTopics), alreadyVisitedTopics));

		return result;
	}

	private static List<Group> toGroups(String languageCode, List<RelatedTopic> topics) throws JSONException {
		ArrayList<Group> result = new ArrayList<Group>();

		for (RelatedTopic topic : topics) {
			Group dto = toGroup(languageCode, topic);
			if (dto != null) {
				result.add(dto);
			}
		}

		return result;
	}

	private static List<Brand> toBrands(String languageCode, List<RelatedTopic> topics) throws JSONException {
		ArrayList<Brand> result = new ArrayList<Brand>();

		for (RelatedTopic topic : topics) {
			Brand dto = toBrand(languageCode, topic);
			if (dto != null) {
				result.add(dto);
			}
		}

		return result;
	}

	private static List<Institution> toInstitutions(String languageCode, List<RelatedTopic> topics) throws JSONException {
		ArrayList<Institution> result = new ArrayList<Institution>();

		for (RelatedTopic topic : topics) {
			Institution dto = toInstitution(languageCode, topic);
			if (dto != null) {
				result.add(dto);
			}
		}

		return result;
	}

	private static List<Person> toPersons(String languageCode, List<RelatedTopic> topics) throws JSONException {
		ArrayList<Person> result = new ArrayList<Person>();

		for (RelatedTopic topic : topics) {
			Person dto = toPersonOrNull(languageCode, topic);
			if (dto != null) {
				result.add(dto);
			}
		}

		return result;
	}

	private static List<Work> toWorks(String languageCode, List<RelatedTopic> topics) throws JSONException {
		ArrayList<Work> result = new ArrayList<Work>();

		for (RelatedTopic topic : topics) {
			Work dto = toWork(languageCode, topic);
			if (dto != null) {
				result.add(dto);
			}
		}

		return result;
	}

	private static List<EventImpl> toEvents(String languageCode, List<? extends Topic> topics, Set<Long> alreadyVisitedTopics) throws JSONException {
		ArrayList<EventImpl> result = new ArrayList<EventImpl>();
		
		for (Topic topic : topics) {
			// When called without the visited set given, then create a distinct set for every event. This
			// means that each event becomes an origin.
			Set<Long> visited = (alreadyVisitedTopics == null ? new HashSet<Long>() : alreadyVisitedTopics);
			
			EventImpl dto = toEventOrNull(languageCode, topic, visited);
			if (dto != null) {
				result.add(dto);
			}
		}

		return result;
	}

	private static List<RelatedTopic> getDefaultRelatedTopics(Topic origin, String typeUri) {
		return origin.getRelatedTopics("dm4.core.association", "dm4.core.default", "dm4.core.default", typeUri);
	}

	private static List<RelatedTopic> getDefaultRelatedTopics(Topic origin, String typeUri, Set<Long> alreadyVisitedTopics) {
		List<RelatedTopic> result = getDefaultRelatedTopics(origin, typeUri);
		
		// Remove all topics we've already seen
		for (long topicId : alreadyVisitedTopics) {
			for (int i = 0; i < result.size(); i++) {
				if (topicId == result.get(i).getId()) {
					result.remove(i);
					break;
				}
			}
		}
		
		// Mark all the topics just selected as seen.
		for (Topic topic : result) {
			alreadyVisitedTopics.add(topic.getId());
		}
		
		return result;
	}

	/** Returns a list of comment Ids for a given topic.
	 *
	 * <p>The list can be empty.</p>
	 *
	 * @param commentedTopic
	 * @return
	 */
	private static List<Long> getCommentIdsForTopic(Topic commentedTopic) {
		List<Long> commentIds = new ArrayList<Long>();

		for (String typeUri : ValidationHelper.getCommentedTopicTypeUris()) {
			List<RelatedTopic> topics = commentedTopic.getRelatedTopics(
					"dm4.core.association", "dm4.core.default", "dm4.core.default", NS("comment"));
			for (RelatedTopic topic : topics) {
				commentIds.add(topic.getId());
			}
		}

		return commentIds;
	}

	/**
	 * Finds the first topic that is associated with the given comment topic out of the list of commentable types and returns it.
	 *
	 * <p>If no such object can be found the result is null.</p>
	 *
	 * @param commentTopic
	 * @return
	 */
	private static Topic getCommentedTopicOrNull(Topic commentTopic) {
		// TODO: Properly return commentFor!
		for (String typeUri : ValidationHelper.getCommentedTopicTypeUris()) {
			List<RelatedTopic> topics = commentTopic.getRelatedTopics("dm4.core.association", "dm4.core.default", "dm4.core.default", typeUri);
			if (!topics.isEmpty()) {
				return topics.get(0);
			}
		}

		return null;

	}

	public static Topic toCommentTopic(CoreService dm4, ModelFactory mf, CommentBean comment, Topic topicCommentOn) throws JSONException {
		// TODO: Check input

		ChildTopicsModel childs = mf.newChildTopicsModel();
		childs.put("dm4.contacts.person_name", toPersonNameTopicModel(dm4, mf, comment.name));
		childs.put("dm4.contacts.email_address", comment.email);
		childs.put("dm4.notes.text", comment.notes);

		Topic topic = dm4.createTopic(mf.newTopicModel(NS("comment"), childs));

		// Sets the relation to the item that is being commented on
		Association assoc = dm4.createAssociation(mf.newAssociationModel("dm4.core.association",
    			mf.newTopicRoleModel(topicCommentOn.getId(), "dm4.core.default"),
			mf.newTopicRoleModel(topic.getId(), "dm4.core.default")));

		assignToCommentsWorkspace(assoc);

		return topic;
	}

	private static TopicModel toPersonNameTopicModel(CoreService dm4, ModelFactory mf, String nameString) {
		nameString = nameString.trim();

		String firstName;
		String lastName;
		int spaceIndex = -1;

		// If a space is available, it'll be in the middle (because of trim())
		if ((spaceIndex = nameString.indexOf(' ')) > -1) {
			firstName = nameString.substring(0, spaceIndex);
			lastName = nameString.substring(spaceIndex + 1);
		} else {
			// Just put everything in the first name
			firstName = nameString;
			lastName = "";
		}

		ChildTopicsModel childs = mf.newChildTopicsModel();

		childs.put("dm4.contacts.first_name", firstName);
		childs.put("dm4.contacts.last_name", lastName);

		return mf.newTopicModel("dm4.contacts.person_name", childs);
	}

	public static Person toPersonOrNull(String languageCode, Topic personTopic) throws JSONException {
		ChildTopics childs = personTopic.getChildTopics();

		String name = getTranslatedStringOrNull(childs, languageCode, "dm4.contacts.person_name");
		String html = childs.getStringOrNull("dm4.contacts.notes");
		PersonImpl dto = null;

		if (name != null || html != null) {
			dto = new PersonImpl();
			dto.put("_type", "person");
			dto.put("id", personTopic.getId());
			dto.put("name", name);
			dto.put("notes", stripHtml(languageCode, html));
	//		dto.put("urls", toStringListOrNull(childs.getTopicsOrNull("dm4.webbrowser.url")));
			dto.put("birth", toMillisSinceEpochOrNull(childs.getTopicOrNull("dm4.datetime.date#dm4.contacts.date_of_birth")));
			dto.put("death", toMillisSinceEpochOrNull(childs.getTopicOrNull("dm4.datetime.date#" + NS("date_of_death"))));
			dto.put("images", toImageList(html));

			dto.put("vimeoVideoId", toVimeoVideoId(personTopic));

		}

		return dto;
	}

	public static Topic toProposalTopic(CoreService dm4, ModelFactory mf, ProposalBean proposal) throws JSONException {
		// TODO: Check input

		ChildTopicsModel childs = mf.newChildTopicsModel();
		childs.put("dm4.contacts.person_name", toPersonNameTopicModel(dm4, mf, proposal.name));
		childs.put("dm4.contacts.email_address", proposal.email);
		childs.put("dm4.notes.text", proposal.notes);
		childs.putRef("dm4.datetime.date#dm4.events.from", toDateTopicModel(dm4, mf, proposal.from).getId());
		childs.putRef("dm4.datetime.date#dm4.events.to", toDateTopicModel(dm4, mf, proposal.to).getId());

		return dm4.createTopic(mf.newTopicModel(NS("proposal"), childs));
	}

	public static Proposal toProposal(Topic proposalTopic) throws JSONException {
		ChildTopics childs = proposalTopic.getChildTopics();
		String html;

		ProposalImpl dto = new ProposalImpl();
		dto.put("_type", "proposal");
		dto.put("id", proposalTopic.getId());
		dto.put("name", childs.getStringOrNull("dm4.contacts.person_name"));
		dto.put("notes", html = childs.getStringOrNull("dm4.notes.text"));
		dto.put("from", toMillisSinceEpochOrNull(childs.getTopicOrNull("dm4.datetime.date#dm4.events.from")));
		dto.put("to", toMillisSinceEpochOrNull(childs.getTopicOrNull("dm4.datetime.date#dm4.events.to")));
		dto.put("images", toImageList(html));

		return dto;
	}

	public static Work toWork(String languageCode, Topic workTopic) throws JSONException {
		ChildTopics childs = workTopic.getChildTopics();

		String html;
		WorkImpl dto = new WorkImpl();
		dto.put("_type", "work");
		dto.put("id", workTopic.getId());
		dto.put("type", childs.getStringOrNull(NS("work.type")));
		dto.put("name", getTranslatedStringOrNull(childs, languageCode, NS("work.label")));
		dto.put("notes", html = childs.getStringOrNull("dm4.notes.text"));
		dto.put("from", toMillisSinceEpochOrNull(childs.getTopicOrNull("dm4.datetime.date#dm4.events.from")));
		dto.put("to", toMillisSinceEpochOrNull(childs.getTopicOrNull("dm4.datetime.date#dm4.events.to")));
		dto.put("images", toImageList(html));
		dto.put("vimeoVideoId", toVimeoVideoId(workTopic));

		return dto;
	}
	
	public static List<Era> toEraList(String languageCode, List<Topic> eraTopics) throws JSONException {
		ArrayList<Era> result = new ArrayList<Era>();
		
		List<EventImpl> allEvents = toEvents(languageCode, dm4.getTopicsByType("dm4.events.event"), null);
		
		for (Topic eraTopic : eraTopics) {
			ChildTopics childs = eraTopic.getChildTopics();
			
			String html;
			
			Integer fromYear = childs.getIntOrNull("dm4.datetime.year#" + NS("era.from"));
			Integer toYear = childs.getIntOrNull("dm4.datetime.year#" + NS("era.to"));
			
			if (fromYear != null && toYear != null) {
				EraImpl dto = new EraImpl();
				dto.put("_type", "era");
				dto.put("id", eraTopic.getId());
				dto.put("name", getTranslatedStringOrNull(childs, languageCode, NS("era.name")));
				dto.put("notes", html = childs.getStringOrNull("dm4.notes.text"));
				dto.put("from", fromYear);
				dto.put("to", toYear);
				dto.put("events", selectEvents(allEvents, fromYear, toYear));
				dto.put("images", toImageList(html));
			
				result.add(dto);
			} else {
				// Era was skipped.
			}
		}
		
		return result;
	}
	
	private static List<EventImpl> selectEvents(List<EventImpl> allEvents, int fromYear, int toYear) {
		Calendar cal = Calendar.getInstance();
		
		ArrayList<EventImpl> result = new ArrayList<EventImpl>();
		
		for (EventImpl event : allEvents) {
			try {
				cal.setTimeInMillis(event.getLong("from"));
				int eventYear = cal.get(Calendar.YEAR);
				if (eventYear >= fromYear && eventYear <= toYear) {
					result.add(event);
				}
			} catch (JSONException jsone) {
				// Skips the event
			}
		}
		
		return result;
	}

	private static List<String> toStringListOrNull(List<RelatedTopic> topics) {
		if (topics != null && topics.size() > 0) {
			List<String> list = new ArrayList<String>();
			for (Topic t : topics) {
				list.add(t.getSimpleValue().toString());
			}

			return list;
		} else {
			return null;
		}
	}

	public static TopicModel toDateTopicModel(CoreService dm4, ModelFactory mf, long millis) throws JSONException {
		Calendar cal = Calendar.getInstance();

		cal.setTimeInMillis(millis);

		ChildTopicsModel childs = mf.newChildTopicsModel();

		putRefOrCreate(dm4, mf, childs, "dm4.datetime.year", cal.get(Calendar.YEAR));
		putRefOrCreate(dm4, mf, childs, "dm4.datetime.month", cal.get(Calendar.MONTH));
		putRefOrCreate(dm4, mf, childs, "dm4.datetime.day", cal.get(Calendar.DAY_OF_MONTH));

		Topic t = dm4.createTopic(mf.newTopicModel("dm4.datetime.date", childs));

		assignToCommentsWorkspace(t);

		return t.getModel();
	}

	private static void putRefOrCreate(CoreService dm4, ModelFactory mf, ChildTopicsModel childs, String typeUri, Object value) {
		SimpleValue sv = new SimpleValue(value);
		List<Topic> results = dm4.getTopicsByType(typeUri);
		for (Topic t : results) {
			if (t.getSimpleValue().equals(sv)) {
				childs.putRef(typeUri, t.getId());

				return;
			}
		}

		TopicModel tm = mf.newTopicModel(typeUri);
		tm.setSimpleValue(sv);
		Topic t = dm4.createTopic(tm);

		assignToCommentsWorkspace(t);

		childs.putRef(typeUri, t.getId());
	}

	private static void assignToCommentsWorkspace(DeepaMehtaObject obj) {
		// Assigns the new value to the 'data' workspace
		long wsId = wsService.getWorkspace(NS("workspace.comments")).getId();
		wsService.assignToWorkspace(obj, wsId);
	}

	private static Long toMillisSinceEpochOrNull(Topic datetimeTopic) throws JSONException {
		if (datetimeTopic == null) {
			return null;
		}
		ChildTopics childs = datetimeTopic.getChildTopics();

		Topic dateTopic;
		Topic timeTopic;

		int year = -1, month = 1, day = 1;
		int hour = 0, minute = 0;

		// dateTimetopic is either dm4.datetime or dm4.datetime.date
		if ("dm4.datetime".equals(datetimeTopic.getTypeUri())) {
			// Retrieves the children
			dateTopic = childs.getTopicOrNull("dm4.datetime.date");
			timeTopic = childs.getTopicOrNull("dm4.datetime.time");
		} else {
			// Initializes dateTopic from datetimeTopic
			dateTopic = datetimeTopic;
			timeTopic = null;
		}

		if (dateTopic != null && (childs = dateTopic.getChildTopics()) != null) {
			year = getInt(childs, "dm4.datetime.year", -1);
			month = getInt(childs, "dm4.datetime.month", 1);
			day = getInt(childs, "dm4.datetime.day", 1);
		}

		if (timeTopic != null && (childs = timeTopic.getChildTopics()) != null) {
			hour = getInt(childs, "dm4.datetime.hour", 0);
			minute = getInt(childs, "dm4.datetime.minute", 0);
		}

		// At least the year needs to have been specified
		if (year != -1) {
			Calendar cal = Calendar.getInstance();

			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, month);
			cal.set(Calendar.DAY_OF_MONTH, day);

			cal.set(Calendar.HOUR, hour);
			cal.set(Calendar.MINUTE, minute);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);

			return cal.getTimeInMillis();
		} else {
			return null;
		}
	}

	private static int getInt(ChildTopics childs, String assocDefUri, int defaultValue) {
		Integer value = childs.getIntOrNull(assocDefUri);

		return (value != null) ? value.intValue() : defaultValue;
	}

	private static JSONObject toAddressOrNull(Topic addressTopic) throws JSONException {
		if (addressTopic == null) {
			return null;
		}

		ChildTopics childs = addressTopic.getChildTopics();

		JSONObject addr = new JSONObject();
		addr.put("street", childs.getStringOrNull("dm4.contacts.street"));
		addr.put("postal_code", childs.getStringOrNull("dm4.contacts.postal_code"));
		addr.put("city", childs.getStringOrNull("dm4.contacts.city"));
		addr.put("country", childs.getStringOrNull("dm4.contacts.country"));

		return addr;
	}

	private static List<JSONObject> toImageList(String html) throws JSONException {
		ArrayList<JSONObject> result = new ArrayList<JSONObject>();
		if (html != null) {
	        Document doc = Jsoup.parse(html);
	        
	        for(Element img : doc.select("p > img[src]")) {
		        JSONObject json = new JSONObject();
	        	json.put("url", img.attr("abs:src"));
	    	        result.add(json);
	        }
	        
	        for(Element figure : doc.select("figure")) {
	        	Element img = figure.getElementsByTag("img").first();
	        	Element figcaption = figure.getElementsByTag("figcaption").first();
	        	
	        	if (img != null && figcaption != null) {
	    	        JSONObject json = new JSONObject();
	        		json.put("url", img.attr("src"));
	        		json.put("caption", figcaption.text());
		    	        result.add(json);
	        	}
	        	
	        }
	        
		}
        return result;
    }
	
	private static String getTranslatedStringOrNull(ChildTopics childs, String languageCode, String typeUri) {
		if (languageCode == null || languageCode.equals("en")) {
			return childs.getStringOrNull(typeUri);
		}
		
		Topic topic = childs.getTopicOrNull(typeUri);
		if (topic == null) {
			return null;
		}
		
		// Try to look up translation
		List<RelatedTopic> translatedTexts = topic.getRelatedTopics(NS("translation"), "dm4.core.default", "dm4.core.default", NS("translatedtext"));
		for (RelatedTopic possibleTranslationTopic : translatedTexts) {
			Association association = possibleTranslationTopic.getRelatingAssociation();
			if (languageCode.equals(association.getSimpleValue().toString())) {
				return possibleTranslationTopic.getSimpleValue().toString();
			}
		}
		
		// Deliver default
		return topic.getSimpleValue().toString();
	}

	private static void addCreationTimestamp(JSONEnabledImpl dto, Topic topic) throws JSONException {
		dto.put("created", timeService.getCreationTime(topic.getId()));
	}

	private static class EraImpl extends JSONEnabledImpl implements Era {
	}
	
	private static class EventImpl extends JSONEnabledImpl implements Event {
	}

	private static class InstitutionImpl extends JSONEnabledImpl implements Institution {
	}

	private static class WorkImpl extends JSONEnabledImpl implements Work {
	}

	private static class BrandImpl extends JSONEnabledImpl implements Brand {
	}

	private static class GroupImpl extends JSONEnabledImpl implements Group {
	}

	private static class PersonImpl extends JSONEnabledImpl implements Person {
	}

	private static class CommentImpl extends JSONEnabledImpl implements Comment {
	}

	private static class ProposalImpl extends JSONEnabledImpl implements Proposal {
	}

}
