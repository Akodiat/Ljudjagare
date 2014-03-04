package se.chalmers.group42.runforlife;


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
	private int 		 	distance;
	private Route			currentRoute;
	private boolean			isTime = true;
	
	private Boolean 			start = true;
	private Location 			prev = new Location("");
	private Timer 				timer = new Timer();
	private long 				delayTime = 1500;
	
	
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
			if(running){
				if(start){ 
					prev = curr; 
					start = false;
				} else{
					if(!pause){
						distance += prev.distanceTo(curr);
					}
					prev = curr;
				}
				db.addPoint(new Point(currentRoute.getId(), 
											   location.getLatitude(),
											   location.getLongitude()));
			}
			
			//runAct.drawMySteps(location);
			
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					isTime = true;
				}
			}, delayTime);
		}
	}
	
	public void newRoute(){
		currentRoute = new Route();
		int id = db.addRoute(currentRoute);
		currentRoute.setId(id);
		
	}
	
	public void startWatch(){
		m_handlerTask = new Runnable()
		{
			@Override 
			public void run() {
				if(!pause){
					seconds++;
					//runAct.updateDisplay(seconds,distance); 
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
	
	public void pauseWatch(){
		if (pause){
			pause = false;
		}else{
			pause = true;
		}
	}
	
	public void resetWatch(){
		m_handler.removeCallbacks(m_handlerTask);
		db.finishRoute(currentRoute, distance, seconds);
		seconds = 0L;
		distance = 0;
		pause = true;
		running = false;
		
	//	runAct.updateDisplay(seconds, distance);
	}
	public boolean getRunningStatus(){
		return running;
	}
	public boolean getPauseStatus(){
		return pause;
	}
}