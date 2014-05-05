package com.microsoft.campaignmanager.datasource;

import java.util.ArrayList;
import java.util.List;
import android.util.Log;
import android.util.SparseArray;

import com.microsoft.campaignmanager.CampaignApplication;
import com.microsoft.campaignmanager.R;
import com.microsoft.campaignmanager.viewmodel.CampaignListViewItem;
import com.microsoft.office365.Query;
import com.microsoft.office365.lists.SPList;
import com.microsoft.office365.lists.SPListItem;
import com.microsoft.campaignmanager.datasource.SharePointListsClientWithUsers;

/**
 * The Class CampaignItemsDataSource.
 */
public class CampaignItemsDataSource {

	/** The m application. */
	private CampaignApplication mApplication;

	/**
	 * Instantiates a new list items data source.
	 *
	 * @param application the application
	 */
	public CampaignItemsDataSource(CampaignApplication application) {
		mApplication = application;
	}

	/**
	 * Gets the lists client.
	 *
	 * @return the lists client
	 */
	private SharePointListsClientWithUsers getListsClient() {
		return mApplication.getCurrentListClient();

	}

	/**
	 * Returns a ArrayList<CampaignListViewItem> with the list item information of a campaign.
	 *
	 * @return ArrayList<CampaignListViewItem>
	 * @throws Exception the exception
	 */
	public ArrayList<CampaignListViewItem> getDefaultListViewItems() throws Exception {
		final ArrayList<CampaignListViewItem> items = new ArrayList<CampaignListViewItem>();

		final SharePointListsClientWithUsers client = (SharePointListsClientWithUsers) getListsClient();

		SparseArray<String> approverNames = new SparseArray<String>();
		Integer approverId;
		String approverName;

		final String listName = mApplication.getPreferences().getLibraryName();
		int topCount = mApplication.getPreferences().getListDisplaySize();

		//Sharepoint list columns we want to query
		String[] columns = mApplication.getApplicationContext().getResources()
				.getStringArray(R.array.visibleListColumns);

		//Get the list of items from a given sharepoint list name. 
		//We do a projection (select) and top (OData operators) in order to retrieve the lists.
		//We call get(), a blocking operation but since this call is being called 
		//from an async task we are not freezing the UI thread
		List<SPListItem> listItems = client.getListItems(listName,
				new Query().select(columns).top(topCount)).get();

		// User fields in list items contain the user id
		// For the view we need the display name
		// the following code will retrieve the user name for a given id
		// and cache it to avoid round trips for the same ids
		for (final SPListItem campaignItem : listItems) {
			approverId = (Integer) campaignItem.getData("VP_x0020_ApproverId");
			if(!approverNames.get(approverId, "not found").equalsIgnoreCase("not found")){
				approverName = approverNames.get(approverId);
			} else{
				approverName = getUserInfo(approverId).getTitle();
				approverNames.put(approverId, approverName);
			}
			items.add(new CampaignListViewItem(campaignItem, approverName));
		}

		return items;
	}

	public ArrayList<CampaignListViewItem> getCampaignsByStatus(String status) throws Exception {
		final ArrayList<CampaignListViewItem> items = new ArrayList<CampaignListViewItem>();

		final SharePointListsClientWithUsers client = (SharePointListsClientWithUsers) getListsClient();

		SparseArray<String> approverNames = new SparseArray<String>();
		Integer approverId;
		String approverName;

		final String listName = mApplication.getPreferences().getLibraryName();
		int topCount = mApplication.getPreferences().getListDisplaySize();

		//Sharepoint list columns we want to query
		String[] columns = mApplication.getApplicationContext().getResources()
				.getStringArray(R.array.visibleListColumns);

		//Get the list of items from a given sharepoint list name. 
		//We do a projection (select) and top (OData operators) in order to retrieve the lists.
		//We call get(), a blocking operation but since this call is being called 
		//from an async task we are not freezing the UI thread
		List<SPListItem> listItems = client.getListItems(listName,
				new Query().select(columns).field("Status").eq().val(status).top(topCount)).get();

		// User fields in list items contain the user id
		// For the view we need the display name
		// the following code will retrieve the user name for a given id
		// and cache it to avoid round trips for the same ids
		for (final SPListItem campaignItem : listItems) {
			approverId = (Integer) campaignItem.getData("VP_x0020_ApproverId");
			if(!approverNames.get(approverId, "not found").equalsIgnoreCase("not found")){
				approverName = approverNames.get(approverId);
			} else{
				approverName = getUserInfo(approverId).getTitle();
				approverNames.put(approverId, approverName);
			}
			items.add(new CampaignListViewItem(campaignItem, approverName));
		}

		return items;
	}

