package se.chalmers.group42.utils;

import se.chalmers.group42.controller.MapFragment;
import se.chalmers.group42.runforlife.Constants;
import se.chalmers.group42.runforlife.R;
import android.location.Location;
import android.util.Log;

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
	 * Updates the position and bearing of the location to a new one, 
	 * positioned distance meters away in the bearing direction
	 * @param location 	the Location to be updated
	 * @param bearing 	The bearing in which to move (in degrees)
	 * @param distance	The distance to move (in meters)
	 */
	public static void moveLocation(Location location, float bearing, float distance) {
		
			Location debugTemp = new Location(location);
			
			distance /= Constants.LAT_LNG_TO_METER;
			bearing = (float) Math.toRadians(bearing);

			double deltaLatitude = 	
					Math.sin(bearing) * distance;
			
			double deltaLongitude =	 
					Math.cos(bearing) * distance
					/ Math.cos(Math.toRadians(location.getLatitude()));

			location.setLongitude(location.getLongitude() 	+ deltaLongitude);
			location.setLatitude (location.getLatitude() 	+ deltaLatitude);
			
			Log.d("GYROREADINGS", "Intended distance: "+ distance * Constants.LAT_LNG_TO_METER + "\t Real distance:" + location.distanceTo(debugTemp));

	}
	
	public static float[] getCoordFromDistBear(float distance, float bearing){
		float[] xy = new float[2];
		xy[0] = (float) (distance * Math.cos(bearing));
		xy[1] = (float) (distance * Math.sin(bearing));
		return xy;
	}
	
	//private static final int earthRadius = 6371;
    public static float calculateDistance(Location l1, Location l2)
    {
        float dLat = (float) Math.toRadians(l2.getLatitude() - l1.getLatitude());
        float dLng = (float) Math.toRadians(l2.getLongitude() - l1.getLongitude());
        float a =	(float) (	Math.sin(dLat / 2) * Math.sin(dLat / 2) + 
        		
        						Math.cos(Math.toRadians(l1.getLatitude())) * 
        						Math.cos(Math.toRadians(l2.getLatitude())) * 
        						Math.sin(dLng / 2) * 
        						Math.sin(dLng / 2)
        					);
        
        float c = (float) (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
        
        float distance = EARTH_RADIUS * c;
        return distance;
    }
}
