package se.chalmers.group42.gameModes;

import se.chalmers.group42.runforlife.*;
import utils.LocationHelper;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

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
	private 	Location 		coinLocation;		//Location of the sound source / Location of current coin
	
	// Handles the sound
	private FXHandler fx;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		coinLocation = LocationHelper.locationFromLatlng(DEFAULT_POSITION);
		
		// Initialise audio
		(fx = new FXHandler()).initSound(this);
	}

	public int getScore() {
		return human.getScore();
	}
	
	@Override
	public void onUpdatedSensors(SensorValues sensorValues) {
		super.onUpdatedSensors(sensorValues);

		//Update human location
		this.human.setLocation(sensorValues.getLocation());

		//Update compass value
		this.compassFromNorth = sensorValues.getCompassFromNorth();

		//If a coin is found..
		if(isAtCoin())
		{
			//Increase the player score by one
			this.human.modScore(1);
			
			//Play sound of a coin
			fx.playFX(fx.getCoin(), 0);

			//And generate a new coin to search for
			generateNewCoin();
		}
		
		//If a current coin is set
		if(this.coinLocation != null)
			adjustPanoration();
	}


	private boolean isAtCoin(){

		return (//If closer than minimum distance
				human.getLocation().distanceTo(coinLocation) 
				< Constants.MIN_DISTANCE 
				//Or the accuracy is less than 50 meters but still larger 
				//than the distance to the sound source.
				||
				(human.getLocation().getAccuracy() < 50 ? 
						human.getLocation().distanceTo(coinLocation) < 
						human.getLocation().getAccuracy() 
						: 
							false)
				);
	}

	private void generateNewCoin() {
		// TODO Auto-generated method stub

	}
	
	private boolean usingCompass() {
		//Use compass if human is moving in less than 1 m/s
		return human.getLocation().getSpeed() < 1;
	}
	

	private void adjustPanoration() {

		float angle = usingCompass() ? 
				compassFromNorth + human.getLocation().bearingTo(coinLocation):
					getRotation_GPS();

//		if(angle < 0){
//			angle += 360;
//		}

		if(fx.getNavigationFX().isPlaying())
			fx.update(fx.getNavigationFX(), (angle),
					human.getLocation().distanceTo(coinLocation));
		
	}
	@Override
	protected void playSound() {
		super.playSound();
		
		if (!fx.getNavigationFX().isPlaying())
			fx.loop(fx.getNavigationFX());
		else
			fx.stopLoop();
	}
	
	

	/**
	 * Gets the rotation according to the GPS bearing
	 * Rotating an upwards pointing arrow with this value will
	 * make the arrow point in the direction of the source
	 */
	public float getRotation_GPS() {
		float bearingTo = human.getLocation().bearingTo(coinLocation);
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
		return compassFromNorth + human.getLocation().bearingTo(coinLocation);
	}
}
