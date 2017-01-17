package fi.aalto.gamechangers.migrations;

import static fi.aalto.gamechangers.GamechangersPlugin.NS;

import java.util.Arrays;

import de.deepamehta.core.TopicType;
import de.deepamehta.core.model.TopicTypeModel;
import de.deepamehta.core.service.Migration;


/**
 */
public class Migration11 extends Migration {

	@Override
	public void run() {
		TopicType tt = dm4.getTopicType(NS("comment"));
		TopicTypeModel ttm = (TopicTypeModel) tt.getModel();
		ttm.setLabelConfig(Arrays.asList("dm4.contacts.person_name", "dm4.contacts.email_address"));
		dm4.updateTopicType(ttm);
		
		/* TODO: Some bug prevents changes to the proposals
		tt = dm4.getTopicType(NS("proposal"));
		ttm = (TopicTypeModel) tt.getModel();
		ttm.setLabelConfig(Arrays.asList("dm4.contacts.person_name", "dm4.contacts.email_address"));
		dm4.updateTopicType(ttm);
		*/
	}
	
}
