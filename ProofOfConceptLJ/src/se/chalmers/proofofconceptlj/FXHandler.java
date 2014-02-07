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

	private HashMap<Integer, Integer> soundPoolMap;
	private SoundPool soundPool;

	private AudioManager am;

	private double panning;

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
	 */
	public void playFX(int soundID, int times) {
		if (loaded)
			soundPool.play(soundID, 1f, 1f, 1, 0, 1f);
	}

	public void setPanning(int soundID) {
		// 0 - all left, pi/2 all right
		double radian = panning;
		float leftVolume = (float) Math.cos(radian);
		float rightVolume = (float) Math.sin(radian);

		soundPool.play(soundID, leftVolume, rightVolume, 1, 0, 1f);
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
}
