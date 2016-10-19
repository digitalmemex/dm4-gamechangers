package fi.aalto.gamechangers.migrations;

import de.deepamehta.accesscontrol.AccessControlService;
import de.deepamehta.core.DeepaMehtaType;
import de.deepamehta.core.TopicType;
import de.deepamehta.core.service.Inject;
import de.deepamehta.core.service.Migration;
import de.deepamehta.workspaces.WorkspacesService;

/**
 */
public class Migration4 extends Migration {

	@Inject
	private WorkspacesService wsService;

	@Inject
	private AccessControlService acService;

	/** Modifies:
	 * 
	 */
	@Override
	public void run() {
		// Adds institution type (and "from" and "to" date)
		addFromAndToDate("dm4.contacts.institution")
			.addAssocDefBefore(
				mf.newAssociationDefinitionModel("dm4.core.aggregation_def",
					"dm4.contacts.institution", "fi.aalto.gamechangers.institution.type",
					"dm4.core.many", "dm4.core.one"),
				"dm4.contacts.phone_number#dm4.contacts.phone_entry");
        
		// Adds "from" and "to" date
		addFromAndToDate("dm4.events.event");
        
		// Adds date of birth and date of death
		String personTypeUri = "dm4.contacts.person";
		dm4.getTopicType(personTypeUri)
		.addAssocDefBefore(
			mf.newAssociationDefinitionModel("dm4.core.composition_def", "fi.aalto.gamechangers.date_of_death",
				personTypeUri, "dm4.datetime.date", "dm4.core.many", "dm4.core.one"),
			"dm4.contacts.phone_number#dm4.contacts.phone_entry");
	}
	
	private DeepaMehtaType addFromAndToDate(String topicTypeUri) {
		return dm4.getTopicType(topicTypeUri)
			.addAssocDef(
				mf.newAssociationDefinitionModel("dm4.core.composition_def", "dm4.events.from",
					topicTypeUri, "dm4.datetime.date", "dm4.core.many", "dm4.core.one"))
			.addAssocDef(
				mf.newAssociationDefinitionModel("dm4.core.composition_def", "dm4.events.to",
					topicTypeUri, "dm4.datetime.date", "dm4.core.many", "dm4.core.one"));
	}
}
