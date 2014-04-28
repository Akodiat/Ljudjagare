package se.chalmers.group42.runforlife;

import sensors.GyroGPSFusion;
import sensors.OrientationInputListener;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class TutorialActivity extends Activity implements OrientationInputListener {

	private FXHandler fx;
	
	private int 	distance;
	private	int 	curr100;
	private boolean hasBeenAnnounced = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tutorial);
		
		//Gyro input
		new GyroGPSFusion(this, this);
		
		//SeekBar input
		((SeekBar)findViewById(R.id.seekBar_distance))
		.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar,
					int progress, boolean fromUser) {
				distance = progress;
			}
			public void onStartTrackingTouch(SeekBar seekBar) {}
			public void onStopTrackingTouch(SeekBar seekBar) {}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tutorial, menu);
		return true;
	}

	@Override
	public void onCompassChanged(float headingAngleOrientation) {
		if (headingAngleOrientation > 180)
			headingAngleOrientation -= 360;

		fx.update(fx.getNavigationFX(), headingAngleOrientation, distance);

		if (isCoinFound() && !hasBeenAnnounced) {
			fx.stopLoop();
			fx.foundCoin();
			hasBeenAnnounced = true;
			// generateNewCoin();
		}

		if (distance < 100) {
			curr100 = 0;
			float delayRatio = (float) Math.pow(distance / 100, 2);

			fx.updateDelay((Constants.MAX_DELAY - Constants.MIN_DELAY)
					* delayRatio + Constants.MIN_DELAY);
		} else {
			// if user has moved forward to new 100s
			if (distance - curr100 < 0) {
				curr100 = ((int) (distance / 100)) * 100;
			}

			else if (!(distance - curr100 >= 100)) {
				float delayRatio, newDist = distance % 100;
				delayRatio = (float) Math.pow(newDist / 100, 2);

				Log.d("dist", "ratio: " + delayRatio);
				fx.updateDelay((Constants.MAX_DELAY - Constants.MIN_DELAY)
						* delayRatio + Constants.MIN_DELAY);
			}
		}
		
	}
	public boolean isCoinFound() {
		return distance < Constants.MIN_DISTANCE; // && Math.abs(angle - coinAngle) < 5;
	}

}
