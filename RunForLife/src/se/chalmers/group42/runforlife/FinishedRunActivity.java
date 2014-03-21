package se.chalmers.group42.runforlife;

import java.util.ArrayList;

import se.chalmers.group42.database.*;
import se.chalmers.group42.runforlife.SwipeableActivity.SectionsPagerAdapter;

import com.google.android.gms.maps.model.LatLng;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class FinishedRunActivity extends SwipeableActivity implements
MapFragment.OnHeadlineSelectedListener{

	//Class for handling database
	//	protected DataHandler dataHandler;

	MySQLiteHelper db;

	//Button
	private ImageButton endButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_finished_run);

		// Setting up the action bar
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		testApplication app = (testApplication) getApplication();
		this.db = app.getDatabase();

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

		runFragment = new FinishedRunFragment();
		mapFragment = new MapFragment();
		statsFragment = new StatsFragment();

		
		
		/*
		 * Button taking you back to main menu.
		 */
		endButton = (ImageButton) findViewById(R.id.button_end);
		endButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		
		Bundle extras = getIntent().getExtras();
		if(extras != null){
			int id = extras.getInt("test");
			FinishedRoute fin = db.getFinishedRoute(id);
			long secs = fin.getTotTime();
			
			Bundle args = new Bundle();
			args.putLong("time", secs);
			runFragment.setArguments(args);
		}

	}

	//	//TODO Varför ärvs inte denna? Borde kunna bortkommenteras men då funkar inte tabarna
	//	@Override
	//	public void onTabSelected(ActionBar.Tab tab,
	//			FragmentTransaction fragmentTransaction) {
	//		// When the given tab is selected, switch to the corresponding page in
	//		// the ViewPager.
	//		mViewPager.setCurrentItem(tab.getPosition());
	//		System.out.println("Tab pos= " + tab.getPosition());
	//	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.run, menu);
		return true;
	}

	@Override
	public void sendMapLocation(LatLng latlng) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendFinalRoute(ArrayList<Location> finalRoute) {
		// TODO Auto-generated method stub
	}
}