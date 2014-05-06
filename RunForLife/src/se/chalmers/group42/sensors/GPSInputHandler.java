package se.chalmers.group42.sensors;

import se.chalmers.group42.controller.RunActivity;
import se.chalmers.group42.utils.LocationHelper;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

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
public class GPSInputHandler implements 
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener,
LocationListener
{
	public static final 	int		MAXIMAL_ACCEPTABLE_ACCURACY = 20;


	private static final 	LatLng STOCKHOLM 		= new LatLng(59.327476, 18.070829);
	private Location		currentLocation;	//Location retrieved through GPS

	private LocationRequest REQUEST; 

	private LocationClient 	locationClient;
	private GPSInputListener 	listener;
	
	private LocationManager locationManager;


	public GPSInputHandler(GPSInputListener listener, Context context){
		this(listener, context, 1000);
	}
	public GPSInputHandler(GPSInputListener listener, Context context, long updateInterval) {
		this.listener = listener;
		
		REQUEST = LocationRequest.create()
		.setInterval(updateInterval)         // milliseconds
		.setFastestInterval(16)    // 16ms = 60fps
		.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

		locationClient = new LocationClient(context, this, this);

		// once we have the reference to the client, connect it
		if(locationClient != null)
			locationClient.connect(); 

		currentLocation = LocationHelper.locationFromLatlng(STOCKHOLM);
		
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		listener.onGPSDisconnect();
	}

	@Override
	public void onConnected(Bundle bundle) {
		locationClient.requestLocationUpdates( REQUEST, this); 
		//runActivity.onGPSConnect();
	} 

	@Override
	public void onDisconnected() {
		listener.onGPSDisconnect();
	}

	@Override
	public void onLocationChanged(Location location) {
		if((location.getAccuracy() < MAXIMAL_ACCEPTABLE_ACCURACY) && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			listener.onGPSConnect();
		}
		else
			listener.onGPSDisconnect();

		
		//If the new location has no bearing, use the old bearing
		if(!location.hasBearing()){
			float bearing = currentLocation.getBearing();
			currentLocation = location;
			currentLocation.setBearing(bearing);
		} else currentLocation = location;
		
		listener.onLocationChanged(location);
	}

	public void pause() {
		locationClient.disconnect();
	}

	public void resume() {
		locationClient.connect();
	}
}