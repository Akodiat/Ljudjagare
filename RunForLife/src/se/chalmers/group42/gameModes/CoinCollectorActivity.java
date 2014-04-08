package se.chalmers.group42.gameModes;

import java.util.ArrayList;

import se.chalmers.group42.controller.FinishedRunActivity;
import se.chalmers.group42.controller.MapFragment;
import se.chalmers.group42.controller.RunActivity;
import se.chalmers.group42.controller.SettingsActivity;
import se.chalmers.group42.runforlife.Constants;
import se.chalmers.group42.runforlife.FXHandler;
import se.chalmers.group42.runforlife.GMapV2Direction;
import se.chalmers.group42.runforlife.Human;
import se.chalmers.group42.runforlife.R;
import utils.LocationHelper;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

/**
 * A game of collecting coins.
 * 
 * @author Joakim Johansson
 * 
 */
public class CoinCollectorActivity extends RunActivity {
	public static LatLng DEFAULT_POSITION = new LatLng(58.705477, 11.990884);
	public static int GAME_MODE_ID = 0;
	private static final int DIALOG_REALLY_EXIT_ID = 0;

	private Human human; // Containing the player position and score
	private float compassFromNorth; // Compass angle
	private Location coinLocation; // Location of the sound source / Location of
	// current coin

	private ArrayList<Location> finalRoute = new ArrayList<Location>();
	// Handles the sound
	private FXHandler fx;
	private boolean generateRoute = true;

	private int curr100, prev100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		human = new Human();
		curr100 = Constants.RUN_DISTANCE;
		coinLocation = LocationHelper.locationFromLatlng(DEFAULT_POSITION);

