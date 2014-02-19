package se.chalmers.proofofconceptlj;

import com.google.android.gms.maps.model.LatLng;
import android.location.Location;


/**
 * 
 * @author Joakim Johansson
 *
 */
public class Human{
	private Location location;
	private int score;
	
	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	/**
	 * Increases the score with the given value
	 * @param score the value to increase the score with. Use a negative value to decrease the score.
	 */
	public void modScore(int score) {
		this.score += score;
	}

	public Human(Location location) {
		this.location 	= location;
		this.score 		= 0;
	}
	
	public Human(LatLng latLng) {
		Location loc = new Location("Trololo"); //Wierd but seems to work
		loc.setLatitude(latLng.latitude);
		loc.setLongitude(latLng.longitude);
		this.location = loc;
	}
	
	public Location getLocation(){
		return this.location;
	}
	
	public LatLng getLatLng(){
		return new LatLng(location.getLatitude(), location.getLongitude());
	}
	
	public void setLatLng(LatLng latLng){
		location.setLatitude(latLng.latitude);
		location.setLongitude(latLng.longitude);
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}

/*	public Human() {
		this(Vector2.zero(), 0);
	}
	
	public double getRotation() {
		return rotation;
	}

	public void setRotation(double rotation) {
		this.rotation = rotation;
	}
*/
/*	public Vector2 getPosition() {
		return position;
	}

	public void setPosition(Vector2 position) {
		this.position = position;
	}
	
	public Vector2 getLeftEarPos() {
		double h 	= distanceBetweenEars/2;
		double dx 	= h * Math.cos(rotation);
		double dy 	= h * Math.sin(rotation);
		double x 	= this.position.getX() - dx;
		double y 	= this.position.getY() - dy;
		
		return new Vector2(x,y);
		
	}
	
	public Vector2 getRightEarPos() {
		double h 	= distanceBetweenEars/2;
		double dx 	= h * Math.cos(rotation);
		double dy 	= h * Math.sin(rotation);
		double x 	= this.position.getX() + dx;
		double y 	= this.position.getY() + dy;
		
		return new Vector2(x,y);
	}
	*/
}
