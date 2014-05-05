package com.microsoft.campaignmanager.datasource;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.util.Log;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.office365.Constants;
import com.microsoft.office365.Credentials;
import com.microsoft.office365.Logger;
import com.microsoft.office365.lists.SharepointListsClient;

public class SharePointListsClientWithUsers extends SharepointListsClient {

	// FieldTypeKind maps numbers to SharePoint data types
	// for this sample we need text, note and user
	// See http://msdn.microsoft.com/en-us/library/office/microsoft.sharepoint.spfieldtype%28v=office.15%29.aspx 
	// for the full list of data type mappings
	final Integer SPFIELDTYPE_TEXT = 2;
	final Integer SPFIELDTYPE_NOTE = 3;
	final Integer SPFIELDTYPE_URL = 11;
	final Integer SPFIELDTYPE_USER = 20;
			
	public SharePointListsClientWithUsers(String serverUrl,
			String siteRelativeUrl, Credentials credentials) {
		super(serverUrl, siteRelativeUrl, credentials);
	}

	public SharePointListsClientWithUsers(String serverUrl, String siteRelativeUrl, Credentials credentials,
			Logger logger) {
		super(serverUrl, siteRelativeUrl, credentials, logger);
	}

	public ListenableFuture<SPUser> getUserById(Integer id) {
		final SettableFuture<SPUser> result = SettableFuture.create();

		String url = getSiteUrl() + "/_api/web/getUserById(" + id.toString() + ")";

		ListenableFuture<JSONObject> request = executeRequestJson(url, "GET");
		Futures.addCallback(request, new FutureCallback<JSONObject>() {

			@Override
			public void onSuccess(JSONObject obj){
				try {
				SPUser userInfo = new SPUser();
				userInfo.loadFromJson(obj);
				result.set(userInfo);
				} catch (Exception e) {
					Log.e("SDKExtension", e.getMessage());
				}
			}

			@Override
			public void onFailure(Throwable error) {
				Log.e("SDKExtension", error.getMessage());
				result.setException(error);
			}
		});

		return result;
	}

	public ListenableFuture<SPUser> getCurrentUser() {
		final SettableFuture<SPUser> result = SettableFuture.create();

		String url = getSiteUrl() + "/_api/web/currentUser";

		ListenableFuture<JSONObject> request = executeRequestJson(url, "GET");
		Futures.addCallback(request, new FutureCallback<JSONObject>() {

			@Override
			public void onSuccess(JSONObject obj){
				try {
					SPUser userInfo = new SPUser();
					userInfo.loadFromJson(obj);
					result.set(userInfo);
				} catch (Exception e) {
					Log.e("SDKExtension", e.getMessage());
				}

			}

			@Override
			public void onFailure(Throwable error) {
				Log.e("SDKExtension", error.getMessage());
				result.setException(error);
			}
		});
		return result;
	}

	public ListenableFuture<Void> createDefaultCustomList(String listName) {
		final SettableFuture<Void> result = SettableFuture.create();

		// REST call for list creation		
		//			url: http://site url/_api/web/lists
		//				method: POST
		//				body: { '__metadata': { 'type': 'SP.List' }, 'AllowContentTypes': true, 'BaseTemplate': 100,
		//				'ContentTypesEnabled': true, 'Description': 'My list description', 'Title': 'Test' }

		String createListUrl = getSiteUrl() + "_api/web/lists";
		try {
			JSONObject payload = new JSONObject();
			JSONObject type = new JSONObject();
			type.put("type", "SP.List");
			payload.put("__metadata", type);
			payload.put("AllowContentTypes", "true");
			payload.put("BaseTemplate", "100");
			payload.put("ContentTypesEnabled", "true");
			payload.put("Description", "Marketing campaigns list created by Android SDK sample.");
			payload.put("Title", listName);

			Map<String, String> headers = new HashMap<String, String>();
			headers.put("If-Match", "*");
			headers.put("accept", "application/json");

			ListenableFuture<JSONObject> request = executeRequestJsonWithDigest(createListUrl, "POST",
					headers, getBytes(payload.toString()));

			Futures.addCallback(request, new FutureCallback<JSONObject>() {

				@Override
				public void onSuccess(JSONObject obj){
					result.set(null);
				}

				@Override
				public void onFailure(Throwable error) {
					Log.e("SDKExtension", error.getMessage());
					result.setException(error);
				}
			});

			//copyFutureHandlers(request, result);
		} catch (Throwable t) {
			result.setException(t);
		}
		return result;
	}

	public ListenableFuture<Void> addListField(String listName, String fieldName, String fieldType, boolean isRequired, boolean enforceUniqueValues) {
		final SettableFuture<Void> result = SettableFuture.create();

		// REST call for list creation		
		//			url: http://site url/_api/web/lists/getbytitle('<listname>')/Fields
		//				method: POST
		//          body: { '__metadata': { 'type': 'SP.Field' }, 'Title': '_Currency', 'FieldTypeKind':10,
		//                  'Required':false, 'EnforceUniqueValues': 'false','StaticName': '_Currency'}
		String createListUrl = getSiteUrl() + "_api/web/lists/GetByTitle('" + listName + "')/Fields";
		try {
			JSONObject payload = new JSONObject();
			JSONObject type = new JSONObject();
			type.put("type", "SP.Field");
			payload.put("__metadata", type);
			payload.put("Title", fieldName);
			payload.put("StaticName", fieldName);
			payload.put("FieldTypeKind", getFieldTypeKind(fieldType));
			payload.put("Required", isRequired);
			payload.put("EnforceUniqueValues", enforceUniqueValues);

			Map<String, String> headers = new HashMap<String, String>();
			headers.put("If-Match", "*");
			headers.put("accept", "application/json");

			ListenableFuture<JSONObject> request = executeRequestJsonWithDigest(createListUrl, "POST",
					headers, getBytes(payload.toString()));

			Futures.addCallback(request, new FutureCallback<JSONObject>() {

				@Override
				public void onSuccess(JSONObject obj){
					result.set(null);
				}

				@Override
				public void onFailure(Throwable error) {
					Log.e("SDKExtension", error.getMessage());
					result.setException(error);
				}
			});

			//copyFutureHandlers(request, result);
		} catch (Throwable t) {
			result.setException(t);
		}
		return result;
	}

	private byte[] getBytes(String s) {
		try {
			return s.getBytes(Constants.UTF8_NAME);
		} catch (UnsupportedEncodingException e) {
			return s.getBytes();
		}
	}
	
	private Integer getFieldTypeKind(String fieldType) {
		if(fieldType.equalsIgnoreCase("text"))
			return SPFIELDTYPE_TEXT;
		if(fieldType.equalsIgnoreCase("note"))
			return SPFIELDTYPE_NOTE;
		if(fieldType.equalsIgnoreCase("user"))
			return SPFIELDTYPE_USER;
		if(fieldType.equalsIgnoreCase("url"))
			return SPFIELDTYPE_URL;
		// there are many more
		// by default return text
		return SPFIELDTYPE_TEXT;
	}
}
