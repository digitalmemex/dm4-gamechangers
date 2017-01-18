package fi.aalto.gamechangers.migrations;

import static fi.aalto.gamechangers.GamechangersPlugin.NS;

import java.util.Arrays;

import de.deepamehta.core.TopicType;
import de.deepamehta.core.service.Migration;


/**
 */
public class Migration11 extends Migration {

	@Override
	public void run() {
/* CAN ONLY BE ACTIVATED WHEN USING DM 4.8.6 !		
		dm4.getTopicType(NS("comment"))
			.setLabelConfig(Arrays.asList("dm4.contacts.person_name", "dm4.contacts.email_address"));
		TopicType tt = dm4.getTopicType(NS("proposal"));
		tt.getAssocDef("dm4.datetime.date#dm4.events.from").getChildTopics().set("dm4.core.include_in_label", false);
		tt.getAssocDef("dm4.datetime.date#dm4.events.to").getChildTopics().set("dm4.core.include_in_label", false);
		
		tt = dm4.getTopicType(NS("proposal"));
		tt.setLabelConfig(Arrays.asList("dm4.contacts.person_name", "dm4.contacts.email_address"));
*/		
	}
	
}
