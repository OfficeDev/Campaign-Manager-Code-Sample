package com.microsoft.campaignmanager.datasource;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.microsoft.office365.OfficeEntity;

public class SPUser extends OfficeEntity {

	public static List<SPUser> listFromJson(JSONObject json) throws JSONException {
		return OfficeEntity.listFromJson(json, SPUser.class);
	}

	public SPUser() {
	}
	
	public int getId() {
		return (Integer) getData("Id");
	}

	public String getTitle() {
		return (String) getData("Title");
	}

	public String getEmail() {
		return (String) getData("Email");
	}

	public String getLoginName() {
		return (String) getData("LoginName");
	}
	
}
