package se.chalmers.group42.database;


public class Point {
	private int id;
	private int routeId;
	private Long time;
	private Double latitude,longitude;
	
	public Point (){}
	
	public Point (int routeId, Double latitude, Double longitude){
		super();
		this.routeId = routeId;
		this.latitude = latitude;
		this.longitude = longitude;
		
		time = System.currentTimeMillis();
		
	}
	
	@Override
	public String toString(){
		return "[ID: "+id+" Route: "+routeId+" Position: " + longitude+":"+latitude + " Time: " + time +"]";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public int getRouteId() {
		return routeId;
	}

	public void setRouteId(int routeId) {
		this.routeId = routeId;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	
	
}
