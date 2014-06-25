package com.microsoft.campaignmanager.datasource;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

// TODO: Auto-generated Javadoc
/**
 * The Class GraphPermission.
 */
public class GraphPermission extends GraphEntity {

	// Permission values
	// Azure Active Directory
	public static final String DirectoryRead = "Directory.Read";
	public static final String DirectoryWrite = "Directory.Write";
	public static final String UserImpersonation = "user_impersonation";
	public static final String UserProfileRead = "UserProfile.Read";
	public static final String UserProfileWrite = "UserProfile.Write";

	// SharePoint
	public static final String MyFilesRead = "MyFiles.Read";
	public static final String MyFilesWrite = "MyFiles.Write";

	public static final String AllSitesRead = "AllSites.Read";
	public static final String AllSitesWrite = "AllSites.Write";
	public static final String AllSitesManage = "AllSites.Manage";
	public static final String AllSitesFullControl = "AllSites.FullControl";

	//2015-06-23T19:54:14.4688373+02:00
	private static final String odataDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZZZZZ";	

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
	public static List<GraphPermission> listFromJson(JSONObject json) throws JSONException {
		return GraphPermission.listFromJson(json, GraphPermission.class);
	}

	/**
	 * Instantiates a new Graph Permission object.
	 */
	public GraphPermission()  {
		mValues = new HashMap<String, Object>();
		// set some defaults
		this.setConsentType("AllPrincipals");
		Calendar cal = Calendar.getInstance();

		Date startTime = cal.getTime();
		this.setStartTime(startTime);

		cal.add(Calendar.YEAR, 1);
		Date expiryTime = cal.getTime();
		this.setExpiryTime(expiryTime);

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
	public String getClientId() {
		return getData("clientId").toString();
	}

	public void setClientId(String value) {
		setData("clientId", value);
	}

	public String getConsentType() {
		return getData("consentType").toString();
	}

	public void setConsentType(String value) {
		setData("consentType", value);
	}

	public String getResourceId() {
		return getData("resourceId").toString();
	}

	public void setResourceId(String value) {
		setData("resourceId", value);
	}

	public String getObjectId() {
		return getData("objectId").toString();
	}

	public String getOdataType() {
		return getData("odata.type").toString();
	}

	public String getScope() {
		return getData("scope").toString();
	}

	public void setScope(String value) {
		setData("scope", value);
	}

	public String getStartTimeOdata() {
		SimpleDateFormat sdf = new SimpleDateFormat(odataDateFormat, Locale.getDefault());
		return sdf.format((Date) getData("startTime"));
	}

	public Date getStartTime() {
		return (Date) getData("startTime");
	}

	public void setStartTime(Date value) {
		setData("startTime", value);
	}

	public String getExpiryTimeOdata() {
		SimpleDateFormat sdf = new SimpleDateFormat(odataDateFormat, Locale.getDefault());
		return sdf.format((Date) getData("expiryTime"));
	}

	public Date getExpiryTime() {
		return (Date) getData("expiryTime");
	}

	public void setExpiryTime(Date value) {
		setData("expiryTime", value);
	}

	//  Omitting several fields that are not required for this sample

	@Override
	public Object getData(String field) {
		if (mValues.containsKey(field)) {
			return mValues.get(field);
		} else {
			return super.getData(field);
		}
	}
}
