package com.microsoft.campaignmanager.datasource;

import java.nio.charset.Charset;

public class GraphConstants {
	
	/**
	 * HTTP GET Verb
	 */
	public static final String HTTP_GET = "GET";
	
	/**
	 * HTTP POST Verb
	 */
	public static final String HTTP_POST = "POST";
	
	/**
	 * UTF-8 Encoding name
	 */
	public static final String UTF8_NAME = "UTF-8";
	
	/**
	 * UTF-8 Charset instance
	 */
	public static final Charset UTF8 = Charset.forName(UTF8_NAME);
	
	public static final String AAD_LOGINURL = "https://login.windows.net";
	
	public static final String AAD_GRAPHURL = "https://graph.windows.net";
	
	// AAD_SetupAppId and AAD_SetupRedirectUrl are configured
	// in Azure AD owned by Microsoft.
	// On first launch, the app will display a configuration screen
	// The button "Create App in Azure AD" will utilize these values
	// to ask for consent to create an Application in the users Azure AD
	// that will in turn have access to SharePoint resources.
	// This is a privileged operation that requires Tenant Admin permissions
	// If you are uncomfortable giving a Microsoft app management permissions
	// on your tenant's Azure AD, you can manually configure an Application
	// as described in the setup instructions
	
	//TODO set this to a client ID in a Microsoft Azure tenant
	public static final String AAD_SetupAppId = "0ac9da4c-4fef-4738-aab6-5745596ff44f";
	public static final String AAD_SetupRedirectUrl = "http://localhost";

}