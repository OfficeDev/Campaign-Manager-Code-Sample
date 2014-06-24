/*******************************************************************************
 * Copyright (c) Microsoft Open Technologies, Inc.
 * All Rights Reserved
 * See License.txt in the project root for license information. 
 ******************************************************************************/
package com.microsoft.campaignmanager;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.SharedPreferences;

// TODO: Auto-generated Javadoc
/**
 * The Class CampaignManagerPreferences.
 */
public class CampaignManagerPreferences {

	/** The m preferences. */
	private SharedPreferences mPreferences;

	/** The Constant SHAREPOINT_SITE_LISTS. */
	public static final String SHAREPOINT_SITE_LISTS = "prefSharepointLists";

	/**
	 * Instantiates a new asset preferences.
	 *
	 * @param context the context
	 * @param preferences the preferences
	 */
	public CampaignManagerPreferences(Context context, SharedPreferences preferences) {
		mPreferences = preferences;
	}

	/**
	 * Gets the library name.
	 *
	 * @return the library name
	 */

	public String getLibraryName() {
		String library = mPreferences.getString("prefLibraryName", null);
		return library;
	}

	public void setLibraryName(String value) {
		mPreferences.edit().putString("prefLibraryName", value).commit();
	}

	/**
	 * Gets the list display size.
	 *
	 * @return the list display size
	 */
	public int getListDisplaySize() {
		return Integer.parseInt(mPreferences.getString("prefListSize", "20"));
	}

	/**
	 * Gets the authentication method.
	 *
	 * @return the authentication method
	 */
	public String getAuthenticationMethod() {
		return "OAUTH";
	}

	public String getGraphAuthorityUrl() {
		return mPreferences.getString("prefGraphOauthAuthorityUrl", null);
	}

	public void setGraphAuthorityUrl(String value) {
		mPreferences.edit().putString("prefGraphOauthAuthorityUrl", value).commit();
	}
	
	public String getGraphClientId() {
		return mPreferences.getString("prefGraphOauthClientId", null);
	}

	public void setGraphClientId(String value) {
		mPreferences.edit().putString("prefGraphOauthClientId", value).commit();
	}

	public String getGraphResourceUrl() {
		return mPreferences.getString("prefGraphOauthResourceUrl", null);
	}

	public void setGraphResourceUrl(String value) {
		mPreferences.edit().putString("prefGraphOauthResourceUrl", value).commit();
	}

	
	public String getGraphRedirectUrl() {
		return mPreferences.getString("prefGraphOauthRedirectUrl", null);
	}

	public void setGraphRedirectUrl(String value) {
		mPreferences.edit().putString("prefGraphOauthRedirectUrl", value).commit();
	}
	
	public String getTenantId() {
		return mPreferences.getString("prefTenantId", null);
	}

	public void setTenantId(String value) {
		mPreferences.edit().putString("prefTenantId", value).commit();
	}
	
	public String getGraphAccessToken() {
		return mPreferences.getString("prefGraphAccessToken", null);
	}

	public void setGraphAccessToken(String value) {
		mPreferences.edit().putString("prefGraphAccessToken", value).commit();
	}

	public String getGraphAccessTokenExpiresOn() {
		return mPreferences.getString("prefGraphAccessTokenExpiresOn", null);
	}

	public void setGraphAccessTokenExpiresOn(String value) {
		mPreferences.edit().putString("prefGraphAccessTokenExpiresOn", value).commit();
	}

	public String getGraphRefreshToken() {
		return mPreferences.getString("prefGraphRefreshToken", null);
	}

	public void setGraphRefreshToken(String value) {
		mPreferences.edit().putString("prefGraphRefreshToken", value).commit();
	}
	
	/**
	 * Gets the sharepoint server.
	 *
	 * @return the sharepoint server
	 */
	public String getSharepointServer() {
		return mPreferences.getString("prefSharepointUrl", null);
	}

	public void setSharepointServer(String value) {
		mPreferences.edit().putString("prefSharepointUrl", value).commit();
	}

	/**
	 * Gets the site relative url.
	 *
	 * @return the site relative url
	 */
	public String getSiteRelativeUrl() {
		return mPreferences.getString("prefSiteRelativeUrl", null);
	}
	
	public void setSiteRelativeUrl(String value) {
		mPreferences.edit().putString("prefSiteRelativeUrl", value).commit();
	}
	
