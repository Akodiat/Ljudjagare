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
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;

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
	private Boolean 			directionFinished = false;

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
	private int pointsTaken = 0;

	private TextView tx_time;
	private TextView tx_distance;
	private TextView tx_speed;
	private Long seconds = 0L;
	private Boolean running = false;
	private Boolean pause = false;
	private Handler m_handler;
	private Runnable m_handlerTask;
	private int distance;
	private Location prev = new Location("");
	private Boolean start = true;

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

	private Human human;

	// Handles all form of audio
	private FXHandler fx;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_map_direction);

		tx_time = (TextView) findViewById(R.id.textView_time);	
		tx_distance = (TextView) findViewById(R.id.textView_distance);
		tx_speed = (TextView) findViewById(R.id.textView_speed);
		m_handler = new Handler();

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

		// Set default soundsource location
		soundSource = new Location("SoundSource");
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
				//generateRandomSoundSource();
				generateRandomRoute(100);

				// The time you are running
				if(!running){
					startWatch();
					running = true;
				}else{
					pauseWatch();
				}
			}
		});

		// Used to set a point on the place you longpress on the map
		map.setOnMapLongClickListener(new OnMapLongClickListener() {
			@Override
			public void onMapLongClick(LatLng latLng) {
				// Animating to the touched position
				map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
				soundSource.setLatitude(latLng.latitude);
				soundSource.setLongitude(latLng.longitude);

				if(human.getLocation() != null){
					findDirections( human.getLocation().getLatitude(), human.getLocation().getLongitude()
							, latLng.latitude, latLng.longitude, GMapV2Direction.MODE_WALKING );
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
		// Calculation random position
		double a = Math.random();
		double b = Math.random();
		double r = 100 / Constants.LAT_LNG_TO_METER;

		double w = r * Math.sqrt(a);
		double t = 2 * Math.PI * b;
		double x = w * Math.cos(t); 
		double y = w * Math.sin(t);

		double xNew = x / Math.cos(human.getLocation().getLongitude());

		Location random = new Location("");
		random.setLatitude(xNew + human.getLocation().getLatitude());
		random.setLongitude(human.getLocation().getLongitude()+y);

		float bearingTo = human.getLocation().bearingTo(random);
		double addLat;
		double addLng;
		double distanceFromLocation = 1000/Constants.LAT_LNG_TO_METER; 
		if(bearingTo<-90){
			bearingTo = bearingTo + 180;
			addLat = -(Math.sin(bearingTo)*distanceFromLocation);
			addLng = -(Math.cos(bearingTo)*distanceFromLocation);
		}else if(bearingTo < 0){
			bearingTo = bearingTo + 90;
			addLat = -(Math.sin(bearingTo)*distanceFromLocation);
			addLng = +(Math.cos(bearingTo)*distanceFromLocation);
		}else if(bearingTo < 90){
			addLat = +(Math.sin(bearingTo)*distanceFromLocation);
			addLng = +(Math.cos(bearingTo)*distanceFromLocation);
		}else{
			bearingTo = bearingTo - 90;
			addLat = -(Math.sin(bearingTo)*distanceFromLocation);
			addLng = +(Math.cos(bearingTo)*distanceFromLocation);	
		}
		random.setLatitude(random.getLatitude() + addLat);
		random.setLongitude(random.getLongitude() + addLng);

		marker = map.addMarker(new MarkerOptions()
		.position(new LatLng(random.getLatitude(), random.getLongitude()))
		.title("Random").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

		//				soundSource.setLatitude(xNew + human.getLocation().getLatitude());
		//				soundSource.setLongitude(human.getLocation().getLongitude()+y);

		//new LatLng(human.getLocation().latitude+x, human.getLocation().longitude+y);
		//				findDirections( human.getLocation().getLatitude(), human.getLocation().getLongitude()
		//						, soundSource.getLatitude(), soundSource.getLongitude(), GMapV2Direction.MODE_WALKING );


	}

	private ArrayList<Location> generateRandomRoute(double distance){
		// Calculation random positions
		map.clear();
		marks=0;
		finalRoute.clear();
		double a = Math.random();
		double b = Math.random();

		double r = distance / Constants.LAT_LNG_TO_METER;

		double w = r * Math.sqrt(a);
		double t = 2 * Math.PI * b;
		double x = w * Math.cos(t);
		double y = w * Math.sin(t);
		double xNew = x / Math.cos(human.getLocation().getLongitude());

		Location routePoint = new Location("route");
		routePoint.setLatitude(xNew + human.getLocation().getLatitude());
		routePoint.setLongitude(human.getLocation().getLongitude()+y);

		float bearingTo = human.getLocation().bearingTo(routePoint);
		double addLat;
		double addLng;
		double distanceFromLocation = 1000/Constants.LAT_LNG_TO_METER; 
		if(bearingTo<-90){
			bearingTo = bearingTo + 180;
			addLat = -(Math.sin(bearingTo)*distanceFromLocation);
			addLng = -(Math.cos(bearingTo)*distanceFromLocation);
		}else if(bearingTo < 0){
			bearingTo = bearingTo + 90;
			addLat = -(Math.sin(bearingTo)*distanceFromLocation);
			addLng = +(Math.cos(bearingTo)*distanceFromLocation);
		}else if(bearingTo < 90){
			addLat = +(Math.sin(bearingTo)*distanceFromLocation);
			addLng = +(Math.cos(bearingTo)*distanceFromLocation);
		}else{
			bearingTo = bearingTo - 90;
			addLat = -(Math.sin(bearingTo)*distanceFromLocation);
			addLng = +(Math.cos(bearingTo)*distanceFromLocation);	
		}
		routePoint.setLatitude(routePoint.getLatitude() + addLat);
		routePoint.setLongitude(routePoint.getLongitude() + addLng);

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
		return route;
	}



	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
		mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
	}

	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	public void playSound(View view) {
		Button button = (Button) findViewById(R.id.bPlay);
		if (!fx.getCowbell().isPlaying()){
			fx.loop(fx.getCowbell());
			button.setText("Stop sound");
		}
		else {
			fx.stopHandler();
			button.setText("Play sound");
		}
	}

	public void handleGetDirectionsResult(ArrayList<LatLng> directionPoints) {
		PolylineOptions rectLine = new PolylineOptions().width(10).color(Color.BLUE);
		int points = directionPoints.size()-1;
		//		int halfway=0; 
		for(int i = 0 ; i < directionPoints.size() ; i++) 
		{
			//points ++;
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
		marks++;

		if(finalRoute.size() == 3){
			directionFinished = true;
		}
		//		marks++;
		//		marker = map.addMarker(new MarkerOptions()
		//		.position(new LatLng(directionPoints.get(halfway).latitude, directionPoints.get(halfway).longitude))
		//		.title("halfway! " + marks + ",  " + directionPoints.get(halfway).latitude 
		//				+ " " + directionPoints.get(halfway).longitude));


		//		marker = map.addMarker(new MarkerOptions()
		//		.position(new LatLng(directionPoints.get(points).latitude, directionPoints.get(points).longitude))
		//		.title("End of route!  " + marks +",  "+ directionPoints.get(points).latitude 
		//				+ " " + directionPoints.get(points).longitude));
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

			Location curr = new Location("");
			curr.set(location);
			if(running){
				if(start){ 
					prev = curr; 
					start = false;
				} else{
					distance += prev.distanceTo(curr);
					prev = curr;
				}
			}

			db.addPoint(new database.Point(currentRoute.getId(), location.getLatitude(), location.getLongitude()));

			//Mark on the map where you walk
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

		if((human.getLocation().distanceTo(soundSource) < Constants.MIN_DISTANCE ||
				(location.getAccuracy() < 50 ? human.getLocation().distanceTo(soundSource) < location.getAccuracy() : false))
				&& directionFinished)
		{
			fx.playFX(fx.getCoin(), 0); //Plays sound to indicate coin acquired.
			human.modScore(1);
			pointsTaken++;

			if(pointsTaken>=finalRoute.size()){	
				generateRandomRoute(100);
				directionFinished = false;
				pointsTaken = 0;
			}else{
				soundSource.set(finalRoute.get(pointsTaken));
				if (marker != null){
					marker.remove();
				}
				marker = map.addMarker(new MarkerOptions()
				.position(new LatLng(soundSource.getLatitude(), soundSource.getLongitude()))
				.title("Sound" ).icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
			}
			TextView score = (TextView) findViewById(R.id.textView_score);
			score.setText("Score: "+human.getScore());
		}
		headingAngle = location.getBearing();
		if(location.hasBearing()){
			ImageView arrow = (ImageView) this.findViewById(R.id.imageView2);
			arrow.setColorFilter(android.graphics.Color.GREEN, Mode.MULTIPLY);
		}
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

		float angle = checkBox.isChecked() ? headingAngleOrientation + human.getLocation().bearingTo(soundSource): angleToSound;

		if(angle < 0){
			angle += 360;
		}

		if(fx.getCowbell().isPlaying())
			fx.update(fx.getCowbell(), (angle),
					human.getLocation().distanceTo(soundSource));

		//		//Text debug:
		//		TextView angleText = (TextView) findViewById(R.id.textView_angle);
		//		angleText.setText("Angle: "+ angle);
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

	public void startWatch(){
		m_handlerTask = new Runnable()
		{
			@Override 
			public void run() {
				if(!pause){
					seconds++;
					updateDisplay();
				}
				else {
					m_handler.removeCallbacks(m_handlerTask);
				}
				m_handler.postDelayed(m_handlerTask, 1000);
			}
		};
		m_handlerTask.run(); 
	}

	public void pauseWatch(){
		if (pause){
			pause = false;
		}else{
			pause = true;
		}
	}

	public void updateDisplay(){
		Time t = new Time();
		t.set(seconds*1000);
		t.switchTimezone("GMT");
		tx_time.setText(t.format("%H:%M:%S")); 
		tx_distance.setText("Total Dist: "+distance+"m");
		tx_speed.setText("Speed: "+((distance/seconds)*3.6)+"km/h"); 
	}
}