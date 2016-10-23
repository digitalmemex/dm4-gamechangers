package fi.aalto.gamechangers;

import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import de.deepamehta.core.JSONEnabled;

public interface GamechangersService {

	Brand getBrand(long id);

	List<Brand> getBrands();

	Event getEvent(long id);	

	List<Event> getEvents();
	
	Group getGroup(long id);
	
	List<Group> getGroups();

	Institution getInstitution(long id);
	
	List<Institution> getInstitutions();

	Work getWork(long id);
	
	List<Work> getWorks();
	
	Person getPerson(long id);
	
	List<Person> getPersons();
	
	Comment getComment(long id);

	Comment createComment(CommentBean comment);
	
	List<Comment> getComments();

	List<Comment> getCommentsOfItem(long id);
	
	Proposal getProposal(long id);
	
	List<Proposal> getProposals();
	
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
		
		public CommentBean(JSONObject o) throws JSONException {
			name = o.getString("name");
			email = o.getString("email");
			notes = o.getString("notes");
		}
	}
	
	interface Comment extends JSONEnabled {}
	
	interface Proposal extends JSONEnabled {}

	
}
