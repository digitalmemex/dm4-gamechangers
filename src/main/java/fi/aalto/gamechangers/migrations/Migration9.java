package fi.aalto.gamechangers.migrations;

import static fi.aalto.gamechangers.GamechangersPlugin.NS;

import java.util.logging.Logger;

import de.deepamehta.core.AssociationDefinition;
import de.deepamehta.core.TopicType;
import de.deepamehta.core.service.Migration;


/**
 */
public class Migration9 extends Migration {
	
	Logger logger = Logger.getLogger(Migration9.class.getName());

	@Override
	public void run() {
		// Comments should have the name and the email address in the label
		TopicType tt = dm4.getTopicType(NS("comment"));
		tt.getAssocDef("dm4.contacts.person_name").getChildTopics()
			.set("dm4.core.include_in_label", true);

		tt.getAssocDef("dm4.contacts.email_address").getChildTopics()
			.set("dm4.core.include_in_label", true);

		// Proposals should have the name and the email address in the label
		tt = dm4.getTopicType(NS("proposal"));
		tt.getAssocDef("dm4.contacts.person_name").getChildTopics()
			.set("dm4.core.include_in_label", true);
		tt.getAssocDef("dm4.contacts.email_address").getChildTopics()
			.set("dm4.core.include_in_label", true);
		
		// Eras should have from and to in their label
		tt = dm4.getTopicType(NS("era"));
		tt.getAssocDef(NS("era.name")).getChildTopics()
			.set("dm4.core.include_in_label", true);
		
		// Fix era.from
		AssociationDefinition at = getAndFix(tt, "dm4.datetime.year#" + NS("era.from"), "dm4.datetime.year#dm4.events.from");
		at.getChildTopics()
			.set("dm4.core.include_in_label", true);
		
		// Fix era.to
		at = getAndFix(tt, "dm4.datetime.year#" + NS("era.to"), "dm4.datetime.year#dm4.events.to");
		at.getChildTopics()
			.set("dm4.core.include_in_label", true);
		
	}
	
	private AssociationDefinition getAndFix(TopicType tt, String expectedUri, String fallbackUri) {
		try {
			return tt.getAssocDef(expectedUri);
		} catch (RuntimeException re) {
			logger.warning("Accessing association definition with expected type URI failed: " + expectedUri);
			AssociationDefinition ad = tt.getAssocDef(fallbackUri);
			logger.info("Found association definition with type URI: " + fallbackUri);
			ad.setTypeUri(expectedUri);
			logger.info("Corrected type URI to " + expectedUri);
			
			return ad;
		}
	}
	
}
