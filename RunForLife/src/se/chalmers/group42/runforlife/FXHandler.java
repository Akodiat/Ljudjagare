package se.chalmers.group42.runforlife;

import java.io.IOException;
import java.util.HashMap;

import org.pielot.openal.Buffer;
import org.pielot.openal.SoundEnv;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

@SuppressLint("HandlerLeak")
public class FXHandler {

	/**
	 * Responsible for repeating a sound.
	 */
	private Handler handler;

	// FX representing a coin (that the user is picking up).
	private int coin;

	// FX representing the navigation sound (
	private FX navFX;

	private SoundEnv env;

	private SoundPool soundPool;

	private Speech say100, say200, say300, say400, say500, say600, say700,
			say800, say900, say1000;

	/**
	 * Initialize sound engine
	 */
	@SuppressLint("UseSparseArrays")
	public void initSound(Context context) {
		env = SoundEnv.getInstance((Activity) context);

		// Load sound into memory. Has to be mono .wav file.
		Buffer navFXBuffer;
		try {
			navFXBuffer = env.addBuffer("nav_fx");

			// Add the audio buffer as a source in the 3D room and
			// create FX instance.
			navFX = new FX(env.addSource(navFXBuffer));
		} catch (IOException e) {
			Log.e(Constants.TAG, "Could not initialize OpenAL4Android", e);
		}

		// OpenAL uses right-handed coordinate system. Place sound in front
		// of listener.
		navFX.source().setPosition(0, 0, -1);

		// Our application doesn't need a roll-off value (volume's not
		// changing).
		// Roll-off is at which distance the gain changes.
		navFX.source().setRolloffFactor(0);

		// Set listener orientation.
		env.setListenerOrientation(0);

		initSoundPool(context);

		// Initialize thread handler
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == Constants.MSG)
					loop(navFX);

				if (msg.what == Constants.MSG_STOP)
					// Stop looping
					handler.removeCallbacksAndMessages(null);
			}
		};
	}

	public void initSoundPool(Context context) {
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

		// initialize speech samples
		say100 = new Speech(soundPool.load(context, R.raw.say100, 1));
		say200 = new Speech(soundPool.load(context, R.raw.say200, 1));
		say300 = new Speech(soundPool.load(context, R.raw.say300, 1));
		say400 = new Speech(soundPool.load(context, R.raw.say400, 1));
		say500 = new Speech(soundPool.load(context, R.raw.say500, 1));
		say600 = new Speech(soundPool.load(context, R.raw.say600, 1));
		say700 = new Speech(soundPool.load(context, R.raw.say700, 1));
		say800 = new Speech(soundPool.load(context, R.raw.say800, 1));
		say900 = new Speech(soundPool.load(context, R.raw.say900, 1));
		say1000 = new Speech(soundPool.load(context, R.raw.say1000, 1));

		// init coin sample
		coin = soundPool.load(context, R.raw.dragon, 1);
	}

	public FX getNavigationFX() {
		return navFX;
	}

	public void playCoin() {
		soundPool.play(coin, 1, 1, 1, 0, 1);
	}

	/**
	 * Loop a sound until it's being stopped manually.
	 * 
	 * @param fx
	 *            the sound to be looped
	 */
	public void loop(FX fx) {
		env.setListenerOrientation(fx.angle());
		fx.play();

		// Send message to handler with delay.
		Message msg = handler.obtainMessage(Constants.MSG);
		handler.sendMessageDelayed(msg, (long) delayInterval(fx));
	}

	/**
	 * Stop the current loop and set stream to not playing.
	 */
	public void stopLoop() {
		navFX.stop();
		
		Message msg = handler.obtainMessage(Constants.MSG_STOP);
		handler.sendMessage(msg);
	}

	/**
	 * Calculate the delay between each repetition on loop.
	 * 
	 * @param fx
	 *            the sound to update
	 * @return the delay time in milliseconds
	 */
	public float delayInterval(FX fx) {
		float delayRatio;

		// Calculate value between 0 and 1, where 0 is when a user has reached
		// destination:
		if (fx.distance() <= Constants.MAX_DISTANCE)
			delayRatio = fx.distance() / Constants.MAX_DISTANCE;
		else
			delayRatio = 1;

		// Delay between each repetition.
		return (Constants.MAX_DELAY - Constants.MIN_DELAY) * delayRatio
				+ Constants.MIN_DELAY;
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
	 * @param distance
	 *            the current distance from goal
	 */
	public void update(FX fx, float angle, float distance) {
		fx.setAngle(angle);

		if (fx.angle() < 0)
			fx.setPitch((1 - Constants.MIN_PITCH) / 180 * fx.angle() + 1);
		else
			fx.setPitch((1 - Constants.MIN_PITCH) / (-180) * fx.angle() + 1);

		fx.setDistance(distance);

		// tell the user how close to goal he/she is
		sayDistance(distance);
	}

	public void sayNow(Speech speech) {
		if (speech.isPlayable()) {
			soundPool.play(speech.id(), 1, 1, 1, 0, 1);
			speech.setPlayed();

			if (Speech.previous() != null)
				Speech.previous().setPlayable();

			Speech.setPrevious(speech);
		}
	}

	public void sayDistance(float distance) {
		int rConst = 10; // meters from coin destination

		if (distance < 1000 + rConst && distance > 1000 - rConst)
			sayNow(say1000);
		if (distance < 900 + rConst && distance > 900 - rConst)
			sayNow(say900);
		if (distance < 800 + rConst && distance > 800 - rConst)
			sayNow(say800);
		if (distance < 700 + rConst && distance > 700 - rConst)
			sayNow(say700);
		if (distance < 600 + rConst && distance > 600 - rConst)
			sayNow(say600);
		if (distance < 500 + rConst && distance > 500 - rConst)
			sayNow(say500);
		if (distance < 400 + rConst && distance > 400 - rConst)
			sayNow(say400);
		if (distance < 300 + rConst && distance > 30 - rConst)
			sayNow(say300);
		if (distance < 200 + rConst && distance > 200 - rConst)
			sayNow(say200);
		if (distance < 100 + rConst && distance > 100 - rConst)
			sayNow(say100);
	}
}