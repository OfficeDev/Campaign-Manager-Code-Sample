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
 * The Class AssetPreferences.
 */
public class AssetPreferences {

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
	public AssetPreferences(Context context, SharedPreferences preferences) {
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
		return mPreferences.getString("listPref", null);
	}

	/**
	 * Gets the sharepoint server.
	 *
	 * @return the sharepoint server
	 */
	public String getSharepointServer() {
		return mPreferences.getString("prefSharepointUrl", null);
	}

	/**
	 * Gets the site relative url.
	 *
	 * @return the site relative url
	 */
	public String getSiteRelativeUrl() {
		return mPreferences.getString("prefSiteRelativeUrl", null);
	}

	/**
	 * Gets the NTLM user.
	 *
	 * @return the NTLM user
	 */
	public String getNTLMUser() {
		return mPreferences.getString("prefNTLMUser", null);
	}

	/**
	 * Gets the NTLM password.
	 *
	 * @return the NTLM password
	 */
	public String getNTLMPassword() {
		return mPreferences.getString("prefNTLMPassword", null);
	}

	/**
	 * Gets the authority url.
	 *
	 * @return the authority url
	 */
	public String getAuthorityUrl() {
		return mPreferences.getString("prefOauthAuthorityUrl", null);
	}

	/**
	 * Gets the client id.
	 *
	 * @return the client id
	 */
	public String getClientId() {
		return mPreferences.getString("prefOauthClientId", null);
	}

	/**
	 * Gets the resource url.
	 *
	 * @return the resource url
	 */
	public String getResourceUrl() {
		return mPreferences.getString("prefOauthResourceUrl", null);
	}

	/**
	 * Gets the user hint.
	 *
	 * @return the user hint
	 */
	public String getUserHint() {
		return mPreferences.getString("prefUserHint", null);
	}

	/**
	 * Sets the default sharepoint list.
	 *
	 * @param listName the new default sharepoint list
	 */
	public void setDefaultSharepointList(String listName) {
		SharedPreferences.Editor editor = mPreferences.edit();
		editor.putString("prefDefaultList", listName);
		editor.commit();
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
