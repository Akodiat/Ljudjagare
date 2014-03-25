package se.chalmers.group42.runforlife;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.group42.database.MySQLiteHelper;
import se.chalmers.group42.database.Route;
import se.chalmers.group42.runforlife.NavDrawerActivity.DrawerItemClickListener;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * An activity representing a list of Completed Runs. This activity has
 * different presentations for handset and tablet-size devices. On handsets, the
 * activity presents a list of items, which when touched, lead to a
 * {@link CompletedRunDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link CompletedRunListFragment} and the item details (if present) is a
 * {@link CompletedRunDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link CompletedRunListFragment.Callbacks} interface to listen for item
 * selections.
 */
public class CompletedRunListActivity extends NavDrawerActivity implements
CompletedRunListFragment.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	private int apiLevel;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_completedrun_list);	
		
		
		//Get API-level
		apiLevel = Integer.valueOf(android.os.Build.VERSION.SDK_INT);

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
		if(apiLevel>=14){
			getActionBar().setHomeButtonEnabled(true);
		}

		if (findViewById(R.id.completedrun_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((CompletedRunListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.completedrun_list))
					.setActivateOnItemClick(true);
		}

		// TODO: If exposing deep links into your app, handle intents here.
	}

	/**
	 * Callback method from {@link CompletedRunListFragment.Callbacks}
	 * indicating that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String id) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(CompletedRunDetailFragment.ARG_ITEM_ID, id);
			CompletedRunDetailFragment fragment = new CompletedRunDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
			.replace(R.id.completedrun_detail_container, fragment)
			.commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this,
					FinishedRunActivity.class);
			detailIntent.putExtra("test", Integer.parseInt(id));
			startActivity(detailIntent);
		}
	}
}
