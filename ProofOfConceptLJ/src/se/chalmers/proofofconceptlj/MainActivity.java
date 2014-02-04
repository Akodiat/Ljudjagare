package se.chalmers.proofofconceptlj;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity {
	private static final int FX_01 = 1;
	private static final int LOOP = -1;

	private HashMap<Integer, Integer> soundPoolMap;
	private SoundPool soundPool;
	private AudioManager audioManager;

	private Button playFX;
	private Button sweepFX;
	private SeekBar seekBar;

	/**
	 * True if sound is loaded correctly.
	 */
	private boolean loaded = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Initialize audio
		initSound();

		playFX = (Button) findViewById(R.id.fx1);
		playFX.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				playFX(FX_01);
			}
		});
		
		sweepFX = (Button) findViewById(R.id.sweep_fx1);
		sweepFX.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					testPanning(FX_01);
				} catch (InterruptedException e) {
					// Need to catch eventual exception thrown by testPanning.
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Initialize sound engine
	 */
	@SuppressLint("UseSparseArrays")
	private void initSound() {
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
		soundPoolMap.put(FX_01, soundPool.load(this, R.raw.percussion, 1));
	}

	// public void playAtCoordinate(Vector2 coord, Human human) {
	// double someConstant = 0.5; // <-- Edit this (has to be between 0 and 1)
	//
	// // Volume for each ear is inverse proportional to the distance to the
	// // sound source
	// float left = (float) (1 / (Vector2.distance(coord,
	// human.getLeftEarPos()) * someConstant));
	// float right = (float) (1 / (Vector2.distance(coord,
	// human.getRightEarPos()) * someConstant));
	//
	// soundPool.setVolume(FX_01, left, right);
	//
	// // -1 means loop forever
	// soundPool.play(FX_01, left, right, 1, 0, 1f);
	// }

	public void playFX(int soundID) {
		audioManager = (AudioManager) this.getSystemService(AUDIO_SERVICE);

		// Get current value from seekbar.
		// Subtract 100 to get a seekbar from -100 to 100 (max is 200).
		// int seekValue = seekBar.getProgress() - 100;

//		float actualVolume = (float) audioManager
//				.getStreamVolume(AudioManager.STREAM_MUSIC);
//		float maxVolume = (float) audioManager
//				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//		float volume = actualVolume / maxVolume;

		// Play sound
		if (loaded)
			soundPool.play(FX_01, 1f, 1f, 1, 0, 1f);
	}

	/**
	 * Take from http://alvinalexander.com/java/
	 * jwarehouse/android/media/tests/SoundPoolTest/
	 * src/com/android/SoundPoolTest.java.shtml
	 * @throws InterruptedException 
	 */
	public void testPanning(int soundID) throws InterruptedException {
		int id = soundPool.play(soundID, 0f, 1f, 1, LOOP, 1f);

		for (int count = 0; count < 101; count++) {
			Thread.sleep(20);
			double radians = (Math.PI / 2.0) * count / 100.0;
			float leftVolume = (float) Math.sin(radians);
			float rightVolume = (float) Math.cos(radians);
			soundPool.setVolume(id, leftVolume, rightVolume);
		}
		
		soundPool.stop(id);
	}
}
