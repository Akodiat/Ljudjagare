package sensors;

import se.chalmers.group42.runforlife.RunActivity;
import utils.LocationHelper;
import android.content.Context;
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
public class GPSInputHandler implements 
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener,
LocationListener
{
	public static final 	int		MAXIMAL_ACCEPTABLE_ACCURACY = 20;
	
	
	private static final 	LatLng STOCKHOLM 		= new LatLng(59.327476, 18.070829);
	private Location		currentLocation;	//Location retrieved through GPS

	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(5000)         // 5 seconds
			.setFastestInterval(16)    // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); 

	private LocationClient 	locationClient;
	private GPSInputListener 	listener;


	public GPSInputHandler(GPSInputListener listener, Context context) {
		this.listener = listener;

		locationClient = new LocationClient(context, this, this);

		// once we have the reference to the client, connect it
		if(locationClient != null)
			locationClient.connect(); 

		
		currentLocation = LocationHelper.locationFromLatlng(STOCKHOLM);
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
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
		if(location.getAccuracy() > MAXIMAL_ACCEPTABLE_ACCURACY){
			listener.onGPSDisconnect();
		}
		else
			listener.onGPSConnect();
		
		currentLocation = location;
		listener.onLocationChanged(location);
	}
}