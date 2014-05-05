/*******************************************************************************
 * Copyright (c) Microsoft Open Technologies, Inc.
 * All Rights Reserved
 * See License.txt in the project root for license information. 
 ******************************************************************************/
package com.microsoft.campaignmanager;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

// TODO: Auto-generated Javadoc
/**
 * The Class AppSettingsActivity.
 */
public class AppSettingsActivity extends PreferenceActivity {


	/* (non-Javadoc)
	 * @see android.app.Activity#onAttachFragment(android.app.Fragment)
	 */
	@Override
	public void onAttachFragment(Fragment fragment) {
		super.onAttachFragment(fragment);
	}

	/* (non-Javadoc)
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AppSettingsFragment fragment = new AppSettingsFragment();

		getFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
	}

	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		startActivity(new Intent(AppSettingsActivity.this,
				MainActivity.class));
	}


	/**
	 * The Class AppSettingsFragment.
	 */
	public static class AppSettingsFragment extends PreferenceFragment {

		/** The m application. */
		//private AssetApplication mApplication;

		/* (non-Javadoc)
		 * @see android.preference.PreferenceFragment#onCreate(android.os.Bundle)
		 */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.auth_settings);
		}
	}

}
