package se.chalmers.group42.gameModes;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import se.chalmers.group42.runforlife.FXHandler;
import se.chalmers.group42.runforlife.R;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class ShooterActivity extends Activity implements SensorEventListener {

	private float angle, distance, coinAngle;

	private SensorManager mSensorManager;

	private FXHandler fx;

	private boolean hasBeenAnnounced = false;

	TextView currentAngle;
	//SeekBar currentDistance;

	private Timer timer;

	private Random random;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shooter);

		currentAngle = (TextView) findViewById(R.id.currentAngle);

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		// set distance
		distance = 500;

		// generate random coin to pick up
		random = new Random();
		coinAngle = random.nextInt(180 - (-180) + 1) + (-180);
		
		// Initialize audio
		(fx = new FXHandler()).initSound(this);
		fx.loop(fx.getNavigationFX());

		// Start running
		timer = new Timer();
		timer.schedule(new RunCloser(), 0, 1000);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();

		// for the system's orientation sensor registered listeners
		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	protected void onPause() {
		super.onPause();

		// to stop the listener and save battery
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		angle = Math.round(event.values[0]);
		if (angle > 180)
			angle = angle - 360;

		//currentAngle.setText("Heading: " + Float.toString(angle) + " degrees");
		currentAngle.setText(Float.toString(distance));

		fx.update(fx.getNavigationFX(), angle, distance);

		if (isCoinFound() && !hasBeenAnnounced) {
			fx.stopLoop();
			fx.foundCoin();
			hasBeenAnnounced = true;
		}
	}

	public boolean isCoinFound() {
		return distance < 5 && Math.abs(angle - coinAngle) < 10;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// not in use
	}

	private class RunCloser extends TimerTask {
		public void run() {
			distance -= 5;
		}
	}
}
