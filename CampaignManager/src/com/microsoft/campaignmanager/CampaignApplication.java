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
import com.microsoft.campaignmanager.datasource.GraphClient;
import com.microsoft.campaignmanager.datasource.SPUser;
import com.microsoft.campaignmanager.datasource.SharePointListsClientWithUsers;
import com.microsoft.office365.Credentials;
import com.microsoft.office365.LogLevel;
import com.microsoft.office365.Logger;
import com.microsoft.office365.http.OAuthCredentials;

public class CampaignApplication extends Application {

	private static Context appContext;

	private CampaignManagerPreferences mPreferences;

	private Credentials mGraphCredentials;
	private Credentials mSharePointCredentials;
	
	private SharePointListsClientWithUsers mSharepointListsClient;
	
	private GraphClient mGraphClient;

	private SPUser mCurrentUser;
	
	private String mTag = "CampaignManager";

	@Override
	public void onCreate() {

		Log.d("Campaign Manager", "onCreate");
		super.onCreate();
		CampaignApplication.appContext = getApplicationContext();

		mPreferences = new CampaignManagerPreferences(appContext,
				PreferenceManager.getDefaultSharedPreferences(this));

	}

	public Credentials getSharePointCredentials() {
		return mSharePointCredentials;
	}

	public Credentials getGraphCredentials() {
		return mGraphCredentials;
	}

	public void setSharePointCredentials(Credentials credentials) {
		mSharePointCredentials = credentials;
	}

	public void setGraphCredentials(Credentials credentials) {
		mGraphCredentials = credentials;
	}

	public SPUser getCurrentUser() {
		if (mCurrentUser != null) {
			return mCurrentUser;
		} else {
			mCurrentUser = getAccountInfo();
			return mCurrentUser;
		}
	}

	public void handleError(Throwable throwable) {
		Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_LONG).show();
		Log.e(mTag, throwable.toString());
	}

	public ListenableFuture<Credentials> authenticateToSharePoint(Activity activity) {
		final SettableFuture<Credentials> result = SettableFuture.create();

		String method = mPreferences.getAuthenticationMethod();
		//only OAuth is supported in this sample
		if (method.equals("OAUTH")){
			String accessToken = mPreferences.getAccessToken();
			mSharePointCredentials = new OAuthCredentials(accessToken);
		}
		return result;
	}

	public ListenableFuture<Credentials> authenticateToGraphAPI(Activity activity) {
		final SettableFuture<Credentials> result = SettableFuture.create();

		String method = mPreferences.getAuthenticationMethod();
		//only OAuth is supported in this sample
		if (method.equals("OAUTH")){
			String accessToken = mPreferences.getGraphAccessToken();
			mGraphCredentials = new OAuthCredentials(accessToken);
		}
		return result;
	}
	
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
	
	public boolean hasBootstrapGraphConfigurationSettings() {
		String authenticationMethod = mPreferences.getAuthenticationMethod();
		if (isNullOrEmpty(authenticationMethod))
			return false;

		if (isNullOrEmpty(mPreferences.getLibraryName()))
			return false;

		// check OAuth Settings
		String userHint = mPreferences.getUserHint();
		String serverUrl = mPreferences.getSharepointServer();
		String siteUrl = mPreferences.getSiteRelativeUrl();
		boolean result = (!isNullOrEmpty(serverUrl)) && (!isNullOrEmpty(siteUrl))
				 && (!isNullOrEmpty(userHint));
		return result;

	}

	private boolean isNullOrEmpty(String value) {
		return value == null || value.length() == 0;
	}

	public Boolean storeSiteUrl(String url) {
		mPreferences.storeSharepointListUrl(url);
		return true;
	}

	public ArrayList<String> getStoredLists() {
		return mPreferences.getSharepointListNames();
	}

	public boolean hasDefaultList() {
		return mPreferences.getLibraryName() != null;
	}

	public CampaignManagerPreferences getPreferences() {
		return mPreferences;
	}

	public void clearPreferences() {
		CookieSyncManager syncManager = CookieSyncManager.createInstance(this);
		if (syncManager != null) {
			CookieManager cookieManager = CookieManager.getInstance();
			cookieManager.removeAllCookie();
		}
	}

	public SharePointListsClientWithUsers getCurrentListClient() {
		String serverUrl = mPreferences.getSharepointServer();
		String siteRelativeUrl = mPreferences.getSiteRelativeUrl();
		Credentials credentials = getSharePointCredentials();
		mSharepointListsClient = new SharePointListsClientWithUsers(serverUrl, siteRelativeUrl,
				credentials, new Logger() {

			@Override
			public void log(String message, LogLevel level) {
				Log.d(mTag, message);
			}
		});
		return mSharepointListsClient;
	}
	
	public GraphClient getCurrentGraphClient() {
		Credentials credentials = getGraphCredentials();
		mGraphClient = new GraphClient(credentials, mPreferences.getTenantId(), new Logger() {

			@Override
			public void log(String message, LogLevel level) {
				Log.d(mTag, message);
			}
		});
		return mGraphClient;
	}

	public SPUser getAccountInfo() {
		SharePointListsClientWithUsers client = getCurrentListClient();
		try {
			//return client.getUserProperties().get();
			return client.getCurrentUser().get();
		} catch (Throwable t) {
			Log.d(mTag, t.getMessage());
		}
		return null;
	}
}
