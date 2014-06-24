/*******************************************************************************
 * Copyright (c) Microsoft Open Technologies, Inc.
 * All Rights Reserved
 * See License.txt in the project root for license information. 
 ******************************************************************************/
package com.microsoft.campaignmanager.tasks;


import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.microsoft.campaignmanager.CampaignApplication;
import com.microsoft.campaignmanager.CampaignManagerPreferences;
import com.microsoft.campaignmanager.datasource.GraphApplication;
import com.microsoft.campaignmanager.datasource.GraphDataSource;
import com.microsoft.campaignmanager.datasource.CampaignItemsDataSource;
import com.microsoft.campaignmanager.datasource.GraphPermission;
import com.microsoft.campaignmanager.datasource.GraphResource;
import com.microsoft.campaignmanager.datasource.GraphServicePrincipal;

// TODO: Auto-generated Javadoc

public class CreateCampaignManagerApplicationTask extends AsyncTask<String, Void, Void> {

	/** The m source. */
	private GraphDataSource mSource;

	/** The m activity. */
	private Activity mActivity;

	/** The m dialog. */
	private ProgressDialog mDialog;

	/** The m application. */
	private CampaignApplication mApplication;
	
	private CampaignManagerPreferences mPreferences;

	/** The m throwable. */
	private Throwable mThrowable;

	/**
	 * Instantiates a new update car task.
	 *
	 * @param activity the activity
	 */
	public CreateCampaignManagerApplicationTask(Activity activity) {
		mActivity = activity;
		mDialog = new ProgressDialog(mActivity);
		mApplication = (CampaignApplication) activity.getApplication();
		mPreferences = (CampaignManagerPreferences) mApplication.getPreferences();
		mSource = new GraphDataSource(mApplication);
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	protected void onPreExecute() {
		mDialog.setTitle("Creating Campaign Manager Application...");
		mDialog.setMessage("Please wait.");
		mDialog.setCancelable(false);
		mDialog.setIndeterminate(true);
		mDialog.show();
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(Void result) {
		if (mDialog.isShowing()) {
			mDialog.dismiss();
		}

		if (mThrowable == null) {
			String toastMsg = String.format("Application has been created. CliendID: %s", mPreferences.getClientId());
			Toast.makeText(mActivity, toastMsg, Toast.LENGTH_LONG).show();

		} else {
			mApplication.handleError(mThrowable);
		}
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Void doInBackground(String... params) {
		try {
			GraphApplication application = new GraphApplication();
			Calendar c = Calendar.getInstance();

			String appName = String.format("Campaign Manager Android App_%s", c.getTime().toString());
			application.setDisplayName(appName);
			application.setPublicClient(true);
			GraphApplication newApp = mSource.createApplication(application);
			
			GraphServicePrincipal newServicePrincipal = mSource.createServicePrincipal(newApp);

			String spScope = String.format("%s %s", GraphPermission.AllSitesManage, GraphPermission.AllSitesWrite);
			GraphPermission sharePointPermission = new GraphPermission();
			sharePointPermission.setClientId(newServicePrincipal.getObjectId());
			sharePointPermission.setResourceId(GraphResource.SharePoint);
			sharePointPermission.setScope(spScope);
			GraphPermission newSharePointPermission = mSource.createPermission(sharePointPermission);
			
			String aadScope = GraphPermission.UserImpersonation;
			GraphPermission aadPermission = new GraphPermission();
			aadPermission.setClientId(newServicePrincipal.getObjectId());
			aadPermission.setResourceId(GraphResource.GraphAPI);
			aadPermission.setScope(aadScope);
			GraphPermission newAzureActiveDirectoryPermission = mSource.createPermission(aadPermission);
			
			String dirScope = GraphPermission.UserProfileRead;
			GraphPermission userProfilePermission = new GraphPermission();
			userProfilePermission.setClientId(newServicePrincipal.getObjectId());
			userProfilePermission.setResourceId(GraphResource.AzureActiveDirectory);
			userProfilePermission.setScope(dirScope);
			GraphPermission newprofilePermission = mSource.createPermission(userProfilePermission);

			// set client id and redirectUrl in app settings
			mPreferences.setClientId(newApp.getAppId());
			mPreferences.setRedirectUrl("http://home");
			
			//mSource.getApplicationByName(appName);
		} catch (Throwable t) {
			mThrowable = t;
		}
		return null;
	}
}
