package se.chalmers.group42.database;

import android.text.format.Time;

public class FinishedRoute extends Route{
	private double speed;
	private int dist;
	private Long totTime;
	
	FinishedRoute(){}
	FinishedRoute(Route r, int dist, double speed, Long totTime){
		super();
		this.setId(r.getId());
		this.setDate(r.getDate());
		this.setMaxCoins(r.getMaxCoins());
		this.dist = dist;
		this.speed = speed;
		this.totTime = totTime;
	}
	public double getSpeed() {
		return speed;
	}
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	public Long getTotTime() {
		return totTime;
	}
	public void setTotTime(Long totTime) {
		this.totTime = totTime;
	}
	
	
	public int getDist() {
		return dist;
	}
	public void setDist(int dist) {
		this.dist = dist;
	}
	@Override
	public String toString(){
		Time t = new Time();
		t.set(totTime*1000);
		t.switchTimezone("GMT");
		if(dist >= 1000){
			double d = dist;
			d = Math.round((d/1000)*100)/100.0d;
			return this.getDate()+"\t\t"+t.format("%H:%M:%S")+"\t\t"+d+" km";
		}else{
			int d = dist;
			return this.getDate()+"\t\t"+t.format("%H:%M:%S")+"\t\t"+d+" m";
		}
	}
}
