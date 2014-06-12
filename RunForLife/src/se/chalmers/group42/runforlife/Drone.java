package se.chalmers.group42.runforlife;

import java.util.Timer;
import java.util.TimerTask;

import se.chalmers.group42.utils.LocationHelper;
import android.location.Location;
import android.util.Log;

public class Drone {
	private static int 		SAFE_DISTANCE = 500;	//Distance in meters at which the drone feels safe
	private static long 	DELAY_TIME = 1500;		//Delay in milliseconds
	private 	Location 	location;				//Location of the monster
	private		Location 	hunter;					//Location of the target (that the monster hunts)
	private 	float 		speed;					//Meters per second
	private		int			power;

	private 	Timer 		timer = new Timer();

	
	private 	DroneLocationListener listener;


	public Drone(Location location, float speed, DroneLocationListener listener){
		this.location 	= location;
		this.hunter		= location;
		this.speed 		= speed;
		this.listener	= listener;
		this.power		= 100;

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
	
	public int getRemainingPower(){
		return power;
	}

	public void escapeHunter(){
		float deltaDist 	= speed * (DELAY_TIME/1000);
		
		if(location.distanceTo(hunter) < SAFE_DISTANCE && power > deltaDist){
			power -= deltaDist;
			
			float direction = location.bearingTo(hunter);
			Log.d("Monster", "moving " + deltaDist + "m in " + direction +"deg.");
			LocationHelper.moveLocation(
					location, 
					direction,
					deltaDist
			);
			listener.onMonsterLocationUpdated(location);
		}
		else if (power < 100){
			power += 2*deltaDist;
			if(power > 100) power = 100;
		}
	}
}
