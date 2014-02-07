package se.chalmers.proofofconceptlj;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.*;
import android.location.*;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity {
	private static final int FX_01 = 1;
	private static final int LOOP = -1;

	private HashMap<Integer, Integer> soundPoolMap;
	private SoundPool soundPool;

	private AudioManager am;

	private double panning;
	private int id;

	private Button playFX;
	private Button sweepFX;
	private Button stopLoop;
	private SeekBar seekBar;
	private SeekBar seekBar2;

	/**
	 * True if sound is loaded correctly.
	 */
	private boolean loaded = false;

	final static String TAG = "PAAR";
	SensorManager sensorManager;
	ImageView arrow2;
	// int orientationSensor;
	// float headingAngle;
	// float pitchAngle;
	// float rollAngle;

	LocationManager locationManager;

	Location source;
	Human human;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Stolen from agumented reality on the Andorid platform pp.23:
		// sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		// orientationSensor = Sensor.TYPE_ORIENTATION;
		// sensorManager.registerListener(sensorEventListener,
		// sensorManager.getDefaultSensor(orientationSensor),
		// SensorManager.SENSOR_DELAY_NORMAL);

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				2000, 2, locationListener);

		arrow2 = (ImageView) this.findViewById(R.id.imageView2);

		// Initialize audio
		initSound();
		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

	}

	/*
	 * final SensorEventListener sensorEventListener = new SensorEventListener()
	 * { public void onSensorChanged(SensorEvent sensorEvent) { if
	 * (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) { float
	 * prevHeading = headingAngle; //Store the old value to rotate arrow
	 * 
	 * headingAngle = sensorEvent.values[0]; pitchAngle = sensorEvent.values[1];
	 * rollAngle = sensorEvent.values[2]; Log.d(TAG, "Heading: " +
	 * String.valueOf(headingAngle)); Log.d(TAG, "Pitch: " +
	 * String.valueOf(pitchAngle)); Log.d(TAG, "Roll: " +
	 * String.valueOf(rollAngle));
	 * 
	 * 
	 * //message("Pitch: " + String.valueOf(pitchAngle)); //message("Roll: " +
	 * String.valueOf(rollAngle)); printOrientation("Heading: " +
	 * String.valueOf(headingAngle) + "\nPitch: " + String.valueOf(pitchAngle) +
	 * "\nRoll: " + String.valueOf(rollAngle));
	 * 
	 * if(usingCompass()) { human.setRotation(headingAngle); } //Rotate arrow:
	 * RotateAnimation anim = new RotateAnimation( prevHeading, (float)
	 * human.getRotation(), Animation.RELATIVE_TO_SELF, 0.5f,
	 * Animation.RELATIVE_TO_SELF, 0.5f); anim.setDuration(210);
	 * anim.setFillAfter(true); arrow2.startAnimation(anim); } } public void
	 * onAccuracyChanged (Sensor senor, int accuracy) { //Not used }
	 * 
	 * 
	 * };
	 */

	LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {

			printLocation("Latitude: "
					+ location.getLatitude()
					+ "Longitude: "
					+ location.getLongitude()
					+ "Altitude: "
					+ location.getAltitude()
					+ "Distance: "
					+ location.distanceTo(source)
					+ "Bearing: "
					+ (location.hasBearing() ? location.bearingTo(source)
							: "no bearing"));

			human.setLocation(location);

			pointArrowToSource();
		}

		public void onProviderDisabled(String argo) {
			// TODO Auto-generated method stub
		}

		public void onProviderEnabled(String argo) {
			// TODO Auto-generated method stub
		}

		public void onStatusChanged(String argO, int argl, Bundle arg2) {
			// TODO Auto-generated method stub
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		// sensorManager.registerListener(sensorEventListener, sensorManager
		// .getDefaultSensor(orientationSensor),
		// SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	public void onPause() {
		// sensorManager.unregisterListener(sensorEventListener);
		super.onPause();
	}

	// End of stolen

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void pointArrowToSource() {
		ImageView arrow = (ImageView) this.findViewById(R.id.imageView3);
		arrow.setRotation(human.getLocation().bearingTo(source));
	}

	private boolean usingCompass() {
		CheckBox checkBox = (CheckBox) this.findViewById(R.id.checkBox_compass);
		return checkBox.isChecked();
	}

	public void playAtCoordinate() {

	}

	public void playSound(View view) {
		if (loaded)
			soundPool.play(FX_01, 1, 1, 1, 0, 1);
	}

	public void setCurrentAsSource(View view) {
		this.source = this.human.getLocation();

		TextView textLongitude = (TextView) this
				.findViewById(R.id.textView_sourceLongitude);
		TextView textLatitude = (TextView) this
				.findViewById(R.id.textView_sourceLatitude);

		textLongitude
				.setText("Source longitude: " + this.source.getLongitude());
		textLatitude.setText("Source latitude: " + this.source.getLatitude());
	}

	private void printOrientation(String s) {
		// Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

		TextView debugText = (TextView) this.findViewById(R.id.textView_debug);

		debugText.setText(s);
		debugText.invalidate();
	}

	private void printLocation(String s) {
		// Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

		TextView debugText = (TextView) this.findViewById(R.id.textView_GPS);

		debugText.setText(s);
		debugText.invalidate();
	}

	/** SOUND **/

	/**
	 * Initialize sound engine
	 */
	@SuppressLint("UseSparseArrays")
	private void initSound() {
		soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
		soundPoolMap = new HashMap<Integer, Integer>();
		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId,
					int status) {
				loaded = true;
			}
		});

		// Load FX
		soundPoolMap.put(FX_01, soundPool.load(this, R.raw.sound, 1));
	}

	public void setPanning(int soundID) {
		// 0 - all left, pi/2 all right
		double radian = panning;
		float leftVolume = (float) Math.cos(radian);
		float rightVolume = (float) Math.sin(radian);

		soundPool.play(soundID, leftVolume, rightVolume, 1, 0, 1f);
	}

	/**
	 * Take from http://alvinalexander.com/java/
	 * jwarehouse/android/media/tests/SoundPoolTest/
	 * src/com/android/SoundPoolTest.java.shtml
	 * 
	 * @throws InterruptedException
	 */
	public void testPanning(int soundID) throws InterruptedException {
		int id = soundPool.play(soundID, 0f, 1f, 1, LOOP, 1f);

		for (int count = 0; count < 101; count++) {
			Thread.sleep(20);
			double radians = (Math.PI / 2.0) * count / 100.0;
			float leftVolume = (float) Math.sin(radians);
			float rightVolume = (float) Math.cos(radians);
			soundPool.setVolume(id, leftVolume, rightVolume);
		}

		soundPool.stop(id);
	}

}