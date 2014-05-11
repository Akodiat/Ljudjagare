package se.chalmers.group42.controller;

import se.chalmers.group42.runforlife.R;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

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
		String distance = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getString("distance", "700");
		Preference distancePref = (Preference) findPreference("distance");
		distancePref.setSummary(distance + " meter radius of the track to generate");

		//Initializing points summary text
		String points = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getString("nrPoints", "4");
		Preference pointsPref = (Preference) findPreference("nrPoints");
		pointsPref.setSummary(points + " number of checkpoints");

		//Initializing random summary text
		Boolean random = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getBoolean("route", false);
		Preference randomPref = (Preference) findPreference("route");
		randomPref.setSummary(Boolean.toString(random));

		//Initializing random summary text
		String random2 = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getString("random2", "0");
		Preference random2Pref = (Preference) findPreference("random2");
		random2Pref.setSummary(random2);
	}
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		//Changing distance summary text
		if(sharedPreferences != null){
			if(key.equals("distance")){
				String distance = sharedPreferences.getString(key, "700");
				Preference distancePref = (Preference) findPreference("distance");
				distancePref.setSummary(distance + " meter radius of the track to generate");
			} else if(key.equals("nrPoints")){
				String points = sharedPreferences.getString(key, "4");
				Preference pointsPref = (Preference) findPreference("nrPoints");
				pointsPref.setSummary(points + " number of checkpoints");
			} else if(key.equals("route")){
				boolean random = sharedPreferences.getBoolean(key, false);
				Preference randomPref = (Preference) findPreference("route");
				randomPref.setSummary(Boolean.toString(random));
			} else if(key.equals("random2")){
				String random2 = sharedPreferences.getString(key, "0");
				Preference random2Pref = (Preference) findPreference("random2");
				random2Pref.setSummary(random2);
			}
		}
		if(getActivity() != null)
			Toast.makeText(getActivity(), "Your settings have been saved", Toast.LENGTH_LONG).show();
	}
}