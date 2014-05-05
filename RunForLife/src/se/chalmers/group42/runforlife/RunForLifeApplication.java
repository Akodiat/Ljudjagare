package se.chalmers.group42.runforlife;

import se.chalmers.group42.database.MySQLiteHelper;
import android.app.Application;

public class RunForLifeApplication extends Application{
	private MySQLiteHelper 	db;
	
	@Override
	public void onCreate() {
	    super.onCreate();

	    db = new MySQLiteHelper(this);
//	    db.onUpgrade(db.getWritableDatabase(), 0, 1);
	    
	}
	
	public MySQLiteHelper getDatabase(){
		return db;
	}
}
