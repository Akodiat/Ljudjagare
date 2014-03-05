package utils;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class LocationHelper {
	
	public static Location locationFromLatlng(LatLng latLng) {
		Location location = new Location("Trololo");
		location.setLatitude(latLng.latitude);
		location.setLongitude(latLng.longitude);
		return location;
	}
}
