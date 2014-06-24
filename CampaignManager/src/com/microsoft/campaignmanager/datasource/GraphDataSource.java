package com.microsoft.campaignmanager.datasource;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.microsoft.campaignmanager.CampaignApplication;

public class GraphDataSource {

	/** The m application. */
	private CampaignApplication mApplication;

	/**
	 * Instantiates a new list items data source.
	 *
	 * @param application the application
	 */
	public GraphDataSource(CampaignApplication application) {
		mApplication = application;
	}

	/**
	 * Gets the lists client.
	 *
	 * @return the lists client
	 */
	private GraphClient getGraphClient() {
		return mApplication.getCurrentGraphClient();

	}

	public List<GraphApplication> getAllApplications() {
		GraphClient client = (GraphClient) getGraphClient();
		List<GraphApplication> applications;
		try {
			applications = client.getAllApplications().get();
			return applications;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public List<GraphApplication> getApplicationByName(String displayName) {
		GraphClient client = (GraphClient) getGraphClient();
		List<GraphApplication> applications;
		try {
			applications = client.getApplicationByName(displayName).get();
			return applications;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public GraphApplication createApplication(GraphApplication application) {
		GraphClient client = (GraphClient) getGraphClient();
		try {
			return client.createApplication(application).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public GraphServicePrincipal createServicePrincipal(GraphApplication application) {
		GraphClient client = (GraphClient) getGraphClient();
		try {
			return client.createServicePrincipal(application).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}	

	public GraphPermission createPermission(GraphPermission permission) {
		// set some defaults
		
		
		GraphClient client = (GraphClient) getGraphClient();
		try {
			return client.createPermission(permission).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}	


}
