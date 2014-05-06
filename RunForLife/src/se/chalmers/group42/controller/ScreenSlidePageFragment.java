package se.chalmers.group42.controller;

import se.chalmers.group42.runforlife.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.app.Fragment;

public class ScreenSlidePageFragment extends Fragment {

	/**
	 * The argument key for the page number this fragment represents.
	 */
	public static final String ARG_PAGE = "page";

	/**
	 * Images to be used for modes.
	 */
	private int[] images = { R.drawable.coin_collector, R.drawable.tutorial };

	private int[] modeIndicators = { R.drawable.mode_indicator,
			R.drawable.mode_indicator_2 };

	private ImageView modeIndicator;

	/**
	 * The name of each mode.
	 */
	private String[] imageDesc = { "COIN COLLECTOR", "TUTORIAL" };

	private String[] infoTexts = {
			"In this mode, the goal is to localize coins using the sound coming" +
			" from your headphones. Read more on how it works in the help section.",
			"The tutorial mode lets you hunt for coins in a safe environment," +
			" without having to run at all. \nTry out the sound navigation by rotating the " +
			"phone and pressing RUN to move forward." };

	/**
	 * The fragment's mode number.
	 */
	private int mModeNumber;

	private ImageView modeImage;
	private TextView infoText;

	/**
	 * True if the mode view is in its info state.
	 */
	private boolean isInInfoState = false;

	/**
	 * Factory method for this class.
	 */
	public static ScreenSlidePageFragment create(int pageNumber) {
		ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PAGE, pageNumber);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mModeNumber = getArguments().getInt(ARG_PAGE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Inflate the layout containing image and text.
		final ViewGroup rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_screen_slide_page, container, false);

		// Set mode indicator images.
		modeImage = (ImageView) rootView.findViewById(R.id.select_mode_image);
		
		// Set descriptive text in the bottom.
		((TextView) rootView.findViewById(R.id.select_mode_text))
		.setText(imageDesc[mModeNumber]);

		modeImage.setImageResource(images[mModeNumber]);

		// Text that will appear when hitting the info button.
		infoText = (TextView) rootView.findViewById(R.id.info_text);

		// When in the info view, user can click anywhere on the mode selector.
		rootView.findViewById(R.id.mode_selector).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {

						if (isInInfoState) {
							modeImage.setVisibility(View.VISIBLE);
							infoText.setVisibility(View.INVISIBLE);

							isInInfoState = false;
						}

						else {
							if (!isInInfoState) {
								modeImage.setVisibility(View.INVISIBLE);
								infoText.setText(infoTexts[mModeNumber]);
								infoText.setVisibility(View.VISIBLE);

								isInInfoState = true;
							}
						}

					}

				});

		return rootView;
	}

	/**
	 * Returns the mode number.
	 */
	public int getModeNumber() {
		return mModeNumber;
	}

	/**
	 * Initialize Coin Collector View
	 */
	public void toCollectorView() {
		
	}

	/**
	 * Initialize Tutorial View
	 */
	public void toTutorialView() {

	}

}
