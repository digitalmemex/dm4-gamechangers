package fi.aalto.gamechangers;

import java.util.List;

public interface GamechangersService {

	Event getEvent(long topicId);	

	List<Event> getEvents();
	
	List<Work> getWorks();
	
	List<Brand> getBrands();
	
	List<Group> getGroups();
	
	List<Person> getPersons();
	
	List<Comment> getComments();
	
	List<Proposal> getProposals();
	
	class Event extends JSONEnabledImpl {}
	
	class Work extends JSONEnabledImpl {}
	
	class Brand extends JSONEnabledImpl {}
	
	class Group extends JSONEnabledImpl {}
	
	class Person extends JSONEnabledImpl {}
	
	class Comment extends JSONEnabledImpl {}
	
	class Proposal extends JSONEnabledImpl {}

	
}
