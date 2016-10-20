package fi.aalto.gamechangers;

import org.codehaus.jettison.json.JSONObject;

import de.deepamehta.core.JSONEnabled;

public abstract class JSONEnabledImpl extends JSONObject implements JSONEnabled {
	
	public final JSONObject toJSON() {
    	return this;
    }
	
}
