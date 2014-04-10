package se.chalmers.group42.controller;

import se.chalmers.group42.runforlife.ModeController;
import se.chalmers.group42.runforlife.R;
import se.chalmers.group42.runforlife.StatusIconEventListener;
import se.chalmers.group42.runforlife.StatusIconHandler;
import sensors.GPSInputHandler;
import sensors.GPSInputListener;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.location.Location;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import at.technikum.mti.fancycoverflow.FancyCoverFlow;
import at.technikum.mti.fancycoverflow.FancyCoverFlowSampleAdapter;

public class MainRunFragment extends Fragment implements
		StatusIconEventListener, GPSInputListener {
	private View view;
	private Activity mainActivity;
	private FancyCoverFlow fancyCoverFlow;
	private Button runButton;
	private Intent runActivityIntent;
	private int coverFlowHeight;
	private ImageView gpsIcon, soundIcon, headPhonesIcon;
	private TextView gpsText, soundText, headPhonesText;
	private boolean gpsOn, soundOn, headphonesIn;
	private FrameLayout fancyCoverFlowContainer;

	private final int ACTION_BAR_HEIGHT_MDPI = 32;

	@Override
	public void onAttach(final Activity activity) {
		super.onAttach(activity);

		mainActivity = (MainActivity) activity;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_main_run, null);

		// Setting up status icons
		gpsIcon = (ImageView) view.findViewById(R.id.imageViewGPS);
		soundIcon = (ImageView) view.findViewById(R.id.imageViewSound);
		headPhonesIcon = (ImageView) view
				.findViewById(R.id.imageViewHeadphones);

		// Setting up status text
		gpsText = (TextView) view.findViewById(R.id.textViewGPS);
		gpsText.setText("Searching for gps...");
		soundText = (TextView) view.findViewById(R.id.textViewSound);
		headPhonesText = (TextView) view.findViewById(R.id.textViewHeadphones);

		// Setting up Sensor input
		new GPSInputHandler(this, mainActivity);

		/*
		 * The screen size and density of the device running the program is
		 * retrieved to draw the components to good proportions.
		 * 
		 * http://stackoverflow.com/questions/1016896/how-to-get-screen-dimensions
		 */
		// Getting the display size
		Display display = mainActivity.getWindowManager().getDefaultDisplay();
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
		// fancyCoverFlow = (FancyCoverFlow)
		// view.findViewById(R.id.fancyCoverFlow);
		fancyCoverFlowContainer = (FrameLayout) view
				.findViewById(R.id.fancyCoverFlowContainer);
		fancyCoverFlow = new FancyCoverFlow(mainActivity);
		fancyCoverFlow.setAdapter(new FancyCoverFlowSampleAdapter(
				coverFlowHeight));
		fancyCoverFlow.setUnselectedAlpha(1.0f);
		fancyCoverFlow.setUnselectedSaturation(0.0f);
		fancyCoverFlow.setUnselectedScale(0.5f);
		fancyCoverFlow.setSpacing(0);
		fancyCoverFlow.setMaxRotation(0);
		fancyCoverFlow.setScaleDownGravity(0.2f);
		fancyCoverFlow.setActionDistance(FancyCoverFlow.ACTION_DISTANCE_AUTO);
		fancyCoverFlowContainer.addView(fancyCoverFlow);

		/*
		 * Setting up the run-button
		 */
		runButton = (Button) view.findViewById(R.id.run_button);
		// runActivityIntent = new Intent(this, RunActivity.class);
		runButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// startActivity(runActivityIntent);
				System.out.println();
				new ModeController(mainActivity)
						.launchMode((int) fancyCoverFlow.getSelectedItemId());
			}
		});

		// Setting up statusIconHandler
		IntentFilter filter = new IntentFilter(
				"android.intent.action.HEADSET_PLUG");
		StatusIconHandler receiver = new StatusIconHandler(this, mainActivity);
		mainActivity.registerReceiver(receiver, filter);
		return view;
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
		// We don't need to do stuff here really...
	}

}
