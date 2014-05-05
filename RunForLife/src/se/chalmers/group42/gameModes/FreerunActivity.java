package se.chalmers.group42.gameModes;

import se.chalmers.group42.controller.MapFragment;
import se.chalmers.group42.controller.RunActivity;
import se.chalmers.group42.runforlife.Constants;
import se.chalmers.group42.runforlife.FXHandler2;
import se.chalmers.group42.runforlife.Human;
import se.chalmers.group42.runforlife.R;
import utils.LocationHelper;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;

/**
 * A game of collecting coins.
 * 
 * @author Joakim Johansson
 * 
 */
public class FreerunActivity extends RunActivity {
	public static LatLng DEFAULT_POSITION = new LatLng(58.705477, 11.990884);
	public static int GAME_MODE_ID = 3;
	public static int RADIUS = 100;

	//private static final int DIALOG_REALLY_EXIT_ID = 0;

	private Human human; // Containing the player position and score
	private float orientation; // Compass angle
	private Location coinLocation; // Location of the sound source / Location of
	// current coin

	// Handles the sound
	private FXHandler2 fx;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		human = new Human();
		coinLocation = LocationHelper.locationFromLatlng(DEFAULT_POSITION);

		// Initialize audio
		(fx = new FXHandler2()).initSound(this);
		
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
		// Update human location
		this.human.setLocation(location);
		
		if(coinLocation == null)
			generatePoint(RADIUS, location);

		// If a coin is found..
		if (isAtCoin() && dataHandler.isRunning()) {
			dataHandler.onAquiredCoin(human.getLocation());
			// Increase the player score by one
			this.human.modScore(1);

			//Signal user
			coinFound();
			

			// And generate a new coin to search for
			generatePoint(RADIUS, location);
		}

		// If a current coin is set
		if (this.coinLocation != null && !usingGyro())
			adjustPanoration();
	}

	private void pointArrowToSource_Compass() {
		ImageView arrow = (ImageView) this
				.findViewById(R.id.imageView_arrow);
		if(arrow != null)
			arrow.setRotation(getRotation());
	}

	@Override
	public void onOrientationChanged(float orientation) {
		super.onOrientationChanged(orientation);
		//human.getLocation().setBearing(orientation);
		this.orientation = orientation;
		if(this.coinLocation != null){
			adjustPanoration();
			pointArrowToSource_Compass();
		}
		//		// Update compass value
		//		this.compassFromNorth = headingAngleOrientation;
		//
		//		// If a current coin is set
		//		if (usingCompass() && this.coinLocation != null) {
		//			adjustPanoration();
		//			pointArrowToSource_Compass(headingAngleOrientation);
		//		}
	}

	private boolean isAtCoin() {
		return (// If closer than minimum distance
				human.getLocation().distanceTo(coinLocation) < Constants.MIN_DISTANCE
				// Or the accuracy is less than 50 meters but still larger
				// than the distance to the sound source.
				|| (human.getLocation().getAccuracy() < 50 ? human.getLocation()
						.distanceTo(coinLocation) < human.getLocation().getAccuracy()
						: false));
	}

	private void coinFound() {
		// Play sound of a coin
		fx.foundCoin();

		MapFragment mapFrag = (MapFragment) getSupportFragmentManager()
				.findFragmentByTag("android:switcher:" + R.id.pager + ":1");

		// Show collected coin on the map
		mapFrag.showCollectedCoin(human.getLocation());

	}

	private void adjustPanoration() {
		// negate to invert angle
		float angle = -getRotation();

		float distance = human.getLocation().distanceTo(coinLocation) 
				- Constants.MIN_DISTANCE; //Subtracting the distance that a coin can be picked up from

		if (fx.getNavigationFX().isPlaying())
			fx.update(fx.getNavigationFX(), (angle), distance);

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
	 * Gets the rotation according to the GPS bearing or the GyroGPSFusion, depending on the value of usingGyro(). 
	 * Rotating an upwards pointing arrow with this value will make the arrow point in the direction
	 * of the source
	 */
	public float getRotation() {
		float bearingTo = human.getLocation().bearingTo(coinLocation);
		if (bearingTo < 0) {
			bearingTo += 360;
		}
		float tempAngle = bearingTo - 
				(usingGyro() ? 
						this.orientation : 
							human.getLocation().getBearing());
		// Marcus added this, was problem before
		if(tempAngle > 180){
			tempAngle = tempAngle - 360;
		}else if(tempAngle < -180){
			tempAngle = tempAngle + 360;
		}
		return tempAngle;
	}

	private Location generatePoint(double radius, Location origo){

		double v = 2 * Math.PI * Math.random();


		double addLat = Math.sin(v) * radius;
		double addLong = Math.cos(v) * radius
				/ Math.cos(Math.toRadians(human.getLocation().getLatitude()));


		Location location = new Location("new Location");
		location.setLongitude(origo.getLongitude() + addLong);
		location.setLatitude(origo.getLatitude() + addLat);


		return location;

	}

}
