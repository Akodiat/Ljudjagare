/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.chalmers.group42.controller;

import se.chalmers.group42.runforlife.ModeController;
import se.chalmers.group42.runforlife.R;
import se.chalmers.group42.runforlife.StatusIconEventListener;
import se.chalmers.group42.runforlife.StatusIconHandler;
import se.chalmers.group42.runforlife.DataHandler.RunStatus;
import se.chalmers.group42.runforlife.R.array;
import se.chalmers.group42.runforlife.R.drawable;
import se.chalmers.group42.runforlife.R.id;
import se.chalmers.group42.runforlife.R.layout;
import se.chalmers.group42.runforlife.R.menu;
import se.chalmers.group42.runforlife.R.string;
import sensors.GPSInputHandler;
import sensors.GPSInputListener;
import android.location.Location;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.graphics.Point;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import at.technikum.mti.fancycoverflow.FancyCoverFlow;
import at.technikum.mti.fancycoverflow.FancyCoverFlowSampleAdapter;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;

public class MainActivity extends FragmentActivity{

	private DrawerLayout navDrawerLayout;
	private ListView navDrawerList;
	private String[] navListOption;
	private ActionBarDrawerToggle actionBarDrawerToggle;
	private CharSequence appTitle;
	private CharSequence navDrawerTitle;

	//TODO titlar f�r navdrawer

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Make hardware buttons control the media volume
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		//Setting up Navigation Drawer from left side of screen
		appTitle = navDrawerTitle = getTitle();
		//The string-array of list options, as "Run" and "History"
		navListOption = getResources().getStringArray(R.array.nav_drawer_array);
		//The whole drawer layout
		navDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		//The list view of options
		navDrawerList = (ListView) findViewById(R.id.drawer_list);

