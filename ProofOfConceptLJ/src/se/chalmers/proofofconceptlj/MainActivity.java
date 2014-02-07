package se.chalmers.proofofconceptlj;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.*;
import android.location.*;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.*;
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;

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
	int orientationSensor;
	float headingAngle;
	float pitchAngle;
	float rollAngle;

	LocationManager locationManager;
	double latitude;
	double longitude;
	double altitude;

	double sourceLatitude;
	double sourceLongitude;
	Human human;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Stolen from augmented reality on the Android platform, p.23
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		orientationSensor = Sensor.TYPE_ORIENTATION;
		sensorManager.registerListener(sensorEventListener,
				sensorManager.getDefaultSensor(orientationSensor),
				SensorManager.SENSOR_DELAY_NORMAL);

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				2000, 2, locationListener);

		human = new Human();

		// Initialize audio
		initSound();

		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		playFX = (Button) findViewById(R.id.fx1);
		playFX.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				id = soundPool.play(FX_01, 1f, 1f, 1, LOOP, 1f);
			}
		});

		sweepFX = (Button) findViewById(R.id.sweep_fx1);
		sweepFX.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					testPanning(FX_01);
				} catch (InterruptedException e) {
					// Need to catch eventual exception thrown by testPanning.
					e.printStackTrace();
				}
			}
		});

		stopLoop = (Button) findViewById(R.id.stop_loop);
		stopLoop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				soundPool.stop(id);
			}
		});

		seekBar = (SeekBar) findViewById(R.id.seekBar1);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				panning = Math.PI / 2 * (progress) / 100;
				soundPool.setVolume(id, (float) Math.cos(panning),
						(float) Math.sin(panning));
			}
		});

		seekBar2 = (SeekBar) findViewById(R.id.seekBar2);
		seekBar2.setProgress(am.getStreamVolume(AudioManager.STREAM_MUSIC));
		seekBar2.setMax(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		seekBar2.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// Toast.makeText(getApplicationContext(),
				// "Volume: " + Integer.toString(Volume),
				// Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
			}
		});
	}

	final SensorEventListener sensorEventListener = new SensorEventListener() {
		public void onSensorChanged(SensorEvent sensorEvent) {
			if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
				float prevHeading = headingAngle; // Store the old value to
													// rotate arrow

				headingAngle = sensorEvent.values[0];
				pitchAngle = sensorEvent.values[1];
				rollAngle = sensorEvent.values[2];
				Log.d(TAG, "Heading: " + String.valueOf(headingAngle));
				Log.d(TAG, "Pitch: " + String.valueOf(pitchAngle));
				Log.d(TAG, "Roll: " + String.valueOf(rollAngle));

				// message("Pitch: " + String.valueOf(pitchAngle));
				// message("Roll: " + String.valueOf(rollAngle));
				printOrientation("Heading: " + String.valueOf(headingAngle)
						+ "\nPitch: " + String.valueOf(pitchAngle) + "\nRoll: "
						+ String.valueOf(rollAngle));

				if (usingCompass()) {
					human.setRotation(headingAngle);
				}
				// Rotate arrow:
				RotateAnimation anim = new RotateAnimation(prevHeading,
						(float) human.getRotation(),
						Animation.RELATIVE_TO_SELF, 0.5f,
						Animation.RELATIVE_TO_SELF, 0.5f);
				anim.setDuration(210);
				anim.setFillAfter(true);
				arrow2.startAnimation(anim);
			}
		}

		public void onAccuracyChanged(Sensor senor, int accuracy) {
			// Not used
		}

	};

	LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			altitude = location.getAltitude();

			printLocation("Latitude: " + String.valueOf(latitude)
					+ "Longitude: " + String.valueOf(longitude) + "Altitude: "
					+ String.valueOf(altitude));

			human.setPosition(new Vector2(latitude, longitude));

			if (!usingCompass())
				human.setRotation(location.getBearing());
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
		sensorManager.registerListener(sensorEventListener,
				sensorManager.getDefaultSensor(orientationSensor),
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	public void onPause() {
		sensorManager.unregisterListener(sensorEventListener);
		super.onPause();
	}

	// End of stolen

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private boolean usingCompass() {
		CheckBox checkBox = (CheckBox) this.findViewById(R.id.checkBox_compass);
		return checkBox.isChecked();
	}

	public void playAtCoordinate(Vector2 coord, Human human) {
		// Initiate mediaPlayer with dragon roar ^^
		MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.dragon);

		double someConstant = 0.5; // <-- Edit this (has to be between 0 and 1)

		// Volume for each ear is inverse proportional to the distance to the
		// sound source
		float left = (float) (1 / (Vector2.distance(coord,
				human.getLeftEarPos()) * someConstant));
		float right = (float) (1 / (Vector2.distance(coord,
				human.getRightEarPos()) * someConstant));

		mediaPlayer.setVolume(left, right); // TODO: has to be in interval (0 <=
											// left&right <= 1)

		// mediaPlayer.setLooping(true);
		mediaPlayer.start(); // no need to call prepare(); create() does that
								// for you

		printOrientation("L:" + left + "\tR:" + right);

		while (mediaPlayer.isPlaying())
			; // Stops thread until done playing (fulhack)

		mediaPlayer.release();
	}

	public void playSound() {
		playAtCoordinate(new Vector2(sourceLongitude, sourceLatitude), human);
	}

	public void playFromAngle() {
		float angle = (float) Vector2.angle(human.getPosition(), new Vector2(longitude,latitude));
		angle += human.getRotation();
		
		if (angle >= 90 && angle <= 180) 
			soundPool.setVolume(id, 1f, 0f);
		
		if (angle >= 180 && angle <= 270) 
			soundPool.setVolume(id, 0f, 1f);
		
		
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

	public void playFX(int soundID) {
		// Play sound
		if (loaded)
			soundPool.play(soundID, 1f, 1f, 1, 0, 1f);
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
