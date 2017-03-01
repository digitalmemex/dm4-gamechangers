package fi.aalto.gamechangers.migrations;

import static fi.aalto.gamechangers.GamechangersPlugin.NS;

import de.deepamehta.core.Topic;
import de.deepamehta.core.service.Inject;
import de.deepamehta.core.service.Migration;
import de.deepamehta.workspaces.WorkspacesService;

/**
 */
public class Migration8 extends Migration {

	@Inject
	private WorkspacesService wsService;

	/**
	 * Modifies:
	 * 
	 */
	@Override
	public void run() {
		// Workspace associations
		long dataWsId = wsService.getWorkspace(NS("workspace.types")).getId();

		groupAssignToWorkspace(dataWsId,
				NS("era.from"),
				NS("era.to"),
				NS("era.name"),
				NS("era")
		);

	}

	private void groupAssignToWorkspace(long wsId, String... topicTypeUris) {
		for (String uri : topicTypeUris) {
			Topic topic = dm4.getTopicByUri(uri);
			wsService.assignToWorkspace(topic, wsId);
		}
	}

}
