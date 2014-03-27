package se.chalmers.group42.database;

import android.location.Location;

public class Coins {
	private int id;
	private int routeID;
	private Location location = new Location("");
	private long time;
	private int distance;
	
	public Coins(){}
	public Coins(int routeID, Location location, long time, int distance){
		this.routeID = routeID;
		this.location = location;
		this.time = time;
		this.distance = distance;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getRouteID() {
		return routeID;
	}

	public void setRouteID(int routeID) {
		this.routeID = routeID;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	@Override
	public String toString(){
		return "[ID: "+id+" Loc: "+location.getLatitude()+":"+location.getLongitude()+"]";
	}
	
}
