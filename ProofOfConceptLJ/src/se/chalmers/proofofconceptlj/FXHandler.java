package se.chalmers.proofofconceptlj;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

public class FXHandler {
	public static final int FX_01 = 1;
	public static final int FX_02 = 2;

	public static final int LOOP = -1;
	public static final int NOT_LOADED = -42;
	public static final int MSG = 3;
	public static final int MSG_STOP = 0;

	private static final int maxAudiableDistance = 50; // Meters

	private HashMap<Integer, Integer> soundPoolMap;
	private SoundPool soundPool;
	private AudioManager am;

	private float previousAngle;

	// True if sound is loaded correctly
	private boolean loaded = false;

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
		soundPoolMap.put(FX_01, soundPool.load(context, R.raw.bip, 1));
		soundPoolMap.put(FX_02, soundPool.load(context, R.raw.dragon, 2));
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
			fx.setStreamID(soundPool.play(fx.ID(), fx.leftVolume(),
					fx.rightVolume(), 1, times, 1f));
	}

	/**
	 * Loop sound until the process is interrupted.
	 * 
	 * @param fx
	 * @return the streamID if successful, non-zero value if not.
	 */
	public void loopFX(FX fx) {
		playFX(fx, LOOP);
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
	 * @throws InterruptedException
	 */
	public void setPosition(FX fx, float angle, float distance) {

		float distFactor = 1 - distance / maxAudiableDistance;

		if (distFactor <= 0.2)
			distFactor = 0.2f;

		// Have to add 90 degrees so that (angle = 0) is heard in front.
		int correctValue = 90;
	

		// The angle after being corrected.
		float dangle = angle + correctValue;
		
		//Correct for angles > 360 degrees
		dangle %= 360;

	/*	if (angle >= 140 && angle <= 220)
			dangle = previousAngle;

		else {
			// Is sound coming from behind the player to the right?
			if (angle > 90 && angle <= 180)
				dangle = 180;

			// Is sound coming from behind the player to the left?
			if (angle > 180 && angle <= 270)
				dangle = 0;

			// From left to middle of listening scope.
			if (angle > 270)
				dangle = angle - 270;
		}
*/
		double radian = dangle * (Math.PI / 180); // Convert to radians

		// Set volume on sound
		fx.setVolume((float) Math.cos(radian / 2), (float) Math.sin(radian / 2));

		soundPool.setVolume(
				fx.streamID(), 
				fx.leftVolume() * distFactor,
				fx.rightVolume() * distFactor
		);
		
		//Set the rate based on the distance
		float rate = (float) (2 - distance * (1.5 / maxAudiableDistance));
		soundPool.setRate(fx.streamID(), rate);

	//	previousAngle = dangle;
	}

	/**
	 * Sweeping sound from left to right
	 */
	public void sweepFX(FX fx) throws InterruptedException {
		loopFX(fx);

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

}