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
import sensors.GPSInputHandler;
import sensors.GPSInputListener;
import android.location.Location;
import android.media.AudioManager;
import android.os.Bundle;
import android.content.IntentFilter;
import android.graphics.Point;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import at.technikum.mti.fancycoverflow.FancyCoverFlow;
import at.technikum.mti.fancycoverflow.FancyCoverFlowSampleAdapter;
import android.support.v4.app.ActionBarDrawerToggle;

public class MainActivity extends NavDrawerActivity implements
		StatusIconEventListener, GPSInputListener {

	private FancyCoverFlow fancyCoverFlow;
	private Button runButton;
	private int coverFlowHeight;
	private ImageView gpsIcon, headPhonesIcon;
	private boolean gpsOn, headphonesIn;

	private int apiLevel;

	private final int ACTION_BAR_HEIGHT_MDPI = 32;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Get API-level
		apiLevel = Integer.valueOf(android.os.Build.VERSION.SDK_INT);

		// Make hardware buttons control the media volume
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		// Setting up status icons
		gpsIcon = (ImageView) findViewById(R.id.gps_icon);
		headPhonesIcon = (ImageView) findViewById(R.id.headphones_icon);

		// Setting up Sensor input
		new GPSInputHandler(this, this);

		// Setting up Navigation Drawer from left side of screen
		appTitle = navDrawerTitle = getTitle();
		// The string-array of list options, as "Run" and "History"
		navListOption = getResources().getStringArray(R.array.nav_drawer_array);
		// The whole drawer layout
		navDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		// The list view of options
		navDrawerList = (ListView) findViewById(R.id.drawer_list);

		/*
		 * Custom shadow set up drawer_shadow.9 images borrowed from
		 * com.example.android.navigationdrawerexample
		 */
		navDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// Setup of drawer list view with items contained in navListOptions and
		// click listener to them
		navDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, navListOption));
		navDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		if (apiLevel >= 14) {
			getActionBar().setHomeButtonEnabled(true);
		}

		actionBarDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		navDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer icon to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description */
		R.string.drawer_close /* "close drawer" description */
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

		/*
		 * The screen size and density of the device running the program is
		 * retrieved to draw the components to good proportions.
		 * 
		 * http://stackoverflow.com/questions/1016896/how-to-get-screen-dimensions
		 */
		// Getting the display size
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;
		System.out.println("Width= " + width);
		System.out.println("Height= " + height);
		// Getting the display density
		int density = (int) getResources().getDisplayMetrics().density;
		System.out.println("Density= "
				+ getResources().getDisplayMetrics().density);
		/*
		 * Setting a good coverflow height as 4/9 of the screen height minus the
		 * actionbar. I need to multiply the density with the standard height of
		 * an action bar.
		 */
		coverFlowHeight = (int) ((4.0 / 9.0) * (height - density
				* ACTION_BAR_HEIGHT_MDPI));
		System.out.println("Coverflow height: " + coverFlowHeight);

		/*
		 * Setting up the cover flow
		 */
		fancyCoverFlow = (FancyCoverFlow) this
				.findViewById(R.id.fancyCoverFlow);
		fancyCoverFlow.setAdapter(new FancyCoverFlowSampleAdapter(
				coverFlowHeight));
		fancyCoverFlow.setUnselectedAlpha(1.0f);
		fancyCoverFlow.setUnselectedSaturation(0.0f);
		fancyCoverFlow.setUnselectedScale(0.5f);
		fancyCoverFlow.setSpacing(0);
		fancyCoverFlow.setMaxRotation(0);
		fancyCoverFlow.setScaleDownGravity(0.2f);
		fancyCoverFlow.setActionDistance(FancyCoverFlow.ACTION_DISTANCE_AUTO);

		/*
		 * Setting up the run-button
		 */
		runButton = (Button) findViewById(R.id.run_button);
		runButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				new ModeController(MainActivity.this)
						.launchMode((int) fancyCoverFlow.getSelectedItemId());
			}
		});

		// Setting up statusIconHandler
		IntentFilter filter = new IntentFilter(
				"android.intent.action.HEADSET_PLUG");
		StatusIconHandler receiver = new StatusIconHandler(this, this);
		registerReceiver(receiver, filter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onGPSConnect() {
		if (!gpsOn) {
			gpsOn = true;
			gpsIcon.setImageResource(R.drawable.gps_activated);
			setGreenToRun();
		}
	}

	@Override
	public void onGPSDisconnect() {
		gpsOn = false;
		gpsIcon.setImageResource(R.drawable.gps_disabled);
		setNotGreenToRun();
	}

	@Override
	public void onHeadphonesIn() {
		if (!headphonesIn) {
			headphonesIn = true;
			headPhonesIcon.setImageResource(R.drawable.headphones_activated);
			setGreenToRun();
		}
	}

	@Override
	public void onHeadphonesOut() {
		headphonesIn = false;
		headPhonesIcon.setImageResource(R.drawable.headphones_disabled);
		setNotGreenToRun();
	}

	private boolean isOkToRun() {
		return (gpsOn && headphonesIn);
	}

	private void setGreenToRun() {
		if (isOkToRun()) {
			runButton.setTextColor(getResources().getColor(
					R.color.common_signin_btn_light_text_focused));
		}
	}

	private void setNotGreenToRun() {
		runButton.setTextColor(getResources().getColor(
				R.color.common_signin_btn_light_text_disabled));
	}

	@Override
	public void onLocationChanged(Location location) {
		// Not needed.
	}

}