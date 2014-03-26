package se.chalmers.group42.runforlife;

import java.io.IOException;

import org.pielot.openal.Buffer;
import org.pielot.openal.SoundEnv;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
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
	private int coin, sayCoinReached, sayNewCoin, finishedTone, goodJob;

	// FX representing the navigation sound (
	private FX navFX;

	private SoundEnv env;

	private SoundPool soundPool;

	private Speech say100, say200, say300, say400, say500, say600, say700,
			say800, say900, say1000;

	private boolean handlerActive = false, coinPlayable = true;

	/**
	 * Initialize sound engine
	 */
	@SuppressLint("UseSparseArrays")
	public void initSound(Context context) {
		env = SoundEnv.getInstance((Activity) context);

		// Load sound into memory. Has to be mono .wav file.
		Buffer navFXFrontBuffer, navFXBehindBuffer;
		try {
			navFXFrontBuffer = env.addBuffer("nav_fx");
			navFXBehindBuffer = env.addBuffer("nav_fx_behind");

			// Add the audio buffer as a source in the 3D room and
			// create FX instance.
			navFX = new FX(env.addSource(navFXFrontBuffer),
					env.addSource(navFXBehindBuffer));
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
		if (!handlerActive) {
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
			handlerActive = true;
		}
	}

	public void initSoundPool(Context context) {
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

		// initialize audio samples
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

		coin = soundPool.load(context, R.raw.dragon, 1);
		sayCoinReached = soundPool.load(context, R.raw.coin_reached, 1);
		sayNewCoin = soundPool.load(context, R.raw.new_coin, 1);
		goodJob = soundPool.load(context, R.raw.good_job, 1);
		finishedTone = soundPool.load(context, R.raw.finished_tone, 1);
		coin = soundPool.load(context, R.raw.coin, 1);
	}

	public FX getNavigationFX() {
		return navFX;
	}

	public void playCoin() {
		soundPool.play(finishedTone, 1, 1, 1, 0, 1);
	}

	public void foundCoin() {
		soundPool.stop(coin); // stop play coin

		// play audio in a new thread to make it possible to change screen
		(new Thread(new ReachedCoinRunnable())).start();
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
		
		// change delayinterval to delayintervaleach100 if preferred 
		handler.sendMessageDelayed(msg, (long) delayIntervalEach100(fx));
	}

	/**
	 * Stop the current loop and set stream to not playing.
	 */
	public void stopLoop() {
		navFX.stop();

		Message msg = handler.obtainMessage(Constants.MSG_STOP);
		handler.sendMessage(msg);

		handlerActive = false;
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

	public float delayIntervalEach100(FX fx) {
		float delayRatio, newDist = fx.distance() % 100;

		// Calculate value between 0 and 1, where 0 is when a user has reached
		// destination:
		if (newDist < 100)
			delayRatio = newDist / 100;
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
		if (angle >= 90 && angle <= 150)
			fx.setAngle(angle);

		if (fx.angle() < 0 && fx.angle() > -90)
			fx.setPitch((1 - Constants.MIN_PITCH) / 90 * fx.angle() + 1);
		else if (fx.angle() >= 0 && fx.angle() < 90)
			fx.setPitch((1 - Constants.MIN_PITCH) / (-90) * fx.angle() + 1);

		fx.setDistance(distance);

		// if distance is below 100, introduce coin
		if (distance < Constants.APPROACHING_COIN
				&& distance > Constants.MIN_DISTANCE)
			// loopCoin(distance);

			// tell the user how close to goal he/she is
			distanceAnnouncer(distance);
	}

	public void loopCoin(float distance) {
		if (coinPlayable) {
			soundPool.play(coin, 0, 0, 1, Constants.LOOP, 1);
			coinPlayable = false;
		}

		soundPool.setVolume(coin, (1 / (-Constants.MIN_DISTANCE)) * distance
				+ 2, (1 / (-Constants.MIN_DISTANCE)) * distance + 2);

		// decrease volume on the navigate sound until coin is reached.
		navFX.setVolume((1 / Constants.MIN_DISTANCE) * distance - 1);
	}

	public void sayDistance(Speech speech) {
		if (speech.isPlayable()) {
			soundPool.play(speech.id(), 1, 1, 1, 0, 1);
			speech.setPlayed();

			if (Speech.previous() != null)
				Speech.previous().setPlayable();

			Speech.setPrevious(speech);
		}
	}

	public void sayCoinReached() {
		soundPool.play(sayCoinReached, 1, 1, 1, 0, 1);
	}

	public void sayNewCoin() {
		soundPool.play(sayNewCoin, 1, 1, 1, 0, 1);
	}

	public void distanceAnnouncer(float distance) {
		int rConst = 5; // meters from coin destination

		if (distance < 1000 + rConst && distance > 1000 - rConst)
			sayDistance(say1000);
		if (distance < 900 + rConst && distance > 900 - rConst)
			sayDistance(say900);
		if (distance < 800 + rConst && distance > 800 - rConst)
			sayDistance(say800);
		if (distance < 700 + rConst && distance > 700 - rConst)
			sayDistance(say700);
		if (distance < 600 + rConst && distance > 600 - rConst)
			sayDistance(say600);
		if (distance < 500 + rConst && distance > 500 - rConst)
			sayDistance(say500);
		if (distance < 400 + rConst && distance > 400 - rConst)
			sayDistance(say400);
		if (distance < 300 + rConst && distance > 300 - rConst)
			sayDistance(say300);
		if (distance < 200 + rConst && distance > 200 - rConst)
			sayDistance(say200);
		if (distance < 100 + rConst && distance > 100 - rConst)
			sayDistance(say100);
	}

	/**
	 * Plays announcements to user in new thread.
	 */
	private class ReachedCoinRunnable implements Runnable {

		@Override
		public void run() {
			try {
				playCoin();
				Thread.sleep(2500);
				soundPool.play(goodJob, 1, 1, 1, 0, 1);
				Thread.sleep(2000);
				soundPool.play(sayNewCoin, 1, 1, 1, 0, 1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // wait
		}
	}
}