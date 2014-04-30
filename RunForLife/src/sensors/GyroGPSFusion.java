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
public class GyroGPSFusion implements GyroInputListener, GPSInputListener
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
		new GyroInputHandler(this, context);

		fusedBearing = 0;
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
			listener.onOrientationChanged(fusedBearing);
		}
	}

	@Override
	public void onNewDeltaAngle(float deltaAngle) {
		//updateGyroBearing(dV);
		fusedBearing += Math.toDegrees(deltaAngle);
		while(fusedBearing<0)
			fusedBearing += 360;
		fusedBearing %= 360;
	}
}