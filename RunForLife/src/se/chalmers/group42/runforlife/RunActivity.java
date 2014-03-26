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

import se.chalmers.group42.runforlife.DataHandler.RunStatus;
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

	private ImageButton pauseButton, stopButton;


	//Class for handling database
	protected DataHandler dataHandler;

	private boolean gpsOn, soundOn, headphonesIn;

	private ImageView gpsIcon, soundIcon, headPhonesIcon;


	private static final 	LatLng HOME_MARCUS 		= new LatLng(58.489657, 13.777925);

	protected GetDirectionsAsyncTask asyncTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_run);



		//Setting up Sensor input
		new GPSInputHandler(this, this);
		new OrientationInputHandler(this, this);
		
		

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


		RunForLifeApplication app = (RunForLifeApplication) getApplication();
		this.dataHandler = new DataHandler(app.getDatabase(),this);



		//Setting up pausebutton
		pauseButton = (ImageButton) findViewById(R.id.button_pause);
		pauseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(dataHandler.isRunning()){
					pause();
					if(!isOkToRun()){
						setNotGreenToRun();
					}
				}else{
					if(dataHandler.isPaused())
						resume();
					else
						start();
				}
				//				dataHandler.pauseWatch();
			}
		});

		pauseButton.setImageResource(R.drawable.play_red);

		//Setting up stopbutton
		stopButton = (ImageButton) findViewById(R.id.button_stop);
		stopButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				stop();
			}
		});
		//			this.modeController.launchMode(Mode.COIN_COLLECTOR); //TODO: Make it possible to actually choose which mode is launched

		//Setting up icons
		gpsIcon = (ImageView) findViewById(R.id.imageViewGPS);
		soundIcon = (ImageView) findViewById(R.id.imageViewSound);
		headPhonesIcon = (ImageView) findViewById(R.id.imageViewHeadphones);

		//Setting up statusIconHandler
		IntentFilter filter = new IntentFilter("android.intent.action.HEADSET_PLUG");
		StatusIconHandler receiver = new StatusIconHandler(this, this);
		registerReceiver(receiver, filter);

	}

	// These are implemented in CoinCollector, etc. instead. This method should perhaps be abstract.
	protected void playSound() {}
	protected void stopSound() {}


	private void start(){
		//START
		dataHandler.newRoute();
		dataHandler.startWatch();
		pauseButton.setImageResource(R.drawable.pause);
		dataHandler.runStatus=RunStatus.RUNNING;
		playSound();
		System.out.println("Start!!!!!");
	}

	private void resume(){
		pauseButton.setImageResource(R.drawable.pause);
		stopButton.setVisibility(View.INVISIBLE);
		playSound();
		dataHandler.runStatus=RunStatus.RUNNING;
		System.out.println("Resume!!!!!");
	}

	private void pause(){
		pauseButton.setImageResource(R.drawable.play);
		stopButton.setVisibility(View.VISIBLE);
		stopSound();
		dataHandler.runStatus=RunStatus.PAUSED;
		System.out.println("Pause!!!!!!");
	}

	private void stop(){
		pauseButton.setImageResource(R.drawable.play);
		stopSound();
		dataHandler.runStatus=RunStatus.STOPPED;
		System.out.println("Stop!!!!!!");

		dataHandler.resetWatch();
		Intent finishedRunActivityIntent = new Intent(RunActivity.this, FinishedRunActivity.class);
		finishedRunActivityIntent.putExtra(Constants.EXTRA_ID, dataHandler.getCurrentRoute());
		startActivity(finishedRunActivityIntent);
		if(asyncTask!=null){
			asyncTask.cancel(true);
		}
		//				// Ska vara "finish()" egentligen men det fungerar inte?
		android.os.Process.killProcess(android.os.Process.myPid());
		//				//				StatsFragment statsFrag = (StatsFragment) getSupportFragmentManager().findFragmentByTag(
		//				//						"android:switcher:"+R.id.pager+":2");
		//				//				if(statsFragment.isAdded()){
		//				//					statsFrag.updateTableData(1,2);
		//				//				}
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
	public void sendFinalRoute(ArrayList<Location> finalRoute, float distance) {
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

	public void updateDisplay(long seconds, int distance, double currentspeed, int coins){
		RunFragment runFrag = (RunFragment) getSupportFragmentManager().findFragmentByTag(
				"android:switcher:"+R.id.pager+":0");
		if(runFragment.isAdded()){
			runFrag.updateDisp(seconds,distance,currentspeed,coins);
		}
	}
	
	private boolean isOkToRun(){
		return (gpsOn && soundOn && headphonesIn);
	}

	private void setGreenToRun(){
		if(isOkToRun() && !dataHandler.isRunning())
			pauseButton.setImageResource(R.drawable.play);
	}
	
	private void setNotGreenToRun(){
		pauseButton.setImageResource(R.drawable.play_red);
	}
	public void onGPSConnect() {
		if(!gpsOn){
			System.out.println("GPS on");
			gpsOn=true;
			gpsIcon.setImageResource(R.drawable.gps_green);
			setGreenToRun();
		}
		//		MapFragment mapFrag = (MapFragment) this.getSupportFragmentManager().findFragmentByTag(
		//                "android:switcher:"+R.id.pager+":1");
		//		if(this.statsFragment.isAdded()){
		//			mapFrag.setIsConnected(true);
		//		}

	}
	public void onGPSDisconnect() {
		gpsIcon.setImageResource(R.drawable.gps_red);
		gpsOn=false;
//		if(dataHandler.runStatus == RunStatus.RUNNING){
//			pause();
//		}
		setNotGreenToRun();

		//		MapFragment mapFrag = (MapFragment) this.getSupportFragmentManager().findFragmentByTag(
		//                "android:switcher:"+R.id.pager+":1");
		//		if(this.statsFragment.isAdded()){
		//			mapFrag.setIsConnected(false);
		//		}
	}
	public void onSoundOn() {
		if(!soundOn){
			System.out.println("Sound On");
			soundOn=true;
			soundIcon.setImageResource(R.drawable.sound_green);
			setGreenToRun();
		}
	}
	public void onSoundOff() {
		soundIcon.setImageResource(R.drawable.sound_red);
		soundOn=false;
		if(dataHandler.runStatus == RunStatus.RUNNING){
			pause();
		}
		setNotGreenToRun();
	}
	public void onHeadphonesIn(){
		if(!headphonesIn){
			System.out.println("HeadPhones In");
			headphonesIn=true;
			headPhonesIcon.setImageResource(R.drawable.headphones_green);
			setGreenToRun();
		}
	}
	public void onHeadphonesOut(){
		headPhonesIcon.setImageResource(R.drawable.headphones_red);
		headphonesIn=false;
		if(dataHandler.runStatus == RunStatus.RUNNING){
			pause();
		}
		setNotGreenToRun();
	}
	public void onCompassChanged(float headingAngleOrientation) {
		// TODO Auto-generated method stub	
	}
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub	
		dataHandler.newLocation(location);
	}
}
