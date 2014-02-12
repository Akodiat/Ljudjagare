package se.chalmers.proofofconceptlj;


import android.app.Activity;
import android.content.Intent;
import android.hardware.*;
import android.location.*;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.*;
import android.widget.*;

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

	// Handles all form of audio
	FXHandler fx;
	int streamID;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//Stolen from augmented reality on the Android platform pp.23:
		sensorManager 		= (SensorManager) getSystemService(SENSOR_SERVICE);
		orientationSensor 	= Sensor.TYPE_ORIENTATION;
		sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(orientationSensor), SensorManager.SENSOR_DELAY_NORMAL);

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 2, locationListener);
		human = new Human(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));

		arrow2 = (ImageView) this.findViewById(R.id.imageView2);
		
		streamID = -1;

		// Initialize audio
		(fx = new FXHandler()).initSound(this);
	}
	final SensorEventListener sensorEventListener = new SensorEventListener() {
		public void onSensorChanged(SensorEvent sensorEvent) {
			if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
				float prevHeading = headingAngle; //Store the old value to rotate arrow

				headingAngle 	= sensorEvent.values[0];
				pitchAngle 		= sensorEvent.values[1];
				rollAngle 		= sensorEvent.values[2];
				//				Log.d(TAG, "Heading: " + String.valueOf(headingAngle));
				//				Log.d(TAG, "Pitch: " + String.valueOf(pitchAngle));
				//				Log.d(TAG, "Roll: " + String.valueOf(rollAngle));


				//message("Pitch: " + String.valueOf(pitchAngle));
				//message("Roll: " + String.valueOf(rollAngle));
				printOrientation("Heading: " + String.valueOf(headingAngle) + 
						"\nPitch: " + String.valueOf(pitchAngle) + 
						"\nRoll: " + String.valueOf(rollAngle));

				//				if(usingCompass()) {
				//					human.setRotation(headingAngle);
				//				}
				if(source != null){
					pointArrowToSource_C();
					if(streamID != -1)
						fx.setPanning(streamID, (headingAngle + human.getLocation().bearingTo(source)), human.getLocation().distanceTo(source));
				}
			}
		}
		public void onAccuracyChanged (Sensor senor, int accuracy) {
			//Not used
		}


	};

	LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			printLocation(	
					"Latitude: " 	+ location.getLatitude() + 
					"\nLongitude: " 	+ location.getLongitude() + 
					"\nAltitude: " 	+ location.getAltitude() + 
					(source == null ? "No source set" : (
							"\nDistance: " + location.distanceTo(source) + 
							"\nBearing: " + (location.hasBearing() ? location.bearingTo(source) : "no bearing")
							)
							)
					);

			human.setLocation(location);

			if(source != null)
				pointArrowToSource_G();
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
		sensorManager.registerListener(sensorEventListener, sensorManager
				.getDefaultSensor(orientationSensor), SensorManager.SENSOR_DELAY_NORMAL);
	}
	@Override
	public void onPause() {
		sensorManager.unregisterListener(sensorEventListener);
		super.onPause();
	}


	//End of stolen

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
		arrow.setRotation(human.getLocation().getBearing() + human.getLocation().bearingTo(source));
	}

	/**
	 * Points arrow to source using compass orientation
	 */
	private void pointArrowToSource_C() {
		ImageView arrow = (ImageView) this.findViewById(R.id.imageView2);
		arrow.setRotation(headingAngle + human.getLocation().bearingTo(source));
	}

	private boolean usingCompass() {
		CheckBox checkBox = (CheckBox) this.findViewById(R.id.checkBox_compass);
		return checkBox.isChecked();
	}

	public void playSound(View view) {
		if(this.streamID == -1) {
			this.streamID = fx.playFX(FXHandler.FX_01);
		}
		else {
			fx.stopFX(streamID);
			this.streamID = -1;
		}
	}

	public void setCurrentAsSource(View view) {
		this.source = this.human.getLocation();

		TextView textLongitude = (TextView) this.findViewById(R.id.textView_sourceLongitude);
		TextView textLatitude = (TextView) this.findViewById(R.id.textView_sourceLatitude);

		textLongitude.setText("Source longitude: " + this.source.getLongitude());
		textLatitude.setText(  "Source latitude: " + this.source.getLatitude());
	}

	private void printOrientation(String s) {
		//Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

		TextView debugText = (TextView) this.findViewById(R.id.textView_debug);

		debugText.setText(s);
		debugText.invalidate();
	}
	private void printLocation(String s) {
		//Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

		TextView debugText = (TextView) this.findViewById(R.id.textView_GPS);

		debugText.setText(s);
		debugText.invalidate();
	}
	
	/** Called when the user clicks the Map button */
	public void mapButton(View view) {
		Intent intent = new Intent(this, MapDirection.class);
		startActivity(intent);
		
	}
	
	


}
