package se.chalmers.group42.runforlife;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
public class SensorInputHandler implements 
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener,
LocationListener,
SensorEventListener
{
	private static final 	LatLng STOCKHOLM 		= new LatLng(59.327476, 18.070829);
	private static 			LatLng DEFAULT_POSITION = new LatLng(58.705477, 11.990884);
	
	private Location 		soundSource;		//Location of the sound source
	private Location		currentLocation;	//Location retrieved through GPS
	
	private float 			headingAngleOrientation;
	private float 			angleToSound;

	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(5000)         // 5 seconds
			.setFastestInterval(16)    // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); 

	private LocationClient locationClient;




	private RunActivity runActivity;


	SensorInputHandler(RunActivity runActivity) {
		this.runActivity = runActivity;


		locationClient = new LocationClient(runActivity, this, this);

		// once we have the reference to the client, connect it
		if(locationClient != null)
			locationClient.connect(); 

		soundSource = locationFromLatlng(DEFAULT_POSITION);
		currentLocation = locationFromLatlng(STOCKHOLM);
	}

	private Location locationFromLatlng(LatLng latLng) {
		Location location = new Location("Trololo");
		soundSource.setLatitude(latLng.latitude);
		soundSource.setLongitude(latLng.longitude);
		return location;
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onConnected(Bundle bundle) {
		locationClient.requestLocationUpdates( REQUEST, this); 
		//human = new Human(myLocationClient.getLastLocation());
	} 

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
	}

	/**
	 * Gets the rotation according to the GPS bearing
	 */
	public float getRotation_GPS() {
		float bearingTo = currentLocation.bearingTo(soundSource);
		if(bearingTo < 0){
			bearingTo += 360;
		}
		angleToSound = bearingTo - currentLocation.getBearing();

		return angleToSound;
	}

	/**
	 * Gets the rotation according to the compass
	 */
	public float getRotation_Compass() {
		return headingAngleOrientation + currentLocation.bearingTo(soundSource);
	}

	@Override
	public void onLocationChanged(Location location) {
		currentLocation = location;

		notifyOfSensorUpdate(); //Notify runActivity of new sensor update

		if((currentLocation.distanceTo(soundSource) < Constants.MIN_DISTANCE ||
				(location.getAccuracy() < 50 ? currentLocation.distanceTo(soundSource) < location.getAccuracy() : false)))
		{
			runActivity.onAqquiredCoin(); //Notify runActivity of new location update
		}
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}
	
	private void notifyOfSensorUpdate(){
		runActivity.onUpdatedSensors(new SensorValues(currentLocation, headingAngleOrientation));
	}

	//The following code is taken from TODO: Fix reference
	float[] gravityMatrix;
	float[] geomagneticMatrix;
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
			gravityMatrix = event.values;
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
			geomagneticMatrix = event.values;
		if (gravityMatrix != null && geomagneticMatrix != null) {
			float R[] = new float[9];
			float I[] = new float[9];
			
			if (SensorManager.getRotationMatrix(R, I, gravityMatrix, geomagneticMatrix)) {
				float orientation[] = new float[3];
				SensorManager.getOrientation(R, orientation);
				headingAngleOrientation =  (float) (-(180/Math.PI) * orientation[0]); // orientation contains: azimut, pitch and roll

				notifyOfSensorUpdate(); //Notify runActivity of new sensor update
			}
		}
	}
}