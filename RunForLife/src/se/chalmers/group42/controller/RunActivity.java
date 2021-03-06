package se.chalmers.group42.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import se.chalmers.group42.database.Coins;
import se.chalmers.group42.database.FinishedRoute;
import se.chalmers.group42.database.MySQLiteHelper;
import se.chalmers.group42.database.Point;
import se.chalmers.group42.database.Route;
import se.chalmers.group42.runforlife.Constants;
import se.chalmers.group42.runforlife.DataHandler;
import se.chalmers.group42.runforlife.GMapV2Direction;
import se.chalmers.group42.runforlife.GetDirectionsAsyncTask;
import se.chalmers.group42.runforlife.R;
import se.chalmers.group42.runforlife.RunForLifeApplication;
import se.chalmers.group42.runforlife.StatusIconEventListener;
import se.chalmers.group42.runforlife.StatusIconHandler;
import se.chalmers.group42.runforlife.DataHandler.RunStatus;
import se.chalmers.group42.sensors.*;

import com.google.android.gms.internal.fx;
import com.google.android.gms.maps.model.LatLng;

/**
 * 
 * Activity-class handling information of a current run. The workspace pattern
 * is used to let you choose between the pages by swiping or by pressing their
 * tab-title.
 * 
 * @version
 * 
 *          0.1 3 Mars 2014
 * @author
 * 
 *         Anton Palmqvist, Linus Karlsson
 * 
 */
