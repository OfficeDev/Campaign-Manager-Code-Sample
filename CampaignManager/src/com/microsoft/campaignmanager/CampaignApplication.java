/*******************************************************************************
 * Copyright (c) Microsoft Open Technologies, Inc.
 * All Rights Reserved
 * See License.txt in the project root for license information. 
 ******************************************************************************/
package com.microsoft.campaignmanager;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.campaignmanager.datasource.SPUser;
import com.microsoft.campaignmanager.datasource.SharePointListsClientWithUsers;
import com.microsoft.office365.Credentials;
import com.microsoft.office365.LogLevel;
import com.microsoft.office365.Logger;
import com.microsoft.office365.http.OAuthCredentials;

// TODO: Auto-generated Javadoc
/**
 * The Class AssetApplication.
 */
public class CampaignApplication extends Application {

	/** The app context. */
	private static Context appContext;

	/** The m preferences. */
	private CampaignManagerPreferences mPreferences;

	/** The m credentials. */
	private Credentials mCredentials;

	/** The m sharepoint lists client. */
	private SharePointListsClientWithUsers mSharepointListsClient;

	private SPUser mCurrentUser;


	/*(non-Javadoc)
	 * @see android.app.Application#onCreate()*/

	@Override
	public void onCreate() {

		Log.d("Campaign Manager", "onCreate");
		super.onCreate();
		CampaignApplication.appContext = getApplicationContext();

		mPreferences = new CampaignManagerPreferences(appContext,
				PreferenceManager.getDefaultSharedPreferences(this));

	}

	/**
	 * Gets the credentials.
	 *
	 * @return the credentials
	 */
	public Credentials getCredentials() {
		return mCredentials;
	}

	/**
	 * Sets the credentials.
	 *
	 * @param credentials the new credentials
	 */
	public void setCredentials(Credentials credentials) {
		mCredentials = credentials;
	}

	public SPUser getCurrentUser() {
		if (mCurrentUser != null) {
			return mCurrentUser;
		} else {
			mCurrentUser = getAccountInfo();
			return mCurrentUser;
		}
	}
	/**
	 * Handle error.
	 *
	 * @param throwable the throwable
	 */
	public void handleError(Throwable throwable) {
		Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_LONG).show();
		Log.e("Asset", throwable.toString());
	}

	/**
	 * Authenticate.
	 *
	 * @param activity the activity
	 * @return the office future
	 */
	public ListenableFuture<Credentials> authenticate(Activity activity) {
		final SettableFuture<Credentials> result = SettableFuture.create();

		String method = mPreferences.getAuthenticationMethod();
		//only OAuth is supported in this sample
		if (method.equals("OAUTH")){
			String accessToken = mPreferences.getAccessToken();
			mCredentials = new OAuthCredentials(accessToken);
		}
		return result;
	}

	/**
	 * Checks for configuration settings.
	 *
	 * @return true, if successful
	 */
	public boolean hasConfigurationSettings() {

		String authenticationMethod = mPreferences.getAuthenticationMethod();
		if (isNullOrEmpty(authenticationMethod))
			return false;

		if (isNullOrEmpty(mPreferences.getLibraryName()))
			return false;

		// check OAuth Settings
		String authorityUrl = mPreferences.getAuthorityUrl();
		String clientId = mPreferences.getClientId();
		String resourceUrl = mPreferences.getResourceUrl();
		String userHint = mPreferences.getUserHint();
		boolean result = (!isNullOrEmpty(authorityUrl)) && (!isNullOrEmpty(clientId))
				&& (!isNullOrEmpty(resourceUrl)) && (!isNullOrEmpty(userHint));
		return result;

	}

	/**
	 * Checks if is null or empty.
	 *
	 * @param value the value
	 * @return true, if is null or empty
	 */
	private boolean isNullOrEmpty(String value) {

		return value == null || value.length() == 0;
	}

	/**
	 * Store site url.
	 *
	 * @param url the url
	 * @return the boolean
	 */
	public Boolean storeSiteUrl(String url) {
		mPreferences.storeSharepointListUrl(url);
		return true;
	}

	/**
	 * Gets the stored lists.
	 *
	 * @return the stored lists
	 */
	public ArrayList<String> getStoredLists() {
		return mPreferences.getSharepointListNames();
	}

	/**
	 * Checks for default list.
	 *
	 * @return true, if successful
	 */
	public boolean hasDefaultList() {
		return mPreferences.getLibraryName() != null;
	}

	/**
	 * Gets the preferences.
	 *
	 * @return the preferences
	 */
	public CampaignManagerPreferences getPreferences() {
		return mPreferences;
	}

	/**
	 * Clear preferences.
	 */
	public void clearPreferences() {
		// mPreferences.clear();
		CookieSyncManager syncManager = CookieSyncManager.createInstance(this);
		if (syncManager != null) {
			CookieManager cookieManager = CookieManager.getInstance();
			cookieManager.removeAllCookie();
		}
	}

	/**
	 * Gets the current list client.
	 *
	 * @return the current list client
	 */
	public SharePointListsClientWithUsers getCurrentListClient() {
		String serverUrl = mPreferences.getSharepointServer();
		String siteRelativeUrl = mPreferences.getSiteRelativeUrl();
		Credentials credentials = getCredentials();
		mSharepointListsClient = new SharePointListsClientWithUsers(serverUrl, siteRelativeUrl,
				credentials, new Logger() {

			@Override
			public void log(String message, LogLevel level) {
				Log.d("Asset", message);
			}
		});
		return mSharepointListsClient;
	}

	/**
	 * Gets the account info.
	 *
	 * @return the account info
	 */
	public SPUser getAccountInfo() {
		SharePointListsClientWithUsers client = getCurrentListClient();
		try {
			//return client.getUserProperties().get();
			return client.getCurrentUser().get();
		} catch (Throwable t) {
			Log.d("Asset", t.getMessage());
		}
		return null;
	}
}
