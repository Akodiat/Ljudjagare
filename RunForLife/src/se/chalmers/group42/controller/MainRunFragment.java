package se.chalmers.group42.controller;

import se.chalmers.group42.runforlife.ModeController;
import se.chalmers.group42.runforlife.R;
import se.chalmers.group42.runforlife.StatusIconEventListener;
import se.chalmers.group42.runforlife.StatusIconHandler;
import sensors.GPSInputHandler;
import sensors.GPSInputListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.graphics.Point;
import android.location.Location;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

public class MainRunFragment extends Fragment implements
		StatusIconEventListener, GPSInputListener {
	private View view;
	private Activity mainActivity;
	private Button runButton;
	private ImageView gpsIcon, headPhonesIcon;
	private boolean gpsOn, headphonesIn;

	/**
	 * The number of modes in the application.
	 */
	private static final int NUM_MODES = 2;

	private ViewPager mPager;

	private PagerAdapter mPagerAdapter;

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
		mPager.setAdapter(mPagerAdapter);

		// Setting up status icons
		gpsIcon = (ImageView) view.findViewById(R.id.gps_icon);
		headPhonesIcon = (ImageView) view.findViewById(R.id.headphones_icon);

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
									"This mode needs GPS to be turned on and headphones to be plugged in.")
							.setPositiveButton(android.R.string.yes,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											// continue to initial state
										}
									}).show();

				} else
					new ModeController(mainActivity).launchMode((int) mPager
							.getCurrentItem());
			}
		});

		// Handle previous and next pointers.
		if (mPager.getCurrentItem() == 0) {
			view.findViewById(R.id.previous_mode_image).setVisibility(
					View.INVISIBLE);
			view.findViewById(R.id.next_mode_image).setVisibility(View.VISIBLE);
		} else if (mPager.getCurrentItem() == (NUM_MODES - 1)) {
			view.findViewById(R.id.previous_mode_image).setVisibility(
					View.VISIBLE);
			view.findViewById(R.id.next_mode_image).setVisibility(
					View.INVISIBLE);
		} else {
			view.findViewById(R.id.previous_mode_image).setVisibility(
					View.VISIBLE);
			view.findViewById(R.id.next_mode_image).setVisibility(View.VISIBLE);
		}

		// Set up statusIconHandler
		IntentFilter filter = new IntentFilter(
				"android.intent.action.HEADSET_PLUG");
		StatusIconHandler receiver = new StatusIconHandler(this, mainActivity);
		mainActivity.registerReceiver(receiver, filter);
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

	// private class ModeNotLaunchableDialogFragment extends DialogFragment {
	//
	// public static ModeNotLaunchableDialogFragment newInstance(int title) {
	// MyAlertDialogFragment frag = new MyAlertDialogFragment();
	// Bundle args = new Bundle();
	// args.putInt("title", title);
	// frag.setArguments(args);
	// return frag;
	// }
	//
	// @Override
	// public Dialog onCreateDialog(Bundle savedInstanceState) {
	// // Use the Builder class for convenient dialog construction
	// AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	// builder.setMessage("For this mode to work, headphones need to be plugged in and GPS needs to be turned on")
	// .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int id) {
	// // Do nothing.
	// }
	// });
	//
	// // Create the AlertDialog object and return it
	// return builder.create();
	// }
	// }

	@Override
	public void onGPSConnect() {
		if (!gpsOn) {
			gpsOn = true;
			gpsIcon.setImageResource(R.drawable.gps_activated);
		}
	}

	@Override
	public void onGPSDisconnect() {
		gpsOn = false;
		gpsIcon.setImageResource(R.drawable.gps_disabled);
	}

	@Override
	public void onHeadphonesIn() {
		if (!headphonesIn) {
			headphonesIn = true;
			headPhonesIcon.setImageResource(R.drawable.headphones_activated);
		}
	}

	@Override
	public void onHeadphonesOut() {
		headphonesIn = false;
		headPhonesIcon.setImageResource(R.drawable.headphones_disabled);
	}

	@Override
	public void onLocationChanged(Location location) {
		// Unneeded
	}

}
