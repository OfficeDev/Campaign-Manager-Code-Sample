package com.microsoft.campaignmanager.datasource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class GraphApplication extends GraphEntity {

	private Map<String, Object> mValues;

	public static List<GraphApplication> listFromJson(JSONObject json) throws JSONException {
		return GraphApplication.listFromJson(json, GraphApplication.class);
	}

	public GraphApplication()  {
		mValues = new HashMap<String, Object>();
	}
	
	public void setData(String key, Object data) {
		mValues.put(key, data);
	}

	Map<String, Object> getValues() {
		return new HashMap<String, Object>(mValues);
	}

	public String getAppId() {
		return getData("appId").toString();
	}

	public void setAppId(String value) {
		setData("appId", value);
	}

	public Boolean getAvailableToOtherTenants() {
		return (Boolean) getData("availableToOtherTenants");
	}

	public String getDisplayName() {
		return getData("displayName").toString();
	}
	
	public void setDisplayName(String value) {
		setData("displayName", value);
	}
	
	public String getErrorUrl() {
		return getData("errorUrl").toString();
	}

	public String getHomePage() {
		return getData("homePage").toString();
	}

	public String getMainLogo() {
		return getData("mainLogo").toString();
	}

	public Boolean getPublicClient() {
		return (Boolean) getData("publicClient");
	}
	
	public void setPublicClient(Boolean value) {
		setData("publicClient", value);
	}

	public String getObjectId() {
		return getData("objectId").toString();
	}

	public String getOdataType() {
		return getData("odata.type").toString();
	}

	public List<GraphEntity> getReplyUrls() {
		JSONObject subItemsJson = (JSONObject) getData("replyUrls");

		try {
			return GraphEntity.listFromJson(subItemsJson, GraphEntity.class);
		} catch (JSONException e) {
			throw new IllegalArgumentException("Cannot get Redirect Urls", e);
		}
	}

	//  Omitting several fields that are not required for this sample

	public List<GraphApplication> getSubItems(String field) {
		JSONObject subItemsJson = (JSONObject) getData(field);

		try {
			return GraphEntity.listFromJson(subItemsJson, GraphApplication.class);
		} catch (JSONException e) {
			throw new IllegalArgumentException("Cannot get sub items from field " + field, e);
		}
	}

	@Override
	public Object getData(String field) {
		if (mValues.containsKey(field)) {
			return mValues.get(field);
		} else {
			return super.getData(field);
		}
	}
}