package com.tincan.telepathiccaller;

import com.tincan.telepathiccaller.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class EditPreferences extends PreferenceActivity {
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    addPreferencesFromResource(R.xml.preferences);
	  }
}
