/*
 * Copyright 2012 The Android Open Source Project
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

import se.chalmers.group42.runforlife.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
			"In this mode, the goal is to localize coins using the sound coming from your headphones."
					+ "Read more on how it works in the help section.",
			"The tutorial mode is for you to get familiar with the application and how to recognize its audio." };

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

		rootView.findViewById(R.id.info_button).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						if (!isInInfoState) {
							modeImage.setVisibility(View.INVISIBLE);
							infoText.setText(infoTexts[mModeNumber]);
							infoText.setVisibility(View.VISIBLE);

							isInInfoState = true;
						}
					}
				});

		// When in the info view, user can click anywhere on the mode selector.
		rootView.findViewById(R.id.mode_selector).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						if (isInInfoState) {
							modeImage.setVisibility(View.VISIBLE);
							infoText.setVisibility(View.INVISIBLE);

							isInInfoState = false;
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

}
