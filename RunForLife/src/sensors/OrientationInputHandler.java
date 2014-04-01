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
public class OrientationInputHandler implements SensorEventListener
{
	private SensorManager mSensorManager = null;

	// angular speeds from gyro
	private float[] gyro = new float[3];

	// rotation matrix from gyro data
	private float[] gyroMatrix = new float[9];

	// orientation angles from gyro matrix
	private float[] gyroOrientation = new float[3];

	// magnetic field vector
	private float[] magnet = new float[3];

	// accelerometer vector
	private float[] accel = new float[3];

	// orientation angles from accel and magnet
	private float[] accMagOrientation = new float[3];

	// final orientation angles from sensor fusion
	private float[] fusedOrientation = new float[3];

	// accelerometer and magnetometer based rotation matrix
	private float[] rotationMatrix = new float[9];

	public static final int 	TIME_CONSTANT = 30;
	public static final float 	FILTER_COEFFICIENT = 0.98f;
	private Timer 				fuseTimer = new Timer();

	private float 				headingAngleOrientation;

	private OrientationInputListener listener;

	PowerManager.WakeLock lock;


	public OrientationInputHandler(OrientationInputListener listener, Context context) {
		this.listener = listener;

		gyroOrientation[0] = 0.0f;
		gyroOrientation[1] = 0.0f;
		gyroOrientation[2] = 0.0f;

		// initialise gyroMatrix with identity matrix
		gyroMatrix[0] = 1.0f; gyroMatrix[1] = 0.0f; gyroMatrix[2] = 0.0f;
		gyroMatrix[3] = 0.0f; gyroMatrix[4] = 1.0f; gyroMatrix[5] = 0.0f;
		gyroMatrix[6] = 0.0f; gyroMatrix[7] = 0.0f; gyroMatrix[8] = 1.0f;
		
		android.util.Log.d("Compass", "OrientationInputHandler initiated");

		SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

		Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		Sensor magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		Sensor gyroscope	  = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
		sensorManager.registerListener(this, magneticField, SensorManager.SENSOR_DELAY_FASTEST);
		sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_FASTEST);

		fuseTimer.scheduleAtFixedRate(new calculateFusedOrientationTask(),
                1000, TIME_CONSTANT);
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
		switch(event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			// copy new accelerometer data into accel array
			// then calculate new orientation
			System.arraycopy(event.values, 0, accel, 0, 3);
			calculateAccMagOrientation();
			break;

		case Sensor.TYPE_GYROSCOPE:
			// process gyro data
			gyroFunction(event);
			gyroToGlobalCoordinates();
			break;

		case Sensor.TYPE_MAGNETIC_FIELD:
			// copy new magnetometer data into magnet array
			System.arraycopy(event.values, 0, magnet, 0, 3);
			break;
		}

		headingAngleOrientation =  (float) (-(180/Math.PI) * fusedOrientation[0]);
		listener.onCompassChanged(headingAngleOrientation);
		
//		//android.util.Log.d("Compass", "Sensor changed");
//		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
//			gravityMatrix = event.values;
//		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
//			geomagneticMatrix = event.values;
//		if (gravityMatrix != null && geomagneticMatrix != null) {
//			float R[] = new float[9];
//			float I[] = new float[9];
//
//			if (SensorManager.getRotationMatrix(R, I, gravityMatrix, geomagneticMatrix)) {
//				float orientation[] = new float[3];
//				SensorManager.getOrientation(R, orientation);
//				headingAngleOrientation =  (float) (-(180/Math.PI) * orientation[0]); // orientation contains: azimut, pitch and roll
//				//android.util.Log.d("Compass", "Heading angle: "+ headingAngleOrientation);
//				listener.onCompassChanged(headingAngleOrientation);
//			}
//		}
//		if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
//			android.util.Log.d("Compass", "Gyroscope" + event.values[0] + "\t" + event.values[1] + "\t" + event.values[2]);
//		}
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
	public void calculateAccMagOrientation() {
		if(SensorManager.getRotationMatrix(rotationMatrix, null, accel, magnet)) {
			SensorManager.getOrientation(rotationMatrix, accMagOrientation);
		}
	}

	public static final float EPSILON = 0.000000001f;

