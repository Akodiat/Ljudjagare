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
	private FX cowbell;

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
				soundPool.load(context, R.raw.sine, 1));

		soundPoolMap.put(Constants.FX_02,
				soundPool.load(context, R.raw.dragon, 1));

		// Initialize audio
		cowbell = new FX(Constants.FX_01);
		coin = new FX(Constants.FX_02);

		// Initialize thread handler
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == Constants.MSG)
					loop(cowbell);

				if (msg.what == Constants.MSG_STOP)
					handler.removeCallbacksAndMessages(null);
			}
		};
	}

	public FX getCowbell() {
		return cowbell;
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
	 * Play sound at specific angle and distance from user.
	 * 
	 * @param soundID
	 *            sound to process
	 * @param angle
	 *            angle between current direction and source (0-360).
	 * @param distance
	 *            distance from audio source in meters.
	 */
	public void loop(FX fx) {

		// Have to add 90 degrees so that (angle = 0) is heard in front.
		int correctValue = 90;

		// The angle after being corrected.
		float dangle = fx.angle() + correctValue;

		if (fx.angle() >= 140 && fx.angle() <= 220)
			dangle = 90;

		else {
			// Is sound coming from behind the player to the right?
			if (fx.angle() > 90 && fx.angle() <= 180)
				dangle = 180;

			// Is sound coming from behind the player to the left?
			if (fx.angle() > 180 && fx.angle() <= 270)
				dangle = 0;

			// From left to middle of listening scope.
			if (fx.angle() > 270)
				dangle = fx.angle() - 270;
		}

		double radian = dangle * (Math.PI / 180); // Convert to radians

		// Set volume on sound
		fx.setVolume((float) Math.cos(radian / 2), (float) Math.sin(radian / 2));

		fx.setStreamID(soundPool.play(fx.sound(), fx.leftVolume(),
				fx.rightVolume(), 0, 1, fx.pitch()));

		int maxDelay = 1000;
		int minDelay = 200;

		float delayRatio;

		// Calculate value between 0 and 1, where 0 is when a user has reached
		// destination:
		if (fx.distance() <= Constants.MAX_DISTANCE)
			delayRatio = fx.distance() / Constants.MAX_DISTANCE;
		else
			delayRatio = 1;

		// Delay between each repetition.
		float delay = (maxDelay - minDelay) * delayRatio + minDelay;

		// Send message to handler
		Message msg = handler.obtainMessage(Constants.MSG);
		handler.sendMessageDelayed(msg, (long) delay);
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

	public void stopHandler() {
		Message msg = handler.obtainMessage(Constants.MSG_STOP);
		handler.sendMessage(msg);

		// Set streamID to not playing
		getCowbell().setStreamID(FX.NOT_PLAYING);
	}

	public void update(FX fx, float angle, float distance) {
		fx.setAngle(angle);

		float rate = 0.6f; // value between 0.5 and 1.
		float k, m;
		if (fx.angle() >= 0 && fx.angle() <= 180) {
			k = (1 - Constants.MIN_PITCH) / (-180);
			m = 1 - k * 0;
			fx.setPitch(k * fx.angle() + m);
		} else {
			k = (1 - Constants.MIN_PITCH) / 180;
			m = 1 - k * 360;
			fx.setPitch(k * fx.angle() + m);
		}

		fx.setDistance(distance);
	}
}