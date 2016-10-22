package fi.aalto.gamechangers;

import static fi.aalto.gamechangers.GamechangersPlugin.NS;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import de.deepamehta.core.ChildTopics;
import de.deepamehta.core.RelatedTopic;
import de.deepamehta.core.Topic;
import fi.aalto.gamechangers.GamechangersService.Brand;
import fi.aalto.gamechangers.GamechangersService.Comment;
import fi.aalto.gamechangers.GamechangersService.Event;
import fi.aalto.gamechangers.GamechangersService.Group;
import fi.aalto.gamechangers.GamechangersService.Institution;
import fi.aalto.gamechangers.GamechangersService.Person;
import fi.aalto.gamechangers.GamechangersService.Proposal;
import fi.aalto.gamechangers.GamechangersService.Work;

public class DTOHelper {
	
	static final DateFormat JSON_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm a z");
	
	public static Event toEvent(Topic eventTopic) throws JSONException {
		ChildTopics childs = eventTopic.getChildTopics();
		
		EventImpl dto = new EventImpl();
		dto.put("_type", "event");
		dto.put("id", eventTopic.getId());
		dto.put("name", childs.getStringOrNull("dm4.events.title"));
		dto.put("type", childs.getStringOrNull(NS("event.type")));
		dto.put("address", toAddressOrNull(childs.getTopicOrNull("dm4.contacts.address")));
		dto.put("from", toJSONDateStringOrNull(childs.getTopicOrNull("dm4.datetime#dm4.events.from")));
		dto.put("to", toJSONDateStringOrNull(childs.getTopicOrNull("dm4.datetime#dm4.events.to")));
		dto.put("notes", childs.getStringOrNull("dm4.events.notes"));
		dto.put("url", childs.getStringOrNull("dm4.webbrowser.url"));

		return dto;
	}
	
	public static Institution toInstitution(Topic instTopic) throws JSONException {
		ChildTopics childs = instTopic.getChildTopics();
		
		InstitutionImpl dto = new InstitutionImpl();
		dto.put("_type", "institution");
		dto.put("id", instTopic.getId());
		dto.put("name", childs.getStringOrNull("dm4.contacts.institution_name"));
		dto.put("type", childs.getStringOrNull(NS("institution.type")));
//		dto.put("address", toAddressOrNull(childs.getTopicOrNull("dm4.contacts.address")));
		dto.put("urls", toStringListOrNull(childs.getTopicsOrNull("dm4.webbrowser.url")));
		dto.put("notes", childs.getStringOrNull("dm4.contacts.notes"));
		dto.put("from", toJSONDateStringOrNull(childs.getTopicOrNull("dm4.datetime.date#dm4.events.from")));
		dto.put("to", toJSONDateStringOrNull(childs.getTopicOrNull("dm4.datetime.date#dm4.events.to")));
		
		return dto;
	}

	
	public static Group toGroup(Topic groupTopic) throws JSONException {
		ChildTopics childs = groupTopic.getChildTopics();
		
		GroupImpl dto = new GroupImpl();
		dto.put("_type", "group");
		dto.put("id", groupTopic.getId());
		dto.put("name", childs.getStringOrNull(NS("group.name")));
		dto.put("notes", childs.getStringOrNull("dm4.notes.text"));
		dto.put("from", toJSONDateStringOrNull(childs.getTopicOrNull("dm4.datetime.date#dm4.events.from")));
		dto.put("to", toJSONDateStringOrNull(childs.getTopicOrNull("dm4.datetime.date#dm4.events.to")));

		return dto;
	}
	
	public static Brand toBrand(Topic brandTopic) throws JSONException {
		ChildTopics childs = brandTopic.getChildTopics();
		
		BrandImpl dto = new BrandImpl();
		dto.put("_type", "brand");
		dto.put("id", brandTopic.getId());
		dto.put("name", childs.getStringOrNull(NS("brand.name")));
		dto.put("notes", childs.getStringOrNull("dm4.notes.text"));
		dto.put("from", toJSONDateStringOrNull(childs.getTopicOrNull("dm4.datetime.date#dm4.events.from")));
		dto.put("to", toJSONDateStringOrNull(childs.getTopicOrNull("dm4.datetime.date#dm4.events.to")));

		return dto;
	}

