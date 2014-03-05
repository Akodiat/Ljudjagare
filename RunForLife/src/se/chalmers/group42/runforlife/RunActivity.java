package se.chalmers.group42.runforlife;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


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
public class RunActivity extends FragmentActivity implements
		ActionBar.TabListener,
		MapFragment.OnHeadlineSelectedListener{

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	Fragment runFragment;
	Fragment mapFragment;
	Fragment statsFragment;
	
	Button pauseButton, finishButton;
	
	//Class for handling GPS and Compass sensors
	SensorInputHandler sensorInputHandler;
	
	//Class for handling database
	DataHandler dataHandler;
	
	//Class for handling different Game modes.
	ModeController modeController;
	
	private static final 	LatLng HOME_MARCUS 		= new LatLng(58.489657, 13.777925);
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_run);

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
		
//		this.sensorInputHandler = new SensorInputHandler(this);
		this.dataHandler = new DataHandler(this);
		
		//Setting up pausebutton
		pauseButton = (Button) findViewById(R.id.button_pause);
		pauseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				System.out.println("Pause clicked");
			}
		});

		//Setting up finishbutton
		finishButton = (Button) findViewById(R.id.button_finish);
		finishButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				System.out.println("Finish clicked");
			}
		});
	//	this.modeController.launchMode(Mode.COIN_COLLECTOR); //TODO: Make it possible to actually choose which mode is launched
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.run, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
		System.out.println("Tab pos= " + tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		/*
		 * getItem is called to instantiate the fragment for the given page.(non-Javadoc)
		 * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
		 */
		@Override
		public Fragment getItem(int position) {
			//Bundle might be used later to send information between fragments
//			Bundle args = new Bundle();
//			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
//			fragment.setArguments(args);
			switch(position){
				case 0:
					return runFragment;
				case 1:
					return mapFragment;
				case 2:
					return statsFragment;
			}
			return null;
		}
		
		@Override
		public int getCount() {
			// Show 3 total pages
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_run).toUpperCase(l);
			case 1:
				return getString(R.string.title_map).toUpperCase(l);
			case 2:
				return getString(R.string.title_stats).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * Called by SensorInputHandler when the sensor values are updated
	 */
	public void onUpdatedSensors(SensorValues sensorValues) {
		//Send the updated sensorValues to the active GameMode
			
	}
	
	@Override
	public void sendMapLocation(LatLng latLng) {
//		System.out.println("Test");
		findDirections( HOME_MARCUS.latitude, HOME_MARCUS.longitude
			, latLng.latitude, latLng.longitude, GMapV2Direction.MODE_WALKING );
		
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

		GetDirectionsAsyncTask asyncTask = new GetDirectionsAsyncTask(this);
		asyncTask.execute(map);	
	}
}
