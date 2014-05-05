package com.microsoft.campaignmanager.datasource;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.microsoft.office365.OfficeEntity;

public class SPUrl extends OfficeEntity {

	public static List<SPUrl> listFromJson(JSONObject json) throws JSONException {
		return OfficeEntity.listFromJson(json, SPUrl.class);
	}

	public SPUrl() {
	}
	
	public String getDescription() {
		return (String) getData("description");
	}

	public String getUrl() {
		return (String) getData("url");
	}


}