	public static Comment toCommentOrNull(Topic commentTopic) throws JSONException {
		ChildTopics childs = commentTopic.getChildTopics();

		if (childs.getBooleanOrNull(NS("comment.public"))) {
			CommentImpl dto = new CommentImpl();
			dto.put("_type", "comment");
			dto.put("id", commentTopic.getId());
			dto.put("name", childs.getStringOrNull("dm4.contacts.person_name"));
			dto.put("email", childs.getStringOrNull("dm4.contacts.email_address"));
			dto.put("notes", childs.getStringOrNull("dm4.notes.text"));
	
			return dto;
		} else {
			return null;
		}
	}
	
	public static Person toPerson(Topic personTopic) throws JSONException {
		ChildTopics childs = personTopic.getChildTopics();
		
		PersonImpl dto = new PersonImpl();
		dto.put("_type", "person");
		dto.put("id", personTopic.getId());
		dto.put("name", childs.getStringOrNull("dm4.contacts.person_name"));
		dto.put("notes", childs.getStringOrNull("dm4.contacts.notes"));
		dto.put("emails", toStringListOrNull(childs.getTopicsOrNull("dm4.contacts.email_address")));
		dto.put("urls", toStringListOrNull(childs.getTopicsOrNull("dm4.webbrowser.url")));
		dto.put("from", toJSONDateStringOrNull(childs.getTopicOrNull("dm4.datetime.date#dm4.contacts.date_of_birth")));
		dto.put("to", toJSONDateStringOrNull(childs.getTopicOrNull("dm4.datetime.date#" + NS("date_of_death"))));

		return dto;
	}
	
	public static Proposal toProposal(Topic proposalTopic) throws JSONException {
		ChildTopics childs = proposalTopic.getChildTopics();
		
		ProposalImpl dto = new ProposalImpl();
		dto.put("_type", "proposal");
		dto.put("id", proposalTopic.getId());
		dto.put("name", childs.getStringOrNull("dm4.contacts.person_name"));
		dto.put("email", childs.getStringOrNull("dm4.contacts.email_address"));
		dto.put("notes", childs.getStringOrNull("dm4.notes.text"));
		dto.put("from", toJSONDateStringOrNull(childs.getTopicOrNull("dm4.datetime.date#dm4.events.from")));
		dto.put("to", toJSONDateStringOrNull(childs.getTopicOrNull("dm4.datetime.date#dm4.events.to")));

		return dto;
	}
	
	public static Work toWork(Topic workTopic) throws JSONException {
		ChildTopics childs = workTopic.getChildTopics();
		
		WorkImpl dto = new WorkImpl();
		dto.put("_type", "work");
		dto.put("id", workTopic.getId());
		dto.put("type", childs.getStringOrNull(NS("work.type")));
		dto.put("name", childs.getStringOrNull(NS("work.label")));
		dto.put("notes", childs.getStringOrNull("dm4.notes.text"));
		dto.put("from", toJSONDateStringOrNull(childs.getTopicOrNull("dm4.datetime.date#dm4.events.from")));
		dto.put("to", toJSONDateStringOrNull(childs.getTopicOrNull("dm4.datetime.date#dm4.events.to")));

		return dto;
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
	
	private static String toJSONDateStringOrNull(Topic datetimeTopic) throws JSONException {
		if (datetimeTopic == null) {
			return null;
		}
		ChildTopics childs = datetimeTopic.getChildTopics();

		Topic dateTopic;
		Topic timeTopic;
		
		int year = -1, month = 1, day = 1;
		int hour = 0, minute = 0;

		if ("dm4.datetime".equals(datetimeTopic.getTypeUri())) {
			dateTopic = childs.getTopicOrNull("dm4.datetime.date");
			timeTopic = childs.getTopicOrNull("dm4.datetime.time");
		} else {
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
					
			return JSON_DATE_FORMAT.format(cal.getTime());
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
