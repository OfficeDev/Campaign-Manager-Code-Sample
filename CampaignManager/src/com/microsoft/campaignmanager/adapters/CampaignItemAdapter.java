/*******************************************************************************
 * Copyright (c) Microsoft Open Technologies, Inc.
 * All Rights Reserved
 * See License.txt in the project root for license information. 
 ******************************************************************************/
package com.microsoft.campaignmanager.adapters;

import java.util.List;

import com.microsoft.campaignmanager.CampaignApplication;
import com.microsoft.campaignmanager.CampaignManagerActivity;
import com.microsoft.campaignmanager.CampaignManagerActivity.PlaceholderFragment;
import com.microsoft.campaignmanager.R;
import com.microsoft.campaignmanager.tasks.ApproveCampaignTask;
import com.microsoft.campaignmanager.tasks.RetrieveCampaignsTask;
import com.microsoft.campaignmanager.viewmodel.CampaignListViewItem;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


// TODO: Auto-generated Javadoc
/**
 * The Class CampaignItemAdapter.
 */
public class CampaignItemAdapter extends BaseAdapter {

	/** The m activity. */
	private CampaignManagerActivity mActivity;
	
	private Fragment mFragment;

	private CampaignApplication mApplication;

	/** The m data. */
	private List<CampaignListViewItem> mData;

	/** The inflater. */
	private static LayoutInflater inflater = null;

	// Control visibility states
	final Integer VISIBLE = 0;
	final Integer GONE = 8;

	/**
	 * Instantiates a new campaign item adapter.
	 *
	 * @param activity the activity
	 * @param data the data
	 */
	public CampaignItemAdapter(Activity activity, Fragment fragment, List<CampaignListViewItem> data, Integer sectionNumber) {
		mActivity = (CampaignManagerActivity) activity;
		mFragment = fragment;
		mData = data;
		mApplication = (CampaignApplication) mActivity.getApplication();
		inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (convertView == null)
			view = inflater.inflate(R.layout.campaignlistitem, parent, false);

		TextView campaignId = (TextView) view.findViewById(R.id.campaignid);
		TextView campaignName = (TextView) view.findViewById(R.id.campaign_name);
		TextView campaignDescription = (TextView) view.findViewById(R.id.campaign_description);
		TextView vpApprover = (TextView) view.findViewById(R.id.vp_approver_name);
		ImageView vpApproverImage = (ImageView) view.findViewById(R.id.vp_approver_image);

		//ImageView thumbnail = (ImageView) view.findViewById(R.id.list_image);

		// Setting Basic List item data
		CampaignListViewItem item = mData.get(position);
		campaignId.setText(item.getCampaignId());
		campaignName.setText(item.getData("Title"));
		campaignDescription.setText(item.getData("Description"));
		vpApprover.setText(item.getApproverName());
		vpApproverImage.setImageResource(getUserImageId(item.getApproverName()));

		// Setting controls based on content
		if(mApplication.getCurrentUser().getTitle().equalsIgnoreCase(item.getApproverName())) {

			// Set VP controls depending on whether status is pending or not
			if(item.getData("Status").equalsIgnoreCase("Pending")){

				((ImageButton) view.findViewById(R.id.imageVPApprove)).setVisibility(VISIBLE);
				((ImageButton) view.findViewById(R.id.imageVPApprove)).setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v){
						//initiate approval
						View rootView = (View) v.getParent().getParent();
						TextView tv = (TextView) rootView.findViewById(R.id.campaignid);
						String campaignId = tv.getText().toString();
						View parentView = (View) v.getParent();
						((ImageButton) parentView.findViewById(R.id.imageVPApprove)).setVisibility(GONE);
						((ImageButton) parentView.findViewById(R.id.imageVPReject)).setVisibility(GONE);
						((ImageView) parentView.findViewById(R.id.imageVPStatus)).setImageResource(getStatusImageId("Approved"));
						((ImageView) parentView.findViewById(R.id.imageVPStatus)).setVisibility(VISIBLE);
						CampaignListViewItem campaign = new CampaignListViewItem();
						campaign.setCampaignId(Integer.parseInt(campaignId));
						campaign.setCampaignStatus("Approved");
						new ApproveCampaignTask(mActivity, mFragment).execute(campaign);
						PlaceholderFragment fragment = (PlaceholderFragment) mActivity.getFragmentManager().findFragmentByTag(mActivity.getInProgressTag());
						new RetrieveCampaignsTask(mActivity, fragment, 2).execute();
					}
				});

