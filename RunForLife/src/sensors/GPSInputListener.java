package sensors;

import android.location.Location;

public interface GPSInputListener {
	/**
	 * Called when the GPS has connected and has an
	 * accuracy less than {@link GPSInputHandler.MAXIMAL_ACCEPTABLE_ACCURACY}
	 */
	public void onGPSConnect();
	
	/**
	 * Called when the GPS has diconnected or has an
	 * accuracy larger than {@link GPSInputHandler.MAXIMAL_ACCEPTABLE_ACCURACY}
	 */
	public void onGPSDisconnect();
	
	/**
	 * Called when a new location is received
	 * @param location the new location
	 */
	public void onLocationChanged(Location location);
}
