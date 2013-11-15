package org.polyml.androidpoly;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class PolySettings extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.polysettings);
	}

}
