package se.chalmers.group42.runforlife;

import se.chalmers.group42.runforlife.Drone.DroneStatus;
import android.location.Location;

public interface DroneListener {
	public void onMonsterLocationUpdated(Location location);
	public void onMonsterStatusUpdated(DroneStatus status, String statusText);
}
