package se.chalmers.proofofconceptlj;

import com.google.android.gms.maps.model.LatLng;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainActivity extends Activity {
	Human human;

	private SeekBar repFreq;
	private SeekBar panning;

	// Handles all form of audio
	FXHandler fx;

	float degreesToDestination;
	float distance;

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		human = new Human(new LatLng(58.489657, 13.777925)); //Set human to be at Marcus's home


		// Initialize audio
		(fx = new FXHandler()).initSound(this);

		repFreq = (SeekBar) findViewById(R.id.seekBarVolume);
		repFreq.setMax(Constants.MAX_DISTANCE);
		repFreq.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				distance = arg1;
				fx.update(fx.getCowbell(), degreesToDestination, distance);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
		});

		panning = (SeekBar) findViewById(R.id.seekBarPanning);
		panning.setMax(360);
		panning.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				degreesToDestination = arg1 - 180;
				fx.update(fx.getCowbell(), degreesToDestination, distance);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

		});
	}



	// End of stolen

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	public void playSound(View view) throws InterruptedException {
		if (!fx.getCowbell().isPlaying())
			fx.loop(fx.getCowbell());
		else  {
			fx.stopHandler();
		}
	}

	public void stopSound(View view) throws InterruptedException {
		fx.stopHandler();
	}

	/** Called when the user clicks the Map button */
	public void mapButton(View view) {
		Intent intent = new Intent(this, MapDirection.class);
		startActivity(intent);
	}
}