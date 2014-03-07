package se.chalmers.group42.runforlife;


import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import se.chalmers.group42.database.*;

import android.location.Location;
import android.os.Handler;

public class DataHandler {
	private RunActivity 	runAct;
	private boolean			running = false;
	private boolean 	 	pause = true;
	private Handler 	 	m_handler;
	private Runnable 	 	m_handlerTask;
	
	private MySQLiteHelper 	db;
	
	private Long 		 	seconds = 0L;
	private int 		 	distance = 0;
	private Route			currentRoute;
	private boolean			isTime = true;
	
	private Boolean 			start = true;
	private Location 			prev = new Location("");
	private Timer 				timer = new Timer();
	private long 				delayTime = 1500;
	
	ArrayList<Location> collection = new ArrayList<Location>();
	int collect = 0;
	Long collectionTime = 0L;

	private double currentSpeed = 0;
	
	DataHandler(RunActivity runAct){
		this.runAct = runAct;
		db = new MySQLiteHelper(this.runAct);
		m_handler = new Handler();
		
		
		//FLUSH DB
		db.onUpgrade(db.getWritableDatabase(), 1, 2);

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
			
			if(running){
				if(start){ 
					//if this is the forst point add no distance
					prev = curr; 
					start = false;
				} else{
					if(!pause){
						//adds the distance from the last point to the current point
						distance += prev.distanceTo(curr);
					}
					prev = curr;
				}
				//adds thte point to the database
				db.addPoint(new Point(currentRoute.getId(), 
											   location.getLatitude(),
											   location.getLongitude()));
			}
			
			//runAct.drawMySteps(location);
			
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
		currentRoute = new Route();
		int id = db.addRoute(currentRoute);
		currentRoute.setId(id);
		
	}
	
	//Starts the stopwatch
	public void startWatch(){
		m_handlerTask = new Runnable()
		{
			@Override 
			public void run() {
				if(!pause){
					seconds++;
					//Update the displayed data in the run fragment
					runAct.updateDisplay(seconds,distance,currentSpeed); 
				}
				else {
					m_handler.removeCallbacks(m_handlerTask);
				}
				m_handler.postDelayed(m_handlerTask, 1000);
			}
		};
		m_handlerTask.run(); 
		pause = false;
		running = true;
	}
	
	//pause the watch
	public void pauseWatch(){
		if (pause){
			pause = false;
		}else{
			pause = true;
		}
	}
	
	//finnishes the route and resets all data
	public void resetWatch(){
		m_handler.removeCallbacks(m_handlerTask);
		db.finishRoute(currentRoute, distance, seconds);
		seconds = 0L;
		distance = 0;
		pause = true;
		running = false;
		
		runAct.updateDisplay(seconds,distance,currentSpeed);
	}
	public boolean getRunningStatus(){
		return running;
	}
	public boolean getPauseStatus(){
		return pause;
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
	
	public void onAquiredCoin(){
//		antonsmetod(distance, time);
	}
}