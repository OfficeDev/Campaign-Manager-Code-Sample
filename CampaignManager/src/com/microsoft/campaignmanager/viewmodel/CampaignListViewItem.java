/*******************************************************************************
 * Copyright (c) Microsoft Open Technologies, Inc.
 * All Rights Reserved
 * See License.txt in the project root for license information. 
 ******************************************************************************/
package com.microsoft.campaignmanager.viewmodel;

import org.json.JSONException;
import org.json.JSONObject;

import com.microsoft.campaignmanager.datasource.SPUrl;
import com.microsoft.office365.lists.SPListItem;

// TODO: Auto-generated Javadoc
/**
 * The Class CampaignListViewItem.
 */
public class CampaignListViewItem {

	/** The m campaign id. */
	private String mCampaignId;
	private String mApproverName;
	
 	/** The m list item. */
	 private SPListItem mListItem;

	/**
	 * Instantiates a new campaign list view item.
	 */
	public CampaignListViewItem() {
		mListItem = new SPListItem();
	}

	/**
	 * Instantiates a new campaign list view item.
	 *
	 * @param listItem the list item
	 * @param picture the picture
	 */
	public CampaignListViewItem(SPListItem listItem, String approverName) {
		mListItem = listItem;
		mApproverName = approverName;
	}

	/**
	 * Gets the list item.
	 *
	 * @return the list item
	 */
	public SPListItem getListItem() {
		return mListItem;
	}

	/**
	 * Populate.
	 */
	public void populate() {
		mCampaignId = getCampaignId();
	}

	/**
	 * Gets the campaign id.
	 *
	 * @return the campaign id
	 */
	public String getCampaignId() {
		mCampaignId = safeString(mListItem.getData("Id"));
		return mCampaignId;
	}
	
	public String getApproverName() {
		return mApproverName;
	}
	
	/**
	 * Gets the data.
	 *
	 * @param key the key
	 * @return the data
	 */
	public String getData(String key) {
		return safeString(mListItem.getData(key));
	}

	/**
	 * Sets the campaign id.
	 *
	 * @param campaignId the new campaign id
	 */
	public void setCampaignId(Integer campaignId) {
		mListItem.setData("Id", campaignId);
	}
	
	/**
	 * Sets the campaign title.
	 *
	 * @param title the new campaign title
	 */
	public void setCampaignTitle(String title) {
		mListItem.setData("Title", title);
	}
	
	/**
	 * Sets the campaign description.
	 *
	 * @param description the new campaign description
	 */
	public void setCampaignDescription(String description) {
		mListItem.setData("Description", description);
	}

	public void setCampaignStatus(String status) {
		mListItem.setData("Status", status);
	}

//	public void setCampaignSite(String site) {
//		mListItem.setData("Site", site);
//	}
	public void setCampaignSite(String siteUrl, String siteDescription){
		JSONObject urlJson = new JSONObject();
		JSONObject type = new JSONObject();
		try {
			type.put("type", "SP.FieldUrlValue");
			urlJson.put("__metadata", type);
			urlJson.put("url", siteUrl);
			urlJson.put("description", siteDescription);
			SPUrl url = new SPUrl();
			url.loadFromJson(urlJson);
			mListItem.setData("Site", url);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void setApproverName(String approverName) {
		mApproverName = approverName;		
	}
	
	public void setApproverId(Integer approverId) {
		mListItem.setData("VP_x0020_ApproverId", approverId);		
	}
	
	/**
	 * Safe string.
	 *
	 * @param object the object
	 * @return the string
	 */
	private String safeString(Object object) {
		if (object == null)
			return "";
		if (object.equals(JSONObject.NULL)) {
			return "";
		}
		return object.toString().trim();
	}
	
}
