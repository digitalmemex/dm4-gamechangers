package fi.aalto.gamechangers.migrations;

import static fi.aalto.gamechangers.GamechangersPlugin.NS;

import de.deepamehta.accesscontrol.AccessControlService;
import de.deepamehta.core.Topic;
import de.deepamehta.core.service.Inject;
import de.deepamehta.core.service.Migration;
import de.deepamehta.core.service.accesscontrol.SharingMode;
import de.deepamehta.workspaces.WorkspacesService;

/**
 */
public class Migration1 extends Migration {

	@Inject
	private WorkspacesService wsService;

	@Inject
	private AccessControlService acService;

	/** Creates workspaces for Gamechangers:
	 * - types: Gamechangers topic types
	 * - data: Persons, groups, institutions, etc
	 * - comments: Comments and proposals
	 */
	@Override
	public void run() {
		Topic typesWs = wsService.createWorkspace("Gamechangers Types", NS("types"),
				SharingMode.PUBLIC);
		acService.setWorkspaceOwner(typesWs, AccessControlService.ADMIN_USERNAME);

		Topic dataWs = wsService.createWorkspace("Gamechangers Data", NS("data"),
				SharingMode.PUBLIC);
		acService.setWorkspaceOwner(dataWs, AccessControlService.ADMIN_USERNAME);

		Topic commentsWs = wsService.createWorkspace("Gamechangers Comments",
				NS("comments"), SharingMode.COMMON);
		acService.setWorkspaceOwner(commentsWs, AccessControlService.ADMIN_USERNAME);
	}
}
