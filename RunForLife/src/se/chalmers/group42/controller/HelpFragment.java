package se.chalmers.group42.controller;

import se.chalmers.group42.runforlife.FXHandler;
import se.chalmers.group42.runforlife.R;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.SeekBar;

public class HelpFragment extends Fragment{

	private FXHandler fx;
	private Button correctDirectionButton, hundredMetersButton, frequencyButton, 
		leftButton, rightButton, wrongDirectionButton, coinPickButton, newCoinButton;
	private SeekBar frequencySeekbar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("Fragment", "Help Fragment created");
		View rootView = inflater.inflate(R.layout.fragment_help,
				container, false);

		// Initialize audio
		(fx = new FXHandler()).initSound(getActivity());
		
		//Setting up buttons
		correctDirectionButton = (Button) rootView.findViewById(R.id.button1);
		
		//Button actions
		correctDirectionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				fx.loop(fx.getNavigationFX());
			}
		});

		return rootView;
	}
}
