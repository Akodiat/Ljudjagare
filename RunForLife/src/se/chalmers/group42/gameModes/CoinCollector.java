package se.chalmers.group42.gameModes;

import com.google.android.gms.maps.model.LatLng;

import android.location.Location;
import se.chalmers.group42.runforlife.Constants;
import se.chalmers.group42.runforlife.GameMode;
import se.chalmers.group42.runforlife.Human;
import se.chalmers.group42.runforlife.SensorValues;
import utils.LocationHelper;

/**
 * A game of collecting coins.
 * @author joakim
 *
 */
public class CoinCollector implements GameMode{
	private static 	LatLng DEFAULT_POSITION = new LatLng(58.705477, 11.990884);
	
	private 	Human 			human;				//Containing the player position and score
	private 	float 			compassFromNorth; 	//Compass angle
	private 	Location 		soundSource;		//Location of the sound source
	

	public CoinCollector(){
		soundSource = LocationHelper.locationFromLatlng(DEFAULT_POSITION);
	}
	
	@Override
	public int getScore() {
		return human.getScore();
	}

	@Override
	public void onSensorUpdate(SensorValues sensorValues) {
		this.human.setLocation(sensorValues.getLocation());
		this.compassFromNorth = sensorValues.getCompassFromNorth();
		
		//If a coin is found..
		if((human.getLocation().distanceTo(soundSource) < Constants.MIN_DISTANCE ||
				(human.getLocation().getAccuracy() < 50 ? 
						human.getLocation().distanceTo(soundSource) < 
						human.getLocation().getAccuracy() 
						: false
				)
			))
		{
			//Increase the player score by one
			this.human.modScore(1);
			
			//And generate a new coin to search for
			generateNewCoin();
		}	
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
