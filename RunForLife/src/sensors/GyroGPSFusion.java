package sensors;

import android.content.Context;
import android.location.Location;

/**
 * Class for fusing Gyroscope and GPS into a reliable user direction
 * @author Joakim Johansson
 */
public class GyroGPSFusion implements GyroInputListener, GPSInputListener
{
	private float fusedBearing;

	private OrientationInputListener listener;

	public GyroGPSFusion(OrientationInputListener listener, Context context) {
		this.listener = listener;

		new GPSInputHandler	(this, context, 5000); //Updating every 5 seconds
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
			fusedBearing = location.getBearing();
			listener.onOrientationChanged(fusedBearing);
		}
	}

	@Override
	public void onNewDeltaAngle(float deltaAngle) {
		//updateGyroBearing(dV);
		fusedBearing -= Math.toDegrees(deltaAngle);
		while(fusedBearing<0)
			fusedBearing += 360;
		fusedBearing %= 360;

		if(listener != null)
			listener.onOrientationChanged(fusedBearing);
	}
}