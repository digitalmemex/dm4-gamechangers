package fi.aalto.gamechangers;

import static fi.aalto.gamechangers.GamechangersPlugin.NS;

import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.validator.routines.EmailValidator;

import de.deepamehta.core.Topic;
import fi.aalto.gamechangers.GamechangersService.CommentBean;
import fi.aalto.gamechangers.GamechangersService.ProposalBean;

public class ValidationHelper {
	
	private static EmailValidator emailValidator = EmailValidator.getInstance(false);
	
	private static HashSet<String> allowedTypeUris = new HashSet<String>();

	static {
		allowedTypeUris.add(NS("group"));
		allowedTypeUris.add(NS("brand"));
		allowedTypeUris.add(NS("work"));
		allowedTypeUris.add("dm4.contacts.institution");
		allowedTypeUris.add("dm4.events.event");
		allowedTypeUris.add("dm4.contacts.person");
	}

	private ValidationHelper() {
		// No instance needed.
	}

	public static boolean isValid(ProposalBean proposal) {
		return nonEmptyMax160Chars(proposal.name)
				&& emailValidator.isValid(proposal.email)
				&& nonEmptyMax160Chars(proposal.notes)
				&& (proposal.from > 0)
				&& (proposal.to > 0);
	}
	
	public static boolean isValid(CommentBean comment) {
		return nonEmptyMax160Chars(comment.name)
				&& emailValidator.isValid(comment.email)
				&& nonEmptyMax160Chars(comment.notes)
				&& (comment.commentedItemId > 0);
	}
	
	private static boolean nonEmptyMax160Chars(String s) {
		if (s == null)
			return false;
		
		s = s.trim();
		if (s.length() == 0)
			return false;
		
		if (s.length() > 160)
			return false;
		
		return true;
	}

	public static boolean isValidCommentedOnTopic(Topic topic) {
		if (topic == null)
			return false;
		
		return allowedTypeUris.contains(topic.getTypeUri());
	}
	
	public static Collection<String> getCommentedTopicTypeUris() {
		return allowedTypeUris;
	}
	
}
