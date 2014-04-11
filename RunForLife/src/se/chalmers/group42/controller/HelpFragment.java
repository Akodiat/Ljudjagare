package se.chalmers.group42.controller;

import se.chalmers.group42.runforlife.R;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HelpFragment extends Fragment{
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("Fragment", "Help Fragment created");
		View rootView = inflater.inflate(R.layout.help_fragment,
				container, false);
		return rootView;
	}
}
