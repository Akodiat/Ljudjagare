package se.chalmers.group42.runforlife;

import java.util.Timer;
import java.util.TimerTask;

import se.chalmers.group42.gameModes.EscapeActivity;
import utils.LocationHelper;
import android.location.Location;
import android.util.Log;

public class Monster {
	private 	EscapeActivity activity;
	private 	Location 	location;				//Location of the monster
	private		Location 	target;					//Location of the target (that the monster hunts)
	private 	float 		speed;					//Meters per second

	private 	Timer 		timer = new Timer();
	private 	long 		delayTime = 1500;		//Delay in milliseconds


	public Monster(EscapeActivity activity, Location location, float speed){
		this.activity 	= activity;
		this.location 	= location;
		this.target		= location;
		this.speed 		= speed;

		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				huntTarget();	
			}
		};
		timer.schedule(task, 0, delayTime);
	}

	public void setSpeed(float speed){
		this.speed = speed;
	}

	public void setTarget(Location target){
		this.target = target;
	}

	public Location getLocation(){
		return location;
	}

	public void huntTarget(){
		float distance 	= speed * (delayTime/1000);
		
		//if(location.distanceTo(target) > distance){
			float direction = location.bearingTo(target);
			Log.d("Monster", "moving " + distance + "m in " + direction +"deg.");
			LocationHelper.moveLocation(target, direction, -distance);
		//}
		//activity.onMonsterLocationChanged();
	}
}
