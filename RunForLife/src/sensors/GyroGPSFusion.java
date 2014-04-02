package sensors;

import java.util.Timer;
import java.util.TimerTask;

import utils.Vector3;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;

/**
 * Class for handling all GPS and other sensor input
 * @author Joakim Johansson
 *
 */
public class GyroGPSFusion implements SensorEventListener
{
	// angular speeds from gyro
	private Vector3 gyro;

	// orientation angles from gyro matrix
	private Vector3 gyroOrientation;

	// accelerometer vector
	private Vector3 accel;

	public static final int 	TIME_CONSTANT = 30;
	public static final float 	FILTER_COEFFICIENT = 0.98f;
	private Timer 				fuseTimer = new Timer();

	private float 				headingAngleOrientation;

	private OrientationInputListener listener;

	PowerManager.WakeLock lock;


	public GyroGPSFusion(OrientationInputListener listener, Context context) {
		this.listener = listener;

		android.util.Log.d("Compass", "OrientationInputHandler initiated");

		SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

		Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		Sensor gyroscope	  = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
		sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_FASTEST);
	}

	public void stop(){
		lock.release();
	}


	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}


	//The following code is taken from TODO: Fix reference
	float[] gravityMatrix;
	float[] geomagneticMatrix;
	public void onSensorChanged(SensorEvent event) {
		switch(event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			// copy new accelerometer data into accel array
			// then calculate new orientation
			System.arraycopy(event.values, 0, accel, 0, 3);
			break;

		case Sensor.TYPE_GYROSCOPE:
			// process gyro data
			listener.onCompassChanged(gyroFunction(event));
			break;
		}

	}

	public static final float EPSILON = 0.000000001f;

	private static final float NS2S = 1.0f / 1000000000.0f;
	private float timestamp;
	private boolean initState = true;

	public float gyroFunction(SensorEvent event) {
		// don't start until first accelerometer orientation has been acquired
		if (accel == null)
			return -1;

		// copy the new gyro values into the gyro array
		// convert the raw gyro data into a rotation vector
		float[] deltaVector = new float[4];
		if(timestamp != 0) {
			final float dT = (event.timestamp - timestamp) * NS2S;
			System.arraycopy(event.values, 0, gyro, 0, 3);

			return (float) (gyroToGlobalCoordinates().x * dT);

		}

		return -1;

	}

	/**
	 * http://electronics.stackexchange.com/questions/29423/rotating-a-gyroscopes-output-values-into-an-earth-relative-reference-frame
	 */
	private Vector3 gyroToGlobalCoordinates(){
		Vector3 H1 = new Vector3(Math.random(), Math.random(), Math.random());

		Vector3 H2 = accel.crossProduct(H1);
		H1 = accel.crossProduct(H2);

		H1.normalize();
		H2.normalize();

		double GV  = (gyro.x * accel.x)  + (gyro.y * accel.y)  + (gyro.z * accel.z);
		double GH1 = (gyro.x * H1.x) + (gyro.y * H1.y) + (gyro.z * H1.z);
		double GH2 = (gyro.x * H2.x) + (gyro.y * H2.y) + (gyro.z * H2.z);

		android.util.Log.d("GYRO", 
				"Global gyro| " +
						"x:"  +Math.round(GV/(Math.PI)*180) + 
						"\ty:"+Math.round(GH1/(Math.PI)*180)  + 
						"\tz:"+Math.round(GH2/(Math.PI)*180)
				);
		return new Vector3(GV, GH1, GH2);
	}
}