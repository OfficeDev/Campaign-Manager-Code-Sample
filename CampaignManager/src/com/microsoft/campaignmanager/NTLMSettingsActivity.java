/*******************************************************************************
 * Copyright (c) Microsoft Open Technologies, Inc.
 * All Rights Reserved
 * See License.txt in the project root for license information. 
 ******************************************************************************/
package com.microsoft.campaignmanager;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

// TODO: Auto-generated Javadoc
/**
 * The Class NTLMSettingsActivity.
 */
public class NTLMSettingsActivity extends PreferenceActivity  {

	/* (non-Javadoc)
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstance) {

		super.onCreate(savedInstance);
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new PrefsFragment(), null).commit();
		PreferenceManager.setDefaultValues(this, R.xml.auth_settings, false);
	}

	/**
	 * The Class PrefsFragment.
	 */
	public static class PrefsFragment extends PreferenceFragment {

		/* (non-Javadoc)
		 * @see android.preference.PreferenceFragment#onCreate(android.os.Bundle)
		 */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.ntlm_settings);
		}
	}
}
