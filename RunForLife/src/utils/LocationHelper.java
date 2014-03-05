package utils;

import android.location.Location;
import com.google.android.gms.maps.model.LatLng;

/**
 * A help class for converting between different position formats
 * @author Joakim Johansson
 */
public class LocationHelper {
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
}
