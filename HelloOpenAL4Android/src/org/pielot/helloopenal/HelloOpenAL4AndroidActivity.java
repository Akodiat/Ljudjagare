package org.pielot.helloopenal;

import org.pielot.openal.Buffer;
import org.pielot.openal.SoundEnv;
import org.pielot.openal.Source;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * This tutorial shows how to use the OpenAL4Android library. It creates a small
 * scene with two lakes (water) and one park (bird chanting).
 * @author Martin Pielot
 */
public class HelloOpenAL4AndroidActivity extends Activity {

	private final static String	TAG	= "HelloOpenAL4Android";

	private SoundEnv			env;

	private Source				lake1;
	private Source				lake2;
	private Source				park1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate()");

		this.setContentView(R.layout.main);

		try {
			/* First we obtain the instance of the sound environment. */
			this.env = SoundEnv.getInstance(this);

			/*
			 * Now we load the sounds into the memory that we want to play
			 * later. Each sound has to be buffered once only. To add new sound
			 * copy them into the assets folder of the Android project.
			 * Currently only mono .wav files are supported.
			 */
			Buffer lake = env.addBuffer("lake");
			Buffer park = env.addBuffer("park");

			/*
			 * To actually play a sound and place it somewhere in the sound
			 * environment, we have to create sources. Each source has its own
			 * parameters, such as 3D position or pitch. Several sources can
			 * share a single buffer.
			 */
			this.lake1 = env.addSource(lake);
			this.lake2 = env.addSource(lake);
			this.park1 = env.addSource(park);

			// Now we spread the sounds throughout the sound room.
			this.lake1.setPosition(0, 0, -10);
			this.lake2.setPosition(-6, 0, 4);
			this.park1.setPosition(6, 0, -12);

			// and change the pitch of the second lake.
			this.lake2.setPitch(1.1f);

			/*
			 * These sounds are perceived from the perspective of a virtual
			 * listener. Initially the position of this listener is 0,0,0. The
			 * position and the orientation of the virtual listener can be
			 * adjusted via the SoundEnv class.
			 */
			this.env.setListenerOrientation(20);
		} catch (Exception e) {
			Log.e(TAG, "could not initialise OpenAL4Android", e);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i(TAG, "onResume()");

		/*
		 * Start playing all sources. 'true' as parameter specifies that the
		 * sounds shall be played as a loop.
		 */
		this.lake1.play(true);
		this.lake2.play(true);
		this.park1.play(true);
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.i(TAG, "onPause()");

		// Stop all sounds
		this.lake1.stop();
		this.lake2.stop();
		this.park1.stop();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy()");

		// Be nice with the system and release all resources
		this.env.stopAllSources();
		this.env.release();
	}

	@Override
	public void onLowMemory() {
		this.env.onLowMemory();
	}
}
