package utils;

import android.location.Location;
import com.google.android.gms.maps.model.LatLng;

/**
 * A help class for converting between different position formats
 * @author Joakim Johansson
 */
public class LocationHelper {
	public static final int EARTH_RADIUS = 6374000; //Earth radius in meters
	
	/**
	 * Converts a LatLng object into a Location
	 * @param latLng the LatLng object describing a position
	 * @return A location describing the same position as latLng
	 */
	public static Location locationFromLatlng(LatLng latLng) {
		Location location = new Location("Trololo");
		location.setLatitude(latLng.latitude);
		location.setLongitude(latLng.longitude);
		return location;
	}
	/**
	 * Converts a Location object into a LatLng
	 * @param location the Location object describing a position
	 * @return A LatLng describing the same position as location
	 */
	public static LatLng latlngFromLocation(Location location) {
		return new LatLng(
				location.getLatitude(), 
				location.getLongitude()
		);
	}
	
	/**
	 * Calculates a new LatLng positioned a specified distance and bearing away.
	 * @param latLng	The LatLng from which to move
	 * @param bearing	The bearing in which to move (in degrees)
	 * @param distance	The distance to move (in meters)
	 * @return			A new LatLng, positioned distance meters away in the bearing direction.
	 */
	public static LatLng calculateNewLatLng(LatLng latLng, float bearing, float distance){
		//Convert lat and long to radians
		double rLat = Math.toRadians(latLng.latitude);
		double rLng = Math.toRadians(latLng.longitude);
		
		//Changing distance to proportion of earth radius;
		distance /= EARTH_RADIUS;
		
		//Changing bearing to radians
		double rBearing = Math.toRadians(bearing);
		
		//Calculate new latitude and longitude (in radians)
		double newRLat = Math.asin(Math.sin(rLat) * Math.cos(distance) +
                Math.cos(rLat) * Math.sin(distance) * Math.cos(rBearing));
		
		double newRLng = rLng + Math.atan2(Math.sin(rBearing) * Math.sin(distance) * Math.cos(rLat),
                Math.cos(distance) - Math.sin(rLat * Math.sin(newRLat)));
		
		return new LatLng(Math.toDegrees(newRLat), Math.toDegrees(newRLng));
		
	}
	/**
	 * Updates the position and bearing of the location to a new one, 
	 * positioned distance meters away in the bearing direction
	 * @param location 	the Location to be updated
	 * @param bearing 	The bearing in which to move (in degrees)
	 * @param distance	The distance to move (in meters)
	 */
	public static void moveLocation(Location location, float bearing, float distance) {
		//Calculates new position
		LatLng updatedLatLng = calculateNewLatLng(
				latlngFromLocation(location), 
				bearing, 
				distance);
		//Sets the calculated position
		location.setLatitude(updatedLatLng.latitude);
		location.setLongitude(updatedLatLng.longitude);
		
		//Updates bearing to match the travelled direction
		location.setBearing(bearing);
	}
	
	public static float[] getCoordFromDistBear(float distance, float bearing){
		float[] xy = new float[2];
		xy[0] = (float) (distance * Math.cos(bearing));
		xy[1] = (float) (distance * Math.sin(bearing));
		return xy;
	}
}