				((ImageButton) view.findViewById(R.id.imageVPReject)).setVisibility(VISIBLE);
				((ImageView) view.findViewById(R.id.imageVPStatus)).setVisibility(GONE);
			} else {
				((ImageButton) view.findViewById(R.id.imageVPApprove)).setVisibility(GONE);
				((ImageButton) view.findViewById(R.id.imageVPReject)).setVisibility(GONE);
				((ImageView) view.findViewById(R.id.imageVPStatus)).setImageResource(getStatusImageId(item.getData("Status")));
				((ImageView) view.findViewById(R.id.imageVPStatus)).setVisibility(VISIBLE);

			}
		} else {
			// Set VP controls
			((ImageButton) view.findViewById(R.id.imageVPApprove)).setVisibility(GONE);
			((ImageButton) view.findViewById(R.id.imageVPReject)).setVisibility(GONE);
			((ImageView) view.findViewById(R.id.imageVPStatus)).setImageResource(getStatusImageId(item.getData("Status")));
			((ImageView) view.findViewById(R.id.imageVPStatus)).setVisibility(VISIBLE);
		}
 
		// Set up link icon and url formatting if site url is provided		
		if(!item.getData("Site").isEmpty()){
			((ImageView) view.findViewById(R.id.sharepointlogo)).setVisibility(VISIBLE);
			TextView tv = (TextView) view.findViewById(R.id.campaign_name);
			tv.setText(Html.fromHtml(item.getData("Site")));
			tv.setClickable(true);
			tv.setMovementMethod(LinkMovementMethod.getInstance());
		} else
		{
			((TextView) view.findViewById(R.id.campaign_name)).setText(item.getData("Title"));
			((ImageView) view.findViewById(R.id.sharepointlogo)).setVisibility(GONE);
		}

		return view;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return mData.size();
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	private Integer getStatusImageId(String status){
		Integer imageId = 0;
		if(status.equalsIgnoreCase("Pending")) imageId = R.drawable.hourglass_black;
		else if (status.equalsIgnoreCase("Approved") || status.equalsIgnoreCase("In Progress")) imageId = R.drawable.ic_action_done_dark;
		else if (status.equalsIgnoreCase("Rejected")) imageId = R.drawable.ic_action_cancel_dark;
		else imageId = R.drawable.hourglass_black;
		return imageId;
	}

	private Integer getUserImageId(String userName){
		Integer imageId = 0;
		if(userName.equalsIgnoreCase("Alex Darrow"))imageId = R.drawable.alex_darrow;
		if(userName.equalsIgnoreCase("Allie bellew")) imageId = R.drawable.allie_bellew;
		if(userName.equalsIgnoreCase("Aziz Hassouoneh"))  imageId = R.drawable.aziz_hassouneh;
		if(userName.equalsIgnoreCase("Belinda Newman"))  imageId = R.drawable.belinda_newman;
		if(userName.equalsIgnoreCase("Bonnie Kearney"))  imageId = R.drawable.bonnie_kearney;
		if(userName.equalsIgnoreCase("David Longmuir"))  imageId = R.drawable.david_longmuir;
		if(userName.equalsIgnoreCase("Dorena Paschke"))  imageId = R.drawable.dorena_paschke;
		if(userName.equalsIgnoreCase("Alex Darrow"))imageId = R.drawable.alex_darrow;

		if(imageId == 0) imageId = R.drawable.no_image;
		return imageId;
	}

}
