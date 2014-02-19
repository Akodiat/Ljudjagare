package se.chalmers.proofofconceptlj;

import java.util.*;

import android.graphics.*;
import android.hardware.*;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.google.android.gms.common.*;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.model.*;

public class MapDirection extends FragmentActivity implements 
	GooglePlayServicesClient.ConnectionCallbacks,
	GooglePlayServicesClient.OnConnectionFailedListener, 
	LocationListener
{

	
	private static final 	LatLng STOCKHOLM 		= new LatLng(59.327476, 18.070829);
	private static 			LatLng CURRENT_POSITION = new LatLng(58.705477, 11.990884);
	private static final 	LatLng AMSTERDAM		= new LatLng(52.37518, 	4.895439);
	private static final 	LatLng PARIS 			= new LatLng(48.856132, 2.352448);
	private static final 	LatLng FRANKFURT 		= new LatLng(50.111772, 8.682632);
	
	private GoogleMap 			map;
	private SupportMapFragment 	fragment;
	private LatLngBounds 		latlngBounds;
	private Button				bRandom;
	private Polyline 			newPolyline;
	private int 				width, height;
	private Location 			soundSource;
	private Marker 				marker;
	private Marker 				selectMarker;
	
	private Boolean 			first = true;

	// Kanske kan ta bort?
	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(5000)         // 5 seconds
			.setFastestInterval(16)    // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); 

	private LocationClient myLocationClient;

	//

	private SensorManager sensorManager;
	private ImageView arrow2;
	private int orientationSensor;
	private float headingAngle;
	private float pitchAngle;
	private float rollAngle;

	private LocationManager locationManager;
	private Human human;

	// Handles all form of audio
	FXHandler fx;
	int streamID;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_map_direction);


		myLocationClient = new LocationClient(getApplicationContext(), this, this);
		// once we have the reference to the client, connect it
		if(myLocationClient != null)
			myLocationClient.connect(); 

		getScreenDimentions();
		fragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
		map = fragment.getMap(); 	
		soundSource = new Location("Trololo");

		// Show my location on the map
		if(map != null)
		{
			map.setMyLocationEnabled(true);
		} 


		// Button for random navigation from current position
		bRandom = (Button) findViewById(R.id.bRandom);
		bRandom.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// Calculation random position, needs more work
				double a = Math.random();
				double b = Math.random();
				double r = 0.010;
				double w = r * Math.sqrt(a);
				double t = 2 * Math.PI * b;
				double x = w * Math.cos(t); 
				double y = w * Math.sin(t);


				soundSource.setLatitude(human.getLocation().getLatitude()+x);
				soundSource.setLongitude(human.getLocation().getLongitude()+y);
						
						//new LatLng(human.getLocation().latitude+x, human.getLocation().longitude+y);
				findDirections( human.getLocation().getLatitude(), human.getLocation().getLongitude()
						, soundSource.getLatitude(), soundSource.getLongitude(), GMapV2Direction.MODE_WALKING );
			}
		});

		// Setting a click event handler for the map
		map.setOnMapLongClickListener(new OnMapLongClickListener() {
			@Override
			public void onMapLongClick(LatLng latLng) {

				// Creating a marker
				//MarkerOptions markerOptions = new MarkerOptions();

				// Setting the position for the marker
				//markerOptions.position(latLng);

				// Setting the title for the marker.
				// This will be displayed on taping the marker
				//markerOptions.title(latLng.latitude + " : " + latLng.longitude);

				// Animating to the touched position
				map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
				//				if ( selectMarker != null){
				//					selectMarker.remove();
				//				}
				soundSource.setLatitude(latLng.latitude);
				soundSource.setLongitude(latLng.longitude);
				
				Log.d("click", "1");
				Log.d("click", "1"+latLng.latitude + " " + latLng.longitude);
				if(human.getLocation() != null){
					findDirections( human.getLocation().getLatitude(), human.getLocation().getLongitude()
							, latLng.latitude, latLng.longitude, GMapV2Direction.MODE_WALKING );
					// Placing a marker on the touched position
					Log.d("click", "2");
					//selectMarker = map.addMarker(markerOptions);
					Log.d("click", "3");
				}else{
					// Creating a marker
					MarkerOptions markerOptions = new MarkerOptions();

					// Setting the position for the marker
					markerOptions.position(latLng);

					// Setting the title for the marker.
					// This will be displayed on taping the marker
					markerOptions.title(latLng.latitude + " : " + latLng.longitude);

					selectMarker = map.addMarker(markerOptions);
				}
			}
		});


		//Stolen from augmented reality on the Android platform pp.23:
		sensorManager 		= (SensorManager) getSystemService(SENSOR_SERVICE);
		orientationSensor 	= Sensor.TYPE_ORIENTATION;
		sensorManager.registerListener(
				sensorEventListener, 
				sensorManager.getDefaultSensor(orientationSensor), 
				SensorManager.SENSOR_DELAY_NORMAL
		);

		
		

		arrow2 = (ImageView) this.findViewById(R.id.imageView2);

		streamID = -1;

		// Initialise audio
		(fx = new FXHandler()).initSound(this);
	}
	final SensorEventListener sensorEventListener = new SensorEventListener() {
		public void onSensorChanged(SensorEvent sensorEvent) {
//			if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
//				headingAngle 	= sensorEvent.values[0];
//
//				if(RANDOM != null){
//					pointArrowToSource_C();
//					if(streamID != -1)
//						fx.setPosition(
//								streamID, 
//								(headingAngle + human.getLocation().bearingTo(RANDOM)), 
//								human.getLocation().distanceTo(RANDOM));
//				}
//			}
		}
		public void onAccuracyChanged (Sensor senor, int accuracy) {
			//Not used
		}


	};

	
	@Override
	protected void onResume() {

		super.onResume();
//		latlngBounds = createLatLngBoundsObject(STOCKHOLM, new LatLng(human.getLocation().getLatitude(), human.getLocation().getLongitude()));
//		map.moveCamera(CameraUpdateFactory.newLatLngBounds(latlngBounds, width, height, 150));

	}

	public void playSound(View view) {
		if(this.streamID == -1) {
			this.streamID = fx.playFX(FXHandler.FX_01);
		}
		else {
			fx.stopFX(streamID);
			this.streamID = -1;
		}
	}

	//	public void updateDistance(int distance){
	//		String distanceText = Integer.toString(distance);
	//		TextView t = (TextView) this.findViewById(R.id.textDistance);
	//		t.setText(distanceText);
	//	}




	public void handleGetDirectionsResult(ArrayList<LatLng> directionPoints) {
		PolylineOptions rectLine = new PolylineOptions().width(10).color(Color.BLUE);
		int points = -1;
		for(int i = 0 ; i < directionPoints.size() ; i++) 
		{          
			points ++;
			rectLine.add(directionPoints.get(i));
		}
		if (newPolyline != null)
		{
			newPolyline.remove();
		}
		newPolyline = map.addPolyline(rectLine);
		//latlngBounds = createLatLngBoundsObject(RANDOM, CURRENT_POSITION);
		float zoom = 19;
		//map.animateCamera(CameraUpdateFactory.newLatLngBounds(latlngBounds, width, height, 500));

		// Add a marker on the last position in the route. 
		if (marker != null){
			marker.remove();
		}
		marker = map.addMarker(new MarkerOptions()
		.position(new LatLng(directionPoints.get(points).latitude, directionPoints.get(points).longitude))
		.title("End of route! " + directionPoints.get(points).latitude 
				+ " " + directionPoints.get(points).longitude));
		//map.animateCamera(CameraUpdateFactory.zoomOut());

	}


	private void getScreenDimentions()
	{
		Display display = getWindowManager().getDefaultDisplay();

		Point size = new Point();
		display.getSize(size);
		width = size.x;
		height = size.y;
	}

	private LatLngBounds createLatLngBoundsObject(LatLng firstLocation, LatLng secondLocation)
	{
		if (firstLocation != null && secondLocation != null)
		{
			LatLngBounds.Builder builder = new LatLngBounds.Builder();    
			builder.include(firstLocation).include(secondLocation);

			return builder.build();
		}
		return null;
	}

	public void findDirections(double fromPositionDoubleLat, double fromPositionDoubleLong, double toPositionDoubleLat, double toPositionDoubleLong, String mode)
	{
		Map<String, String> map = new HashMap<String, String>();
		map.put(GetDirectionsAsyncTask.USER_CURRENT_LAT, String.valueOf(fromPositionDoubleLat));
		map.put(GetDirectionsAsyncTask.USER_CURRENT_LONG, String.valueOf(fromPositionDoubleLong));
		map.put(GetDirectionsAsyncTask.DESTINATION_LAT, String.valueOf(toPositionDoubleLat));
		map.put(GetDirectionsAsyncTask.DESTINATION_LONG, String.valueOf(toPositionDoubleLong));
		map.put(GetDirectionsAsyncTask.DIRECTIONS_MODE, mode);

		GetDirectionsAsyncTask asyncTask = new GetDirectionsAsyncTask(this);
		asyncTask.execute(map);	
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle bundle) {
		myLocationClient.requestLocationUpdates( REQUEST, this); 
		human = new Human(myLocationClient.getLastLocation());
	} 

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}
	
	/**
	 * Points arrow to source using compass orientation
	 */
	private void pointArrowToSource_C() {
		ImageView arrow = (ImageView) this.findViewById(R.id.imageView2);
		arrow.setRotation(headingAngle + human.getLocation().bearingTo(soundSource));
	}
	

	@Override
	public void onLocationChanged(Location location) {
		//CURRENT_POSITION = new LatLng(location.getLatitude(), location.getLongitude());
		
		human.setLocation(location);
		headingAngle = location.getBearing();
		
		ImageView arrow = (ImageView) this.findViewById(R.id.imageView1);
		arrow.setRotation(human.getLocation().bearingTo(soundSource));

		//
		if(soundSource != null){
			pointArrowToSource_C();
			if(streamID != -1)
				fx.setPosition(
						streamID, 
						(headingAngle + human.getLocation().bearingTo(soundSource)), 
						human.getLocation().distanceTo(soundSource));
		}
		//
		
		if(first){
			first = false;
			map.animateCamera(CameraUpdateFactory.newCameraPosition(
					new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude()))
					.zoom(15.5f)
					.bearing(0)
					.tilt(25)
					.build()
					), new GoogleMap.CancelableCallback() {
				@Override
				public void onFinish() {
					// Your code here to do something after the Map is rendered
				}

				@Override
				public void onCancel() {
					// Your code here to do something after the Map rendering is cancelled
				}
			});
		}
	}


}