		/*
		 * Custom shadow set up
		 * drawer_shadow.9 images borrowed from com.example.android.navigationdrawerexample
		 */
		navDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		//Setup of drawer list view with items contained in navListOptions and click listener to them
		navDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, navListOption));
		/* Setting up listener for the ListView in the navigation drawer */
		navDrawerList.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectItem(position);
			}
		});

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		actionBarDrawerToggle = new ActionBarDrawerToggle(
				this,                  /* host Activity */
				navDrawerLayout,         /* DrawerLayout object */
				R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
				R.string.drawer_open,  /* "open drawer" description */
				R.string.drawer_close  /* "close drawer" description */
				) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				getActionBar().setTitle("Start a run");
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getActionBar().setTitle(appTitle);
			}
		};

		// Set the drawer toggle as the DrawerListener
		navDrawerLayout.setDrawerListener(actionBarDrawerToggle);

		//Start MainRunFragment at startup
		if (getFragmentManager().findFragmentById(R.id.content_frame) == null) {
			selectItem(0);
		}

		//		/*
		//		 * The screen size and density of the device running the program is
		//		 * retrieved to draw the components to good proportions.
		//		 * 
		//		 * http://stackoverflow.com/questions/1016896/how-to-get-screen-dimensions
		//		 */
		//		// Getting the display size
		//		Display display = getWindowManager().getDefaultDisplay();
		//		Point size = new Point();
		//		display.getSize(size);
		//		int width = size.x;
		//		int height = size.y;
		//		System.out.println("Width= " + width);
		//		System.out.println("Height= " + height);
		//		// Getting the display density
		//		int density = (int) getResources().getDisplayMetrics().density;
		//		System.out.println("Density= "
		//				+ getResources().getDisplayMetrics().density);
		//		/*
		//		 * Setting a good coverflow height as 4/9 of the screen height minus
		//		 * the actionbar. I need to multiply the density with the standard
		//		 * height of an action bar.
		//		 */
		//		coverFlowHeight = (int) ((4.0 / 9.0) * (height - density
		//				* ACTION_BAR_HEIGHT_MDPI));
		//		System.out.println("Coverflow height: " + coverFlowHeight);
		//
		//		/*
		//		 * Setting up the cover flow
		//		 */
		//		fancyCoverFlow = (FancyCoverFlow) this
		//				.findViewById(R.id.fancyCoverFlow);
		//		fancyCoverFlow.setAdapter(new FancyCoverFlowSampleAdapter(
		//				coverFlowHeight));
		//		fancyCoverFlow.setUnselectedAlpha(1.0f);
		//		fancyCoverFlow.setUnselectedSaturation(0.0f);
		//		fancyCoverFlow.setUnselectedScale(0.5f);
		//		fancyCoverFlow.setSpacing(0);
		//		fancyCoverFlow.setMaxRotation(0);
		//		fancyCoverFlow.setScaleDownGravity(0.2f);
		//		fancyCoverFlow.setActionDistance(FancyCoverFlow.ACTION_DISTANCE_AUTO);
		//
		//		/*
		//		 * Setting up the run-button
		//		 */
		//		runButton = (ImageButton) findViewById(R.id.runButton);
		//		//		runActivityIntent = new Intent(this, RunActivity.class);
		//		runButton.setOnClickListener(new View.OnClickListener() {
		//			@Override
		//			public void onClick(View view) {
		//				//				//startActivity(runActivityIntent);
		//				//				System.out.println();
		//				new ModeController(MainActivity.this).launchMode((int) fancyCoverFlow
		//						.getSelectedItemId());
		//			}
		//		});

		//		//Setting up statusIconHandler
		//		IntentFilter filter = new IntentFilter("android.intent.action.HEADSET_PLUG");
		//		StatusIconHandler receiver = new StatusIconHandler(this, this);
		//		registerReceiver(receiver, filter);
	}

	/*
	 * Method handling the transition between the different fragments that may be chosen
	 * from the Navigation Drawer-menu.
	 */
	private void selectItem(int position) {
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction ft;
		switch(position) {
		case 0:
			// Insert the fragment by replacing any existing fragment
			ft = fragmentManager.beginTransaction();
			Fragment mainRunFragment = new MainRunFragment();
			ft.replace(R.id.content_frame, mainRunFragment);
			ft.commit();
			//			Intent mainIntent = new Intent(NavDrawerActivity.this, MainActivity.class);
			//			/*Flag clearing other activities from backstack to make sure that back press
			//			 * from main will exit the app.
			//			 */
			//
			//			mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			//			startActivity(mainIntent);
			// update selected item and title, then close the drawer
			navDrawerList.setItemChecked(position, true);
			setTitle(navDrawerTitle);
			navDrawerLayout.closeDrawer(navDrawerList);
			break;
		case 1:
			ft = fragmentManager.beginTransaction();
			Fragment completedRunListFragment = new CompletedRunListFragment();
			ft.replace(R.id.content_frame, completedRunListFragment);
			ft.commit();
			//			Intent historyIntent = new Intent(NavDrawerActivity.this, CompletedRunListActivity.class);
			//			//Flag making sure that no new instances of a current running activity are launched
			//			historyIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			//			startActivity(historyIntent);
			// update selected item and title, then close the drawer
			navDrawerList.setItemChecked(position, true);
			setTitle(navDrawerTitle);
			navDrawerLayout.closeDrawer(navDrawerList);
			break;
		default:
		}
	}

	//Method needed to get the hamburgermenu working
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		actionBarDrawerToggle.onConfigurationChanged(newConfig);
	}

	//Method needed to get the hamburgermenu working
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// call ActionBarDrawerToggle.onOptionsItemSelected(), if it returns true
		// then it has handled the app icon touch event
		if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		else if(item.getItemId()==R.id.action_settings){
			//						Intent settingsIntent = new Intent(NavDrawerActivity, SettingsActivity.class);
			//						//Flag making sure that no new instances of a current running activity are launched
			//						startActivity(settingsIntent);

			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction ft = fragmentManager.beginTransaction();
			Fragment fragmentSettings = new SettingsFragment();
			ft.replace(R.id.content_frame, fragmentSettings);
			ft.addToBackStack("settings");
			ft.commit();

			//			fragmentManager.beginTransaction().replace(R.id.content_frame, fragmentSettings).addToBackStack("settings").commit();
		}
		return super.onOptionsItemSelected(item);
	}

	//Method needed to get the hamburgermenu working
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		actionBarDrawerToggle.syncState();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	//	@Override
	//	public void onGPSConnect() {
	//		if(!gpsOn){
	//			System.out.println("GPS on");
	//			gpsOn=true;
	//			gpsIcon.setImageResource(R.drawable.gps_green);
	//			gpsText.setText("GPS connected");
	//			setGreenToRun();
	//		}
	//	}
	//
	//	@Override
	//	public void onGPSDisconnect() {
	//		gpsOn=false;
	//		gpsIcon.setImageResource(R.drawable.gps_red);
	//		gpsText.setText("Searching for gps...");
	//		setNotGreenToRun();
	//	}
	//
	//	@Override
	//	public void onSoundOn() {
	//		if(!soundOn){
	//			System.out.println("Sound On");
	//			soundOn=true;
	//			soundIcon.setImageResource(R.drawable.sound_green);
	//			soundText.setText("Sound on");
	//			setGreenToRun();
	//		}
	//	}
	//
	//	@Override
	//	public void onSoundOff() {
	//		soundOn=false;
	//		soundIcon.setImageResource(R.drawable.sound_red);
	//		soundText.setText("Turn up media volume");
	//		setNotGreenToRun();
	//	}
	//
	//	@Override
	//	public void onHeadphonesIn(){
	//		if(!headphonesIn){
	//			System.out.println("HeadPhones In");
	//			headphonesIn=true;
	//			headPhonesIcon.setImageResource(R.drawable.headphones_green);
	//			headPhonesText.setText("Headphones: connected");
	//			setGreenToRun();
	//		}
	//	}
	//
	//	@Override
	//	public void onHeadphonesOut(){
	//		headphonesIn=false;
	//		headPhonesIcon.setImageResource(R.drawable.headphones_red);
	//		headPhonesText.setText("Plug in headphones");
	//		setNotGreenToRun();
	//	}
	//
	//	private boolean isOkToRun(){
	//		return (gpsOn && soundOn && headphonesIn);
	//	}
	//
	//	private void setGreenToRun(){
	//		if(isOkToRun()){
	//			runButton.setImageResource(R.drawable.run);
	//		}
	//	}
	//
	//	private void setNotGreenToRun(){
	//		runButton.setImageResource(R.drawable.run_red);
	//	}
	//
	//	@Override
	//	public void onLocationChanged(Location location) {
	//		// We don't need to do stuff here really...
	//	}

}