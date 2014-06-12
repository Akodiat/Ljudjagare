package se.chalmers.group42.runforlife;

import java.io.IOException;
import java.util.ArrayList;

import org.pielot.openal.Buffer;
import org.pielot.openal.SoundEnv;
import org.pielot.openal.Source;

import se.chalmers.group42.utils.LocationHelper;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.util.Log;

@SuppressLint("HandlerLeak")
public class FXHandler2 {

	/**
	 * Responsible for repeating a sound.
	 */
	private Handler handler;


	// FX representing the navigation sound (
	private Source drone;

	private SoundEnv env;

	private SoundPool soundPool;

	private ArrayList<Speech> speech = new ArrayList<Speech>();


	/**
	 * Initialize sound engine
	 */
	@SuppressLint("UseSparseArrays")
	public void initSound(Context context) {
		env = SoundEnv.getInstance((Activity) context);

		// Load sound into memory. Has to be mono .wav file.
		Buffer  navFXToSourceBuffer;
		try {
			navFXToSourceBuffer = env.addBuffer("techno2");

			// Add the audio buffer as a source in the 3D room and
			drone = env.addSource(navFXToSourceBuffer);

		} catch (IOException e) {
			Log.e(Constants.TAG, "Could not initialize OpenAL4Android", e);
		}

		// OpenAL uses right-handed coordinate system. Place sound in front
		// of listener.
		drone.setPosition(0, 0, -1);

		// Roll-off is at which distance the gain changes.
		drone.setRolloffFactor(1);

		// Set listener orientation.
		env.setListenerOrientation(0);

		initSoundPool(context);
	}

	public void setSoundSourcePosition(float x, float y, float z){

		//Coordinate system looks different in openAL.
		//one meter above listener
		drone.setPosition(z, x, y);
	}

	public void setListenerOrientation(float orientation){
		env.setListenerOrientation(orientation);
	}

	public Speech getSpeech(int i){
		return speech.get(i);
	}

	public void initSoundPool(Context context) {
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

		// initialize audio samples
		speech.add(new Speech(soundPool.load(context, R.raw.say100, 1)));
		speech.add(new Speech(soundPool.load(context, R.raw.say200, 1)));
		speech.add(new Speech(soundPool.load(context, R.raw.say300, 1)));
		speech.add(new Speech(soundPool.load(context, R.raw.say400, 1)));
		speech.add(new Speech(soundPool.load(context, R.raw.say500, 1)));
		speech.add(new Speech(soundPool.load(context, R.raw.say600, 1)));
		speech.add(new Speech(soundPool.load(context, R.raw.say700, 1)));
		speech.add(new Speech(soundPool.load(context, R.raw.say800, 1)));
		speech.add(new Speech(soundPool.load(context, R.raw.say900, 1)));
		speech.add(new Speech(soundPool.load(context, R.raw.say1000, 1)));
	}

	/**
	 * Loop a sound until it's being stopped manually.
	 * 
	 * @param fx
	 *            the sound to be looped
	 */
	public void startLoop() {
		drone.play(false);
	}

	/**
	 * Stop the current loop.
	 */
	public void stopLoop() {
		drone.stop();
	}

	public Handler getHandler() {
		return handler;
	}

	/**
	 * To be called each time the position of the user is being updated.
	 * 
	 * @param fx
	 *            the sound to be updated
	 * @param angle
	 *            the new angle
	 */

	public void sayDistance(Speech speech) {
		if (speech.isPlayable()) {
			soundPool.play(speech.id(), 1, 1, 1, 0, 1);
			speech.setPlayed();

			if (Speech.previous() != null)
				Speech.previous().setPlayable();

			Speech.setPrevious(speech);
		}
	}


}