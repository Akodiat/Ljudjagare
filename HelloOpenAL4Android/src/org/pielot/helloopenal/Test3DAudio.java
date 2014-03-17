package org.pielot.helloopenal;

import org.pielot.openal.Buffer;
import org.pielot.openal.SoundEnv;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class Test3DAudio extends Activity {

	private final static String TAG = "Test3DAudio";

	private SoundEnv env;

	private FX navFX;

	private SeekBar userOrientation;
	private SeekBar distanceSeek;

	private float distance, angleFromUser;

	private Handler handler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate()");

		this.setContentView(R.layout.main);

		try {
			env = SoundEnv.getInstance(this);

			// Load sound into memory. Has to be mono .wav file.
			Buffer naxFXBuffer = env.addBuffer("nav_fx");

			// Add the audio buffer as a source in the 3D room and create FX
			// instance.
			navFX = new FX(env.addSource(naxFXBuffer));

			// OpenAL uses right-handed coordinate system. Place sound in front
			// of listener.
			navFX.source().setPosition(0, 0, -1);

			// Our application doesn't need a roll-off value (volume's not
			// changing).
			// Roll-off is at which distance the gain changes.
			navFX.source().setRolloffFactor(0);

			// Set listener orientation.
			env.setListenerOrientation(0);
		} catch (Exception e) {
			Log.e(TAG, "Could not initialize OpenAL4Android", e);
		}

		userOrientation = (SeekBar) findViewById(R.id.userOrientation);
		userOrientation.setMax(360);
		userOrientation
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progressValue, boolean fromUser) {
						update(navFX, (angleFromUser = progressValue - 180),
								distance);
					}

					@Override
					public void onStartTrackingTouch(SeekBar arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onStopTrackingTouch(SeekBar arg0) {
						// TODO Auto-generated method stub

					}
				});

		distanceSeek = (SeekBar) findViewById(R.id.distance);
		distanceSeek.setMax(Constants.MAX_DISTANCE);
		distanceSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progressValue,
					boolean fromUser) {
				update(navFX, angleFromUser, (distance = progressValue));
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}
			
			
		});

		// Initialize thread handler
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == Constants.MSG)
					loop(navFX);

				if (msg.what == Constants.MSG_STOP)
					handler.removeCallbacksAndMessages(null);
			}
		};

		loop(navFX);
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.i(TAG, "onPause()");

		// Stop all sounds
		navFX.stop();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy()");

		// Release resources
		env.stopAllSources();
		env.release();
	}

	@Override
	public void onLowMemory() {
		env.onLowMemory();
	}

	/**
	 * Called by the handler at a certain interval. Plays a sound and sends a
	 * message to handler with new delay time.
	 */
	public void loop(FX fx) {
		env.setListenerOrientation(fx.angle());
		fx.source().setPitch(fx.pitch());
		fx.play();

		Message msg = handler.obtainMessage(Constants.MSG);
		handler.sendMessageDelayed(msg, (long) delayInterval(fx));
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
		if (fx.distance() < Constants.MAX_DISTANCE)
			delayRatio = fx.distance() / Constants.MAX_DISTANCE;
		else
			delayRatio = 1;

		// Delay between each repetition.
		return (Constants.MAX_DELAY - Constants.MIN_DELAY) * delayRatio
				+ Constants.MIN_DELAY;
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
	}
}