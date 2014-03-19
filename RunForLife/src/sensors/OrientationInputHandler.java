package sensors;

import se.chalmers.group42.runforlife.RunActivity;
import utils.LocationHelper;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;

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


	public OrientationInputHandler(OrientationInputListener listener) {
		this.listener = listener;
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