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

package se.chalmers.group42.runforlife;

import se.chalmers.group42.runforlife.NavDrawerActivity.DrawerItemClickListener;
import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Point;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import at.technikum.mti.fancycoverflow.FancyCoverFlow;
import at.technikum.mti.fancycoverflow.FancyCoverFlowSampleAdapter;

public class MainActivity extends NavDrawerActivity {

	private FancyCoverFlow fancyCoverFlow;
	private ImageButton runButton;
	private Intent runActivityIntent;
	private int coverFlowHeight;

	//TODO titlar för navdrawer

	private final int ACTION_BAR_HEIGHT_MDPI = 32;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Setting up Navigation Drawer from left side of screen
		navListOption = getResources().getStringArray(R.array.nav_drawer_array);
		navDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		navDrawerList = (ListView) findViewById(R.id.drawer_list);

		/*
		 * Custom shadow set up
		 * drawer_shadow.9 images borrowed from com.example.android.navigationdrawerexample
		 */
		navDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		//Setup of drawer list view with items and click listener
		navDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, navListOption));
		navDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

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
		 * Setting a good coverflow height as 4/9 of the screen height minus
		 * the actionbar. I need to multiply the density with the standard
		 * height of an action bar.
		 */
		coverFlowHeight = (int) ((4.0 / 9.0) * (height - density
				* ACTION_BAR_HEIGHT_MDPI));
		System.out.println("Coverflow height: " + coverFlowHeight);

		/*
		 * Setting up the cover flow
		 */
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

		/*
		 * Setting up the run-button
		 */
		runButton = (ImageButton) findViewById(R.id.runButton);
		//		runActivityIntent = new Intent(this, RunActivity.class);
		runButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//startActivity(runActivityIntent);
				System.out.println();
				new ModeController(MainActivity.this).launchMode((int) fancyCoverFlow
						.getSelectedItemId());
			}
		});
	}

	//		//Setting up Navigation Drawer from left side of screen
	//		navListOption = getResources().getStringArray(R.array.nav_drawer_array);
	//		navDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
	//		navDrawerList = (ListView) findViewById(R.id.drawer_list);
	//
	//		/*
	//		 * Custom shadow set up
	//		 * drawer_shadow.9 images borrowed from com.example.android.navigationdrawerexample
	//		 */
	//		navDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
	//		//Setup of drawer list view with items and click listener
	//		navDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, navListOption));
	//		navDrawerList.setOnItemClickListener(new DrawerItemClickListener());
	//
	//		// enable ActionBar app icon to behave as action to toggle nav drawer
	//		getActionBar().setDisplayHomeAsUpEnabled(true);
	//		getActionBar().setHomeButtonEnabled(true);
	//
	//
	//
	//	}
	//
	//	/* The click listner for ListView in the navigation drawer */
	//	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	//		@Override
	//		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	//			selectItem(position);
	//		}
	//	}
	//
	//	private void selectItem(int position) {
	//		//Transition to History-activity
	//		switch(position) {
	//		case 1:
	//			Intent a = new Intent(MainActivity.this, CompletedRunListActivity.class);
	//			startActivity(a);
	//			break;
	//		case 2:
	//			Intent b = new Intent(MainActivity.this, MainActivity.class);
	//			startActivity(b);
	//			break;
	//		default:
	//		}
	//	}

	//        // update the main content by replacing fragments
	//        Fragment fragment = new PlanetFragment();
	//        Bundle args = new Bundle();
	//        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
	//        fragment.setArguments(args);
	//
	//        FragmentManager fragmentManager = getFragmentManager();
	//        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
	//
	//        // update selected item and title, then close the drawer
	//        mDrawerList.setItemChecked(position, true);
	//        setTitle(mPlanetTitles[position]);
	//        mDrawerLayout.closeDrawer(mDrawerList);

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}