package se.chalmers.group42.controller;

import se.chalmers.group42.runforlife.ModeController;
import se.chalmers.group42.runforlife.R;
import se.chalmers.group42.runforlife.StatusIconEventListener;
import se.chalmers.group42.runforlife.StatusIconHandler;
import se.chalmers.group42.sensors.GPSInputHandler;
import se.chalmers.group42.sensors.GPSInputListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.location.Location;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v13.app.FragmentStatePagerAdapter;

public class MainRunFragment extends Fragment implements
StatusIconEventListener, GPSInputListener {
	private View view;
	private Activity mainActivity;
	private Button runButton;
	private ImageView gpsIcon, headPhonesIcon;
	private boolean gpsOn, headphonesIn;
	private ImageView modeIndicator;
	private StatusIconHandler statusIconHandler;

	/**
	 * The number of modes in the application.
	 */
	private static final int NUM_MODES = 2;

	private ViewPager mPager;

	private PagerAdapter mPagerAdapter;

	private int[] modeIndicators = { R.drawable.mode_indicator,
			R.drawable.mode_indicator_2 };

	@Override
	public void onAttach(final Activity activity) {
		super.onAttach(activity);

		mainActivity = (MainActivity) activity;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_main_run, null);

		// Instantiate ViewPager and PagerAdapter.
		mPager = (ViewPager) view.findViewById(R.id.pager);
		mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
		mPager.setPageTransformer(true, new DepthPageTransformer());
		mPager.setAdapter(mPagerAdapter);
		mPager.setOnPageChangeListener(new DetailOnPageChangeListener());

		// Setting up status icons
		gpsIcon = (ImageView) view.findViewById(R.id.gps_icon);
		headPhonesIcon = (ImageView) view.findViewById(R.id.headphones_icon);
		modeIndicator = (ImageView) view.findViewById(R.id.mode_indicator);

		// Setting up Sensor input
		new GPSInputHandler(this, mainActivity);

		// Get the display size
		Display display = mainActivity.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;
		System.out.println("Width= " + width);
		System.out.println("Height= " + height);

		// Get display density
		System.out.println("Density= "
				+ getResources().getDisplayMetrics().density);

		/*
		 * Set up the run-button
		 */
		runButton = (Button) view.findViewById(R.id.run_button);
		runButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				int selectedMode = mPager.getCurrentItem();

				if (selectedMode == 0 && (!gpsOn || !headphonesIn)) {
					// show informative dialog

					new AlertDialog.Builder(getActivity())
					.setMessage(
							"This mode requires GPS to be turned on and headphones to be plugged in.")
							.setPositiveButton(android.R.string.yes,
									new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialog,
										int which) {
									// continue to initial state
								}
							}).show();

				} else {
					setPrefs();
					new ModeController(mainActivity).launchMode((int) mPager
							.getCurrentItem());
				}
			}
		});

		// Set up statusIconHandler
		IntentFilter filter = new IntentFilter(
				"android.intent.action.HEADSET_PLUG");
		statusIconHandler = new StatusIconHandler(this, mainActivity);
		mainActivity.registerReceiver(statusIconHandler, filter);
		return view;

	}

	/**
	 * A pager adapter that represents 3 modes in sequence.
	 */
	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
		public ScreenSlidePagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public Fragment getItem(int position) {
			return ScreenSlidePageFragment.create(position);
		}

		@Override
		public int getCount() {
			return NUM_MODES;
		}
	}

	@Override
	public void onGPSConnect() {
		if (!gpsOn) {
			gpsOn = true;
			gpsIcon.setImageResource(R.drawable.ic_action_location_found);
		}
	}

	@Override
	public void onGPSDisconnect() {
		gpsOn = false;
		gpsIcon.setImageResource(R.drawable.ic_action_location_off);
	}

	@Override
	public void onHeadphonesIn() {
		if (!headphonesIn) {
			headphonesIn = true;
			headPhonesIcon.setImageResource(R.drawable.ic_action_headphones);
		}
	}

	@Override
	public void onHeadphonesOut() {
		headphonesIn = false;
		headPhonesIcon.setImageResource(R.drawable.ic_action_headphones_off);
	}

	@Override
	public void onLocationChanged(Location location) {
		// Unneeded
	}

	public void setPrefs() {
		SharedPreferences preferences = getActivity().getSharedPreferences(
				"MODE", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("application_mode", "RUN_MODE");
		editor.commit();
	}

	/**
	 * Get the current view position from the ViewPager by extending
	 * SimpleOnPageChangeListener class and adding your method
	 */
	public class DetailOnPageChangeListener extends
	ViewPager.SimpleOnPageChangeListener {

		@Override
		public void onPageSelected(int position) {
			modeIndicator.setImageResource(modeIndicators[position]);
		}
	}
	@Override
	public void onPause() {
		super.onPause();
		mainActivity.unregisterReceiver(statusIconHandler);
	}
	
//	@Override
//	public void onResume() {
//		IntentFilter filter = new IntentFilter(
//				"android.intent.action.HEADSET_PLUG");
//		mainActivity.registerReceiver(statusIconHandler, filter);
//	}

}