	private void getRotationVectorFromGyro(float[] gyroValues,
			float[] deltaRotationVector,
			float timeFactor)
	{
		float[] normValues = new float[3];

		// Calculate the angular speed of the sample
		float omegaMagnitude =
				(float)Math.sqrt(gyroValues[0] * gyroValues[0] +
						gyroValues[1] * gyroValues[1] +
						gyroValues[2] * gyroValues[2]);

		// Normalize the rotation vector if it's big enough to get the axis
		if(omegaMagnitude > EPSILON) {
			normValues[0] = gyroValues[0] / omegaMagnitude;
			normValues[1] = gyroValues[1] / omegaMagnitude;
			normValues[2] = gyroValues[2] / omegaMagnitude;
		}

		// Integrate around this axis with the angular speed by the timestep
		// in order to get a delta rotation from this sample over the timestep
		// We will convert this axis-angle representation of the delta rotation
		// into a quaternion before turning it into the rotation matrix.
		float thetaOverTwo = omegaMagnitude * timeFactor;
		float sinThetaOverTwo = (float)Math.sin(thetaOverTwo);
		float cosThetaOverTwo = (float)Math.cos(thetaOverTwo);
		deltaRotationVector[0] = sinThetaOverTwo * normValues[0];
		deltaRotationVector[1] = sinThetaOverTwo * normValues[1];
		deltaRotationVector[2] = sinThetaOverTwo * normValues[2];
		deltaRotationVector[3] = cosThetaOverTwo;
	}

	private static final float NS2S = 1.0f / 1000000000.0f;
	private float timestamp;
	private boolean initState = true;

	public void gyroFunction(SensorEvent event) {
		// don't start until first accelerometer/magnetometer orientation has been acquired
		if (accMagOrientation == null)
			return;

		// initialisation of the gyroscope based rotation matrix
		if(initState) {
			float[] initMatrix = new float[9];
			initMatrix = getRotationMatrixFromOrientation(accMagOrientation);
			float[] test = new float[3];
			SensorManager.getOrientation(initMatrix, test);
			gyroMatrix = matrixMultiplication(gyroMatrix, initMatrix);
			initState = false;
		}

		// copy the new gyro values into the gyro array
		// convert the raw gyro data into a rotation vector
		float[] deltaVector = new float[4];
		if(timestamp != 0) {
			final float dT = (event.timestamp - timestamp) * NS2S;
			System.arraycopy(event.values, 0, gyro, 0, 3);
			
//			//Test av Joakim
//			Vector3 v = gyroToGlobalCoordinates();
//			gyro[0] = (float) v.x;
//			gyro[1] = (float) v.y;
//			gyro[2] = (float) v.z;
//			//Slut på test av Joakim
			
			getRotationVectorFromGyro(gyro, deltaVector, dT / 2.0f);
		}

		// measurement done, save current time for next interval
		timestamp = event.timestamp;

		// convert rotation vector into rotation matrix
		float[] deltaMatrix = new float[9];
		SensorManager.getRotationMatrixFromVector(deltaMatrix, deltaVector);

		// apply the new rotation interval on the gyroscope based rotation matrix
		gyroMatrix = matrixMultiplication(gyroMatrix, deltaMatrix);

		// get the gyroscope based orientation from the rotation matrix
		SensorManager.getOrientation(gyroMatrix, gyroOrientation);
	}
	
