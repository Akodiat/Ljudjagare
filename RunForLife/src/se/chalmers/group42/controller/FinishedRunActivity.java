package se.chalmers.group42.controller;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.group42.controller.MapFragment.OnHeadlineSelectedListener;
import se.chalmers.group42.controller.SwipeableActivity.SectionsPagerAdapter;
import se.chalmers.group42.database.*;
import se.chalmers.group42.runforlife.Constants;
import se.chalmers.group42.runforlife.R;
import se.chalmers.group42.runforlife.RunForLifeApplication;

import com.google.android.gms.maps.model.LatLng;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class FinishedRunActivity extends SwipeableActivity implements
MapFragment.OnHeadlineSelectedListener{

	//Class for handling database
	//	protected DataHandler dataHandler;

	MySQLiteHelper db;
	int id;

	//Button
	private ImageButton finishButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_finished_run);

		// Setting up the action bar
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayHomeAsUpEnabled(true);

		RunForLifeApplication app = (RunForLifeApplication) getApplication();
		this.db = app.getDatabase();

		Bundle extras = getIntent().getExtras();
		if(extras != null){
			id = extras.getInt(Constants.EXTRA_ID);
		}
		
//		/*
//		 *  Creating the adapter that will return a fragment for each of the three 
//		 *  primary sections of the app
//		 */
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

		setRunFragment(new FinishedRunFragment());
		setMapFragment(new FinishedMapFragment());
		setStatsFragment(new FinishedStatsFragment());



		/*
		 * Button taking you back to main menu.
		 */
		finishButton = (ImageButton) findViewById(R.id.button_finish);
		finishButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});


			FinishedRoute fin = db.getFinishedRoute(id);
			Bundle args = new Bundle();
			args.putLong("time", fin.getTotTime());
			args.putInt("distance", fin.getDist());
			args.putDouble("speed", fin.getSpeed());


			Bundle locs = new Bundle();
			List<Point> points = db.getAllPointsByRoute(id);
			double[] latitudes = new double[points.size()];
			double[] longitudes = new double[points.size()];
			for(int i = 0 ; i < points.size() ; i++){
				latitudes[i] = points.get(i).getLatitude();
				longitudes[i] = points.get(i).getLongitude();
			}
			locs.putDoubleArray("latitudes", latitudes);
			locs.putDoubleArray("longitudes", longitudes);

			List<Coins> coins = db.getAllCoinsByRoute(id);
			int nrCoins = coins.size();
			
			args.putInt("nrCoins", nrCoins);

			double[] coinlat = new double[nrCoins];
			double[] coinlng = new double[nrCoins];

			long[] times = new long[nrCoins];
			int[] dists = new int[nrCoins];

			for(int i = 0 ; i < nrCoins ; i++){
				Location l = coins.get(i).getLocation();
				coinlat[i] = l.getLatitude();
				coinlng[i] = l.getLongitude();

				times[i] = coins.get(i).getTime();
				dists[i] = coins.get(i).getDistance();
			}
			locs.putDoubleArray("coinlat", coinlat);
			locs.putDoubleArray("coinlng", coinlng);

			getMapFragment().setArguments(locs);

			getRunFragment().setArguments(args);

			Bundle stats = new Bundle();

			stats.putLongArray("times", times);
			stats.putIntArray("dists", dists);

			getStatsFragment().setArguments(stats);

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
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}

	@Override
	public void sendMapLocation(LatLng latlng) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendFinalRoute(ArrayList<Location> finalRoute, float distance) {
		// TODO Auto-generated method stub
	}
	
	public void test(){
		
	}
}