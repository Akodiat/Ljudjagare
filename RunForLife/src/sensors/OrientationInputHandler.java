package sensors;

import com.google.android.gms.internal.am;

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
public class OrientationInputHandler implements SensorEventListener
{
	private float 			headingAngleOrientation;

	private OrientationInputListener listener;
	
	PowerManager.WakeLock lock;


	public OrientationInputHandler(OrientationInputListener listener, Context context) {
		this.listener = listener;
		
		android.util.Log.d("Compass", "OrientationInputHandler initiated");
		
		 SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		 
		 Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		 Sensor magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		 Sensor gyroscope	  = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

		 sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
		 sensorManager.registerListener(this, magneticField, SensorManager.SENSOR_DELAY_FASTEST);
		 sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_FASTEST);
		
		// http://stackoverflow.com/questions/7471226/method-onsensorchanged-when-screen-is-lock
//		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//		lock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SensorRead");
//		lock.acquire();
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
		//android.util.Log.d("Compass", "Sensor changed");
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
			gravityMatrix = event.values;
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
			geomagneticMatrix = event.values;
		if (gravityMatrix != null && geomagneticMatrix != null) {
			float R[] = new float[9];
			float I[] = new float[9];
			
			if (SensorManager.getRotationMatrix(R, I, gravityMatrix, geomagneticMatrix)) {
				float orientation[] = new float[3];
				SensorManager.getOrientation(R, orientation);
				headingAngleOrientation =  (float) (-(180/Math.PI) * orientation[0]); // orientation contains: azimut, pitch and roll
				//android.util.Log.d("Compass", "Heading angle: "+ headingAngleOrientation);
				listener.onCompassChanged(headingAngleOrientation);
			}
		}
		if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
			android.util.Log.d("Compass", "Gyroscope" + event.values[0] + "\t" + event.values[1] + "\t" + event.values[2]);
		}
	}
	
	/*
	 * http://www.thousand-thoughts.com/wp-content/uploads/MasterThesis_Lawitzki_2012.pdf
	 * sidan 67
	 
The integration of the gyroscope data was performed by measuring the time
between each sensor update and multiplying the measured time with the rota-
tion speed values from the gyroscope. The result was an orientation increment
which was then added to a variable containing the absolute orientation. But
since this absolute orientation data was completely derived from the gyroscope
measurements, it was still containing the gyroscope drift error and needed fur-
ther processing. In the following this particular orientation variable will be called
gyroOrientation. In principle the code for the calculation was as follows:

gyroOrientation = gyroOrientation + deltaTime * gyroRotation;

where deltaTime was the time in seconds elapsed since the last update of the variable
gyroOrientation, and gyroRotation contained the speed values measured by the gyroscope. 
Of course this is a simplified code line, since in practice gyroOrientationwas a vector 
containing orientation angles and the values were integrated component-wise.

etc, etc, etc.
	 */
	
	private float calculateAlfa(float samplePeriod, float relativeDuration){
		return relativeDuration / (relativeDuration + samplePeriod);
	}
	
	private float calculateFilteredOrientation(float gyroOrientation, float accMagOrientation, float alfa){
		return alfa * gyroOrientation + (1-alfa) * accMagOrientation;
	}
}