public class RunActivity extends SwipeableActivity implements
MapFragment.OnHeadlineSelectedListener, StatusIconEventListener,
GPSInputListener, OrientationInputListener {

	private Button runButton, stopButton, finishButton;

	private ImageView btnImage;

	// Class for handling database
	protected DataHandler dataHandler;

	private boolean gpsOn, headphonesIn;

	private ImageView gpsIcon, headPhonesIcon;

	private static final LatLng HOME_MARCUS = new LatLng(58.489657, 13.777925);

	protected GetDirectionsAsyncTask asyncTask;

	private GPSInputHandler gpsInputHandler;

	private GyroGPSFusion gyroGPSFusion;

	private RunFragment runFragment;
	private MapFragment mapFragment;
	private StatsFragment statsFragment;

	public MySQLiteHelper db;

	private int routeId;

	private String appMode;

	private BroadcastReceiver statusIconHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_run);

		// Providing an up button
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Setting up the action bar
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		/*
		 * Creating the adapter that will return a fragment for each of the
		 * three primary sections of the app
		 */

		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// Offscreenlimit set to 2 to avoid fragments being destroyed
		mViewPager.setOffscreenPageLimit(2);

		/*
		 * When swiping between different sections, select the corresponding
		 * tab. We can also use ActionBar.Tab#select() to do this if we have a
		 * reference to the Tab
		 */
		mViewPager
		.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			/*
			 * Create a tab with text corresponding to the page title defined by
			 * the adapter. Also specify this Activity object, which implements
			 * the TabListener interface, as the callback (listener) for when
			 * this tab is selected
			 */
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}

		runFragment = new RunFragment();
		mapFragment = new MapFragment();
		statsFragment = new StatsFragment();

		RunForLifeApplication app = (RunForLifeApplication) getApplication();
		db = app.getDatabase();

		// Setting up icons
		gpsIcon = (ImageView) findViewById(R.id.gps_icon);
		btnImage = (ImageView) findViewById(R.id.run_button_img);
		headPhonesIcon = (ImageView) findViewById(R.id.headphones_icon);
		runButton = (Button) findViewById(R.id.run_button);
		stopButton = (Button) findViewById(R.id.button_stop);
		finishButton = (Button) findViewById(R.id.done_button);
		finishButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		SharedPreferences pref = getSharedPreferences("MODE", MODE_PRIVATE);
		appMode = pref.getString("application_mode", "");

		if (appMode.equals("RUN_MODE")) {
			// Setting up Sensor input
			gpsInputHandler = new GPSInputHandler(this, this);
			dataHandler = new DataHandler(db, this);

			if(usingGyro()) //Gyro sensor if requested
				gyroGPSFusion = new GyroGPSFusion(this, this);


			//Set actionbar title text
			getActionBar().setTitle("Run");

			runButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (dataHandler.isRunning()) {
						pause();
						if (!isOkToRun()) {
							setNotGreenToRun();
						}
					} else {
						if (dataHandler.isPaused())
							resume();
						else
							start();
					}
					// dataHandler.pauseWatch();
				}
			});

			// Setting up stop button
			stopButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					stop();
					SharedPreferences preferences = getSharedPreferences(
							"MODE", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = preferences.edit();
					editor.putString("application_mode", "DISPLAY_MODE");
					editor.commit();
				}
			});

			// Setting up statusIconHandler
			IntentFilter filter = new IntentFilter(
					"android.intent.action.HEADSET_PLUG");
			statusIconHandler = new StatusIconHandler(this, this);
			registerReceiver(statusIconHandler, filter);

		} else if (appMode.equals("DISPLAY_MODE")) {

			//Set actionbar title text
			getActionBar().setTitle("Finished Run");

			setUpDisplay();

			runButton.setVisibility(View.GONE);
			stopButton.setVisibility(View.GONE);
			gpsIcon.setVisibility(View.GONE);
			headPhonesIcon.setVisibility(View.GONE);
			finishButton.setVisibility(View.VISIBLE);
			btnImage.setVisibility(View.GONE);

		}

		setRunFragment(runFragment);
		setMapFragment(mapFragment);
		setStatsFragment(statsFragment);
	}

	// These are implemented in CoinCollector, etc. instead. This method should
	// perhaps be abstract.
	protected void playSound() {
	}

	// protected void playLongSound() {}
	protected void stopSound() {
	}

	private void start() {
		// START
		//Retrieving distancevalue from preferences
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		String nrPoints = sharedPref.getString("nrPoints", "3");


		dataHandler.newRoute(Integer.parseInt(nrPoints));
		dataHandler.startWatch();
		btnImage.setImageResource(R.drawable.pause_button);
		dataHandler.runStatus = RunStatus.RUNNING;
		playSound();
	}

	private void resume() {
		btnImage.setImageResource(R.drawable.pause_button);
		stopButton.setVisibility(View.INVISIBLE);
		playSound();
		dataHandler.runStatus = RunStatus.RUNNING;
	}

	private void pause() {
		btnImage.setImageResource(R.drawable.play_button);
		stopButton.setVisibility(View.VISIBLE);
		stopSound();
		dataHandler.runStatus = RunStatus.PAUSED;
	}

	public void stop() {
		//Set actionbar title text
		getActionBar().setTitle("Finished Run");

		runButton.setText("►");
		stopSound();

		routeId = dataHandler.getCurrentRoute();

		dataHandler.runStatus = RunStatus.STOPPED;

		dataHandler.resetWatch();

		gpsInputHandler.pause();

		runButton.setVisibility(View.GONE);
		stopButton.setVisibility(View.GONE);
		gpsIcon.setVisibility(View.GONE);
		headPhonesIcon.setVisibility(View.GONE);
		finishButton.setVisibility(View.VISIBLE);
		btnImage.setVisibility(View.GONE);
		setUpDisplay();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.run, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_delete) {
			//DELETE
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setCancelable(false);
			builder.setMessage("Do you  want to delete the run?");
			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					final Route r = db.getFinishedRoute(routeId);
					db.deleteRoute(r);
					finish();
				}
			});
			builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void sendMapLocation(LatLng latLng) {
		findDirections(HOME_MARCUS.latitude, HOME_MARCUS.longitude,
				latLng.latitude, latLng.longitude, GMapV2Direction.MODE_WALKING);
	}

	@Override
	public void sendFinalRoute(ArrayList<Location> finalRoute, float distance) {
		// TODO
	}

	public void handleGetDirectionsResult(ArrayList<LatLng> directionPoints) {
		MapFragment mapFrag = (MapFragment) getSupportFragmentManager()
				.findFragmentByTag("android:switcher:" + R.id.pager + ":1");
		mapFrag.handleGetDirectionsResult(directionPoints);
	}

	public void findDirections(double fromPositionDoubleLat,
			double fromPositionDoubleLong, double toPositionDoubleLat,
			double toPositionDoubleLong, String mode) {
		Map<String, String> map = new HashMap<String, String>();
		map.put(GetDirectionsAsyncTask.USER_CURRENT_LAT,
				String.valueOf(fromPositionDoubleLat));
		map.put(GetDirectionsAsyncTask.USER_CURRENT_LONG,
				String.valueOf(fromPositionDoubleLong));
		map.put(GetDirectionsAsyncTask.DESTINATION_LAT,
				String.valueOf(toPositionDoubleLat));
		map.put(GetDirectionsAsyncTask.DESTINATION_LONG,
				String.valueOf(toPositionDoubleLong));
		map.put(GetDirectionsAsyncTask.DIRECTIONS_MODE, mode);

		asyncTask = new GetDirectionsAsyncTask(this);
		asyncTask.execute(map);
	}

	public void updateDisplay(long seconds, int distance, double currentspeed,
			int coins) {
		RunFragment runFrag = (RunFragment) getSupportFragmentManager()
				.findFragmentByTag("android:switcher:" + R.id.pager + ":0");
		if (getRunFragment().isAdded()) {
			runFrag.updateDisp(seconds, distance, currentspeed, coins);
		}
	}

	private boolean isOkToRun() {
		return (gpsOn && headphonesIn);
	}

	private void setGreenToRun() {
		//
	}

	private void setNotGreenToRun() {
		btnImage.setImageResource(R.drawable.play_button);
	}

	public void onGPSConnect() {
		if (!gpsOn) {
			gpsOn = true;
			gpsIcon.setImageResource(R.drawable.ic_action_location_found);
			setGreenToRun();
		}

	}

	public void onGPSDisconnect() {
		gpsIcon.setImageResource(R.drawable.ic_action_location_off);
		gpsOn = false;
		if (dataHandler.isPaused()) {
			setNotGreenToRun();
		}
	}

	public void onHeadphonesIn() {
		if (!headphonesIn) {
			headphonesIn = true;
			headPhonesIcon.setImageResource(R.drawable.ic_action_headphones);
			setGreenToRun();
		}
	}

	public void onHeadphonesOut() {
		headPhonesIcon.setImageResource(R.drawable.ic_action_headphones_off);
		headphonesIn = false;
		if (dataHandler.runStatus == RunStatus.RUNNING) {
			pause();
		}
		setNotGreenToRun();
	}

	public void onOrientationChanged(float headingAngleOrientation) {
		// TODO Auto-generated method stub
	}

	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		dataHandler.newLocation(location);
	}

	protected boolean usingGyro() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		return sharedPreferences.getBoolean("gyro", false);
	}

	public void setUpDisplay() {
		int id = routeId;
		if (appMode.equals("DISPLAY_MODE")) {
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				routeId = extras.getInt(Constants.EXTRA_ID);
			}
		}

		FinishedRoute fin = db.getFinishedRoute(routeId);
		Bundle args = new Bundle();
		args.putLong("time", fin.getTotTime());
		args.putInt("distance", fin.getDist());
		args.putDouble("speed", fin.getSpeed());

		Bundle locs = new Bundle();
		List<Point> points = db.getAllPointsByRoute(routeId);
		double[] latitudes = new double[points.size()];
		double[] longitudes = new double[points.size()];
		for (int i = 0; i < points.size(); i++) {
			latitudes[i] = points.get(i).getLatitude();
			longitudes[i] = points.get(i).getLongitude();
		}
		locs.putDoubleArray("latitudes", latitudes);
		locs.putDoubleArray("longitudes", longitudes);

		List<Coins> coins = db.getAllCoinsByRoute(routeId);
		int nrCoins = coins.size();
		int maxCoins = fin.getMaxCoins();

		args.putInt("nrCoins", nrCoins);
		args.putInt("maxCoins", maxCoins);

		double[] coinlat = new double[nrCoins];
		double[] coinlng = new double[nrCoins];

		long[] times = new long[nrCoins];
		int[] dists = new int[nrCoins];

		for (int i = 0; i < nrCoins; i++) {
			Location l = coins.get(i).getLocation();
			coinlat[i] = l.getLatitude();
			coinlng[i] = l.getLongitude();

			times[i] = coins.get(i).getTime();
			dists[i] = coins.get(i).getDistance();
		}
		locs.putDoubleArray("coinlat", coinlat);
		locs.putDoubleArray("coinlng", coinlng);

		Bundle stats = new Bundle();

		stats.putLongArray("times", times);
		stats.putIntArray("dists", dists);

		if (appMode.equals("DISPLAY_MODE")) {
			runFragment.setArguments(args);
			mapFragment.setArguments(locs);
			statsFragment.setArguments(stats);

		} else {
			findViewById(R.id.finished_run).setVisibility(View.VISIBLE);
			findViewById(R.id.finished_run_desc).setVisibility(View.VISIBLE);
			findViewById(R.id.imageView_arrow).setVisibility(View.GONE);

			RunFragment runFrag = (RunFragment) getSupportFragmentManager()
					.findFragmentByTag("android:switcher:" + R.id.pager + ":0");
			if (getRunFragment().isAdded()) {
				runFrag.setDisplay(args);
			}
			MapFragment mapFrag = (MapFragment) getSupportFragmentManager()
					.findFragmentByTag("android:switcher:" + R.id.pager + ":1");
			if (getMapFragment().isAdded()) {
				mapFrag.displayFinishedMap(locs);
			}
		}
	}
	protected void quitRunActivity(){
		SharedPreferences pref = getSharedPreferences("MODE", MODE_PRIVATE);
		String appMode = pref.getString("application_mode", "");
		//If within run-mode the user is asked if he really wants to exit the run
		if(appMode.equals("RUN_MODE")){
			// Ask if you really want to close the activity
			// From,
			// http://www.c-sharpcorner.com/UploadFile/88b6e5/display-alert-on-back-button-pressed-in-android-studio/
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setCancelable(false);
			builder.setMessage("Do you  want to exit the run?");
			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// if user pressed "yes", then he is allowed to exit from
					// application

					stopSound();
					
					if(usingGyro())
						gyroGPSFusion.stop();
					
					if(dataHandler.isPaused() || dataHandler.isRunning()){
						Route r = db.getRoute(dataHandler.getCurrentRoute());
						db.deleteRoute(r);
					}
					finish();
				}
			});
			builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// if user select "No", just cancel this dialog and continue with app
					dialog.cancel();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		}
		//If within display/finish-mode no run is lost if exited, and no dialog is needed
		else{
			finish();
		}
	}
	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(statusIconHandler);
	}
	
//	@Override
//	public void onResume() {
//		
//		IntentFilter filter = new IntentFilter(
//				"android.intent.action.HEADSET_PLUG");
//		registerReceiver(statusIconHandler, filter);
//	}
}
