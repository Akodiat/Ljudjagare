package se.chalmers.group42.database;

public class FinishedRoute extends Route{
	private double speed;
	private int dist;
	private Long totTime;
	
	FinishedRoute(){}
	FinishedRoute(Route r, int dist, double speed, Long totTime){
		super();
		this.setId(r.getId());
		this.setDate(r.getDate());
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
		return "ID: "+this.getId()+"                                         "+this.getDate()+"";
	}
}
