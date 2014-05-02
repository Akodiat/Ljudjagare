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

import se.chalmers.group42.controller.HistoryListFragment.Callbacks;
import se.chalmers.group42.gameModes.CoinCollectorActivity;
import se.chalmers.group42.runforlife.Constants;
import se.chalmers.group42.runforlife.R;
import android.media.AudioManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

import android.widget.ListView;

import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;

public class MainActivity extends Activity implements Callbacks {

	private DrawerLayout navDrawerLayout;
	private ListView navDrawerList;
	private String[] navListOption;
	private ActionBarDrawerToggle actionBarDrawerToggle;
	private CharSequence appTitle;
	private CharSequence navDrawerTitle;
	private FragmentManager fm = getFragmentManager();
	private FragmentTransaction ft;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Make hardware buttons control the media volume
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

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
		/* Setting up listener for the ListView in the navigation drawer */
		navDrawerList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				selectItem(position);
			}
		});

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

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

		// Start MainRunFragment at startup
		if (getFragmentManager().findFragmentById(R.id.content_frame) == null) {
			selectItem(0);
		}

		// Show help dialog at first run of the app
		boolean firstrun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
				.getBoolean("firstrun", true);
		if (firstrun) {

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);

			// set title
			alertDialogBuilder.setTitle("Welcome");

			// set dialog message
			alertDialogBuilder
			.setMessage(
					"Welcome to Run for Life! Would you like to learn how to play?")
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int id) {
							// if this button is clicked, open help
							// fragment
							selectItem(2);
						}
					})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int id) {
							// if this button is clicked, just close the
							// dialog box and do nothing
							dialog.cancel();
						}
					});

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();

			// Save the state
			getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
			.putBoolean("firstrun", false).commit();
		}
	}

	/*
	 * Method handling the transition between the different fragments that may
	 * be chosen from the Navigation Drawer-menu.
	 */
	private void selectItem(int position) {
		switch (position) {
		case 0:
			// Insert the fragment by replacing any existing fragment
			ft = fm.beginTransaction();
			Fragment mainRunFragment = new MainRunFragment();
			ft.replace(R.id.content_frame, mainRunFragment, "mainRunFragment");
			// ft.addToBackStack(null);
			ft.commit();
			// update selected item and title, then close the drawer
			navDrawerList.setItemChecked(position, true);
			setTitle(navDrawerTitle);
			navDrawerLayout.closeDrawer(navDrawerList);
			break;
		case 1:
			ft = fm.beginTransaction();
			Fragment historyListFragment = new HistoryListFragment();
			ft.replace(R.id.content_frame, historyListFragment,
					"historyListFragment");
//			if (fragmentMainVisible) {
//				ft.addToBackStack(null);
//			}
			ft.commit();
			// update selected item and title, then close the drawer
			navDrawerList.setItemChecked(position, true);
			setTitle(navDrawerTitle);
			navDrawerLayout.closeDrawer(navDrawerList);
			break;
		case 2:
			ft = fm.beginTransaction();
			Fragment helpFragment = new HelpFragment();
			ft.replace(R.id.content_frame, helpFragment, "helpFragment");
//			if (fragmentMainVisible) {
//				ft.addToBackStack(null);
//			}
			ft.commit();
			// update selected item and title, then close the drawer
			navDrawerList.setItemChecked(position, true);
			setTitle(navDrawerTitle);
			navDrawerLayout.closeDrawer(navDrawerList);
			break;
		default:
		}
	}

	// Method needed to get the hamburgermenu working
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		actionBarDrawerToggle.onConfigurationChanged(newConfig);
	}

	// Method needed to get the hamburgermenu working
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// call ActionBarDrawerToggle.onOptionsItemSelected(), if it returns
		// true
		// then it has handled the app icon touch event
		if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		} else if (item.getItemId() == R.id.action_settings) {
			android.app.FragmentManager fragmentManager = getFragmentManager();
			android.app.FragmentTransaction ft = fragmentManager
					.beginTransaction();
			SettingsFragment fragmentSettings = new SettingsFragment();
			ft.replace(R.id.content_frame, fragmentSettings);
//			ft.hide(mMyMainFragment);
//			ft.addToBackStack(null);
			ft.commit();

			// fragmentManager.beginTransaction().replace(R.id.content_frame,
			// fragmentSettings).addToBackStack("settings").commit();
		}
		return super.onOptionsItemSelected(item);
	}

	// Method needed to get the hamburgermenu working
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

	@Override
	public void onHistoryItemSelected(String id) {

		SharedPreferences preferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("application_mode", "DISPLAY_MODE");
		editor.commit();

		// for the selected item ID.
		Intent detailIntent = new Intent(this, CoinCollectorActivity.class);
		detailIntent.putExtra(Constants.EXTRA_ID, Integer.parseInt(id));

		// Skicak med runläge eller finishedläge
		startActivity(detailIntent);
	}
	@Override
	public void onBackPressed(){
		if((fm.findFragmentByTag("mainRunFragment")==null)){
			selectItem(0);
		}
		else{
			super.onBackPressed();
		}
	}


}