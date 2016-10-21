package fi.aalto.gamechangers;

import static fi.aalto.gamechangers.GamechangersPlugin.NS;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import de.deepamehta.core.ChildTopics;
import de.deepamehta.core.RelatedTopic;
import de.deepamehta.core.Topic;
import fi.aalto.gamechangers.GamechangersService.Brand;
import fi.aalto.gamechangers.GamechangersService.Event;
import fi.aalto.gamechangers.GamechangersService.Group;
import fi.aalto.gamechangers.GamechangersService.Institution;
import fi.aalto.gamechangers.GamechangersService.Work;

public class DTOHelper {

	public static Event toEvent(Topic eventTopic) throws JSONException {
		ChildTopics childs = eventTopic.getChildTopics();
		
		Event dto = new Event();
		dto.put("_type", "event");
		dto.put("id", eventTopic.getId());
		dto.put("title", childs.getStringOrNull("dm4.events.title"));
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
		
		Institution dto = new Institution();
		dto.put("_type", "institution");
		dto.put("id", instTopic.getId());
		dto.put("title", childs.getStringOrNull("dm4.contacts.institution_name"));
		dto.put("type", childs.getStringOrNull(NS("institution.type")));
//		dto.put("address", toAddressOrNull(childs.getTopicOrNull("dm4.contacts.address")));
		dto.put("urls", toStringListOrNull(childs.getTopics("dm4.webbrowser.url")));
		dto.put("notes", childs.getStringOrNull("dm4.contacts.notes"));
		dto.put("from", toJSONDateStringOrNull(childs.getTopicOrNull("dm4.datetime.date#dm4.events.from")));
		dto.put("to", toJSONDateStringOrNull(childs.getTopicOrNull("dm4.datetime.date#dm4.events.to")));
		
		return dto;
	}

	
	public static Group toGroup(Topic groupTopic) throws JSONException {
		ChildTopics childs = groupTopic.getChildTopics();
		
		Group dto = new Group();
		dto.put("_type", "group");
		dto.put("id", groupTopic.getId());
		dto.put("title", childs.getStringOrNull(NS("group.name")));
		dto.put("notes", childs.getStringOrNull("dm4.notes.text"));
		dto.put("from", toJSONDateStringOrNull(childs.getTopicOrNull("dm4.datetime.date#dm4.events.from")));
		dto.put("to", toJSONDateStringOrNull(childs.getTopicOrNull("dm4.datetime.date#dm4.events.to")));

		return dto;
	}
	
	public static Brand toBrand(Topic brandTopic) throws JSONException {
		ChildTopics childs = brandTopic.getChildTopics();
		
		Brand dto = new Brand();
		dto.put("_type", "brand");
		dto.put("id", brandTopic.getId());
		dto.put("title", childs.getStringOrNull(NS("brand.name")));
		dto.put("notes", childs.getStringOrNull("dm4.notes.text"));
		dto.put("from", toJSONDateStringOrNull(childs.getTopicOrNull("dm4.datetime.date#dm4.events.from")));
		dto.put("to", toJSONDateStringOrNull(childs.getTopicOrNull("dm4.datetime.date#dm4.events.to")));

		return dto;
	}
	
	public static Work toWork(Topic workTopic) throws JSONException {
		ChildTopics childs = workTopic.getChildTopics();
		
		Work dto = new Work();
		dto.put("_type", "work");
		dto.put("id", workTopic.getId());
		dto.put("type", childs.getStringOrNull(NS("work.type")));
		dto.put("title", childs.getStringOrNull(NS("work.label")));
		dto.put("notes", childs.getStringOrNull("dm4.notes.text"));
		dto.put("from", toJSONDateStringOrNull(childs.getTopicOrNull("dm4.datetime.date#dm4.events.from")));
		dto.put("to", toJSONDateStringOrNull(childs.getTopicOrNull("dm4.datetime.date#dm4.events.to")));

		return dto;
	}
	
	private static List<String> toStringListOrNull(List<RelatedTopic> topics) {
		if (topics.size() > 0) {
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
				
		return "NOT YET HANDLED: " + datetimeTopic.getTypeUri();
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
}
