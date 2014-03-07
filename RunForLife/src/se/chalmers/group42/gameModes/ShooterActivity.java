package se.chalmers.group42.gameModes;

import se.chalmers.group42.runforlife.FX;
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

	private TextView monsterNotification;
	private FXHandler fx;

	private float[] accelerometer;
	private float[] magneticField;

	private SensorManager sensorManager;
	private Sensor sensorAccelerometer;
	private Sensor sensorMagneticField;

	private float[] matrixR;
	private float[] matrixI;
	private float[] matrixValues;

	/**
	 * Default hp destruction of a gun.
	 */
	public static final int DEFAULT_GUN_POWER = 20;

	/**
	 * Angle of which the user is pointing the phone.
	 */
	private double pointingAngle;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		monsterNotification = (TextView) findViewById(R.id.monster_notification);

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorAccelerometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorMagneticField = sensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

		accelerometer = new float[3];
		magneticField = new float[3];

		matrixR = new float[9];
		matrixI = new float[9];
		matrixValues = new float[3];

		(fx = new FXHandler()).initSound(this);
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
	 * Place monster at given angle.
	 * 
	 * @param angle
	 */
	public void createMonsterAtAngle(double angle) {

	}

	/**
	 * Places a new monster at a random angle (not being close to previous).
	 * 
	 * @param previousPointingAngle
	 */
	public void createNewMonster(double previousPointingAngle) {

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

	private class Monster {

		// Start stepping through the array from the beginning
		private double angle;
		private FX sound;
		private int hp;

		public int hp() {
			return hp;
		}
		
		
	}
}
