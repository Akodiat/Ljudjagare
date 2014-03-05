package se.chalmers.group42.database;

import android.text.format.Time;

public class Route {
	private int id;
	private String date;
	
	public Route(){
		super();
		Time t = new Time();
		t.setToNow();
		date = t.format3339(true);
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDate() {
		return date;
	}
	/**
	 * form (yyyy-mm-dd)
	 * @param date
	 */
	public void setDate(String date) {
		this.date = date;
	}
	@Override
	public String toString(){
		return "[Id: "+this.id+" date: "+this.date+"]";
	}
	
}
