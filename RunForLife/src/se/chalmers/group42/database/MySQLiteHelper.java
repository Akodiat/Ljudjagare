package se.chalmers.group42.database;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper{

	// Database Version
	private static final int DATABASE_VERSION = 1;
	// Database Name
	private static final String DATABASE_NAME = "BDROUTE";

	/*------------------------------TABLES--------------------------------*/

	private static final String TABLE_ROUTES = "routes";
	private static final String TABLE_FINISHEDROUTES = "finishedRoutes";
	private static final String TABLE_POINTS = "points";
	private static final String TABLE_COINS = "coins";

	/*------------------------------Column names--------------------------*/

	// Routes Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_DATE = "date";

	private static final String[] COLUMNS_ROUTES = {KEY_ID, KEY_DATE};

	// Finished Routes table columns names
	private static final String KEY_FINISHED_ID = "finished_id";
	private static final String KEY_DIST = "distance";
	private static final String KEY_SPEED = "speed";
	private static final String KEY_TOTTIME = "time";

	private static final String[] COLUMNS_FINROUTES = {KEY_FINISHED_ID,KEY_DIST,KEY_SPEED,KEY_TOTTIME};

	// Points Table Columns names
	private static final String KEY_POINTID = "pointId";
	private static final String KEY_ROUTE = "routeId";
	private static final String KEY_LAT = "latitude";
	private static final String KEY_LNG = "longitude";
	private static final String KEY_TIME = "time";

	private static final String[] COLUMNS_POINTS = {KEY_POINTID,KEY_ROUTE, KEY_LAT,KEY_LNG,KEY_TIME};


	// Coins Table Columns names
	private static final String KEY_COINID = "coinId";
	private static final String KEY_ROUTEID = "routeId";
	private static final String KEY_COINLAT = "latitude";
	private static final String KEY_COINLNG = "longitude";
	private static final String KEY_COINTIME = "time";
	private static final String KEY_COINDIST = "distance";

	private static final String[] COLUMNS_COINS = {KEY_COINID,KEY_ROUTEID, KEY_COINLAT,KEY_COINLNG,KEY_COINTIME,KEY_COINDIST};

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);  
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		/*------------------------------CREATE_ROUTES_TABLE--------------------------------*/

		// SQL statement to create route table
		String CREATE_ROUTES_TABLE = "CREATE TABLE "+TABLE_ROUTES+" ( " +
				KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " + 						
				KEY_DATE+" INTEGER)"; 

		// create position table
		db.execSQL(CREATE_ROUTES_TABLE);

		/*------------------------------CREATE_POINTS_TABLE--------------------------------*/

		// SQL statement to create points table
		String CREATE_POINTS_TABLE = "CREATE TABLE "+TABLE_POINTS+" ( " +
				KEY_POINTID+" INTEGER PRIMARY KEY AUTOINCREMENT, " + 
				KEY_ROUTE+" INTEGER, "+
				KEY_TIME+" LONG, "+
				KEY_LAT+" DOUBLE, " +
				KEY_LNG+" DOUBLE, " +
				"FOREIGN KEY ("+KEY_ROUTE+") REFERENCES "+TABLE_ROUTES+"("+KEY_ID+"))";

		// create position table
		db.execSQL(CREATE_POINTS_TABLE);

		/*------------------------------CREATE_FINISHEDROUTES_TABLE--------------------------------*/

		// SQL to create
		String CREATE_FINISHED_TABLE = "CREATE TABLE "+TABLE_FINISHEDROUTES+"("+
				KEY_FINISHED_ID +" INTEGER," +
				KEY_DIST + " INTEGER, "+
				KEY_SPEED + " DOUBLE, "+
				KEY_TOTTIME + " LONG, "+
				"FOREIGN KEY ("+KEY_FINISHED_ID + ") REFERENCES "+TABLE_ROUTES+" ("+KEY_ID+"))";     

		//create
		db.execSQL(CREATE_FINISHED_TABLE);

		/*------------------------------CREATE_COINS_TABLE--------------------------------*/

		// SQL statement to create points table
		String CREATE_COINS_TABLE = "CREATE TABLE "+TABLE_COINS+" ( " +
				KEY_COINID+" INTEGER PRIMARY KEY AUTOINCREMENT, " + 
				KEY_ROUTEID+" INTEGER, "+
				KEY_COINTIME+" LONG, "+
				KEY_COINLAT+" DOUBLE, " +
				KEY_COINLNG+" DOUBLE, " +
				KEY_DIST+" INTEGER , "+
				"FOREIGN KEY ("+KEY_ROUTEID+") REFERENCES "+TABLE_ROUTES+"("+KEY_ID+"))";

		// create position table
		db.execSQL(CREATE_COINS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older tables if existed
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_POINTS);
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_ROUTES);
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_COINS);
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_FINISHEDROUTES);

		// create fresh tables
		this.onCreate(db);
	}


	/*------------------------------ROUTE TABLE METHODS--------------------------------*/
	/**
	 * adds a route to the database
	 * @param r the route to be added
	 * @return the id of the route
	 */
	public int addRoute(Route r){
		Log.d("addRoute",r.toString());						//Log

		SQLiteDatabase db = this.getWritableDatabase(); 	//get db

		ContentValues values = new ContentValues();			//Content values
		values.put(KEY_DATE, r.getDate());

		Long row = db.insert(TABLE_ROUTES, null, values);	//insert

		db.close(); //close
		return getIdFromRow(row);
	}
	/**
	 * returns a route
	 * @param id the id of the route
	 * @return
	 */
	public Route getRoute(int id){
		SQLiteDatabase db = this.getReadableDatabase(); //get db

		Cursor cursor = db.query(TABLE_ROUTES, 			//build query
				COLUMNS_ROUTES, 
				" id = ?", 
				new String[] {String.valueOf(id)}, 
				null, null, null);
		if(cursor != null)
			cursor.moveToFirst();						//check content

		Route r = new Route();							//build object
		r.setId(cursor.getInt(0));
		r.setDate(cursor.getString(1));

		Log.d("getRoute", r.toString());				//log

		return r; 
	}
	/**
	 * returns all routes
	 * @return
	 */
	public List<Route> getAllRoutes(){
		List<Route> routes = new LinkedList<Route>();
		SQLiteDatabase db = this.getWritableDatabase();

		Cursor cursor = db.query(TABLE_ROUTES, COLUMNS_ROUTES, null, null, null, null, null);

		if(cursor.moveToFirst()){
			do{
				Route r = new Route();
				r.setId(cursor.getInt(0));
				r.setDate(cursor.getString(1));
				routes.add(r);
			}while(cursor.moveToNext());
		}

		Log.d("getAllRoutes",routes.toString());
		return routes; 
	}
	/**
	 * Updates a route in the database
	 * @param route the route to be updated
	 * @return
	 */
	public int updateRoute(Route route){
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, route.getId());
		values.put(KEY_DATE, route.getDate());

		int i = db.update(TABLE_ROUTES, values, KEY_ID+" = ? ", new String[] {String.valueOf(route.getId())});

		db.close();

		return i;	
	}
	/**
	 * Deletes a route from the database along with associated finished routes and points.
	 * @param route the route to be deleted
	 */
	public void deleteRoute(Route route){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_ROUTES, KEY_ID+"=?", new String[] {String.valueOf(route.getId())});
		db.delete(TABLE_FINISHEDROUTES, KEY_FINISHED_ID+"=?",new String[] {String.valueOf(route.getId())} );
		db.delete(TABLE_POINTS, KEY_ROUTE+"=?", new String[] {String.valueOf(route.getId())});
		db.close();
		Log.d("deleteRoute", route.toString());
	}

	/*------------------------------POINT TABLE METHODS--------------------------------*/
	/**
	 * adds a point to the database.
	 * @param p the point to add
	 */
	public void addPoint(Point p){
		Log.d("addPoint",p.toString());					//Log
		SQLiteDatabase db = this.getWritableDatabase(); //get db

		ContentValues values = new ContentValues();		//values
		values.put(KEY_ROUTE, p.getRouteId());
		values.put(KEY_LAT, p.getLatitude());	
		values.put(KEY_LNG, p.getLongitude());
		values.put(KEY_TIME, p.getTime());

		db.insert(TABLE_POINTS, null, values);			//insert

		db.close();										//close
	}

	/**
	 * Returns a specific point from the database.
	 * 
	 * @param id the id of the point
	 * @return the point
	 */
	public Point getPoint(int id){
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_POINTS, // a. table
				COLUMNS_POINTS, // b. column names
				" id = ?", // c. selections 
				new String[] { String.valueOf(id) }, // d. selections args
				null, // e. group by
				null, // f. having
				null, // g. order by
				null); // h. limit

		if(cursor != null)
			cursor.moveToFirst();

		Point point = new Point();
		point.setId(cursor.getInt(0));
		point.setRouteId(cursor.getInt(1));
		point.setLatitude(cursor.getDouble(2));
		point.setLongitude(cursor.getDouble(3));
		point.setTime(cursor.getLong(4));

		Log.d("getPoint("+id+")", point.toString());
		return point;
	}

	/**
	 * Lists all the points conecccted to a specific route.
	 * @param routeId the route
	 * @return a list of the points
	 */
	public List<Point> getAllPointsByRoute(int routeId){
		List<Point> points = new LinkedList<Point>();

		//get db
		SQLiteDatabase db = this.getWritableDatabase();

		//build query
		Cursor cursor = db.query(TABLE_POINTS, // a. table
				COLUMNS_POINTS, // b. column names
				" routeId = ?", // c. selections 
				new String[] { String.valueOf(routeId) }, // d. selections args
				null, // e. group by
				null, // f. having
				null, // g. order by
				null); // h. limit


		//set up result
		Point point = null;
		if(cursor.moveToFirst()){
			do{
				point = new Point();
				point.setId(Integer.parseInt(cursor.getString(0)));
				point.setRouteId(Integer.parseInt(cursor.getString(1)));
				point.setLatitude(cursor.getDouble(2));
				point.setLongitude(cursor.getDouble(3));
				point.setTime(Long.parseLong(cursor.getString(4)));

				points.add(point);
			} while (cursor.moveToNext());
		}

		//Log
		Log.d("getAllPoints("+routeId+")", points.toString());

		return points;
	}

	/**
	 * Updates a point in the database
	 * 
	 * @param point the updated point
	 * @return
	 */
	public int updatePoint(Point point){

		//get db
		SQLiteDatabase db = this.getWritableDatabase();

		//create content
		ContentValues values = new ContentValues();
		values.put(KEY_ROUTE,point.getRouteId());
		values.put(KEY_LAT, point.getLatitude());
		values.put(KEY_LNG, point.getLongitude());
		values.put("time", point.getTime());

		//update row
		int i = db.update(TABLE_POINTS, values, KEY_POINTID+" = ?", new String[] {String.valueOf(point.getId())});

		db.close();

		return i;
	}

	/**
	 * Deletes a specific point from the database
	 * 
	 * @param point the point to be deleted
	 */
	public void deletePoint(Point point){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_POINTS, KEY_POINTID+"=?", new String[] {String.valueOf(point.getId())});
		db.close();
		Log.d("deletePoint", point.toString());
	}

	/*------------------------------COINS TABLE METHODS--------------------------------*/

	public void addCoin(Coins c){
		Log.d("addCoin",c.toString());					//Log
		SQLiteDatabase db = this.getWritableDatabase(); //get db

		ContentValues values = new ContentValues();		//values
		values.put(KEY_ROUTEID, c.getRouteID());
		values.put(KEY_COINLAT, c.getLocation().getLatitude());	
		values.put(KEY_COINLNG, c.getLocation().getLongitude());
		values.put(KEY_COINTIME, c.getTime());
		values.put(KEY_COINDIST, c.getDistance());

		db.insert(TABLE_COINS, null, values);			//insert

		db.close();										//close
	}

	public Coins getCoin(int id){
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_COINS, // a. table
				COLUMNS_COINS, // b. column names
				" id = ?", // c. selections 
				new String[] { String.valueOf(id) }, // d. selections args
				null, // e. group by
				null, // f. having
				null, // g. order by
				null); // h. limit

		if(cursor != null)
			cursor.moveToFirst();

		Location loc = new Location("");

		Coins coin = new Coins();
		coin.setId(cursor.getInt(0));
		coin.setRouteID(cursor.getInt(1));
		loc.setLatitude(cursor.getDouble(2));
		loc.setLongitude(cursor.getDouble(3));
		coin.setLocation(loc);
		coin.setTime(cursor.getLong(4));
		coin.setDistance(cursor.getInt(5));

		Log.d("getCoin("+id+")", coin.toString());
		return coin;
	}
	public List<Coins> getAllCoinsByRoute(int routeId){
		List<Coins> coins = new LinkedList<Coins>();

		//get db
		SQLiteDatabase db = this.getWritableDatabase();

		//build query
		Cursor cursor = db.query(TABLE_COINS, // a. table
				COLUMNS_COINS, // b. column names
				" routeId = ?", // c. selections 
				new String[] { String.valueOf(routeId) }, // d. selections args
				null, // e. group by
				null, // f. having
				null, // g. order by
				null); // h. limit


		//set up result
		Coins coin = null;
		Location loc = new Location("");
		if(cursor.moveToFirst()){
			do{
				coin = new Coins();
				coin.setId(Integer.parseInt(cursor.getString(0)));
				coin.setRouteID(Integer.parseInt(cursor.getString(1)));
				loc.setLatitude(cursor.getDouble(2));
				loc.setLongitude(cursor.getDouble(3));
				coin.setLocation(loc);
				coin.setTime(cursor.getLong(4));
				coin.setDistance(cursor.getInt(5));

				coins.add(coin);
			} while (cursor.moveToNext());
		}

		//Log
		Log.d("getAllCoins("+routeId+")", coins.toString());

		return coins;
	}
	/*------------------------------FINISHED_ROUTE TABLE METHODS--------------------------------*/

	/**
	 * This method is used to finish a route, it retrieves the time of the first and last point 
	 * in the database and uses it along with the provided distance to calculate an avarage time.
	 * Then it is added to the database.
	 * 
	 * @param r Route to be finished
	 * @param dist the total distance of the route
	 */
	public void finishRoute(Route r, int dist, Long time){
		SQLiteDatabase db = this.getWritableDatabase();
		int id = r.getId();
		
		double d = dist;
		double s = time;

		//speed
		double speed = (d / s) *3.6; 

		//Store data!!    	
		FinishedRoute f = new FinishedRoute(r,dist,speed,time);
		Log.d("finishRoute",f.toString());

		ContentValues values = new ContentValues();
		values.put(KEY_FINISHED_ID, f.getId());
		values.put(KEY_DIST, f.getDist());
		values.put(KEY_SPEED, f.getSpeed());
		values.put(KEY_TOTTIME, f.getTotTime());

		db.insert(TABLE_FINISHEDROUTES, null, values);
		db.close();
	}
	/**
	 * This method returns a finished route.
	 * @param r the Route to be retrieved from the database
	 * @return the finished route
	 */
	public FinishedRoute getFinishedRoute(int id){
		SQLiteDatabase db = this.getReadableDatabase();
		Route r = this.getRoute(id);
		
		Cursor cursor = db.query(TABLE_FINISHEDROUTES, COLUMNS_FINROUTES, KEY_FINISHED_ID+"=?", 
				new String[] {String.valueOf(id)}, null, null, null);

		if(cursor != null)
			cursor.moveToFirst();

		FinishedRoute f = new FinishedRoute();
		f.setId(cursor.getInt(0));
		f.setDist(cursor.getInt(1));
		f.setSpeed(cursor.getDouble(2));
		f.setTotTime(cursor.getLong(3));
		f.setDate(r.getDate());

		Log.d("getFinished", f.toString());
		return f;
	}
	public List<FinishedRoute> getAllFinishedRoutes(){
		List<FinishedRoute> routes = new LinkedList<FinishedRoute>();
		SQLiteDatabase db = this.getWritableDatabase();

		Cursor cursor = db.query(TABLE_FINISHEDROUTES, COLUMNS_FINROUTES, null, null, null, null, null);

		if(cursor.moveToFirst()){
			do{
				Route r = getRoute(cursor.getInt(0));
				FinishedRoute f = new FinishedRoute();
				
				f.setId(cursor.getInt(0));
				f.setDist(cursor.getInt(1));
				f.setSpeed(cursor.getDouble(2));
				f.setTotTime(cursor.getLong(3));
				f.setDate(r.getDate());
				routes.add(f);
			}while(cursor.moveToNext());
		}

		Log.d("getAllRoutes",routes.toString());
		return routes; 
	}

	/****************************HELPER************************************************/

	public int getIdFromRow(Long row){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.query(TABLE_ROUTES, 
				COLUMNS_ROUTES, 
				"ROWID=?", 
				new String[] {String.valueOf(row)}, 
				null, null, null);
		if(c != null)
			c.moveToFirst();
		return c.getInt(0);
	}
}
