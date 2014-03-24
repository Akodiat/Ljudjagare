package se.chalmers.group42.gameModes;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import se.chalmers.group42.runforlife.Constants;
import se.chalmers.group42.runforlife.FXHandler;
import se.chalmers.group42.runforlife.R;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Message;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class ShooterActivity extends Activity implements SensorEventListener {

	private float angle, distance, prevDistance, coinAngle;

	private SensorManager mSensorManager;

	private FXHandler fx;

	private boolean hasBeenAnnounced = false;

	TextView currentAngle;
	SeekBar currentDistance;

	private Timer timer;

	private Random random;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shooter);

		currentAngle = (TextView) findViewById(R.id.currentAngle);
		currentDistance = (SeekBar) findViewById(R.id.currentDistance);
		currentDistance.setMax(500);
		currentDistance.setProgress(500);
		
		distance = 500;
		
		currentDistance.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
               	distance = progress;
            }
 
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
 
            public void onStopTrackingTouch(SeekBar seekBar) {
            	// TODO Auto-generated method stub
            }
        });		

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		// generate random coin to pick up
		random = new Random();
		coinAngle = 0;

		// Initialize audio
		(fx = new FXHandler()).initSound(this);

		// Start running
//		timer = new Timer();
//		timer.schedule(new RunCloser(), 0, 1000);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();

		// for the system's orientation sensor registered listeners
		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_GAME);

		fx.update(fx.getNavigationFX(), angle, distance);
		fx.loop(fx.getNavigationFX());
	}

	@Override
	protected void onPause() {
		super.onPause();
		// to stop the listener and save battery
		mSensorManager.unregisterListener(this);

		Message msg = fx.getHandler().obtainMessage(Constants.MSG_STOP);
		fx.getHandler().sendMessage(msg);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		angle = Math.round(event.values[0]);
		if (angle > 180)
			angle = angle - 360;

		// currentAngle.setText("Heading: " + Float.toString(angle) +
		// " degrees");
		currentAngle.setText(Float.toString(distance) + "Angle: "
				+ Float.toString(angle));

		fx.update(fx.getNavigationFX(), angle, distance);

		if (isCoinFound() && !hasBeenAnnounced) {
			fx.stopLoop();
			fx.foundCoin();
			hasBeenAnnounced = true;
			// generateNewCoin();
		}
	}

	public boolean isCoinFound() {
		return distance < 5 && Math.abs(angle - coinAngle) < 5;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// not in use
	}

	public void generateNewCoin() {
		coinAngle = random.nextInt(180 - (-180) + 1) + (-180);
		hasBeenAnnounced = false;
	}

	private class RunCloser extends TimerTask {
		public void run() {
//			distance = -5;
//			 distance = (float) Math.sqrt((Math.pow(5, 2)
//			 + Math.pow(distance, 2) - 2 * 5 * distance
//			 * Math.cos(angle)));
		}
	}
}
