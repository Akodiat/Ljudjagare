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

import java.util.Locale;

import se.chalmers.group42.runforlife.R;



import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v4.app.ActionBarDrawerToggle;

/**
 * This example illustrates a common usage of the DrawerLayout widget
 * in the Android support library.
 * <p/>
 * <p>When a navigation (left) drawer is present, the host activity should detect presses of
 * the action bar's Up affordance as a signal to open and close the navigation drawer. The
 * ActionBarDrawerToggle facilitates this behavior.
 * Items within the drawer should fall into one of two categories:</p>
 * <p/>
 * <ul>
 * <li><strong>View switches</strong>. A view switch follows the same basic policies as
 * list or tab navigation in that a view switch does not create navigation history.
 * This pattern should only be used at the root activity of a task, leaving some form
 * of Up navigation active for activities further down the navigation hierarchy.</li>
 * <li><strong>Selective Up</strong>. The drawer allows the user to choose an alternate
 * parent for Up navigation. This allows a user to jump across an app's navigation
 * hierarchy at will. The application should treat this as it treats Up navigation from
 * a different task, replacing the current task stack using TaskStackBuilder or similar.
 * This is the only form of navigation drawer that should be used outside of the root
 * activity of a task.</li>
 * </ul>
 * <p/>
 * <p>Right side drawers should be used for actions, not navigation. This follows the pattern
 * established by the Action Bar that navigation should be to the left and actions to the right.
 * An action should be an operation performed on the current contents of the window,
 * for example enabling or disabling a data overlay on top of the current content.</p>
 */

/*
 * Abstract base class handling the logic of the navigation drawer.
 */
public abstract class NavDrawerActivity extends FragmentActivity {

	protected DrawerLayout navDrawerLayout;
	protected ListView navDrawerList;
	protected String[] navListOption;
	protected ActionBarDrawerToggle actionBarDrawerToggle;
	protected CharSequence appTitle;
	protected CharSequence navDrawerTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	/* The click listner for ListView in the navigation drawer */
	public class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position);
		}		
	}
	
	/*
	 * Method handling the transition between the different activities that may be chosen
	 * from the Navigation Drawer-menu.
	 */
	protected void selectItem(int position) {
		switch(position) {
		case 0:
			Intent mainIntent = new Intent(NavDrawerActivity.this, MainActivity.class);
			/*Flag clearing other activities from backstack to make sure that back press
			 * from main will exit the app.
			 */
			
			mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(mainIntent);
			// update selected item and title, then close the drawer
			navDrawerList.setItemChecked(position, true);
			setTitle(navDrawerTitle);
			navDrawerLayout.closeDrawer(navDrawerList);
			break;
		case 1:
			Intent historyIntent = new Intent(NavDrawerActivity.this, CompletedRunListActivity.class);
			//Flag making sure that no new instances of a current running activity are launched
			historyIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(historyIntent);
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
			Intent settingsIntent = new Intent(NavDrawerActivity.this, SettingsActivity.class);
			//Flag making sure that no new instances of a current running activity are launched
			startActivity(settingsIntent);
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
}