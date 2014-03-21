package se.chalmers.group42.runforlife;

import se.chalmers.group42.database.MySQLiteHelper;
import android.app.Application;

public class testApplication extends Application{
	private MySQLiteHelper 	db;
	
	@Override
	public void onCreate() {
	    super.onCreate();

	    db = new MySQLiteHelper(this);

	}
	
	public MySQLiteHelper getDatabase(){
		return db;
	}
}
