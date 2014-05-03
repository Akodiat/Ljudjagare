package se.chalmers.group42.controller;

import java.util.Timer;
import java.util.TimerTask;

import se.chalmers.group42.runforlife.FXHandler;
import se.chalmers.group42.runforlife.R;
import se.chalmers.group42.runforlife.Speech;
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

	private FXHandler fxHandler;
	private Button correctDirectionButton, hundredMetersButton, frequencyButton, 
		leftButton, rightButton, wrongDirectionButton, coinPickButton, newCoinButton;
	private SeekBar frequencySeekbar;
	private Timer timer;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("Fragment", "Help Fragment created");
		View rootView = inflater.inflate(R.layout.fragment_help,
				container, false);

		// Initialize audio
		(fxHandler = new FXHandler()).initSound(getActivity());
		
		//Setting up
		correctDirectionButton = (Button) rootView.findViewById(R.id.button1);
		hundredMetersButton = (Button) rootView.findViewById(R.id.button2);
		frequencySeekbar = (SeekBar) rootView.findViewById(R.id.seekBar1);
		frequencyButton = (Button) rootView.findViewById(R.id.button3);
		leftButton = (Button) rootView.findViewById(R.id.buttonLeft);
		rightButton = (Button) rootView.findViewById(R.id.buttonRight);
		wrongDirectionButton = (Button) rootView.findViewById(R.id.buttonBehind);
		coinPickButton = (Button) rootView.findViewById(R.id.button4);
		newCoinButton = (Button) rootView.findViewById(R.id.button5);
		timer = new Timer();
		
		//Button actions
		//Correct direction sound
		correctDirectionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				fxHandler.loop(fxHandler.getNavigationFX());
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						fxHandler.stopLoop();
					}
				}, 2000);
			}
		});
		
		//Hundred meters sound
		hundredMetersButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				fxHandler.sayDistance(new Speech(2));
			}
		});
		
		
		
		

		return rootView;
	}
}
