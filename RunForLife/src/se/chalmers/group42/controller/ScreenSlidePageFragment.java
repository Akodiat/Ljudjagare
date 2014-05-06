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

		// Inflate the layout containing image and text
		final ViewGroup rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_screen_slide_page, container, false);

		final ImageView modeImage = (ImageView) rootView
				.findViewById(R.id.select_mode_image);
		modeImage.setImageResource(images[mModeNumber]);

		((TextView) rootView.findViewById(R.id.select_mode_text))
				.setText(imageDesc[mModeNumber]);

		// Text that will appear when hitting the info button.
		final TextView infoText = (TextView) rootView
				.findViewById(R.id.info_text);

		final ImageView arrowRight = (ImageView) rootView
				.findViewById(R.id.next_mode_image);
		final ImageView arrowLeft = (ImageView) rootView
				.findViewById(R.id.previous_mode_image);

		// Set arrows to guide user.
		if (mModeNumber == 0) {
			arrowRight.setVisibility(View.VISIBLE);
			arrowLeft.setVisibility(View.INVISIBLE);
		} else {
			arrowRight.setVisibility(View.INVISIBLE);
			arrowLeft.setVisibility(View.VISIBLE);
		}

		// When in the info view, user can click anywhere on the mode selector.
		rootView.findViewById(R.id.mode_selector).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						if (isInInfoState) {
							modeImage.setVisibility(View.VISIBLE);
							infoText.setVisibility(View.INVISIBLE);

							// Set arrows to guide user.
							if (mModeNumber == 0) {
								arrowRight.setVisibility(View.VISIBLE);
								arrowLeft.setVisibility(View.INVISIBLE);
							} else {
								arrowRight.setVisibility(View.INVISIBLE);
								arrowLeft.setVisibility(View.VISIBLE);
							}

							isInInfoState = false;
						}

						else {
							if (!isInInfoState) {
								modeImage.setVisibility(View.INVISIBLE);
								infoText.setText(infoTexts[mModeNumber]);
								infoText.setVisibility(View.VISIBLE);

								// Hide arrows
								arrowRight.setVisibility(View.INVISIBLE);
								arrowLeft.setVisibility(View.INVISIBLE);

								isInInfoState = true;
							}
						}
					}

				});

		rootView.findViewById(R.id.mode_selector).setOnTouchListener(
				new OnTouchListener() {
					@Override
					public boolean onTouch(View view, MotionEvent event) {
						if (event.getAction() == MotionEvent.ACTION_DOWN
								&& event.getAction() == MotionEvent.ACTION_MOVE) {
							arrowRight.setVisibility(View.INVISIBLE);
							arrowLeft.setVisibility(View.INVISIBLE);

							return true;
						}

						else {
							// Set arrows to guide user.
							if (mModeNumber == 0) {
								arrowRight.setVisibility(View.VISIBLE);
								arrowLeft.setVisibility(View.INVISIBLE);
							} else {
								arrowRight.setVisibility(View.INVISIBLE);
								arrowLeft.setVisibility(View.VISIBLE);
							}
						}

						return false;
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

}
