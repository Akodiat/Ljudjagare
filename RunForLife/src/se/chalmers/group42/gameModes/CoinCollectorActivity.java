package se.chalmers.group42.gameModes;

import se.chalmers.group42.runforlife.Constants;
import se.chalmers.group42.runforlife.Human;
import se.chalmers.group42.runforlife.RunActivity;
import se.chalmers.group42.runforlife.SensorValues;
import utils.LocationHelper;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * A game of collecting coins.
 * @author Joakim Johansson
 *
 */
public class CoinCollectorActivity extends RunActivity {
	public	static 	LatLng 	DEFAULT_POSITION 	= new LatLng(58.705477, 11.990884);
	public 	static 	int 	GAME_MODE_ID 		= 0;

	private 	Human 			human;				//Containing the player position and score
	private 	float 			compassFromNorth; 	//Compass angle
	private 	Location 		soundSource;		//Location of the sound source


	public CoinCollectorActivity(){
		soundSource = LocationHelper.locationFromLatlng(DEFAULT_POSITION);
	}

	public int getScore() {
		return human.getScore();
	}

	public void onSensorUpdate(SensorValues sensorValues) {
		//Update human location
		this.human.setLocation(sensorValues.getLocation());

		//Update compass value
		this.compassFromNorth = sensorValues.getCompassFromNorth();

		//If a coin is found..
		if(isAtCoin())
		{
			//Increase the player score by one
			this.human.modScore(1);

			//And generate a new coin to search for
			generateNewCoin();
		}	
	}

	private boolean isAtCoin(){

		return (//If closer than minimum distance
				human.getLocation().distanceTo(soundSource) 
				< Constants.MIN_DISTANCE 
				//Or the accuracy is less than 50 meters but still larger 
				//than the distance to the sound source.
				||
				(human.getLocation().getAccuracy() < 50 ? 
						human.getLocation().distanceTo(soundSource) < 
						human.getLocation().getAccuracy() 
						: 
							false)
				);
	}

	private void generateNewCoin() {
		// TODO Auto-generated method stub

	}

	/**
	 * Gets the rotation according to the GPS bearing
	 * Rotating an upwards pointing arrow with this value will
	 * make the arrow point in the direction of the source
	 */
	public float getRotation_GPS() {
		float bearingTo = human.getLocation().bearingTo(soundSource);
		if(bearingTo < 0){
			bearingTo += 360;
		}
		return bearingTo - human.getLocation().getBearing();

	}

	/**
	 * Gets the rotation according to the compass
	 * Rotating an upwards pointing arrow with this value will
	 * make the arrow point in the direction of the source
	 */
	public float getRotation_Compass() {
		return compassFromNorth + human.getLocation().bearingTo(soundSource);
	}
}
