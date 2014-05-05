/*******************************************************************************
 * Copyright (c) Microsoft Open Technologies, Inc.
 * All Rights Reserved
 * See License.txt in the project root for license information. 
 ******************************************************************************/
package com.microsoft.campaignmanager.tasks;


import com.microsoft.campaignmanager.CampaignApplication;
import com.microsoft.campaignmanager.datasource.CampaignItemsDataSource;
import com.microsoft.campaignmanager.viewmodel.CampaignListViewItem;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

// TODO: Auto-generated Javadoc
/**
 * The Class ApproveCampaignTask.
 */
public class ApproveCampaignTask extends AsyncTask<CampaignListViewItem, Void, Void> {

	/** The m source. */
	private CampaignItemsDataSource mSource;
	
	/** The m activity. */
	private Activity mActivity;
	
	/** The m dialog. */
	private ProgressDialog mDialog;
	
	/** The m application. */
	private CampaignApplication mApplication;
	
	/** The m throwable. */
	private Throwable mThrowable;

	/**
	 * Instantiates a new update car task.
	 *
	 * @param activity the activity
	 */
	public ApproveCampaignTask(Activity activity, Fragment fragment) {
		mActivity = activity;
		mDialog = new ProgressDialog(mActivity);
		mApplication = (CampaignApplication) activity.getApplication();
		mSource = new CampaignItemsDataSource(mApplication);
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	protected void onPreExecute() {
		mDialog.setTitle("Updating campaign...");
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
			Toast.makeText(mActivity, "Campaign is approved.", Toast.LENGTH_SHORT).show();
			
		} else {
			mApplication.handleError(mThrowable);
		}
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Void doInBackground(CampaignListViewItem... params) {
		CampaignListViewItem viewItem = params[0];
		if (viewItem != null) {
			try {
				mSource.updateSelectedCampaign(viewItem);
			} catch (Throwable t) {
				mThrowable = t;
			}
		} else {
			mThrowable = new IllegalArgumentException(
					"params argument must contain at least a CampaignListViewItem");
		}
		return null;
	}
}
