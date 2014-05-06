package se.chalmers.group42.gameModes;

import se.chalmers.group42.controller.MapFragment;
import se.chalmers.group42.controller.RunActivity;
import se.chalmers.group42.runforlife.Constants;
import se.chalmers.group42.runforlife.FXHandler;
import se.chalmers.group42.runforlife.Human;
import se.chalmers.group42.runforlife.Monster;
import se.chalmers.group42.runforlife.R;
import se.chalmers.group42.utils.LocationHelper;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

/**
 * A game of being chased by monsters.
 * 
 * @author Joakim Johansson
 * 
 */
public class EscapeActivity extends RunActivity {
	public static LatLng 	DEFAULT_POSITION 			= new LatLng(58.705477, 11.990884);
	public static int 		GAME_MODE_ID 				= 1;
	public static int 		MAX_MONSTER_SPAWN_DISTANCE 	= 500;
	public static int 		MIN_MONSTER_SPAWN_DISTANCE	= 210;
	public static float 	MONSTER_SPEED 				= 3; //	Meters per second

	private Human 	human; 				// Containing the player position and score
	private Monster monster;			// Containing the monster position
	private float 	compassFromNorth; 	// Compass angle

	// Handles the sound
	private FXHandler fx; //TODO: Add sound of vildvittror

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		human = new Human();

		//generateNewMonster();

		// Initialise audio
		(fx = new FXHandler()).initSound(this);
		playSound();
	}

	private void generateNewMonster() {
		Location monsterLocation = new Location(human.getLocation());
		float bearing = (float) (Math.random() * 360);
		float distance = (float) ((Math.random() * 
				(MAX_MONSTER_SPAWN_DISTANCE - MIN_MONSTER_SPAWN_DISTANCE)
				+ MIN_MONSTER_SPAWN_DISTANCE));


		LocationHelper.moveLocation(monsterLocation, bearing, distance);

//		android.util.Log.d("Monster", 
//				"Distance: "+ distance + 
//				"\tLat: " 	+ monsterLocation.getLatitude() +
//				"\tLng: " 	+ monsterLocation.getLongitude());

		monster = new Monster(this, monsterLocation, MONSTER_SPEED);
		monster.setTarget(human.getLocation());

		android.util.Log.d("Monster", "Distance2: "+ human.getLocation().distanceTo(monster.getLocation()));
	}

	public int getScore() {
		return human.getScore();
	}

	public void onMonsterLocationChanged(){
		//Toast.makeText(this, human.getLocation().distanceTo(monster.getLocation()) + "meters", Toast.LENGTH_SHORT).show();

		updateMarker();
		//fx.distanceAnnouncer();
	}

	@Override
	public void onLocationChanged(Location location) {
		super.onLocationChanged(location);
		
		// Update human location
		human.setLocation(location);
		
		if(monster == null)
			generateNewMonster();

		// Update monster target
		monster.setTarget(human.getLocation());

	//	Toast.makeText(this, human.getLocation().distanceTo(monster.getLocation()) + "meters", Toast.LENGTH_SHORT).show();

		updateMarker();
		//fx.distanceAnnouncer();

		adjustPanoration();
	}
	
	private void updateMarker() {
		Log.d("Monster", "Distance: "+ human.getLocation().distanceTo(monster.getLocation()));
		MapFragment mapFrag = (MapFragment) getSupportFragmentManager().findFragmentByTag(
				"android:switcher:"+R.id.pager+":1");
		//mapFrag.handleNewCoin(monster.getLocation());
		// Show collected coin on the map
		mapFrag.showCollectedCoin(monster.getLocation());	
	}

	@Override
	public void onOrientationChanged(float headingAngleOrientation) {
		super.onOrientationChanged(headingAngleOrientation);

		// Update compass value
		this.compassFromNorth = headingAngleOrientation;
	}

	private boolean eatenByMonster() {
		return (// If closer than minimum distance
				human.getLocation().distanceTo(monster.getLocation()) < Constants.MIN_DISTANCE
				// Or the accuracy is less than 50 meters but still larger
				// than the distance to the sound source.
				|| (human.getLocation().getAccuracy() < 50 ? human.getLocation()
						.distanceTo(monster.getLocation()) < human.getLocation().getAccuracy()
						: false));
	}

	private boolean usingCompass() {
		// Use compass if human is moving in less than 1 m/s
		return human.getLocation().getSpeed() < 1;
	}

	private void adjustPanoration() {

		float angle = usingCompass() ? compassFromNorth
				+ human.getLocation().bearingTo(monster.getLocation())
				: getRotation_GPS();

				// if(angle < 0){
				// angle += 360;
				// }

				if (fx.getNavigationFX().isPlaying())
					fx.update(fx.getNavigationFX(), (angle));

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
		float bearingTo = human.getLocation().bearingTo(monster.getLocation());
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
		return compassFromNorth + human.getLocation().bearingTo(monster.getLocation());
	}

}