		// Initialize audio
		(fx = new FXHandler()).initSound(this);
	}

	public int getScore() {
		return human.getScore();
	}

	// Get the finalRoute from mapFragment when the route is calculated.
	@Override
	public void sendFinalRoute(ArrayList<Location> finalRoute, float distance) {
		this.finalRoute = finalRoute;
		coinLocation = finalRoute.get(0);
		final TextView textViewToChange = (TextView) findViewById(R.id.textView_distance);
		textViewToChange.setText(distance + " m");
	}

	// Ask if you really want to close the activity
	// From,
	// http://www.c-sharpcorner.com/UploadFile/88b6e5/display-alert-on-back-button-pressed-in-android-studio/
	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setMessage("Do you  want to exit the run?");
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// if user pressed "yes", then he is allowed to exit from
				// application
				// Ska vara "finish()" egentligen men det fungerar inte?
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// if user select "No", just cancel this dialog and continue
				// with app
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	public void onLocationChanged(Location location) {
		super.onLocationChanged(location);

		// map.animateCamera(CameraUpdateFactory.newLatLngZoom(new
		// LatLng(location.getLatitude(), location.getLongitude()), 12.0f));

		// Update human location
		this.human.setLocation(location);

		if (generateRoute) {
			generateRoute = false;
			//Retrieving distancevalue from preferences
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
			//Setting value to 700 meters if no values have been set
			String distance = sharedPref.getString("distance", "700");
			generateRandomRoute(Integer.parseInt(distance));

			MapFragment mapFrag = (MapFragment) getSupportFragmentManager()
					.findFragmentByTag("android:switcher:" + R.id.pager + ":1");
			mapFrag.zoomToPosition(location);
		}

		// If a coin is found..
		if (isAtCoin()) {
			dataHandler.onAquiredCoin(human.getLocation());
			// Increase the player score by one
			this.human.modScore(1);

			// Play sound of a coin
			fx.foundCoin();

			// And generate a new coin to search for
			generateNewCoin();
		}

		// If a current coin is set
		if (this.coinLocation != null)
			adjustPanoration();
	}

	private void pointArrowToSource_Compass(float headingAngleOrientation) {
		if (generateRoute == false) {
			ImageView arrow = (ImageView) this
					.findViewById(R.id.imageView_arrow);
			arrow.setRotation(headingAngleOrientation
					+ human.getLocation().bearingTo(coinLocation));
		}
	}

	@Override
	public void onCompassChanged(float headingAngleOrientation) {
		super.onCompassChanged(headingAngleOrientation);

		// Update compass value
		this.compassFromNorth = headingAngleOrientation;

		// If a current coin is set
		if (usingCompass() && this.coinLocation != null) {
			adjustPanoration();
			pointArrowToSource_Compass(headingAngleOrientation);
		}
	}

	private boolean isAtCoin() {
		double dist = human.getLocation().distanceTo(coinLocation);
		return (// If closer than minimum distance
				human.getLocation().distanceTo(coinLocation) < Constants.MIN_DISTANCE
				// Or the accuracy is less than 50 meters but still larger
				// than the distance to the sound source.
				|| (human.getLocation().getAccuracy() < 50 ? human.getLocation()
						.distanceTo(coinLocation) < human.getLocation().getAccuracy()
						: false));
	}

	private void generateNewCoin() {
		if (human.getScore() < 3) {
			coinLocation = this.finalRoute.get(human.getScore());

			float distance = human.getLocation().distanceTo(coinLocation) 
					- Constants.MIN_DISTANCE;
			curr100 = ((int) (distance / 100)) * 100;
			
			MapFragment mapFrag = (MapFragment) getSupportFragmentManager()
					.findFragmentByTag("android:switcher:" + R.id.pager + ":1");
			mapFrag.handleNewCoin(coinLocation);
			// Show collected coin on the map
			mapFrag.showCollectedCoin(human.getLocation());

		} else {
			stop();
		}
	}

	private boolean usingCompass() {
		return Constants.USING_COMPASS; // human.getLocation().getSpeed() < 1;
	}

	private void adjustPanoration() {

		// negate to invert angle
		float angle = -(usingCompass() ? compassFromNorth
				+ human.getLocation().bearingTo(coinLocation)
				: getRotation_GPS());

		float distance = human.getLocation().distanceTo(coinLocation) 
				- Constants.MIN_DISTANCE; //Subtracting the distance that a coin can be picked up from

		if (fx.getNavigationFX().isPlaying())
			fx.update(fx.getNavigationFX(), (angle), distance);

		if (distance <= 100) {
			curr100 = 0;
			//float delayRatio = distance / 100;
			float delayRatio = (float) Math.pow(distance / 100, 2);
			fx.sayDistance(fx.getSpeech(curr100));
			fx.updateDelay((Constants.MAX_DELAY - Constants.MIN_DELAY)
					* delayRatio + Constants.MIN_DELAY);
		} else {
			// if user has moved forward to new 100s
			if (distance - curr100 <= 0) {

				prev100 = curr100; // set previous
				int currHundred = ((int) (distance / 100));
				curr100 = currHundred * 100;
				fx.sayDistance(fx.getSpeech(currHundred));
			}

			else if (!(distance - curr100 > 100)) {
				float delayRatio, newDist = distance % 100;
				//delayRatio = newDist / 100;
				delayRatio = (float) Math.pow(newDist / 100, 2);
				fx.updateDelay((Constants.MAX_DELAY - Constants.MIN_DELAY)
						* delayRatio + Constants.MIN_DELAY);
			}
		}
	}

	@Override
	protected void playSound() {
		super.playSound();

		if (!fx.getNavigationFX().isPlaying())
			fx.loop(fx.getNavigationFX());

	}

	/**
	 * Play long sound without audible repetition.
	 */
	// @Override
	// protected void playLongSound() {
	// super.playSound();
	//
	// if (!fx.getNavigationFX().isPlaying())
	// fx.loopLong(fx.getNavigationFX());
	// }

	@Override
	protected void stopSound() {
		super.stopSound();

		fx.stopLoop();
	}

	/**
	 * Gets the rotation according to the GPS bearing Rotating an upwards
	 * pointing arrow with this value will make the arrow point in the direction
	 * of the source
	 */
	public float getRotation_GPS() {
		float bearingTo = human.getLocation().bearingTo(coinLocation);
		if (bearingTo < 0) {
			bearingTo += 360;
		}
		return bearingTo - human.getLocation().getBearing();

	}

	private float GPS_Bearing(){
		return 	human.getOldLocation().bearingTo(human.getLocation());
	}

	/**
	 * Gets the rotation according to the compass Rotating an upwards pointing
	 * arrow with this value will make the arrow point in the direction of the
	 * source
	 */
	public float getRotation_Compass() {
		return compassFromNorth + human.getLocation().bearingTo(coinLocation);
	}

	private ArrayList<Location> generateRoute(int checkpoints, double radius, Location origo){
		double radiansPerCheckpoint = 2 * Math.PI / checkpoints;
		ArrayList<Location> route = new ArrayList<Location>();
		double currentCheckpoint = 2 * Math.PI * 3/4;
		
		for(int i = 0; i < checkpoints; i++){
			currentCheckpoint -= radiansPerCheckpoint;
			double addLat = Math.sin(currentCheckpoint) * radius;
			double addLong = Math.cos(currentCheckpoint) * radius / Math.cos(Math.toRadians(human.getLocation().getLatitude()));
			
			Location routeLocation = new Location("new Location");
			routeLocation.setLongitude(origo.getLongitude() + addLong);
			routeLocation.setLatitude(origo.getLatitude() + addLat);
			
			route.add(routeLocation);
		}
		
		return route;
		
	}
	
	private ArrayList<Location> generateRandomRoute(double distance) {
		// Calculation random positions
		int checkpoints = 10;
		
		finalRoute.clear();
		double a = Math.random();
		double b = Math.random();

		double r = 50 / Constants.LAT_LNG_TO_METER;

		double w = r * Math.sqrt(a);
		double t = 2 * Math.PI * b;
		double x = w * Math.cos(t);
		double y = w * Math.sin(t);
		double xNew = x / Math.cos(Math.toRadians(human.getLocation().getLatitude()));

		Location routePoint = new Location("route");
		routePoint.setLongitude(xNew + human.getLocation().getLongitude());
		routePoint.setLatitude(human.getLocation().getLatitude() + y);

		
		double bearingTo = human.getLocation().bearingTo(routePoint);
		if (bearingTo < 0) {
			bearingTo += 360;
		}
		bearingTo = Math.toRadians(bearingTo);
		
		double addLat;
		double addLng;
		double distanceFromLocation = distance / Constants.LAT_LNG_TO_METER;
		addLat = Math.sin(bearingTo) * distanceFromLocation;
		addLng = Math.cos(bearingTo) * distanceFromLocation / Math.cos(Math.toRadians(human.getLocation().getLatitude()));
		routePoint.setLatitude(routePoint.getLatitude() + addLat);
		routePoint.setLongitude(routePoint.getLongitude() + addLng);

		ArrayList<Location> route = new ArrayList<Location>();
		route.add(human.getLocation());
		route.add(routePoint);
		Location routePoint2 = new Location("route2");
		double differLat = human.getLocation().getLatitude()
				- routePoint.getLatitude();
		double differLng = (human.getLocation().getLongitude()
				- routePoint.getLongitude()) * Math.cos(Math.toRadians(human.getLocation().getLatitude()));
		
		double hypotenusa = Math.sqrt((Math.pow(differLat, 2) + Math.pow(differLng, 2)));
		
		Location newOrigo = new Location("MidLocation");
		newOrigo.setLongitude(human.getLocation().getLongitude());
		newOrigo.setLatitude(human.getLocation().getLatitude() + (hypotenusa / 2));

		ArrayList<Location> routeTest = generateRoute(checkpoints, hypotenusa / 2, newOrigo);
		
		double angle = human.getLocation().bearingTo(routePoint);
		if (angle < 0) {
			angle += 360;
		}
		angle = Math.toRadians(angle);
		
		for(int i = 0; i < routeTest.size(); i++){
			//float angleToRoute = human.getLocation().bearingTo(routeTest.get(i));
			//float angleToNew = angleToRoute + angle;
			double diffLong = -(human.getLocation().getLongitude() - routeTest.get(i).getLongitude()) * Math.cos(Math.toRadians(human.getLocation().getLatitude()));
			double diffLat = -(human.getLocation().getLatitude() - routeTest.get(i).getLatitude());

			// Rotation around human location, rotation matrix
			double newLong = Math.cos(angle) * diffLong + Math.sin(angle) * diffLat;
			double newLat =  -Math.sin(angle) * diffLong + Math.cos(angle) * diffLat;

			routeTest.get(i).setLongitude(human.getLocation().getLongitude() + newLong/ Math.cos(Math.toRadians(human.getLocation().getLatitude())));
			routeTest.get(i).setLatitude(human.getLocation().getLatitude() + newLat);
		}
		
//		MapFragment mapFrag = (MapFragment) getSupportFragmentManager()
//				.findFragmentByTag("android:switcher:" + R.id.pager + ":1");
//		mapFrag.randomTest(routeTest, routePoint);
		
		
		if (Math.random() > 0.5) {
			routePoint2.setLatitude(human.getLocation().getLatitude()
					+ differLng);
			routePoint2.setLongitude(human.getLocation().getLongitude()
					- differLat);
		} else {
			routePoint2.setLatitude(human.getLocation().getLatitude()
					- differLng);
			routePoint2.setLongitude(human.getLocation().getLongitude()
					+ differLat);
		}
		route.add(routePoint2);
		route.add(human.getLocation());

		for (int i = 0; i < route.size() - 1; i++) {
			findDirections(route.get(i).getLatitude(), route.get(i)
					.getLongitude(), route.get(i + 1).getLatitude(),
					route.get(i + 1).getLongitude(),
					GMapV2Direction.MODE_WALKING);
		}
		return route;
	}
}
