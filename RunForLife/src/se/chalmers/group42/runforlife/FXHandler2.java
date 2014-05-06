package se.chalmers.group42.runforlife;

import java.io.IOException;
import java.util.ArrayList;

import org.pielot.openal.Buffer;
import org.pielot.openal.SoundEnv;

import se.chalmers.group42.utils.LocationHelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

@SuppressLint("HandlerLeak")
public class FXHandler2 {

	/**
	 * Responsible for repeating a sound.
	 */
	private Handler handler;

	// FX representing a coin (that the user is picking up).
	private int coin, sayCoinReached, sayNewCoin, finishedTone, goodJob, runFinished;

	// FX representing the navigation sound (
	private FX navFX;

	private SoundEnv env;

	private SoundPool soundPool;

	private ArrayList<Speech> speech = new ArrayList<Speech>();

	private boolean handlerActive = false;
			//, coinPlayable = true;
	/**
	 * Initialize sound engine
	 */
	@SuppressLint("UseSparseArrays")
	public void initSound(Context context) {
		env = SoundEnv.getInstance((Activity) context);

		// Load sound into memory. Has to be mono .wav file.
		Buffer navFXFrontBuffer, navFXBehindBuffer, navFXToSourceBuffer;
		try {
			navFXFrontBuffer = env.addBuffer("nav_fx");
			navFXBehindBuffer = env.addBuffer("nav_fx_behind");
			navFXToSourceBuffer = env.addBuffer("techno2");

			// Add the audio buffer as a source in the 3D room and
			// create FX instance.
			navFX = new FX(env.addSource(navFXFrontBuffer),
					env.addSource(navFXBehindBuffer), 
					env.addSource(navFXToSourceBuffer));
		} catch (IOException e) {
			Log.e(Constants.TAG, "Could not initialize OpenAL4Android", e);
		}

		// OpenAL uses right-handed coordinate system. Place sound in front
		// of listener.
		navFX.source().setPosition(0, 0, -1);

		// Roll-off is at which distance the gain changes.
		navFX.source().setRolloffFactor(1);

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

		sayCoinReached = soundPool.load(context, R.raw.coin_reached, 1);
		sayNewCoin = soundPool.load(context, R.raw.new_coin, 1);
		goodJob = soundPool.load(context, R.raw.good_job, 1);
		finishedTone = soundPool.load(context, R.raw.finished_tone, 1);
		coin = soundPool.load(context, R.raw.coin, 1);
		runFinished = soundPool.load(context, R.raw.run_finished, 1);
	}

	public FX getNavigationFX() {
		return navFX;
	}

	public void playCoin() {
		soundPool.play(finishedTone, 1, 1, 1, 0, 1);
	}

	public void foundCoin() {
		soundPool.stop(coin); // stop play coin

		// play in new thread to not be interrupted
		(new Thread(new ReachedCoinRunnable())).start();
	}

	/**
	 * Loop a sound until it's being stopped manually.
	 * 
	 * @param fx
	 *            the sound to be looped
	 */
	public void loop(FX fx) {
		//env.setListenerOrientation(fx.angle());
		fx.play();
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
	public void update(FX fx, float angle, float distance) {		
		fx.setAngle(angle);

		//env.setListenerOrientation(fx.angle());
		float[] xy = LocationHelper.getCoordFromDistBear(distance, angle);
		env.setListenerPos(0, xy[0], xy[1]);

//		if (Math.abs(fx.angle()) < Constants.TOSOURCE_ANGLE){
//			fx.setPitch(1);
//		}
//		else if (fx.angle() < 0 && fx.angle() > -Constants.FRONT_ANGLE ){
//			fx.setPitch((1 - Constants.MIN_PITCH) / Constants.FRONT_ANGLE * fx.angle() + 1);
//			// To easier hear if the sound is on the right or left
//			float tempAngle = (float) ((90 - Math.abs(fx.angle())) * 0.85);
//			env.setListenerOrientation(fx.angle() - tempAngle);
//		}
//		else if (fx.angle() >= 0 && fx.angle() < Constants.FRONT_ANGLE){
//			fx.setPitch((1 - Constants.MIN_PITCH) / (-Constants.FRONT_ANGLE) * fx.angle() + 1);
//			// To easier hear if the sound is on the right or left
//			float tempAngle = (float) ((90 - Math.abs(fx.angle())) * 0.85);
//			env.setListenerOrientation(fx.angle() + tempAngle);
//		} 
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

	public void playRouteFinished() {
		soundPool.stop(coin); // stop play coin

		// play in new thread to not be interrupted
		(new Thread(new RunFinishedRunnable())).start();

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
	
	/**
	 * Plays announcements to user in new thread.
	 */
	private class RunFinishedRunnable implements Runnable {

		@Override
		public void run() {
			try {
				playCoin();
				Thread.sleep(2500);
				soundPool.play(runFinished, 1, 1, 1, 0, 1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // wait
		}
	}
}