	/**
	 * Gets the authority url.
	 *
	 * @return the authority url
	 */
	public String getAuthorityUrl() {
		return mPreferences.getString("prefOauthAuthorityUrl", null);
	}

	public void setAuthorityUrl(String value) {
		mPreferences.edit().putString("prefOauthAuthorityUrl", value).commit();
	}

	/**
	 * Gets the client id.
	 *
	 * @return the client id
	 */
	public String getClientId() {
		return mPreferences.getString("prefOauthClientId", null);
	}

	public void setClientId(String value) {
		mPreferences.edit().putString("prefOauthClientId", value).commit();
	}


	
	/**
	 * Gets the resource url.
	 *
	 * @return the resource url
	 */
	public String getResourceUrl() {
		return mPreferences.getString("prefOauthResourceUrl", null);
	}

	public void setResourceUrl(String value) {
		mPreferences.edit().putString("prefOauthResourceUrl", value).commit();
	}

	
	public String getRedirectUrl() {
		return mPreferences.getString("prefOauthRedirectUrl", null);
	}

	public void setRedirectUrl(String value) {
		mPreferences.edit().putString("prefOauthRedirectUrl", value).commit();
	}

	
	/**
	 * Gets the user hint.
	 *
	 * @return the user hint
	 */
	public String getUserHint() {
		return mPreferences.getString("prefUserHint", null);
	}

	public void setUserHint(String value) {
		mPreferences.edit().putString("prefUserHint", value).commit();
	}
	
	
	public String getAccessToken() {
		return mPreferences.getString("prefAccessToken", null);
	}

	public void setAccessToken(String value) {
		mPreferences.edit().putString("prefAccessToken", value).commit();
	}

	public String getAccessTokenExpiresOn() {
		return mPreferences.getString("prefAccessTokenExpiresOn", null);
	}

	public void setAccessTokenExpiresOn(String value) {
		mPreferences.edit().putString("prefAccessTokenExpiresOn", value).commit();
	}

	public String getRefreshToken() {
		return mPreferences.getString("prefRefreshToken", null);
	}

	public void setRefreshToken(String value) {
		mPreferences.edit().putString("prefRefreshToken", value).commit();
	}



	/**
	 * Sets the default sharepoint list.
	 *
	 * @param listName the new default sharepoint list
	 */
	public void setDefaultSharepointList(String value) {
		mPreferences.edit().putString("prefDefaultList", value).commit();
	}

	public String getDefaultSharepointList() {
		return mPreferences.getString("prefDefaultList", null);
	}
	
	/**
	 * Store sharepoint list url.
	 *
	 * @param listName the list name
	 */
	public void storeSharepointListUrl(String listName) {
		ArrayList<String> listNames = getStringArrayPref(SHAREPOINT_SITE_LISTS);
		listNames.add(listName);
		setStringArrayPref(SHAREPOINT_SITE_LISTS, listNames);
		setDefaultSharepointList(listName);
	}

	/**
	 * Gets the sharepoint list names.
	 *
	 * @return the sharepoint list names
	 */
	public ArrayList<String> getSharepointListNames() {
		return getStringArrayPref(SHAREPOINT_SITE_LISTS);
	}

	/**
	 * Clear.
	 */
	public void clear() {
		SharedPreferences.Editor editor = mPreferences.edit();
		editor.clear();
		editor.commit();
	}

	/**
	 * Sets the string array pref.
	 *
	 * @param key the key
	 * @param values the values
	 */
	private void setStringArrayPref(String key, ArrayList<String> values) {
		SharedPreferences.Editor editor = mPreferences.edit();
		JSONArray a = new JSONArray();
		for (int i = 0; i < values.size(); i++) {
			a.put(values.get(i));
		}
		if (!values.isEmpty()) {
			editor.putString(key, a.toString());
		} else {
			editor.putString(key, null);
		}
		editor.commit();
	}

	/**
	 * Gets the string array pref.
	 *
	 * @param key the key
	 * @return the string array pref
	 */
	private ArrayList<String> getStringArrayPref(String key) {
		String json = mPreferences.getString(key, null);
		ArrayList<String> urls = new ArrayList<String>();
		if (json != null) {
			try {
				JSONArray a = new JSONArray(json);
				for (int i = 0; i < a.length(); i++) {
					String url = a.optString(i);
					urls.add(url);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return urls;
	}
}
