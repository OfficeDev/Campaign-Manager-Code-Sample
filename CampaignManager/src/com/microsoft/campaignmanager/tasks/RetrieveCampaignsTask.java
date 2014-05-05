package com.microsoft.campaignmanager.tasks;

import java.util.ArrayList;


import com.microsoft.campaignmanager.CampaignApplication;
import com.microsoft.campaignmanager.CampaignManagerActivity;
import com.microsoft.campaignmanager.CampaignManagerActivity.PlaceholderFragment;
import com.microsoft.campaignmanager.adapters.CampaignItemAdapter;
import com.microsoft.campaignmanager.datasource.CampaignItemsDataSource;
import com.microsoft.campaignmanager.viewmodel.CampaignListViewItem;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.widget.Toast;

public class RetrieveCampaignsTask extends AsyncTask<String, Void, ArrayList<CampaignListViewItem>> {

	/** The m dialog. */
	private ProgressDialog mDialog;

	/** The m context. */
	private Context mContext;

	/** The m activity. */
	private CampaignManagerActivity mActivity;

	private PlaceholderFragment mFragment;

	/** The m source. */
	private CampaignItemsDataSource mSource;

	/** The m application. */
	private CampaignApplication mApplication;

	/** The m throwable. */
	private Throwable mThrowable;

	/** The m stored rotation. */
	private int mStoredRotation;

	private int mSectionNumber;

	public RetrieveCampaignsTask(CampaignManagerActivity activity, PlaceholderFragment fragment, Integer sectionNumber){
		mActivity = activity;
		mFragment = fragment;
		mContext = activity;
		mDialog = new ProgressDialog(mContext);
		mApplication = (CampaignApplication) activity.getApplication();
		mApplication.authenticate(activity);
		mSource = new CampaignItemsDataSource(mApplication);
		mSectionNumber = sectionNumber;
	}

	@Override
	protected void onPreExecute() {
		mStoredRotation = mActivity.getRequestedOrientation();
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

		switch (mSectionNumber) {
		case 1: {
			mDialog.setTitle("Retrieving pending campaigns...");
			break;
		}
		case 2: {
			mDialog.setTitle("Retrieving campaigns in progress...");
			break;

		}
		}
		//mDialog.setTitle("Retrieving campaigns...");
		mDialog.setMessage("Please wait.");
		mDialog.setCancelable(false);
		mDialog.setIndeterminate(true);
		mDialog.show();
	}

	@Override
	protected void onPostExecute(final ArrayList<CampaignListViewItem> campaignItems) {
		if (mDialog.isShowing()) {
			mDialog.dismiss();
			mActivity.setRequestedOrientation(mStoredRotation);
		}
		if (campaignItems != null) {

			CampaignItemAdapter adapter = new CampaignItemAdapter(mActivity, mFragment, campaignItems, mSectionNumber);
			mFragment.setListAdapter(adapter);
			adapter.notifyDataSetChanged();
			Toast.makeText(mContext, "Finished loading campaigns", Toast.LENGTH_LONG).show();
		} else {
			mApplication.handleError(mThrowable);
		}
	}	
	@Override
	protected ArrayList<CampaignListViewItem> doInBackground(String... params) {

		try {
			switch (mSectionNumber){
			case 1: {
				ArrayList<CampaignListViewItem> items = mSource.getCampaignsByStatus("Pending");
				return items;
			}
			case 2: {
				ArrayList<CampaignListViewItem> items = mSource.getCampaignsByStatus("Approved");
				return items;
			}
			default: {
				return null;
			}
			}
		} catch (Exception e){
			mThrowable = e;
			e.printStackTrace();
			return null;
		}
	}
}
