package se.chalmers.proofofconceptlj;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Handler;
import android.os.Message;

@SuppressLint("HandlerLeak")
public class FXHandler {

	private HashMap<Integer, Integer> soundPoolMap;
	private SoundPool soundPool;
	private AudioManager am;

	// True if sound is loaded correctly
	private boolean loaded = false;

	private Handler handler;

	// FX representing a coin (that the user is picking up).
	private FX coin;

	// FX representing the navigation sound (
	private FX navigationFX;

	private SoundPool speechPool;

	private Speech say100, say200, say300, say400, say500, say600, say700,
			say800, say900, say1000;

	/**
	 * Initialize sound engine
	 */
	@SuppressLint("UseSparseArrays")
	public void initSound(Context context) {
		am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
		soundPoolMap = new HashMap<Integer, Integer>();
		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId,
					int status) {
				loaded = true;
			}
		});

		// Load FX
		soundPoolMap.put(Constants.FX_01,
				soundPool.load(context, R.raw.navigate_fx, 1));

		soundPoolMap.put(Constants.FX_02,
				soundPool.load(context, R.raw.dragon, 1));

		// Initialize audio
		navigationFX = new FX(Constants.FX_01);
		coin = new FX(Constants.FX_02);

		initSpeech(context);

		// Initialize thread handler
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == Constants.MSG)
					loop(navigationFX);

				if (msg.what == Constants.MSG_STOP)
					handler.removeCallbacksAndMessages(null);
			}
		};
	}

	public void initSpeech(Context context) {
		speechPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);

		// Load speech voices.
		say100 = new Speech(speechPool.load(context, R.raw.speech100, 1));
		say200 = new Speech(speechPool.load(context, R.raw.speech200, 1));
		say300 = new Speech(speechPool.load(context, R.raw.speech300, 1));
		say400 = new Speech(speechPool.load(context, R.raw.speech400, 1));
		say500 = new Speech(speechPool.load(context, R.raw.speech500, 1));
		say600 = new Speech(speechPool.load(context, R.raw.speech600, 1));
		say700 = new Speech(speechPool.load(context, R.raw.speech700, 1));
		say800 = new Speech(speechPool.load(context, R.raw.speech800, 1));
		say900 = new Speech(speechPool.load(context, R.raw.speech900, 1));
		say1000 = new Speech(speechPool.load(context, R.raw.speech1000, 1));
	}

	public FX getNavigationFX() {
		return navigationFX;
	}

	public FX getCoin() {
		return coin;
	}

	/**
	 * Play a sound a specific amount of times.
	 * 
	 * @param fx
	 * @param times
	 * @return the streamID if successful, non-zero value if not.
	 */
	public void playFX(FX fx, int times) {
		if (loaded)
			fx.setStreamID(soundPool.play(fx.sound(), fx.leftVolume(),
					fx.rightVolume(), 1, times, 1f));
	}

	public void stopFX(FX fx) {
		if (loaded) {
			soundPool.stop(fx.streamID());
			fx.setStreamID(FX.NOT_PLAYING); // reset streamID
		}
	}

	/**
	 * Mode to be used with radio orienteering.
	 */
	public void levelOnRotate(FX fx) {
		float max = 1, min = 0;
		float vol = (min - max) * Math.abs(fx.angle()) / 180;

		fx.setVolume(vol, vol);

		// Send message to handler
		Message msg = handler.obtainMessage(Constants.MSG);
		handler.sendMessageDelayed(msg, (long) 1000);
		// TODO CHANGE 1000 to variable (delay)
	}

	/**
	 * Loop a sound until it's being stopped manually.
	 * 
	 * @param fx
	 *            the sound to be looped
	 */
	public void loop(FX fx) {

		float angle = fx.angle();

		// If sound is coming from behind the user, set to 0.
		if (Math.abs(angle) > 180 - Constants.BEHIND_ANGLE / 2)
			angle = 0;

		angle = (angle > 90) ? 90 : angle;
		angle = (angle < (-90)) ? (-90) : angle;

		double radian = angle * (Math.PI / 360); // Convert to radians

		fx.setVolume((float) Math.cos(radian + Math.PI / 4),
				(float) Math.sin(radian + Math.PI / 4));

		// Play sound at given coordinate
		fx.setStreamID(soundPool.play(fx.sound(), fx.leftVolume(),
				fx.rightVolume(), 0, 1, fx.pitch()));

		// Send message to handler with delay.
		Message msg = handler.obtainMessage(Constants.MSG);
		handler.sendMessageDelayed(msg, (long) delayInterval(fx));
	}

	/**
	 * Stop the current loop and set stream to not playing.
	 */
	public void stopLoop() {
		Message msg = handler.obtainMessage(Constants.MSG_STOP);
		handler.sendMessage(msg);

		// Set streamID to not playing
		getNavigationFX().setStreamID(FX.NOT_PLAYING);
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

	/**
	 * Sound source need to be within a specific range to be 'within reach'.
	 * 
	 * @return true if the current angle to location is okay
	 */
	public boolean isWithinRange(FX fx) {
		return Math.abs(fx.angle()) < Constants.ACCURACY;
	}

	/**
	 * Sweeping sound from left to right
	 */
	public void sweepFX(FX fx) throws InterruptedException {
		playFX(fx, Constants.LOOP);

		for (int count = 0; count < 101; count++) {
			Thread.sleep(10);
			double radians = (Math.PI / 2) * count / 100;

			fx.setVolume((float) Math.cos(radians), (float) Math.sin(radians));
		}

		stopFX(fx);
	}

	/**
	 * Get max value of device
	 */
	public int maxVolume() {
		return am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	}

	public Handler getHandler() {
		return handler;
	}

	public void sayString(String whatToSay) {

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

		speech(distance);
	}

	public void sayDistance(Speech speech) {
		if (!speech.hasBeenPlayed()) {
			speechPool.play(speech.ID(), 1, 1, 1, 0, 1);
			speech.setPlayed();
			if (Speech.previous() != null)
				Speech.previous().setPlayable(); // make previous playable again.
			
			// set current speech as previous.
			Speech.setPrevious(speech);
		}
	}

	public void speech(float distance) {
		int sWidth = 10;
		if (distance < 1000 + sWidth && distance > 1000 - sWidth)
			sayDistance(say1000);
		if (distance < 900 + sWidth && distance > 900 - sWidth)
			sayDistance(say900);
		if (distance < 800 + sWidth && distance > 800 - sWidth)
			sayDistance(say800);
		if (distance < 700 + sWidth && distance > 700 - sWidth)
			sayDistance(say700);
		if (distance < 600 + sWidth && distance > 600 - sWidth)
			sayDistance(say600);
		if (distance < 500 + sWidth && distance > 500 - sWidth)
			sayDistance(say500);
		if (distance < 400 + sWidth && distance > 400 - sWidth)
			sayDistance(say400);
		if (distance < 300 + sWidth && distance > 300 - sWidth)
			sayDistance(say300);
		if (distance < 200 + sWidth && distance > 200 - sWidth)
			sayDistance(say200);
		if (distance < 100 + sWidth && distance > 100 - sWidth)
			sayDistance(say100);
	}
}