package se.chalmers.proofofconceptlj;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

public class FXHandler {
	public static final int FX_01 = 1;
	public static final int LOOP = -1;
	public static final int NOT_LOADED = -42;

	private static final int maxAudiableDistance = 50; //Meters

	private HashMap<Integer, Integer> soundPoolMap;
	private SoundPool soundPool;
	private AudioManager am;

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
		soundPoolMap.put(FX_01, soundPool.load(context, R.raw.sound, 1));
	}

	/**
	 * Play a sound one without any panning.
	 * 
	 * @param soundID
	 * @param times
	 * @return the streamID if successful, non-zero value if not.
	 */
	public int playFX(int soundID, int times) {
		if (loaded)
			return soundPool.play(soundID, 1f, 1f, 1, 0, 1f);
		return NOT_LOADED;
	}

	public int playFX(int soundID) {
		if (loaded)
			return soundPool.play(soundID, 1f, 1f, 1, -1, 1f);
		return NOT_LOADED;
	}

	public void stopFX(int soundID) {
		if (loaded)
			soundPool.stop(soundID);
	}

	/**
	 * Play sound at specific angle and distance from user.
	 * @param soundID sound to process
	 * @param angle angle between current direction and source (0-360)
	 */
	public void setPosition(int soundID, float angle, float distance) {
		float distFactor = (float) ((-1* Math.pow(distance, 2) / Math.pow(maxAudiableDistance, 2)) + 1);
		if (distFactor<0)
			distFactor = 0;

//		// Is sound behind the player to the left?
//		if (angle >= 90 && angle < 180)
//			soundPool.setVolume(soundID, 1f*distFactor, 0);
//
//		// Is sound behind the player to the right?
//		if (angle >= 180)
//			soundPool.setVolume(soundID, 0, 1f*distFactor);

		// 0 - All left, PI/2 All right
		float radian = angle;
		//double radian = angle * (Math.PI / 180); // Convert to radians
		float leftVolume = (float) Math.cos(radian);
		float rightVolume = (float) Math.sin(radian);

		soundPool.setVolume(soundID, leftVolume * distance, rightVolume * distance);
	}

	/**
	 * Sweeping sound from left to right
	 */
	public void sweepFX(int soundID) throws InterruptedException {
		int id = soundPool.play(soundID, 0f, 1f, 1, LOOP, 1f);

		for (int count = 0; count < 101; count++) {
			Thread.sleep(20);
			double radians = (Math.PI / 2) * count / 100;

			soundPool.setVolume(id, (float) Math.cos(radians),
					(float) Math.sin(radians));
		}

		soundPool.stop(id);
	}

	/** 
	 * Get max value of device
	 */
	public int maxVolume() {
		return am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	}

	/**
	 * 
	 */
}