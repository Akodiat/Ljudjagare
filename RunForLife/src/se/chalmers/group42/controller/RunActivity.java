package se.chalmers.group42.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.ActionBar;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import se.chalmers.group42.runforlife.Constants;
import se.chalmers.group42.runforlife.DataHandler;
import se.chalmers.group42.runforlife.GMapV2Direction;
import se.chalmers.group42.runforlife.GetDirectionsAsyncTask;
import se.chalmers.group42.runforlife.R;
import se.chalmers.group42.runforlife.RunForLifeApplication;
import se.chalmers.group42.runforlife.StatusIconEventListener;
import se.chalmers.group42.runforlife.StatusIconHandler;
import se.chalmers.group42.runforlife.DataHandler.RunStatus;
import sensors.*;

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

	private Button runButton, stopButton;

	private ImageView btnImage;

	// Class for handling database
	protected DataHandler dataHandler;

	private boolean gpsOn, headphonesIn;

	private ImageView gpsIcon, headPhonesIcon;

	private static final LatLng HOME_MARCUS = new LatLng(58.489657, 13.777925);

	protected GetDirectionsAsyncTask asyncTask;

	private GPSInputHandler gpsInputHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_run);

		// Setting up GPS input needed to get GPS updates
		gpsInputHandler = new GPSInputHandler(this, this);

		// Setting up orientation input.
		if (usingGyro())
			new GyroGPSFusion(this, this);

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

		setRunFragment(new RunFragment());
		setMapFragment(new MapFragment());
		setStatsFragment(new StatsFragment());

		RunForLifeApplication app = (RunForLifeApplication) getApplication();
		this.dataHandler = new DataHandler(app.getDatabase(), this);

		btnImage = (ImageView) findViewById(R.id.run_button_img);

		// Setting up pause button
		runButton = (Button) findViewById(R.id.run_button);
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
		stopButton = (Button) findViewById(R.id.button_stop);
		stopButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				stop();
			}
		});

		// Setting up icons
		gpsIcon = (ImageView) findViewById(R.id.gps_icon);
		headPhonesIcon = (ImageView) findViewById(R.id.headphones_icon);

		// Setting up statusIconHandler
		IntentFilter filter = new IntentFilter(
				"android.intent.action.HEADSET_PLUG");
		StatusIconHandler receiver = new StatusIconHandler(this, this);
		registerReceiver(receiver, filter);
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
		dataHandler.newRoute();
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
		btnImage.setImageResource(R.drawable.play_button);
		stopSound();
		dataHandler.runStatus = RunStatus.STOPPED;

		dataHandler.resetWatch();
		Intent finishedRunActivityIntent = new Intent(RunActivity.this,
				FinishedRunActivity.class);
		finishedRunActivityIntent.putExtra(Constants.EXTRA_ID,
				dataHandler.getCurrentRoute());
		startActivity(finishedRunActivityIntent);
		if (asyncTask != null) {
			asyncTask.cancel(true);
		}

		android.os.Process.killProcess(android.os.Process.myPid());

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.run, menu);
		return true;
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
			gpsIcon.setImageResource(R.drawable.gps_activated);
			setGreenToRun();
		}

	}

	public void onGPSDisconnect() {
		gpsIcon.setImageResource(R.drawable.gps_disabled);
		gpsOn = false;
		if (dataHandler.isPaused()) {
			setNotGreenToRun();
		}
	}

	public void onHeadphonesIn() {
		if (!headphonesIn) {
			headphonesIn = true;
			headPhonesIcon.setImageResource(R.drawable.headphones_activated);
			setGreenToRun();
		}
	}

	public void onHeadphonesOut() {
		headPhonesIcon.setImageResource(R.drawable.headphones_disabled);
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
}
