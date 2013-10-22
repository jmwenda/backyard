package com.backyard.backyard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;


public  class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

}
/*
public class SettingsFragment extends PreferenceListFragment implements
SharedPreferences.OnSharedPreferenceChangeListener,
PreferenceListFragment.OnPreferenceAttachedListener {
public static final String SHARED_PREFS_NAME = "preferences";

@Override
public void onCreate(Bundle icicle) {

super.onCreate(icicle);
PreferenceManager preferenceManager = getPreferenceManager();
preferenceManager.setSharedPreferencesName(SHARED_PREFS_NAME);
addPreferencesFromResource(R.xml.preferences);
preferenceManager.getSharedPreferences()
        .registerOnSharedPreferenceChangeListener(this);

}

@Override
public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
    String key) {
}

@Override
public void onPreferenceAttached(PreferenceScreen root, int xmlId) {
if (root == null)
    return;
}
}*/
