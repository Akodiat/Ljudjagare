package sensors;

import utils.Vector3;

import android.content.Context;
import android.hardware.*;
import android.location.Location;
import android.util.Log;

/**
 * Class for fusing Gyroscope and GPS into a reliable user direction
 * @author Joakim Johansson
 */
public class GyroGPSFusion implements SensorEventListener, GPSInputListener
{
	// angular speeds from gyro
	private Vector3 gyro;

	// accelerometer vector
	private Vector3 accel;

	private float fusedBearing;

	private OrientationInputListener listener;

	public GyroGPSFusion(OrientationInputListener listener, Context context) {
		this.listener = listener;

		new GPSInputHandler(this, context);

		fusedBearing = 0;

		accel 	= new Vector3(0, 0, 0);
		gyro 	= new Vector3(0, 0, 0);

		SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

		Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		Sensor gyroscope	 = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
		sensorManager.registerListener(this, gyroscope, 	SensorManager.SENSOR_DELAY_FASTEST);
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {}


	//The following code is taken from TODO: Fix reference
	float[] gravityMatrix;
	float[] geomagneticMatrix;

	//Nano-seconds to seconds
	private static final float NS2S = 1.0f / 1000000000.0f;

	private float timestamp;

	public void onSensorChanged(SensorEvent event) {
		switch(event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			accel  = new Vector3(event.values[0], event.values[1], event.values[2]);
			break;

		case Sensor.TYPE_GYROSCOPE:
			/* http://developer.android.com/reference/android/hardware/SensorEvent.html
			 * Initial values are in radians/second and measure the rate of rotation 
			 * around the device's local X, Y and Z axis. 
			 * The coordinate system is the same as is used for the 
			 * acceleration sensor.
			 * Rotation is positive in the counter-clockwise direction. 
			 */
			gyro = new Vector3(event.values[0], event.values[1], event.values[2]);

			//Calculate time difference since last measure (and convert to seconds)
			float dT = (event.timestamp - this.timestamp) * NS2S;

			//Update timestamp with new value
			this.timestamp = event.timestamp;

			double v = gyroToGlobalCoordinates();
			
			v /= 10; //Really dunno why this is neccecary (perhaps wrong order of mag on time?)
			
			float dV = (float) (v * dT);

			//updateGyroBearing(dV);
			fusedBearing += Math.toDegrees(dV);
			while(fusedBearing<0)
				fusedBearing += 360;
			fusedBearing %= 360;

			listener.onCompassChanged(fusedBearing);

			Log.d("GYROSCOPE", "v: "+Math.round(v) + "\t\tfusedBearing: " + fusedBearing);
			break;
		}

	}

	/**
	 * http://electronics.stackexchange.com/questions/29423/rotating-a-gyroscopes-output-values-into-an-earth-relative-reference-frame
	 */
	private double gyroToGlobalCoordinates(){

		return 	(gyro.x * -accel.x) + 
				(gyro.y *  accel.y) + 
				(gyro.z * -accel.z);
	}

	@Override
	public void onGPSConnect() {}

	@Override
	public void onGPSDisconnect() {}

	@Override
	public void onLocationChanged(Location location) {
		if(location.hasBearing()){
			//this.gpsBearing = location.getBearing();
			fusedBearing = location.getBearing(); //+= (fusedBearing - location.getBearing()) / 2;
			listener.onCompassChanged(fusedBearing);
		}
	}
}