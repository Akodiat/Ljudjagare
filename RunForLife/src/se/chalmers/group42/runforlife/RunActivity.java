package se.chalmers.group42.runforlife;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.ActionBar;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import sensors.*;

import com.google.android.gms.maps.model.LatLng;


/**
 * 
 * Activity-class handling information of a current run. The workspace 
 * pattern is used to let you choose between the pages by swiping or
 * by pressing their tab-title.
 * 
 * @version
 * 
 *          0.1 3 Mars 2014
 * @author
 * 
 *         Anton Palmqvist
 * 
 */
public class RunActivity extends SwipeableActivity implements
		MapFragment.OnHeadlineSelectedListener,
		StatusIconEventListener,
		GPSInputListener,
		OrientationInputListener
		{

	private ImageButton pauseButton, finishButton;

	
	//Class for handling database
	protected DataHandler dataHandler;
	
	
	private ImageView gpsIcon, soundIcon, headPhonesIcon;
	
	
	private static final 	LatLng HOME_MARCUS 		= new LatLng(58.489657, 13.777925);
	
	GetDirectionsAsyncTask asyncTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_run);

		//Setting up statusIconHandler
		IntentFilter filter = new IntentFilter("android.intent.action.HEADSET_PLUG");
		StatusIconHandler receiver = new StatusIconHandler(this);
		registerReceiver(receiver, filter);
		
		//Setting up Sensor input
		new GPSInputHandler(this, this);
		new OrientationInputHandler(this);
		
		// Setting up the action bar
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		/*
		 *  Creating the adapter that will return a fragment for each of the three 
		 *  primary sections of the app
		 */
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		//Offscreenlimit set to 2 to avoid fragments being destroyed
		mViewPager.setOffscreenPageLimit(2);

		/*
		 *  When swiping between different sections, select the corresponding
		 *  tab. We can also use ActionBar.Tab#select() to do this if we have
		 *  a reference to the Tab
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
			 *  Create a tab with text corresponding to the page title defined by
			 *  the adapter. Also specify this Activity object, which implements
			 *  the TabListener interface, as the callback (listener) for when
			 *  this tab is selected
			 */
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		
		runFragment = new RunFragment();
		mapFragment = new MapFragment();
		statsFragment = new StatsFragment();
		

		this.dataHandler = new DataHandler(this);
		
		//START
		if(!dataHandler.getRunningStatus()){
			dataHandler.newRoute();
			dataHandler.startWatch();
		}
		
		//Setting up pausebutton
		pauseButton = (ImageButton) findViewById(R.id.button_pause);
		pauseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(dataHandler.getRunningStatus()){
					if(!dataHandler.getPauseStatus()){
						pauseButton.setImageResource(R.drawable.play);
					}else{
						pauseButton.setImageResource(R.drawable.pause);
					}
					playSound();
					dataHandler.pauseWatch();
				}
				onGPSDisconnect();
			}
		});

		//Setting up finishbutton
		finishButton = (ImageButton) findViewById(R.id.button_stop);
		finishButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(dataHandler.getRunningStatus()){
					dataHandler.resetWatch();
					playSound();
				}
				onGPSConnect();
				Intent finishedRunActivityIntent = new Intent(RunActivity.this, FinishedRunActivity.class);
				startActivity(finishedRunActivityIntent);
				if(asyncTask!=null){
					asyncTask.cancel(true);
				}
				finish();
//				StatsFragment statsFrag = (StatsFragment) getSupportFragmentManager().findFragmentByTag(
//						"android:switcher:"+R.id.pager+":2");
//				if(statsFragment.isAdded()){
//					statsFrag.updateTableData(1,2);
//				}
			}
		});
	//	this.modeController.launchMode(Mode.COIN_COLLECTOR); //TODO: Make it possible to actually choose which mode is launched
		
		//Setting up icons
		gpsIcon = (ImageView) findViewById(R.id.imageViewGPS);
		soundIcon = (ImageView) findViewById(R.id.imageViewSound);
		headPhonesIcon = (ImageView) findViewById(R.id.imageViewHeadphones);
		
	}

	protected void playSound() {
		// This is created in CoinCollector, etc. instead. This method should perhaps be abstract instead.
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.run, menu);
		return true;
	}

	@Override
	public void sendMapLocation(LatLng latLng) {
		findDirections( HOME_MARCUS.latitude, HOME_MARCUS.longitude
			, latLng.latitude, latLng.longitude, GMapV2Direction.MODE_WALKING );
	
	}
	
	@Override
	public void sendFinalRoute(ArrayList<Location> finalRoute) {
		// TODO
	}
	
	public void handleGetDirectionsResult(ArrayList<LatLng> directionPoints) {
		MapFragment mapFrag = (MapFragment) getSupportFragmentManager().findFragmentByTag(
                "android:switcher:"+R.id.pager+":1");
		mapFrag.handleGetDirectionsResult(directionPoints);
	}

	
	public void findDirections(double fromPositionDoubleLat, double fromPositionDoubleLong, double toPositionDoubleLat, double toPositionDoubleLong, String mode)
	{
		Map<String, String> map = new HashMap<String, String>();
		map.put(GetDirectionsAsyncTask.USER_CURRENT_LAT, String.valueOf(fromPositionDoubleLat));
		map.put(GetDirectionsAsyncTask.USER_CURRENT_LONG, String.valueOf(fromPositionDoubleLong));
		map.put(GetDirectionsAsyncTask.DESTINATION_LAT, String.valueOf(toPositionDoubleLat));
		map.put(GetDirectionsAsyncTask.DESTINATION_LONG, String.valueOf(toPositionDoubleLong));
		map.put(GetDirectionsAsyncTask.DIRECTIONS_MODE, mode);

		asyncTask = new GetDirectionsAsyncTask(this);
		asyncTask.execute(map);	
	}
	
	public void updateDisplay(long seconds, int distance, double currentspeed){
		RunFragment runFrag = (RunFragment) getSupportFragmentManager().findFragmentByTag(
                "android:switcher:"+R.id.pager+":0");
		if(runFragment.isAdded()){
			runFrag.setTime(seconds,distance,currentspeed);
		}
	}

	public void onGPSConnect() {
		gpsIcon.setImageResource(R.drawable.gps_green);
		
//		MapFragment mapFrag = (MapFragment) this.getSupportFragmentManager().findFragmentByTag(
//                "android:switcher:"+R.id.pager+":1");
//		if(this.statsFragment.isAdded()){
//			mapFrag.setIsConnected(true);
//		}
		
	}
	public void onGPSDisconnect() {
		gpsIcon.setImageResource(R.drawable.gps_red);
		
//		MapFragment mapFrag = (MapFragment) this.getSupportFragmentManager().findFragmentByTag(
//                "android:switcher:"+R.id.pager+":1");
//		if(this.statsFragment.isAdded()){
//			mapFrag.setIsConnected(false);
//		}
	}
	public void onSoundOn() {
		soundIcon.setImageResource(R.drawable.sound_green);
	}
	public void onSoundOff() {
		soundIcon.setImageResource(R.drawable.sound_red);
	}
	public void onHeadphonesIn(){
		headPhonesIcon.setImageResource(R.drawable.headphones_green);
	}
	public void onHeadphonesOut(){
		headPhonesIcon.setImageResource(R.drawable.headphones_red);
	}
	public void onCompassChanged(float headingAngleOrientation) {
		// TODO Auto-generated method stub	
	}
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub	
		dataHandler.newLocation(location);
	}
}
