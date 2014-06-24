package com.microsoft.campaignmanager.datasource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.microsoft.office365.OfficeEntity;

// TODO: Auto-generated Javadoc
/**
 * The Class GraphServicePrincipal.
 */
public class GraphServicePrincipal extends GraphEntity {

	/** The m values. */
	private Map<String, Object> mValues;

	/**
	 * List from json.
	 * 
	 * @param json
	 *            the json
	 * @return the list
	 * @throws JSONException
	 *             the JSON exception
	 */
	public static List<GraphServicePrincipal> listFromJson(JSONObject json) throws JSONException {
		return GraphServicePrincipal.listFromJson(json, GraphServicePrincipal.class);
	}

	/**
	 * Instantiates a new Graph ServicePrincipal object.
	 */
	public GraphServicePrincipal()  {
		mValues = new HashMap<String, Object>();
	}
	
	/**
	 * Adds data to the item.
	 * 
	 * @param key
	 *            the key
	 * @param data
	 *            the data
	 */
	public void setData(String key, Object data) {
		mValues.put(key, data);
	}

	/**
	 * Gets the values.
	 * 
	 * @return the values
	 */
	Map<String, Object> getValues() {
		return new HashMap<String, Object>(mValues);
	}

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public String getAppId() {
		return getData("appId").toString();
	}

	public void setAppId(String value) {
		setData("appId", value);
	}

	public Boolean getAppOwnerTenantId() {
		return (Boolean) getData("appOwnerTenantId");
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

	public String getObjectId() {
		return getData("objectId").toString();
	}

	public String getOdataType() {
		return getData("odata.type").toString();
	}
	
	public String getPublisherName() {
		return getData("publisherName").toString();
	}
	
	public void setPublisherName(String value) {
		setData("publisherName", value);
	}

	public List<GraphEntity> getServicePrincipalNames() {
		JSONObject subItemsJson = (JSONObject) getData("servicePrincipalNames");

		try {
			return GraphEntity.listFromJson(subItemsJson, GraphEntity.class);
		} catch (JSONException e) {
			throw new IllegalArgumentException("Cannot get Service Principal names", e);
		}
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

	/**
	 * Gets the sub items.
	 * 
	 * @param field
	 *            the field
	 * @return the sub items
	 */
	public List<GraphServicePrincipal> getSubItems(String field) {
		JSONObject subItemsJson = (JSONObject) getData(field);

		try {
			return GraphEntity.listFromJson(subItemsJson, GraphServicePrincipal.class);
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
