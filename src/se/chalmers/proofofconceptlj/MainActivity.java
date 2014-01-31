package se.chalmers.proofofconceptlj;


import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	final static String TAG = "PAAR";
	SensorManager sensorManager;
	ImageView arrow2;
	int orientationSensor;
	float headingAngle;
	float pitchAngle;
	float rollAngle;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//Stolen from agumented reality on the Andorid platform pp.23:
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		orientationSensor = Sensor.TYPE_ORIENTATION;
		sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(orientationSensor), SensorManager.SENSOR_DELAY_NORMAL);
		this.arrow2 = (ImageView) this.findViewById(R.id.imageView2);
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
				message("Heading: " + String.valueOf(headingAngle) + 
						"\nPitch: " + String.valueOf(pitchAngle) + 
						 "\nRoll: " + String.valueOf(rollAngle));
				
				
				//Rotate arrow:
				RotateAnimation anim = new RotateAnimation(
						prevHeading, 
						rollAngle,
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

		message("L:"+ left + "\tR:" + right);





		while(mediaPlayer.isPlaying()); //Stops thread until done playing (fulhack)

		mediaPlayer.release();
	}
	/**
	 * Makes the sound source move along the parable y=x² starting from -x
	 * @param x horizontal distance to sound at beginning
	 */
	public void driveByFrom(int x) {
		for(int i=-x; i<x; i++) {
			playAtCoordinate(new Vector2(i,Math.pow(i, 2)), new Human()); // follow parable y=x²
		}
	}

	public void playGivenCoordinate(View view) {
		//AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		//Read x and y (sound source) coordinates
		EditText ETx = (EditText) this.findViewById(R.id.editText_x);
		EditText ETy = (EditText) this.findViewById(R.id.editText_y);

		//Create position vector of sound source
		Vector2 soundPos = new Vector2(
				Integer.parseInt(ETx.getText().toString()),
				Integer.parseInt(ETy.getText().toString()));

		//Plays sound at sound source
		playAtCoordinate(soundPos, new Human());

	}

	public void playDriveBy(View view) {
		EditText ETd = (EditText) this.findViewById(R.id.editText_driveBy);
		int d = Integer.parseInt(ETd.getText().toString());

		driveByFrom(d);
	}

	public void playFromAngle(View view) {
		//EditText ETd = (EditText) this.findViewById(R.id.editText_angle);
		//double d = (double) Integer.parseInt(ETd.getText().toString());
		//double r = (d/180)*Math.PI; //Conversion from degrees to radians;

		Human orientatedHuman = new Human(Vector2.zero(), this.headingAngle);
		playAtCoordinate(new Vector2(0, 20), orientatedHuman);
	}

	public void message(String s) {
		//Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

		TextView debugText = (TextView) this.findViewById(R.id.textView_debug);

		debugText.setText(s);
		debugText.invalidate();
	}


}
