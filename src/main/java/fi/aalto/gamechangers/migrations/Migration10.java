package fi.aalto.gamechangers.migrations;

import static fi.aalto.gamechangers.GamechangersPlugin.NS;

import java.util.logging.Logger;

import de.deepamehta.core.AssociationDefinition;
import de.deepamehta.core.TopicType;
import de.deepamehta.core.service.Migration;


/**
 */
public class Migration10 extends Migration {
	
	Logger logger = Logger.getLogger(Migration10.class.getName());

	@Override
	public void run() {
		// Eras should have from and to in their label
		TopicType tt = dm4.getTopicType(NS("era"));
		
		// Fix era.from:
		// Try accessing the expected type and if it does not work, delete the fallback associating and create a new one
		AssociationDefinition at = getAndFix(tt, "dm4.datetime.year#" + NS("era.from"), "dm4.datetime.year#dm4.events.from", NS("era.from"));
		
		// Fix era.to
		at = getAndFix(tt, "dm4.datetime.year#" + NS("era.to"), "dm4.datetime.year#dm4.events.to", NS("era.to"));
		
	}
	
	private AssociationDefinition getAndFix(TopicType tt, String expectedUri, String fallbackUri, String customType) {
		try {
			return tt.getAssocDef(expectedUri);
		} catch (RuntimeException re) {
			logger.warning("Accessing association definition with expected type URI failed: " + expectedUri);
			AssociationDefinition ad = tt.getAssocDef(fallbackUri);
			ad.delete();
			logger.info("Found association definition with type URI: " + fallbackUri);
			tt.addAssocDef(
					mf.newAssociationDefinitionModel("dm4.core.composition_def",
						customType, true,
						NS("era"), "dm4.datetime.year",
						"dm4.core.one", "dm4.core.one"));
			logger.info("Corrected type URI to " + expectedUri);
			
			return ad;
		}
	}
	
}
