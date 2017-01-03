package fi.aalto.gamechangers;

import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import de.deepamehta.core.JSONEnabled;

public interface GamechangersService {

	Brand getBrand(long id);

	List<Brand> getBrands();

	Brand getBrand(String languageCode, long id);

	List<Brand> getBrands(String languageCode);
	
	Event getEvent(long id);	

	List<Event> getEvents();
	
	Event getEvent(String languageCode, long id);	

	List<Event> getEvents(String languageCode);

	Group getGroup(long id);
	
	List<Group> getGroups();

	Group getGroup(String languageCode, long id);
	
	List<Group> getGroups(String languageCode);

	Institution getInstitution(long id);
	
	List<Institution> getInstitutions();

	Institution getInstitution(String languageCode, long id);
	
	List<Institution> getInstitutions(String languageCode);

	Work getWork(long id);
		
	List<Work> getWorks();

	Work getWork(String languageCode, long id);

	List<Work> getWorks(String languageCode);
	
	Person getPerson(long id);
	
	List<Person> getPersons();

	Person getPerson(String languageCode, long id);
	
	List<Person> getPersons(String languageCode);
	
	Comment getComment(long id);

	Comment createComment(CommentBean comment);
	
	List<Comment> getComments();

	List<Comment> getCommentsOfItem(long id);
	
	Proposal getProposal(long id);

	Proposal createProposal(ProposalBean proposal);
	
	List<Proposal> getProposals();

	long getFeaturedVideo();
	
	interface Event extends JSONEnabled {}
	
	interface Institution extends JSONEnabled {}

	interface Work extends JSONEnabled {}
	
	interface Brand extends JSONEnabled {}
	
	interface Group extends JSONEnabled {}
	
	interface Person extends JSONEnabled {}
	
	public static class CommentBean {
		String name;
		String email;
		String notes;
		long commentedItemId;
		
		public CommentBean(JSONObject o) throws JSONException {
			name = o.getString("name");
			email = o.getString("email");
			notes = o.getString("notes");
			commentedItemId = o.getLong("commentedItemId");
		}
	}
	
	interface Comment extends JSONEnabled {}
	
	public static class ProposalBean {
		String name;
		String email;
		String notes;
		long from;
		long to;
		
		public ProposalBean(JSONObject o) throws JSONException {
			name = o.getString("name");
			email = o.getString("email");
			notes = o.getString("notes");
			from = o.getLong("from");
			to = o.getLong("to");
		}
	}
	
	interface Proposal extends JSONEnabled {}

}
