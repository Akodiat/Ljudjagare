package se.chalmers.group42.gameModes;

import se.chalmers.group42.runforlife.FXHandler;
import se.chalmers.group42.runforlife.R;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class ShooterActivity extends Activity implements SensorEventListener {

	private float angle, distance;

	private SensorManager mSensorManager;

	private FXHandler fx;

	private boolean hasBeenAnnounced = false;
	
	TextView currentAngle;
	SeekBar currentDistance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shooter);

		currentAngle = (TextView) findViewById(R.id.currentAngle);

		currentDistance = (SeekBar) findViewById(R.id.currentDistance);
		currentDistance.setMax(100);
		currentDistance
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
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

		// Initialize audio
		(fx = new FXHandler()).initSound(this);
		fx.loop(fx.getNavigationFX());
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

		currentAngle.setText("Heading: " + Float.toString(angle) + " degrees");

		fx.update(fx.getNavigationFX(), angle, distance);
		
		if(isCoinFound() && !hasBeenAnnounced) {
			fx.stopLoop();
			fx.foundCoin();
			hasBeenAnnounced = true;
		}
	}
	
	public boolean isCoinFound() {
		return distance < 5 && Math.abs(angle) < 10;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// not in use
	}
}
