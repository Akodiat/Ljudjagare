package sensors;

import android.location.Location;

public class SensorValues {
	private Location 	location;
	private float		compassFromNorth;
	
	public SensorValues(Location location, float compassFromNorth){
		this.location = location;
		this.compassFromNorth = compassFromNorth;
	}
	/**
	 * Returns the location
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}
	
	/**
	 * Return the angle (retrieved through the compass) between the user 
	 * orientation and north.
	 * @return
	 */
	public float getCompassFromNorth() {
		return compassFromNorth;
	}
	
	/**
	 * Return the angle (retrieved through the gps bearing) between the user orientation and north.
	 * @return
	 */
	public float getGpsBearingFromNorth() {
		return location.getBearing();
	}
	
	
	
}
