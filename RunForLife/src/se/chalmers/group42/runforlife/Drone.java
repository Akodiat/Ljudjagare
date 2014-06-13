package se.chalmers.group42.runforlife;

import java.util.Timer;
import java.util.TimerTask;

import se.chalmers.group42.utils.LocationHelper;
import android.location.Location;
import android.util.Log;

public class Drone {
	private static int 		SAFE_DISTANCE = 150;	//Distance in meters at which the drone feels safe
	private static long 	DELAY_TIME = 1500;		//Delay in milliseconds
	private 	Location 	location;				//Location of the monster
	private		Location 	hunter;					//Location of the target (that the monster hunts)
	private 	float 		speed;					//Meters per second
	private		int			power;
	private		String		description;
	private		String		statusText;
	private 	DroneStatus	status;

	private 	Timer 		timer = new Timer();
	public enum DroneStatus {FLYING, RESTING};


	private 	DroneListener listener;


	public Drone(Location location, float speed, String description, DroneListener listener){
		this.location 	= location;
		this.hunter		= location;
		this.speed 		= speed;
		this.description= description;
		this.listener	= listener;
		this.power		= 100;
		this.statusText		= "";
		this.status = DroneStatus.FLYING;

		resume();
	}
	
	public void pause(){
		timer.cancel();
		statusText = "GAME IS PAUSED";
		listener.onMonsterStatusUpdated(status, statusText);
	}
	public void resume(){
		timer = new Timer();
		
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				escapeHunter();	
			}
		};
		timer.schedule(task, 0, DELAY_TIME);
		
	}

	public void setSpeed(float speed){
		this.speed = speed;
	}

	public void setHunter(Location hunter){
		this.hunter = hunter;
	}

	public Location getLocation(){
		return location;
	}
	public String getDescription(){
		return description;
	}
	
	@Override
	public String toString() {
		return this.description + "\n" + statusText;
	}

	public int getRemainingPower(){
		return power;
	}
	
	private float distToHunter(){
		return LocationHelper.calculateDistance(location, hunter);
	}

	public void escapeHunter(){
		float distToMove 	= speed * (DELAY_TIME/1000);

		if(distToHunter() < SAFE_DISTANCE){ //If in danger
			if (power > distToMove){ 					//Escape if power left

				power -= distToMove;

				float direction = location.bearingTo(hunter);
				statusText = "Power: "+power+"%\n"+(int)distToHunter()+"m from hunter. \nMoving " + (int) distToMove + "m in " + (int) direction +"deg.";
				status = DroneStatus.FLYING;
				LocationHelper.moveLocation(
						location, 
						direction,
						distToMove
						);
				listener.onMonsterLocationUpdated(location);
			}

			else {										//If power is drained
				statusText = "Power: "+power+"%\n"+(int)distToHunter()+"m from hunter. \nCannot escape. Power drained. Resting";
				status = DroneStatus.RESTING;
				power += 2*distToMove;
				if(power > 100) power = 100;
			}
		}
		else {
			statusText = "Power: "+power+"%\n"+(int)distToHunter()+"m from hunter. \nResting";
			status = DroneStatus.RESTING;
			power += 2*distToMove;
			if(power > 100) power = 100;
		}
		Log.d("MONSTER", statusText);
		listener.onMonsterStatusUpdated(status, statusText);
	}
}