	/**
	 * Update selected campaign.
	 *
	 * @param campaignViewItem the campaign view item
	 * @throws Exception the exception
	 */
	public void updateSelectedCampaign(CampaignListViewItem campaignViewItem) throws Exception {
		updateCampaignData(campaignViewItem);
	}


	/**
	 * Update campaign data.
	 *
	 * @param campaignViewItem the campaign view item
	 * @throws Exception the exception
	 */
	private void updateCampaignData(CampaignListViewItem campaignViewItem) throws Exception {
		SharePointListsClientWithUsers client = getListsClient();
		String listName = mApplication.getPreferences().getLibraryName();
		//get the campaign list
		SPList carList = client.getList(listName).get();
		SPListItem item = campaignViewItem.getListItem();
		//updates the list item from the view model
		client.updateListItem(item, carList).get();
	}

	/**
	 * Delete campaign.
	 *
	 * @param mCampaignViewItem the m campaign view item
	 */
	public void deleteCampaign(CampaignListViewItem mCampaignViewItem) {
		SharePointListsClientWithUsers client = getListsClient();
		String listName = mApplication.getPreferences().getLibraryName();

		try {
			//we delete a given list item from a list.
			client.deleteListItem(mCampaignViewItem.getListItem(), listName).get();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public SPUser getUserInfo(Integer userId) {
		SharePointListsClientWithUsers client = getListsClient();

		try {
			//we delete a given list item from a list.
			SPUser userInfo = client.getUserById(userId).get();
			return userInfo;
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}

	public void createCampaignList() {
		SharePointListsClientWithUsers client = getListsClient();
		String listName = mApplication.getPreferences().getLibraryName();
		try {
			//check if list exists, if it does, getList will throw an exception
			client.getList(listName).get();
			Log.w("CampaignList", "List with the name " + listName + " already exists. Skipping list creation.");
		} catch (Exception e) {
			try {
				//we delete a given list item from a list.
				client.createDefaultCustomList(listName).get();
				client.addListField(listName, "Description", "Note", false, false).get();
				client.addListField(listName, "Status", "Text", false, false).get();
				client.addListField(listName, "VP Approver", "User", false, false).get();
				client.addListField(listName, "Site", "Url", false, false).get();
			} catch (Throwable t) {
				t.printStackTrace();
			}
			populateSampleData();
		}
	}

	public void createCampaign(CampaignListViewItem campaign) {
		SharePointListsClientWithUsers client = getListsClient();
		String listName = mApplication.getPreferences().getLibraryName();
		try {
			SPList campaignList = client.getList(listName).get();
			client.insertListItem(campaign.getListItem(), campaignList).get();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private void populateSampleData() {
		CampaignListViewItem campaign1 = new CampaignListViewItem();
		campaign1.setCampaignTitle("Back to School");
		campaign1.setCampaignDescription("This campaign will highlight the smart and fun new devices for students that let them work and play while looking cool.");
		campaign1.setCampaignStatus("Pending");
		campaign1.setApproverId(mApplication.getCurrentUser().getId());
		createCampaign(campaign1);

		CampaignListViewItem campaign2 = new CampaignListViewItem();
		campaign2.setCampaignTitle("Office 365 API Preview");
		campaign2.setCampaignDescription("Highlight the new APIs that allow developers to build native apps with access to Office 365 data.");
		campaign2.setCampaignStatus("Approved");
		campaign2.setApproverId(mApplication.getCurrentUser().getId());
		//Url types are not yet supported by SPList
		//campaign2.setCampaignSite("http://msdn.microsoft.com/en-us/office", "Office 365 API Preview");
		createCampaign(campaign2);

		CampaignListViewItem campaign3 = new CampaignListViewItem();
		campaign3.setCampaignTitle("Energy Efficient TVs");
		campaign3.setCampaignDescription("Highlight the benefits of the new generation of highly energy efficient flat screen tvs.");
		campaign3.setCampaignStatus("Pending");
		campaign3.setApproverId(mApplication.getCurrentUser().getId());
		createCampaign(campaign3);
	}
}



