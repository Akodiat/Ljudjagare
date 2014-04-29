package se.chalmers.group42.runforlife;

import sensors.GyroGPSFusion;
import sensors.OrientationInputListener;
import utils.DrawableView;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Message;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class TutorialActivity extends Activity implements OrientationInputListener {

	private FXHandler fx;

	private int 	x,y;
	private int		coinX, coinY;
	private	int 	curr100;
	private boolean hasBeenAnnounced = false;
	private DrawableView drawableView;
	private TextView distanceText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tutorial);

		//Gyro input
		new GyroGPSFusion(this, this);

		drawableView = (DrawableView) 	findViewById(R.id.drawableView1);

		distanceText = (TextView)		findViewById(R.id.textView_distance);
		
		x = y = 500;

		//SeekBar x
		((SeekBar)findViewById(R.id.seekBar_x))
		.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar,
					int progress, boolean fromUser) {
				x = progress;
				drawableView.setXCoord(x);
				drawableView.invalidate();
				drawableView.postInvalidate();
			}
			public void onStartTrackingTouch(SeekBar seekBar) {}
			public void onStopTrackingTouch(SeekBar seekBar) {}
		});

		//SeekBar y
		((SeekBar)findViewById(R.id.seekBar_y))
		.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar,
					int progress, boolean fromUser) {
				y = progress;
				drawableView.setYCoord(y);
				drawableView.invalidate();

				drawableView.postInvalidate();
			}
			public void onStartTrackingTouch(SeekBar seekBar) {}
			public void onStopTrackingTouch(SeekBar seekBar) {}
		});

		// Initialize audio
		(fx = new FXHandler()).initSound(this);
		//fx.update(fx.getNavigationFX(), 0, 1000);

		//Start playing
		fx.loop(fx.getNavigationFX());
	}
	@Override
	protected void onResume() {
		super.onResume();

		//fx.update(fx.getNavigationFX(), angle, getDistance);
		fx.loop(fx.getNavigationFX());
	}

	@Override
	protected void onPause() {
		super.onPause();

		Message msg = fx.getHandler().obtainMessage(Constants.MSG_STOP);
		fx.getHandler().sendMessage(msg);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tutorial, menu);
		return true;
	}

	@Override
	public void onCompassChanged(float headingAngleOrientation) {
		//distanceText.setText("Distance: "+ getDistance() + "m");
		
		if (headingAngleOrientation > 180)
			headingAngleOrientation -= 360;

		fx.update(fx.getNavigationFX(), headingAngleOrientation, getDistance());

		if (isCoinFound() && !hasBeenAnnounced) {
			fx.stopLoop();
			fx.foundCoin();
			hasBeenAnnounced = true;
			// generateNewCoin();
		}

		if (getDistance() < 100) {
			curr100 = 0;
			float delayRatio = (float) Math.pow(getDistance() / 100, 2);

			fx.updateDelay((Constants.MAX_DELAY - Constants.MIN_DELAY)
					* delayRatio + Constants.MIN_DELAY);
		} else {
			// if user has moved forward to new 100s
			if (getDistance() - curr100 < 0) {
				curr100 = ((int) (getDistance() / 100)) * 100;
			}

			else if (!(getDistance() - curr100 >= 100)) {
				float delayRatio, newDist = getDistance() % 100;
				delayRatio = (float) Math.pow(newDist / 100, 2);

				Log.d("dist", "ratio: " + delayRatio);
				fx.updateDelay((Constants.MAX_DELAY - Constants.MIN_DELAY)
						* delayRatio + Constants.MIN_DELAY);
			}
		}

	}
	public int getDistance(){
		return (int) Math.sqrt(x*x + y*y);
	}
	public boolean isCoinFound() {
		return getDistance() < Constants.MIN_DISTANCE; // && Math.abs(angle - coinAngle) < 5;
	}

}
