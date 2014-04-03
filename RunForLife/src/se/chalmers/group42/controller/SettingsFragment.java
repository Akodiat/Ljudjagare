package se.chalmers.group42.controller;

import se.chalmers.group42.runforlife.R;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 * 
 * Class handling preferences of the app.
 * 
 * @version
 * 
 *          0.1 4 April 2014
 * @author
 * 
 *         Anton Palmqvist
 * 
 */
public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        
        //Setting up listener for preferences
        getPreferenceScreen().getSharedPreferences()
        .registerOnSharedPreferenceChangeListener(this);
        
        //Initializing distance summary text
        String distance = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getString("distance", "");
        Preference distancePref = (Preference) findPreference("distance");
        distancePref.setSummary(distance + " meters interval between each checkpoint");
    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    	//Changing distance summary text
        String distance = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getString("distance", "");
        Preference distancePref = (Preference) findPreference("distance");
        distancePref.setSummary(distance + " meters interval between each checkpoint");
    }
}