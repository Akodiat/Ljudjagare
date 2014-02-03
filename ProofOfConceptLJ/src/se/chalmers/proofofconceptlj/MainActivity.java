package se.chalmers.proofofconceptlj;


import android.app.Activity;
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
	double latitude;
	double longitude;
	double altitude;

	double sourceLatitude;
	double sourceLongitude;
	Human human;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//Stolen from agumented reality on the Andorid platform pp.23:
		sensorManager 		= (SensorManager) getSystemService(SENSOR_SERVICE);
		orientationSensor 	= Sensor.TYPE_ORIENTATION;
		sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(orientationSensor), SensorManager.SENSOR_DELAY_NORMAL);

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 2, locationListener);

		arrow2 = (ImageView) this.findViewById(R.id.imageView2);
		human = new Human();
	}
	final SensorEventListener sensorEventListener = new SensorEventListener() {
		public void onSensorChanged(SensorEvent sensorEvent) {
			if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
				float prevHeading = headingAngle; //Store the old value to rotate arrow

				headingAngle 	= sensorEvent.values[0];
				pitchAngle 		= sensorEvent.values[1];
				rollAngle 		= sensorEvent.values[2];
				Log.d(TAG, "Heading: " + String.valueOf(headingAngle));
				Log.d(TAG, "Pitch: " + String.valueOf(pitchAngle));
				Log.d(TAG, "Roll: " + String.valueOf(rollAngle));


				//message("Pitch: " + String.valueOf(pitchAngle));
				//message("Roll: " + String.valueOf(rollAngle));
				printOrientation("Heading: " + String.valueOf(headingAngle) + 
						"\nPitch: " + String.valueOf(pitchAngle) + 
						"\nRoll: " + String.valueOf(rollAngle));

				if(usingCompass()) {
					human.setRotation(headingAngle);
				}
				//Rotate arrow:
				RotateAnimation anim = new RotateAnimation(
						prevHeading, 
						(float) human.getRotation(),
						Animation.RELATIVE_TO_SELF, 0.5f, 
						Animation.RELATIVE_TO_SELF,
						0.5f);
				anim.setDuration(210);
				anim.setFillAfter(true);
				arrow2.startAnimation(anim);
			}
		}
		public void onAccuracyChanged (Sensor senor, int accuracy) {
			//Not used
		}


	};

	LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			latitude 	= location.getLatitude();
			longitude 	= location.getLongitude();
			altitude 	= location.getAltitude();

			printLocation(	"Latitude: " 	+ String.valueOf(latitude) + 
					"Longitude: " 	+ String.valueOf(longitude)+ 
					"Altitude: " 	+ String.valueOf(altitude));

			human.setPosition(new Vector2(latitude,longitude));

			
			if(!usingCompass())
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
	private void pointArrowToSource() {
		this.human.getRotation();
	}
	private boolean usingCompass() {
		CheckBox checkBox = (CheckBox) this.findViewById(R.id.checkBox_compass);
		return checkBox.isChecked();
	}
	public void playAtCoordinate(Vector2 coord, Human human) {
		//Initiate mediaPlayer with dragon roar ^^
		MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.dragon);

		double 	someConstant  		= 0.5;	//  <-- Edit this (has to be between 0 and 1)

		//Volume for each ear is inverse proportional to the distance to the sound source
		float left  = (float) (1 / (Vector2.distance(coord, human.getLeftEarPos())  * someConstant));
		float right = (float) (1 / (Vector2.distance(coord, human.getRightEarPos()) * someConstant));

		//Rotate arrow:
		ImageView arrow = (ImageView) this.findViewById(R.id.imageView1);

		RotateAnimation anim = new RotateAnimation(
				0, 
				(float) (-180*(human.getRotation()/Math.PI)),
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF,
				0.5f);
		anim.setDuration(210);
		anim.setFillAfter(true);
		arrow.startAnimation(anim);


		mediaPlayer.setVolume(left, right); //TODO: has to be in interval (0 <= left&right <= 1)


		//mediaPlayer.setLooping(true);
		mediaPlayer.start(); // no need to call prepare(); create() does that for you

		printOrientation("L:"+ left + "\tR:" + right);





		while(mediaPlayer.isPlaying()); //Stops thread until done playing (fulhack)

		mediaPlayer.release();
	}

	public void playSound(View view) {
		playAtCoordinate(new Vector2(sourceLongitude, sourceLatitude), human);
	}

	public void setCurrentAsSource(View view) {
		this.sourceLongitude = longitude;
		this.sourceLatitude = latitude;

		TextView textLongitude = (TextView) this.findViewById(R.id.textView_sourceLongitude);
		TextView textLatitude = (TextView) this.findViewById(R.id.textView_sourceLatitude);

		textLongitude.setText("Source longitude: " + this.sourceLongitude);
		textLatitude.setText(  "Source latitude: " + this.sourceLatitude );
	}

	public void playFromAngle(View view) {
		//EditText ETd = (EditText) this.findViewById(R.id.editText_angle);
		//double d = (double) Integer.parseInt(ETd.getText().toString());
		//double r = (d/180)*Math.PI; //Conversion from degrees to radians;

		Human orientatedHuman = new Human(Vector2.zero(), this.headingAngle);
		playAtCoordinate(new Vector2(0, 20), orientatedHuman);
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


}
