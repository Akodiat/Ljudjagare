package se.chalmers.group42.gameModes;

import java.util.ArrayList;

import se.chalmers.group42.runforlife.Constants;
import se.chalmers.group42.runforlife.FXHandler;
import se.chalmers.group42.runforlife.FinishedRunActivity;
import se.chalmers.group42.runforlife.GMapV2Direction;
import se.chalmers.group42.runforlife.Human;
import se.chalmers.group42.runforlife.MapFragment;
import se.chalmers.group42.runforlife.R;
import se.chalmers.group42.runforlife.RunActivity;
import utils.LocationHelper;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		human = new Human();

		coinLocation = LocationHelper.locationFromLatlng(DEFAULT_POSITION);

		// Initialise audio
		(fx = new FXHandler()).initSound(this);
		playSound();
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
			generateRandomRoute(Constants.RUN_DISTANCE);

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

	@Override
	public void onCompassChanged(float headingAngleOrientation) {
		super.onCompassChanged(headingAngleOrientation);

		// Update compass value
		this.compassFromNorth = headingAngleOrientation;

		// If a current coin is set
		if (usingCompass() && this.coinLocation != null)
			adjustPanoration();
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

			MapFragment mapFrag = (MapFragment) getSupportFragmentManager()
					.findFragmentByTag("android:switcher:" + R.id.pager + ":1");
			mapFrag.handleNewCoin(coinLocation);
			// Show collected coin on the map
			mapFrag.showCollectedCoin(human.getLocation());

		} else {
			dataHandler.resetWatch();
			stopSound();

			Intent finishedRunActivityIntent = new Intent(this,
					FinishedRunActivity.class);
			finishedRunActivityIntent.putExtra("test",
					dataHandler.getCurrentRoute());
			startActivity(finishedRunActivityIntent);
			if (asyncTask != null) {
				asyncTask.cancel(true);
			}
			// Ska vara "finish()" egentligen men det fungerar inte?
			android.os.Process.killProcess(android.os.Process.myPid());
		}
	}

	private boolean usingCompass() {
		return Constants.USING_COMPASS; //human.getLocation().getSpeed() < 1;
	}

	private void adjustPanoration() {

		// negate to invert angle
		float angle = -(usingCompass() ? compassFromNorth
				+ human.getLocation().bearingTo(coinLocation)
				: getRotation_GPS());

		if (fx.getNavigationFX().isPlaying())
			fx.update(fx.getNavigationFX(), (angle), human.getLocation()
					.distanceTo(coinLocation));

	}

	@Override
	protected void playSound() {
		super.playSound();

		if (!fx.getNavigationFX().isPlaying())
			fx.loop(fx.getNavigationFX());

	}

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

	/**
	 * Gets the rotation according to the compass Rotating an upwards pointing
	 * arrow with this value will make the arrow point in the direction of the
	 * source
	 */
	public float getRotation_Compass() {
		return compassFromNorth + human.getLocation().bearingTo(coinLocation);
	}

	private ArrayList<Location> generateRandomRoute(double distance) {
		// Calculation random positions
		finalRoute.clear();
		double a = Math.random();
		double b = Math.random();

		double r = 50 / Constants.LAT_LNG_TO_METER;

		double w = r * Math.sqrt(a);
		double t = 2 * Math.PI * b;
		double x = w * Math.cos(t);
		double y = w * Math.sin(t);
		double xNew = x / Math.cos(human.getLocation().getLongitude());

		Location routePoint = new Location("route");
		routePoint.setLatitude(xNew + human.getLocation().getLatitude());
		routePoint.setLongitude(human.getLocation().getLongitude() + y);

		float bearingTo = human.getLocation().bearingTo(routePoint);
		double addLat;
		double addLng;
		double distanceFromLocation = distance / Constants.LAT_LNG_TO_METER;
		addLat = Math.sin(bearingTo) * distanceFromLocation;
		addLng = Math.cos(bearingTo) * distanceFromLocation;
		routePoint.setLatitude(routePoint.getLatitude() + addLat);
		routePoint.setLongitude(routePoint.getLongitude() + addLng);

		ArrayList<Location> route = new ArrayList<Location>();
		route.add(human.getLocation());
		route.add(routePoint);
		Location routePoint2 = new Location("route2");
		double differLat = human.getLocation().getLatitude()
				- routePoint.getLatitude();
		double differLng = human.getLocation().getLongitude()
				- routePoint.getLongitude();

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
