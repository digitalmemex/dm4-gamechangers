package fi.aalto.gamechangers;

import java.util.List;

public interface GamechangersService {

	Event getEvent(long id);	

	List<Event> getEvents();
	
	Group getGroup(long id);
	
	List<Group> getGroups();

	Institution getInstitution(long id);
	
	List<Institution> getInstitutions();

	List<Work> getWorks();
	
	List<Brand> getBrands();
	
	
	List<Person> getPersons();
	
	List<Comment> getComments();
	
	List<Proposal> getProposals();
	
	class Event extends JSONEnabledImpl {}
	
	class Institution extends JSONEnabledImpl {}

	class Work extends JSONEnabledImpl {}
	
	class Brand extends JSONEnabledImpl {}
	
	class Group extends JSONEnabledImpl {}
	
	class Person extends JSONEnabledImpl {}
	
	class Comment extends JSONEnabledImpl {}
	
	class Proposal extends JSONEnabledImpl {}

	
}
