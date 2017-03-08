package fi.aalto.gamechangers.migrations;

import static fi.aalto.gamechangers.GamechangersPlugin.NS;

import java.util.Arrays;

import de.deepamehta.core.TopicType;
import de.deepamehta.core.service.Migration;


/**
 */
public class Migration9 extends Migration {

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
		tt.getAssocDef("dm4.datetime.year#dm4.events.from").getChildTopics()
			.set("dm4.core.include_in_label", true);
		tt.getAssocDef("dm4.datetime.year#dm4.events.to").getChildTopics()
			.set("dm4.core.include_in_label", true);
		
	}
	
}
