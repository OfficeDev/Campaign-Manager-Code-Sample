package com.microsoft.campaignmanager;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

import com.microsoft.adal.AuthenticationCallback;
import com.microsoft.adal.AuthenticationContext;
import com.microsoft.adal.AuthenticationException;
import com.microsoft.adal.AuthenticationResult;
import com.microsoft.adal.Logger;
import com.microsoft.adal.PromptBehavior;
import com.microsoft.campaignmanager.AppSettingsActivity;
import com.microsoft.campaignmanager.MainActivity;
import com.microsoft.campaignmanager.tasks.CreateCampaignListTask;
import com.microsoft.campaignmanager.tasks.CreateCampaignManagerApplicationTask;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link FragmentPagerAdapter} derivative, which will keep every
	 * loaded fragment in memory. If this becomes too memory intensive, it
	 * may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */

	// ADAL Settings
	AuthenticationContext mContext;
	AuthenticationResult mResult;
	final String TAG = "ADAL";

	private CampaignApplication mApplication;
	private CampaignManagerPreferences mPreferences;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		Context appContext = this.getApplicationContext();
		mApplication = (CampaignApplication) getApplication();
		mPreferences = new CampaignManagerPreferences(appContext,
				PreferenceManager.getDefaultSharedPreferences(this));

		boolean hasBootstrapConfig = mApplication.hasBootstrapConfigurationSettings();
		boolean hasConfig = mApplication.hasConfigurationSettings()
				&& mApplication.hasDefaultList();
		if (hasBootstrapConfig && !hasConfig){
			try {
				authenticateWithOAuthToGraphAPI();
			} catch (Throwable t) {
				Log.e("MainActivity", t.getMessage());
			}			
		} else {
			if (hasConfig) {
				try {
					authenticateWithOAuthToSharePoint();
				} catch (Throwable t) {
					Log.e("MainActivity", t.getMessage());
				}
			} else {
				if(!preferencesAreComplete()) {
					if (savedInstanceState == null) {
						getFragmentManager().beginTransaction()
						.add(R.id.container, new PlaceholderFragment())
						.commit();
					}
				}
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (mContext != null) {
			mContext.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.action_settings: {
			startActivity(new Intent(this, AppSettingsActivity.class));
			return true;
		}
		default:
			return true;
		}
	}
	
	public CampaignManagerPreferences getPreferences() {
		return mPreferences;
	}

	/**
	 * Check preferences.
	 */
	private boolean preferencesAreComplete() {
		//		Intent i = null;
		return mApplication.hasConfigurationSettings();
		//		if (!hasConfig) {
		//			i = new Intent(MainActivity.this, AppSettingsActivity.class);
		//			startActivity(i);

	}


	//	Authentication callback for when configuration is complete
	private AuthenticationCallback<AuthenticationResult> callback = new AuthenticationCallback<AuthenticationResult>() {

		@Override
		public void onError(Exception exc) {
			if (exc instanceof AuthenticationException) {
				//textViewStatus.setText("Cancelled");
				Log.d(TAG, "Cancelled");
			} else {
				//textViewStatus.setText("Authentication error:" + exc.getMessage());
				Log.d(TAG, "Authentication error:" + exc.getMessage());
			}
		}

		@Override
		public void onSuccess(AuthenticationResult result) {
			mResult = result;

			if (result == null || result.getAccessToken() == null
					|| result.getAccessToken().isEmpty()) {
				//textViewStatus.setText("Token is empty");
				Log.d(TAG, "Token is empty");
			} else {
				// request is successful
				Log.d(TAG, "Status:" + result.getStatus() + " Expired:"
						+ result.getExpiresOn().toString());

				// Store tokens in App Settings, so they can be viewed and used
				// by other activities
				mPreferences.setAccessToken(result.getAccessToken());
				mPreferences.setAccessTokenExpiresOn(result.getExpiresOn().toString());
				mPreferences.setRefreshToken(result.getRefreshToken());

				// Create the campaigns list if it doesn't exist
				createList();
				Button launchButton = (Button) findViewById(R.id.launchButton);
				// if the launch button exists, we are in first run experience
				// otherwise everything should be set up and the app should be
				// launched
				if(launchButton != null) {
					launchButton.setEnabled(true);
				} else {

					// Launch Campaign Manager Activity
					startActivity(new Intent(MainActivity.this,
							CampaignManagerActivity.class));
				}
			}
		}
	};

	// Authentication callback for initial bootstrapping
	private AuthenticationCallback<AuthenticationResult> graphCallback = new AuthenticationCallback<AuthenticationResult>() {

		@Override
		public void onError(Exception exc) {
			if (exc instanceof AuthenticationException) {
				//textViewStatus.setText("Cancelled");
				Log.d(TAG, "Cancelled");
			} else {
				//textViewStatus.setText("Authentication error:" + exc.getMessage());
				Log.d(TAG, "Authentication error:" + exc.getMessage());
			}
		}

		@Override
		public void onSuccess(AuthenticationResult result) {
			mResult = result;

			if (result == null || result.getAccessToken() == null
					|| result.getAccessToken().isEmpty()) {
				//textViewStatus.setText("Token is empty");
				Log.d(TAG, "Token is empty");
			} else {
				// request is successful
				Log.d(TAG, "Status:" + result.getStatus() + " Expired:"
						+ result.getExpiresOn().toString());
				// Store tokens in App Settings, so they can be viewed and used
				// by other activities
				mPreferences.setGraphAccessToken(result.getAccessToken());
				mPreferences.setGraphAccessTokenExpiresOn(result.getExpiresOn().toString());
				mPreferences.setGraphRefreshToken(result.getRefreshToken());

				// Create the application in Azure AD
				createApplication();

			}
		}
	};


	private void authenticateWithOAuthToSharePoint() {
		Logger.v(TAG, "get Token");

		String resource = mPreferences.getResourceUrl();
		if (resource == null || resource.isEmpty()) {
			Log.e(TAG,"Resource has not been configured in app settings.");
		}

		String clientId = mPreferences.getClientId();
		if (clientId == null || clientId.isEmpty()) {
			Log.e(TAG,"Client ID has not been configured in app settings.");
		}

		// Optional field, so acquireToken accepts null fields
		String userid = mPreferences.getUserHint();

		try {
			mContext = new AuthenticationContext(MainActivity.this, mPreferences.getAuthorityUrl(), true);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Optional field, so acquireToken accepts null fields
		mResult = null;
		//				mContext.setRequestCorrelationId(mRequestCorrelationId);

		mContext.acquireToken(MainActivity.this, resource, clientId, mPreferences.getRedirectUrl(), userid, PromptBehavior.Auto, "",
				callback);
	}

	private void authenticateWithOAuthToGraphAPI() {
		Logger.v(TAG, "get Token");
		// We'll use the graph api to create the application
		mPreferences.setGraphResourceUrl("https://graph.windows.net");
		mPreferences.setGraphRedirectUrl("http://localhost");
		// Global Client ID configured by app owner
		// Used only to create an Application  for the actual Campaign Manager demo
		// in Azure AD on the Office 365 subscription of the user
		// This requires tenant admin permissions in O365
		mPreferences.setGraphClientId("914a80b9-6cfb-4a88-b3fd-0cc40687293a");
		// Authority Url is the same as for Campaign Manager proper
		mPreferences.setGraphAuthorityUrl(mPreferences.getAuthorityUrl());

		String resource = mPreferences.getGraphResourceUrl();
		if (resource == null || resource.isEmpty()) {
			Log.e(TAG,"Resource has not been configured in app settings.");
		}

		String clientId = mPreferences.getGraphClientId();
		if (clientId == null || clientId.isEmpty()) {
			Log.e(TAG,"Client ID has not been configured in app settings.");
		}

		// Optional field, so acquireToken accepts null fields
		String userid = mPreferences.getUserHint();

		try {
			mContext = new AuthenticationContext(MainActivity.this, mPreferences.getGraphAuthorityUrl(), true);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Optional field, so acquireToken accepts null fields
		mResult = null;
		//				mContext.setRequestCorrelationId(mRequestCorrelationId);

		mContext.acquireToken(MainActivity.this, resource, clientId, mPreferences.getGraphRedirectUrl(), userid, PromptBehavior.Auto, "",
				graphCallback);
	}

	private void createList() {
		mApplication.authenticateToSharePoint(this);
		new CreateCampaignListTask(this).execute();
	}

	private void createApplication() {
		mApplication.authenticateToGraphAPI(this);
		new CreateCampaignManagerApplicationTask(this).execute();
	}
	
	public static class PlaceholderFragment extends Fragment {



		public PlaceholderFragment() {
		}

		CampaignManagerPreferences mPreferences;
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_easysettings, container, false);

			MainActivity activity = (MainActivity) this.getActivity();
			mPreferences = activity.getPreferences();

			Button createAppButton = (Button) rootView.findViewById(R.id.createAppButton);
			createAppButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					configureApp(v);
					((MainActivity) getActivity()).authenticateWithOAuthToGraphAPI();
				}				
			});
			Button configureButton = (Button) rootView.findViewById(R.id.configureButton);
			configureButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					updateSettingsView(v);
					configureApp(v);
					((MainActivity) getActivity()).authenticateWithOAuthToSharePoint();
				}				
			});
			Button launchButton = (Button) rootView.findViewById(R.id.launchButton);
			launchButton.setEnabled(false);
			launchButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					startActivity(new Intent(getActivity(),
							CampaignManagerActivity.class));
				}				
			});

			return rootView;
		}
		
		private void updateSettingsView(View v)
		{
			View rootView = v.getRootView();
			
			String clientId = mPreferences.getClientId();
			String redirectUrl = mPreferences.getRedirectUrl();
			if(clientId != null && clientId != "" && redirectUrl != null && redirectUrl != "")
			{
				Button configureButton = (Button) rootView.findViewById(R.id.configureButton);
				configureButton.setEnabled(true);

				EditText clientIdField = (EditText) rootView.findViewById(R.id.editClientId);
				clientIdField.setText(clientId);

				EditText redirectUrlField = (EditText) rootView.findViewById(R.id.editRedirectUri);
				redirectUrlField.setText(redirectUrl);	
			}
		}
			
		private void configureApp(View v) {
			View rootView = v.getRootView();
			String loginName = ((EditText) rootView.findViewById(R.id.editLoginName)).getText().toString();
			String siteUrl = ((EditText) rootView.findViewById(R.id.editSiteUrl)).getText().toString();
			String clientId = ((EditText) rootView.findViewById(R.id.editClientId)).getText().toString();
			String redirectUri = ((EditText) rootView.findViewById(R.id.editRedirectUri)).getText().toString();
			
			// Generate app settings from simplified settings
			String sharePointBaseUrl = getSharePointBaseUrl(siteUrl);
			String resourceUrl = sharePointBaseUrl;
			String sharePointSiteRelativeUrl = getSharePointRelativeSiteUrl(siteUrl);
			String authorityUrl = getAuthorityUrl(loginName);

			// Set Preferences
			Context appContext = getActivity().getApplicationContext();
			CampaignManagerPreferences prefs = new CampaignManagerPreferences(appContext,
					PreferenceManager.getDefaultSharedPreferences(getActivity()));
			prefs.setClientId(clientId);
			prefs.setRedirectUrl(redirectUri);
			prefs.setAuthorityUrl(authorityUrl);
			prefs.setSharepointServer(sharePointBaseUrl);
			prefs.setSiteRelativeUrl(sharePointSiteRelativeUrl);
			prefs.setResourceUrl(resourceUrl);
			prefs.setUserHint(loginName);
			prefs.setLibraryName("Campaigns");
			prefs.setTenantId(getTenantId(loginName));
		}

		private String getSharePointBaseUrl(String siteAddress) {
			URL siteUrl;
			try {
				siteUrl = new URL(siteAddress);
				String baseUrl = siteUrl.getProtocol() + "://" + siteUrl.getHost() + "/";
				return baseUrl;
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}

		private String getSharePointRelativeSiteUrl(String siteAddress) {
			URL siteUrl;
			try {
				siteUrl = new URL(siteAddress);
				return siteUrl.getPath();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}

		private String getAuthorityUrl(String loginName) {
			if(isValidEmail(loginName)){
				String authorityUrl = "https://login.windows.net/" + getTenantId(loginName);
				return authorityUrl;
			}
			return null;

		}
		
		private String getTenantId(String loginName) {
			if(isValidEmail(loginName)){
				String[] splitString = loginName.split("@");
				String tenantId = splitString[1];
				return tenantId;
			}
			return null;

		}
		private final static boolean isValidEmail(CharSequence target) {
			if (target == null) {
				return false;
			} else {
				return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
			}
		}
	}
}

