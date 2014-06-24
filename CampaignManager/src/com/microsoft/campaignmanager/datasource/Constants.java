package com.microsoft.campaignmanager.datasource;

import java.nio.charset.Charset;

public class Constants {
	
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
}