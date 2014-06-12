package se.chalmers.group42.gameModes;

import se.chalmers.group42.controller.MapFragment;
import se.chalmers.group42.controller.RunActivity;
import se.chalmers.group42.runforlife.Constants;
import se.chalmers.group42.runforlife.FXHandler;
import se.chalmers.group42.runforlife.FXHandler2;
import se.chalmers.group42.runforlife.Human;
import se.chalmers.group42.runforlife.Drone;
import se.chalmers.group42.runforlife.DroneLocationListener;
import se.chalmers.group42.runforlife.R;
import se.chalmers.group42.utils.LocationHelper;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * A game of being chased by monsters.
 * 
 * @author Joakim Johansson
 * 
 */
public class DroneHuntActivity extends RunActivity implements DroneLocationListener {
	public static LatLng 	DEFAULT_POSITION 			= new LatLng(58.705477, 11.990884);
	public static int 		GAME_MODE_ID 				= 1;
	public static int 		MAX_MONSTER_SPAWN_DISTANCE 	= 500;
	public static int 		MIN_MONSTER_SPAWN_DISTANCE	= 210;
	public static float 	MONSTER_SPEED 				= 30; //	Meters per second

	private Human 	human; 				// Containing the player position and score
	private Drone 	drone;			// Containing the drone position
	private float 	compassFromNorth; 	// Compass angle

	private Marker monsterMarker;

	// Handles the sound
	private FXHandler2 sound; //TODO: Add sound of vildvittror

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		human = new Human();

		//generateNewMonster();

		// Initialise audio
		(sound = new FXHandler2()).initSound(this);
		sound.startLoop();
	}

	private void generateNewDrone() {
		Location droneLocation = new Location(human.getLocation());
		float bearing = (float) (Math.random() * 360);
		float distance = (float) ((Math.random() * 
				(MAX_MONSTER_SPAWN_DISTANCE - MIN_MONSTER_SPAWN_DISTANCE)
				+ MIN_MONSTER_SPAWN_DISTANCE));


		LocationHelper.moveLocation(droneLocation, bearing, distance);

		//		android.util.Log.d("Monster", 
		//				"Distance: "+ distance + 
		//				"\tLat: " 	+ monsterLocation.getLatitude() +
		//				"\tLng: " 	+ monsterLocation.getLongitude());



		MapFragment mapFrag = (MapFragment) getSupportFragmentManager().findFragmentByTag(
				"android:switcher:"+R.id.pager+":1");	

		monsterMarker = mapFrag.newMarker(
				LocationHelper.latlngFromLocation(
						droneLocation), 
						R.drawable.map_coin, 
						"Rouge drone!"
				);

		drone = new Drone(droneLocation, MONSTER_SPEED, this);
		drone.setHunter(human.getLocation());

		android.util.Log.d("Monster", "Distance2: "+ human.getLocation().distanceTo(drone.getLocation()));
	}

	public int getScore() {
		return human.getScore();
	}

	@Override
	public void onLocationChanged(Location location) {
		super.onLocationChanged(location);

		// Update human location
		human.setLocation(location);

		if(drone == null)
			generateNewDrone();

		// Update monster target
		drone.setHunter(human.getLocation());

		//	Toast.makeText(this, human.getLocation().distanceTo(monster.getLocation()) + "meters", Toast.LENGTH_SHORT).show();

		updateMarker();
		//fx.distanceAnnouncer();

		adjustPanoration();
	}

	private void updateMarker() {
		

		final MapFragment mapFrag = (MapFragment) getSupportFragmentManager().
				findFragmentByTag(
						"android:switcher:"+R.id.pager+":1");
		
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				monsterMarker.setPosition( 
						LocationHelper.latlngFromLocation(drone.getLocation()));
				monsterMarker.setTitle(
						"Rouge drone! " +
						"\nPower: "	 + drone.getRemainingPower() + 
						"\nDistance: " + human.getLocation().distanceTo(drone.getLocation()));
				
				((TextView) findViewById(R.id.textView_distance)).setText(
						"Distance: "+ 
								human.getLocation().distanceTo(
										drone.getLocation()));
			}
		});

	}

	@Override
	public void onOrientationChanged(float headingAngleOrientation) {
		super.onOrientationChanged(headingAngleOrientation);

		// Update compass value
		this.compassFromNorth = headingAngleOrientation;
	}

	@SuppressWarnings("unused")
	private boolean eatenByMonster() {
		return (// If closer than minimum distance
				human.getLocation().distanceTo(drone.getLocation()) < Constants.MIN_DISTANCE
				// Or the accuracy is less than 50 meters but still larger
				// than the distance to the sound source.
				|| (human.getLocation().getAccuracy() < 50 ? human.getLocation()
						.distanceTo(drone.getLocation()) < human.getLocation().getAccuracy()
						: false));
	}

	private boolean usingCompass() {
		// Use compass if human is moving in less than 1 m/s
		return human.getLocation().getSpeed() < 1;
	}

	private void adjustPanoration() {

		float distance = human.getLocation().distanceTo(drone.getLocation()) 
				- Constants.MIN_DISTANCE; //Subtracting the distance that a coin can be picked up from
		float bearing = human.getLocation().bearingTo(drone.getLocation());

		sound.setListenerOrientation(-	human.getLocation().getBearing());

		float[] xy = LocationHelper.getCoordFromDistBear(distance, bearing);
		sound.setSoundSourcePosition(xy[0], xy[1], 1);

	}

	@Override
	protected void playSound() {
		super.playSound();

		sound.startLoop();

	}

	@Override
	protected void stopSound() {
		super.stopSound();

		sound.stopLoop();
	}

	/**
	 * Gets the rotation according to the GPS bearing Rotating an upwards
	 * pointing arrow with this value will make the arrow point in the direction
	 * of the source
	 */
	public float getRotation_GPS() {
		float bearingTo = human.getLocation().bearingTo(drone.getLocation());
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
		return compassFromNorth + human.getLocation().bearingTo(drone.getLocation());
	}

	@Override
	public void onMonsterLocationUpdated(Location location) {
		updateMarker();
		adjustPanoration();
	}

}
