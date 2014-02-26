package se.chalmers.proofofconceptlj;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.hardware.*;
import android.location.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainActivity extends Activity {
	final static String TAG = "PAAR";

	SensorManager sensorManager;
	ImageView arrow2;
	int orientationSensor;
	float headingAngle;
	float pitchAngle;
	float rollAngle;

	LocationManager locationManager;

	Location source;
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
	
		human = new Human(
				locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER));

		arrow2 = (ImageView) this.findViewById(R.id.imageView2);

		// Initialize audio
		(fx = new FXHandler()).initSound(this);

		repFreq = (SeekBar) findViewById(R.id.seekBarVolume);
		repFreq.setMax(50);
		repFreq.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				distance = arg1;
				fx.update(fx.cowbell(), degreesToDestination, distance);
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
				fx.update(fx.cowbell(), arg1, distance);
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

	/**
	 * Points arrow to source using GPS bearing
	 */
	private void pointArrowToSource_G() {
		ImageView arrow = (ImageView) this.findViewById(R.id.imageView3);
		arrow.setRotation(human.getLocation().getBearing()
				+ human.getLocation().bearingTo(source));
	}

	/**
	 * Points arrow to source using compass orientation
	 */
	private void pointArrowToSource_C() {
		ImageView arrow = (ImageView) this.findViewById(R.id.imageView2);
		arrow.setRotation(headingAngle + human.getLocation().bearingTo(source));
	}

	public void playSound(View view) throws InterruptedException {
		if (!fx.cowbell().isPlaying())
			fx.setPosition(fx.cowbell());
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