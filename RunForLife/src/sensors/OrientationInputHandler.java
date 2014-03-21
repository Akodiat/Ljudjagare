package sensors;

import se.chalmers.group42.runforlife.RunActivity;
import utils.LocationHelper;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.PowerManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

/**
 * Class for handling all GPS and other sensor input
 * @author Joakim Johansson
 *
 */
public class OrientationInputHandler implements 
SensorEventListener
{
	private float 			headingAngleOrientation;

	private OrientationInputListener listener;
	
	PowerManager.WakeLock lock;


	public OrientationInputHandler(OrientationInputListener listener, Context context) {
		this.listener = listener;
		
		
		// http://stackoverflow.com/questions/7471226/method-onsensorchanged-when-screen-is-lock
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		lock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SensorRead");
		lock.acquire();
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

				listener.onCompassChanged(headingAngleOrientation);
			}
		}
	}
}