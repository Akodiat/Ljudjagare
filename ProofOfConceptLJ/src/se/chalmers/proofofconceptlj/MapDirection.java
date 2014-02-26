package se.chalmers.proofofconceptlj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import database.MySQLiteHelper;
import database.Route;

public class MapDirection extends FragmentActivity implements 
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener, 
LocationListener,
SensorEventListener
{


	private static final 	LatLng STOCKHOLM 		= new LatLng(59.327476, 18.070829);
	private static 			LatLng CURRENT_POSITION = new LatLng(58.705477, 11.990884);
	private static final 	LatLng AMSTERDAM		= new LatLng(52.37518, 	4.895439);
	private static final 	LatLng PARIS 			= new LatLng(48.856132, 2.352448);
	private static final 	LatLng FRANKFURT 		= new LatLng(50.111772, 8.682632);
	private static final 	LatLng HOME_MARCUS 		= new LatLng(58.489657, 13.777925);
	private GoogleMap 			map;
	private SupportMapFragment 	fragment;
	private LatLngBounds 		latlngBounds;
	private Button				bRandom;
	private Polyline 			newPolyline;
	private Polyline			myPolyRoute;
	private int 				width, height;
	private Location 			soundSource;
	private Marker 				marker;
	private Marker 				selectMarker;

	private Boolean 			first = true;

	private SensorManager	mSensorManager;
	private Sensor 			accelerometer;
	private Sensor 			magnetometer;

	private Route currentRoute;
	private MySQLiteHelper db;
	private Boolean isTime = true;
	private Timer timer = new Timer();
	private long delayTime = 1500;
	private PolylineOptions routeLine = new PolylineOptions().width(10).color(Color.RED);
	private ArrayList<Location> finalRoute = new ArrayList<Location>(); 
	private int marks;
	private int currentSoundSource;

	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(5000)         // 5 seconds
			.setFastestInterval(16)    // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); 

	private LocationClient myLocationClient;

	private SensorManager sensorManager;
	private ImageView arrow2;
	private int orientationSensor;
	private float headingAngle;
	private float headingAngleOrientation;
	private float angleToSound;

	private LocationManager locationManager;
	private Human human;

	// Handles all form of audio
	private FXHandler fx;
	private FX dragon;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_map_direction);

		dragon = new FX(2);

		db = new MySQLiteHelper(this);
		db.onUpgrade(db.getWritableDatabase(), 1, 2);

		currentRoute = new Route();
		currentRoute.setId(db.addRoute(currentRoute));

		myLocationClient = new LocationClient(getApplicationContext(), this, this);
		// once we have the reference to the client, connect it
		if(myLocationClient != null)
			myLocationClient.connect(); 

		getScreenDimentions();
		fragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
		map = fragment.getMap(); 	
		soundSource = new Location("Trololo");
		soundSource.setLatitude(CURRENT_POSITION.latitude);
		soundSource.setLongitude(CURRENT_POSITION.longitude);
		human = new Human(STOCKHOLM);

		// Show my location on the map
		if(map != null)
		{
			map.setMyLocationEnabled(true);
		} 

		mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		// Button for random navigation from current position
		bRandom = (Button) findViewById(R.id.bRandom);
		bRandom.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				map.clear();
				marks=0;
				currentSoundSource = 1;
				finalRoute.clear();
				//generateRandomSoundSource();
				generateRandomRoute(100);
				//soundSource.set(finalRoute.get(1));

			}
		});

		// Setting a click event handler for the map
		map.setOnMapLongClickListener(new OnMapLongClickListener() {
			@Override
			public void onMapLongClick(LatLng latLng) {
				// Animating to the touched position
				map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
				soundSource.setLatitude(latLng.latitude);
				soundSource.setLongitude(latLng.longitude);

				Log.d("click", "1");
				Log.d("click", "1"+latLng.latitude + " " + latLng.longitude);
				if(human.getLocation() != null){
					findDirections( human.getLocation().getLatitude(), human.getLocation().getLongitude()
							, latLng.latitude, latLng.longitude, GMapV2Direction.MODE_WALKING );
					Log.d("click", "2");
					Log.d("click", "3");
				}else{
					MarkerOptions markerOptions = new MarkerOptions();
					markerOptions.position(latLng);
					markerOptions.title(latLng.latitude + " : " + latLng.longitude);
					selectMarker = map.addMarker(markerOptions);
				}
			}
		});

		arrow2 = (ImageView) this.findViewById(R.id.imageView2);

		// Initialise audio
		(fx = new FXHandler()).initSound(this);
	}



	private void generateRandomSoundSource() {
		// Calculation random position, needs more work
		double a = (Math.random()*0.5) + 0.5;
		double b = (Math.random()*0.5) + 0.5;
		double r = 100 / 111300f;

		double w = r * Math.sqrt(a);
		double t = 2 * Math.PI * b;
		double x = w * Math.cos(t); 
		double y = w * Math.sin(t);

		double xNew = x / Math.cos(human.getLocation().getLongitude());


		soundSource.setLatitude(xNew + human.getLocation().getLatitude());
		soundSource.setLongitude(human.getLocation().getLongitude()+y);

		//new LatLng(human.getLocation().latitude+x, human.getLocation().longitude+y);
		findDirections( human.getLocation().getLatitude(), human.getLocation().getLongitude()
				, soundSource.getLatitude(), soundSource.getLongitude(), GMapV2Direction.MODE_WALKING );

	}

	private ArrayList<Location> generateRandomRoute(double distance){
		// Calculation random positions
		double a = (Math.random()*0.5) + 0.5;
		double b = (Math.random()*0.5) + 0.5;
		double r = distance / 111300f;

		double w = r * Math.sqrt(a);
		double t = 2 * Math.PI * b;
		double x = w * Math.cos(t);
		double y = w * Math.sin(t);
		double xNew = x / Math.cos(human.getLocation().getLongitude());

		Location routePoint = new Location("route");
		routePoint.setLatitude(xNew + human.getLocation().getLatitude());
		routePoint.setLongitude(human.getLocation().getLongitude()+y);

		ArrayList<Location> route = new ArrayList<Location>(); 
		route.add(human.getLocation());
		route.add(routePoint);
		Location routePoint2 = new Location("route2");
		double differLat = human.getLocation().getLatitude() - routePoint.getLatitude();
		double differLng = human.getLocation().getLongitude() - routePoint.getLongitude();

		if(Math.random()>0.5){
			routePoint2.setLatitude(human.getLocation().getLatitude() + differLng);
			routePoint2.setLongitude(human.getLocation().getLongitude() - differLat);
		}else{
			routePoint2.setLatitude(human.getLocation().getLatitude() - differLng);
			routePoint2.setLongitude(human.getLocation().getLongitude() + differLat);
		}

		route.add(routePoint2);
		route.add(human.getLocation());

		for(int i = 0; i < route.size()-1; i++){
			findDirections( route.get(i).getLatitude(), route.get(i).getLongitude()
					, route.get(i+1).getLatitude(), route.get(i+1).getLongitude(), GMapV2Direction.MODE_WALKING );
		}
		//finalRoute.add(human.getLocation());
		return route;

		//		// Calculation random positions at Marcus home. (P� landet)
		//		double a = (Math.random()*0.5) + 0.5;
		//		double b = (Math.random()*0.5) + 0.5;
		//		double r = distance / 111300f;
		//
		//		double w = r * Math.sqrt(a);
		//		double t = 2 * Math.PI * b;
		//		double x = w * Math.cos(t);
		//		double y = w * Math.sin(t);
		//		double xNew = x / Math.cos(HOME_MARCUS.longitude);
		//
		//		Location home = new Location("");
		//		home.setLatitude(HOME_MARCUS.latitude);
		//		home.setLongitude(HOME_MARCUS.longitude);
		//		Location routePoint = new Location("route");
		//		routePoint.setLatitude(xNew + HOME_MARCUS.latitude);
		//		routePoint.setLongitude(HOME_MARCUS.longitude+y);
		//
		//		ArrayList<Location> route = new ArrayList<Location>(); 
		//		route.add(home);
		//		route.add(routePoint);
		//		Location routePoint2 = new Location("route2");
		//		double differLat = 		HOME_MARCUS.latitude - routePoint.getLatitude();
		//		double differLng = HOME_MARCUS.longitude - routePoint.getLongitude();
		//
		//		if(Math.random()>0.5){
		//			routePoint2.setLatitude(HOME_MARCUS.latitude + differLng);
		//			routePoint2.setLongitude(HOME_MARCUS.longitude - differLat);
		//		}else{
		//			routePoint2.setLatitude(HOME_MARCUS.latitude - differLng);
		//			routePoint2.setLongitude(HOME_MARCUS.longitude + differLat);
		//		}
		//
		//		route.add(routePoint2);
		//		route.add(home);
		//
		//		for(int i = 0; i < route.size()-1; i++){
		//			findDirections( route.get(i).getLatitude(), route.get(i).getLongitude()
		//					, route.get(i+1).getLatitude(), route.get(i+1).getLongitude(), GMapV2Direction.MODE_WALKING );
		//		}
		//		return route;

	}



	@Override
	protected void onResume() {

		super.onResume();
		//		latlngBounds = createLatLngBoundsObject(STOCKHOLM, new LatLng(human.getLocation().getLatitude(), human.getLocation().getLongitude()));
		//		map.moveCamera(CameraUpdateFactory.newLatLngBounds(latlngBounds, width, height, 150));
		mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
		mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);

	}

	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	public void playSound(View view) {
		if (!fx.cowbell().isPlaying())
			fx.setPosition(fx.cowbell());
		else {
			Message msg = fx.getHandler().obtainMessage(Constants.MSG_STOP);
			fx.getHandler().sendMessage(msg);
			fx.stopFX(fx.cowbell());
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
		//		int halfway=0; 
		for(int i = 0 ; i < directionPoints.size() ; i++) 
		{
			points ++;
			rectLine.add(directionPoints.get(i));
		}

		//--- Is used to make more points on the directions, dont remove without asking Marcus ---
		//		halfway = directionPoints.size() / 2;
		//		Location halfwayLocation = new Location("");
		//		halfwayLocation.setLatitude(directionPoints.get(halfway).latitude);
		//		halfwayLocation.setLongitude(directionPoints.get(halfway).longitude);
		//		finalRoute.add(halfwayLocation);

		Location wholeWayLocation = new Location("");
		wholeWayLocation.setLatitude(directionPoints.get(points).latitude);
		wholeWayLocation.setLongitude(directionPoints.get(points).longitude);
		finalRoute.add(wholeWayLocation);
		//		if (newPolyline != null)
		//		{
		//			newPolyline.remove();
		//		}
		newPolyline = map.addPolyline(rectLine);
		//latlngBounds = createLatLngBoundsObject(RANDOM, CURRENT_POSITION);
		//float zoom = 19;
		//map.animateCamera(CameraUpdateFactory.newLatLngBounds(latlngBounds, width, height, 500));

		// Add a marker on the last position in the route. 
		//		if (marker != null){
		//			marker.remove();
		//		}
		if(marks==0){
			soundSource.set(wholeWayLocation);

			marker = map.addMarker(new MarkerOptions()
			.position(new LatLng(soundSource.getLatitude(), soundSource.getLongitude()))
			.title("First! " + marks ).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
		}

		//		marks++;
		//		marker = map.addMarker(new MarkerOptions()
		//		.position(new LatLng(directionPoints.get(halfway).latitude, directionPoints.get(halfway).longitude))
		//		.title("halfway! " + marks + ",  " + directionPoints.get(halfway).latitude 
		//				+ " " + directionPoints.get(halfway).longitude));

		marks++;
		marker = map.addMarker(new MarkerOptions()
		.position(new LatLng(directionPoints.get(points).latitude, directionPoints.get(points).longitude))
		.title("End of route!  " + marks +",  "+ directionPoints.get(points).latitude 
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
		//human = new Human(myLocationClient.getLastLocation());
	} 

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
	}

	/**
	 * Points arrow to source using gps orientation
	 */
	private void pointArrowToSource_GPS() {
		ImageView arrow = (ImageView) this.findViewById(R.id.imageView2);
		float bearingTo = human.getLocation().bearingTo(soundSource);
		if(bearingTo < 0){
			bearingTo += 360;
		}
		angleToSound = bearingTo - headingAngle;
		arrow.setRotation(angleToSound);
		//		arrow.setRotation(headingAngle + human.getLocation().bearingTo(soundSource));

	}

	/**
	 * Points arrow to source using compass orientation
	 */
	private void pointArrowToSource_Compass() {
		ImageView arrow = (ImageView) this.findViewById(R.id.imageView1);
		arrow.setRotation(headingAngleOrientation + human.getLocation().bearingTo(soundSource));
	}


	@Override
	public void onLocationChanged(Location location) {
		//CURRENT_POSITION = new LatLng(location.getLatitude(), location.getLongitude());
		human.setLocation(location);

		if(isTime){
			isTime = false;

			db.addPoint(new database.Point(currentRoute.getId(), location.getLatitude(), location.getLongitude()));

			//RITAR UT D�R MAN G�TT
			LatLng p = new LatLng(location.getLatitude(),location.getLongitude());
			routeLine.add(p);
			myPolyRoute = map.addPolyline(routeLine);

			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					isTime = true;
				}
			}, delayTime);
		}

		if(human.getLocation().distanceTo(soundSource) < 15){
			fx.playFX(dragon, 0);
			human.modScore(1);
			generateRandomSoundSource();

			TextView score = (TextView) findViewById(R.id.textView_score);
			score.setText("Score: "+human.getScore());
		}
		headingAngle = location.getBearing();
		if(location.hasBearing()){
			ImageView arrow = (ImageView) this.findViewById(R.id.imageView2);
			arrow.setColorFilter(android.graphics.Color.GREEN, Mode.MULTIPLY);
		}
		//			arrow.setRotation(human.getLocation().bearingTo(soundSource));

		//
		if(soundSource != null){
			pointArrowToSource_GPS();
			TextView source = (TextView) findViewById(R.id.textView_source);
			source.setText("Distance to: " + human.getLocation().distanceTo(soundSource));

			ImageView arrow = (ImageView) this.findViewById(R.id.imageView3);
			arrow.setRotation(human.getLocation().getBearing());
			adjustPanoration();
			arrow.setColorFilter(android.graphics.Color.BLUE, Mode.MULTIPLY);	
		}

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

	private void adjustPanoration() {
		CheckBox checkBox = (CheckBox) this.findViewById(R.id.checkBox1);

		if(fx.cowbell().isPlaying())
			fx.update(
					fx.cowbell(), 
					// Har �ndrat f�r att innan s� var inte ljudet r�tt, om ni f�r f�r er och �ndra prata med Marcus f�rst.
					((
							checkBox.isChecked() ? 
									headingAngleOrientation + human.getLocation().bearingTo(soundSource): angleToSound
							)
							//+ 	human.getLocation().bearingTo(soundSource)
							), 
							human.getLocation().distanceTo(soundSource));
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	float[] mGravity;
	float[] mGeomagnetic;
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
			mGravity = event.values;
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
			mGeomagnetic = event.values;
		if (mGravity != null && mGeomagnetic != null) {
			float R[] = new float[9];
			float I[] = new float[9];
			boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
			if (success) {
				float orientation[] = new float[3];
				SensorManager.getOrientation(R, orientation);
				headingAngleOrientation =  (float) (-(180/Math.PI) * orientation[0]); // orientation contains: azimut, pitch and roll
				pointArrowToSource_Compass();
				adjustPanoration();
			}
		}
	}
}