	private float[] getRotationMatrixFromOrientation(float[] o) {
	    float[] xM = new float[9];
	    float[] yM = new float[9];
	    float[] zM = new float[9];
	 
	    float sinX = (float)Math.sin(o[1]);
	    float cosX = (float)Math.cos(o[1]);
	    float sinY = (float)Math.sin(o[2]);
	    float cosY = (float)Math.cos(o[2]);
	    float sinZ = (float)Math.sin(o[0]);
	    float cosZ = (float)Math.cos(o[0]);
	 
	    // rotation about x-axis (pitch)
	    xM[0] = 1.0f; xM[1] = 0.0f; xM[2] = 0.0f;
	    xM[3] = 0.0f; xM[4] = cosX; xM[5] = sinX;
	    xM[6] = 0.0f; xM[7] = -sinX; xM[8] = cosX;
	 
	    // rotation about y-axis (roll)
	    yM[0] = cosY; yM[1] = 0.0f; yM[2] = sinY;
	    yM[3] = 0.0f; yM[4] = 1.0f; yM[5] = 0.0f;
	    yM[6] = -sinY; yM[7] = 0.0f; yM[8] = cosY;
	 
	    // rotation about z-axis (azimuth)
	    zM[0] = cosZ; zM[1] = sinZ; zM[2] = 0.0f;
	    zM[3] = -sinZ; zM[4] = cosZ; zM[5] = 0.0f;
	    zM[6] = 0.0f; zM[7] = 0.0f; zM[8] = 1.0f;
	 
	    // rotation order is y, x, z (roll, pitch, azimuth)
	    float[] resultMatrix = matrixMultiplication(xM, yM);
	    resultMatrix = matrixMultiplication(zM, resultMatrix);
	    return resultMatrix;
	}
	
	private float[] matrixMultiplication(float[] A, float[] B) {
	    float[] result = new float[9];
	 
	    result[0] = A[0] * B[0] + A[1] * B[3] + A[2] * B[6];
	    result[1] = A[0] * B[1] + A[1] * B[4] + A[2] * B[7];
	    result[2] = A[0] * B[2] + A[1] * B[5] + A[2] * B[8];
	 
	    result[3] = A[3] * B[0] + A[4] * B[3] + A[5] * B[6];
	    result[4] = A[3] * B[1] + A[4] * B[4] + A[5] * B[7];
	    result[5] = A[3] * B[2] + A[4] * B[5] + A[5] * B[8];
	 
	    result[6] = A[6] * B[0] + A[7] * B[3] + A[8] * B[6];
	    result[7] = A[6] * B[1] + A[7] * B[4] + A[8] * B[7];
	    result[8] = A[6] * B[2] + A[7] * B[5] + A[8] * B[8];
	 
	    return result;
	}
	
	class calculateFusedOrientationTask extends TimerTask {
	    public void run() {
	        float oneMinusCoeff = 1.0f - FILTER_COEFFICIENT;
	        fusedOrientation[0] =
	            FILTER_COEFFICIENT * gyroOrientation[0]
	            + oneMinusCoeff * accMagOrientation[0];
	 
	        fusedOrientation[1] =
	            FILTER_COEFFICIENT * gyroOrientation[1]
	            + oneMinusCoeff * accMagOrientation[1];
	 
	        fusedOrientation[2] =
	            FILTER_COEFFICIENT * gyroOrientation[2]
	            + oneMinusCoeff * accMagOrientation[2];
	 
	        // overwrite gyro matrix and orientation with fused orientation
	        // to comensate gyro drift
	        gyroMatrix = getRotationMatrixFromOrientation(fusedOrientation);
	        System.arraycopy(fusedOrientation, 0, gyroOrientation, 0, 3);
	    }
	}
	/**
	 * http://electronics.stackexchange.com/questions/29423/rotating-a-gyroscopes-output-values-into-an-earth-relative-reference-frame
	 */
	private Vector3 gyroToGlobalCoordinates(){
		Vector3 V  = new Vector3(accel[0], accel[1], accel[2]);
		Vector3 G  = new Vector3( gyro[0],  gyro[1],  gyro[2]);
		
		Vector3 H1 = new Vector3(Math.random(), Math.random(), Math.random());

		Vector3 H2 = V.crossProduct(H1);
		H1 = V.crossProduct(H2);

		H1.normalize();
		H2.normalize();
		
		double GV  = (G.x*V.x)  + (G.y*V.y)  + (G.z*V.z);
		double GH1 = (G.x*H1.x) + (G.y*H1.y) + (G.z*H1.z);
		double GH2 = (G.x*H2.x) + (G.y*H2.y) + (G.z*H2.z);
		
		android.util.Log.d("GYRO", 
				"Global gyro| " +
				"x:"  +Math.round(GV/(Math.PI)*180) + 
				"\ty:"+Math.round(GH1/(Math.PI)*180)  + 
				"\tz:"+Math.round(GH2/(Math.PI)*180)
				);
		return new Vector3(GV, GH1, GH2);
	}
}