package se.chalmers.group42.gameModes;

import se.chalmers.group42.runforlife.R;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ShooterActivity extends Activity implements SensorEventListener {

	public static final int MIN_ANGLE = 10;

	private TextView monsterNotification;
	private Button generateNewAngle;

	private float[] accelerometer;
	private float[] magneticField;

	private SensorManager sensorManager;
	private Sensor sensorAccelerometer;
	private Sensor sensorMagneticField;

	private float[] matrixR;
	private float[] matrixI;
	private float[] matrixValues;

	/**
	 * Angle of which the user is pointing the phone.
	 */
	private double pointingAngle;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		monsterNotification = (TextView) findViewById(R.id.monster_notification);

		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensorAccelerometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorMagneticField = sensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

		accelerometer = new float[3];
		magneticField = new float[3];

		matrixR = new float[9];
		matrixI = new float[9];
		matrixValues = new float[3];

		pointingAngle = -69;

		generateNewAngle = (Button) findViewById(R.id.testRandomAngle);
		generateNewAngle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				pointingAngle = generateNewAngle(pointingAngle);
				System.out.println(pointingAngle);
			}
		});
	}

	public void monsterSpotted() {
		monsterNotification.setText("RAAWR");
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent evt) {
		int type = evt.sensor.getType();

		if (type == Sensor.TYPE_ACCELEROMETER)
			for (int i = 0; i < 3; i++)
				accelerometer[i] = evt.values[i];

		else if (type == Sensor.TYPE_MAGNETIC_FIELD)
			for (int i = 0; i < 3; i++)
				magneticField[i] = evt.values[i];

		boolean success = SensorManager.getRotationMatrix(matrixR, matrixI,
				accelerometer, magneticField);

		if (success) {
			SensorManager.getOrientation(matrixR, matrixValues);
			pointingAngle = Math.toDegrees(matrixValues[0]);

			// double pitch = Math.toDegrees(matrixValues[1]);
			// double roll = Math.toDegrees(matrixValues[2]);
		}
	}

	/**
	 * Places a new monster at a random angle (not being close to previous).
	 * 
	 * @param previousPointingAngle
	 */
	public double generateNewAngle(double previousPointingAngle) {
		return (((previousPointingAngle + 180) + (MIN_ANGLE + (int) (Math
				.random() * (((360 - MIN_ANGLE) - MIN_ANGLE) + 1)))) % 360) - 180;
	}

	@Override
	protected void onResume() {

		sensorManager.registerListener(this, sensorAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, sensorMagneticField,
				SensorManager.SENSOR_DELAY_NORMAL);
		super.onResume();
	}

	@Override
	protected void onPause() {

		sensorManager.unregisterListener(this, sensorAccelerometer);
		sensorManager.unregisterListener(this, sensorMagneticField);
		super.onPause();
	}
}
