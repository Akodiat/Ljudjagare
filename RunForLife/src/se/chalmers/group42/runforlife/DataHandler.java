package se.chalmers.group42.runforlife;


import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import se.chalmers.group42.database.*;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentSender.SendIntentException;
import android.location.Location;
import android.os.Handler;

public class DataHandler {
	private RunActivity 	runAct;
	public RunStatus		runStatus=RunStatus.STOPPED;
	private Handler 	 	m_handler;
	private Runnable 	 	m_handlerTask;
	
	private MySQLiteHelper 	db;
	
	private Long 		 	seconds = 0L;
	private float 		 	distance = 0;
	private int				routeId;
	private boolean			isTime = true;
	private int				coins = 0;
	
	private Boolean 			start = true;
	private Location 			prev = new Location("");
	private Timer 				timer = new Timer();
	private long 				delayTime = 1500;
	
	ArrayList<Location> collection = new ArrayList<Location>();
	int collect = 0;
	Long collectionTime = 0L;

	private double currentSpeed = 0;
	
	DataHandler(MySQLiteHelper db, RunActivity runAct){
		this.runAct = runAct;
		this.db = db;
		m_handler = new Handler();
	}
	
	public void newLocation(Location location){
		if(isTime){
			isTime = false;
			Location curr = new Location("");
			curr.set(location);
			
			//------------------------------------------
			//Calculates the speed over the last 5 points
			collection.add(curr);
			collect++;
			if(collect == 5){
				calculateSpeed(seconds - collectionTime);
				collectionTime = seconds;
				collection.clear();
				collect = 0;
			}
			//------------------------------------------
			
			if(runStatus==RunStatus.RUNNING){
				if(start){ 
					//if this is the first point add no distance
					prev = curr; 
					start = false;
				} else{
					if(runStatus==RunStatus.RUNNING){
						//adds the distance from the last point to the current point
						distance += prev.distanceTo(curr);
						
						MapFragment mapFrag = (MapFragment) runAct.getSupportFragmentManager().findFragmentByTag(
				                "android:switcher:"+R.id.pager+":1");
						if(runAct.statsFragment.isAdded()){
							mapFrag.drawMyPath(location);
						}
						
					}
					prev = curr;
				}
				//adds the point to the database
				db.addPoint(new Point(routeId, 
											   location.getLatitude(),
											   location.getLongitude()));
			}
			
			//Timer, only saves one point evey 1.5s
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					isTime = true;
				}
			}, delayTime);
		}
	}
	
	//Adds a new route to the database
	public void newRoute(){
		Route currentRoute = new Route();
		routeId = db.addRoute(currentRoute);
		
	}
	
	//Starts the stopwatch
	public void startWatch(){
		m_handlerTask = new Runnable()
		{
			@Override 
			public void run() {
				if(runStatus==RunStatus.RUNNING){
					seconds++;
					//Update the displayed data in the run fragment
					int d = (int) distance;
					
					runAct.updateDisplay(seconds,d,currentSpeed,coins); 
				}
				m_handler.postDelayed(m_handlerTask, 1000);
			}
		};
		m_handlerTask.run(); 
	}
	
	//pause the watch
	public void pauseWatch(){
		if (runStatus==RunStatus.PAUSED){
			runStatus=RunStatus.RUNNING;
		}else{
			runStatus=RunStatus.PAUSED;
		}
	}
	
	//finnishes the route and resets all data
	public void resetWatch(){
		int d = (int) distance;
		
//		m_handler.removeCallbacks(m_handlerTask);
		db.finishRoute(db.getRoute(routeId), d, seconds);
		seconds = 0L;
		distance = 0;
		coins = 0;
		
		
		runAct.updateDisplay(seconds,d,currentSpeed,coins);
	}
	public boolean isRunning(){
		return runStatus==RunStatus.RUNNING;
	}
	public boolean isPaused(){
		return runStatus==RunStatus.PAUSED;
	}
	
	//help method to calculate the current speed
	public void calculateSpeed(Long time){
		double d = 0;
		double s = time;
		for(int i=0;i < collect-1; i++){
				d += collection.get(i).distanceTo(collection.get(i+1));
		}
		currentSpeed = d / s;
	}
	
	public void onAquiredCoin(Location coinLoc){
		int d = (int) distance;
		coins++;
		
		Coins coin = new Coins();
		coin.setRouteID(routeId);
		coin.setLocation(coinLoc);
		coin.setTime(seconds);
		coin.setDistance(d);
		
		db.addCoin(coin);
		StatsFragment statsFrag = (StatsFragment) runAct.getSupportFragmentManager().findFragmentByTag(
                "android:switcher:"+R.id.pager+":2");
		if(runAct.statsFragment.isAdded()){
			statsFrag.updateTableData(d,seconds);
		}
	}
	public void finnishRoute(){
		int d = (int) distance;
		db.finishRoute(db.getRoute(routeId), d, seconds);
	}
	public int getCurrentRoute(){
		return routeId;
	}
	public static enum RunStatus{
		RUNNING, PAUSED, STOPPED;
	}
}