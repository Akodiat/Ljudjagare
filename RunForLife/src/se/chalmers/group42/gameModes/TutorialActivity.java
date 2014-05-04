package se.chalmers.group42.gameModes;

import se.chalmers.group42.runforlife.Constants;
import se.chalmers.group42.runforlife.FXHandler;
import se.chalmers.group42.runforlife.R;
import sensors.GyroInputHandler;
import sensors.GyroInputListener;
import android.app.Activity;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class TutorialActivity extends Activity implements GyroInputListener, OnSeekBarChangeListener {

	private FXHandler fx;

	public static int MAX_PROGRESS = 1000;

	private float 	orientation;
	private int 	x,y;
	private int		coinX, coinY;
	private	int 	curr100;
	private boolean hasBeenAnnounced = false;
	private int updateCounter = 0;
	//	private DrawableView drawableView;
	private Canvas 	canvas;
	private Paint 	paint1;
	private Paint 	paint2;
	private TextView distanceText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tutorial);

		orientation = 0;

		//Gyro input
		new GyroInputHandler(this, this);

		//		drawableView = (DrawableView) 	findViewById(R.id.drawView);
		//		drawableView.setBackgroundColor(color.holo_purple);


		paint1 = new Paint();
		paint1.setColor(Color.parseColor("#CD5C5C"));
		paint2 = new Paint();
		paint2.setColor(Color.parseColor("#5CCD5C"));
		
//		Bitmap bg = Bitmap.createBitmap(480, 800, Bitmap.Config.ARGB_8888);
//		canvas = new Canvas(bg); 
//		canvas.drawRect(50, 50, 200, 200, paint); 
//		LinearLayout ll = (LinearLayout) findViewById(R.id.drawingLayout);
//		ll.setBackgroundDrawable((new BitmapDrawable(bg)));

		distanceText = (TextView)		findViewById(R.id.textView_TutDistance);

		x = y = 500;
		
		generateNewCoin();

		//SeekBar x
		SeekBar sbX = (SeekBar)findViewById(R.id.seekBar_x);
		sbX.setOnSeekBarChangeListener(this);
		sbX.setMax(MAX_PROGRESS);

		//SeekBar y
		SeekBar sbY = (SeekBar)findViewById(R.id.seekBar_y);
		sbY.setOnSeekBarChangeListener(this);
		sbX.setMax(MAX_PROGRESS);

		// Initialise audio
		(fx = new FXHandler()).initSound(this);
		//fx.update(fx.getNavigationFX(), 0, MAX_DISTANCE);

		//Start playing
		fx.loop(fx.getNavigationFX());

		//drawableView.invalidate();
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
	public void onNewDeltaAngle(float deltaAngle) {
		orientation += Math.toDegrees(deltaAngle);
		while(orientation<0)
			orientation += 360;
		orientation %= 360;

		Log.d("GYROSCOPE", "v: "+Math.round(orientation));

		onOrientationChanged();
		
		//This could be done a lot nicer in C
		updateCounter++;
		if((updateCounter %= 40) == 0)
			draw();
	}


	private void onOrientationChanged() {
		//distanceText.setText("Distance: "+ getDistance() + "m");
		

//		if (orientation > 180)
//			orientation -= 360;

		fx.update(
				fx.getNavigationFX(), 
				getAngleToCoin());

		if (isCoinFound() && !hasBeenAnnounced) {
			fx.stopLoop();
			fx.foundCoin();
			hasBeenAnnounced = true;
			generateNewCoin();
		}

		if (getDistanceToCoin() < 100) {
			curr100 = 0;
			float delayRatio = (float) Math.pow(getDistanceToCoin() / 100, 2);

			fx.updateDelay((Constants.MAX_DELAY - Constants.MIN_DELAY)
					* delayRatio + Constants.MIN_DELAY);
		} else {
			// if user has moved forward to new 100s
			if (getDistanceToCoin() - curr100 < 0) {
				curr100 = ((int) (getDistanceToCoin() / 100)) * 100;
			}

			else if (!(getDistanceToCoin() - curr100 >= 100)) {
				float delayRatio, newDist = getDistanceToCoin() % 100;
				delayRatio = (float) Math.pow(newDist / 100, 2);


				fx.updateDelay((Constants.MAX_DELAY - Constants.MIN_DELAY)
						* delayRatio + Constants.MIN_DELAY);
			}
		}

	}
	private void generateNewCoin() {
		coinX = (int) (MAX_PROGRESS * Math.random());
		coinY = (int) (MAX_PROGRESS * Math.random());
	}
	public int getDistanceToCoin(){
		return (int) Math.sqrt(Math.pow(x-coinX, 2) + Math.pow(y-coinY, 2));
	}
	public float getAngleToCoin(){
		return orientation + (float) Math.toDegrees(
				Math.atan2(	x - coinX, 
							y - coinY)
				);
		
	}
	public boolean isCoinFound() {
		return getDistanceToCoin() < Constants.MIN_DISTANCE; // && Math.abs(angle - coinAngle) < 5;
	}

	public void onProgressChanged(SeekBar seekBar,
			int progress, boolean fromUser) {
		if			(seekBar.getId() == R.id.seekBar_x) {
			x = progress;
			//			drawableView.setXCoord(x);
		}
		else if(	seekBar.getId() == R.id.seekBar_y) {
			y = progress;
			//			drawableView.setYCoord(y);
		}
		else return;
		//drawableView.invalidate();

		draw();
		//drawableView.postInvalidate();
		distanceText.setText("Distance: " + getDistanceToCoin() +" m");
		//Log.d("TUTORIAL", "Distance: " + getDistance() +" m");

	}
	
	public void draw(){
		LinearLayout ll = (LinearLayout) findViewById(R.id.drawingLayout);
		Bitmap bg = Bitmap.createBitmap(
				ll.getWidth() 	> 0 ? ll.getWidth()  : 800, 
				ll.getHeight()	> 0 ? ll.getHeight() : 800, 
				Bitmap.Config.ARGB_8888);
		canvas = new Canvas(bg);

		
		Bitmap b= BitmapFactory.decodeResource(getResources(), R.drawable.arrow);
		Matrix matrix = new Matrix();
		matrix.postRotate(orientation);
		//Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmapOrg,width,height,true);

		Bitmap rotatedBitmap = Bitmap.createBitmap(b , 0, 0, b.getWidth(), b.getHeight(), matrix, true);
        canvas.drawBitmap(rotatedBitmap, 
        		x	* canvas.getWidth()/TutorialActivity.MAX_PROGRESS - rotatedBitmap.getWidth()/2, 
				y	* canvas.getHeight()/TutorialActivity.MAX_PROGRESS - rotatedBitmap.getHeight()/2, 
				null);
     
		//Paint
		canvas.drawCircle(
				x	* canvas.getWidth()/TutorialActivity.MAX_PROGRESS, 
				y	* canvas.getHeight()/TutorialActivity.MAX_PROGRESS, 
				15, paint1);
		
		canvas.drawCircle(
				coinX	* canvas.getWidth()/TutorialActivity.MAX_PROGRESS, 
				coinY	* canvas.getHeight()/TutorialActivity.MAX_PROGRESS, 
				15, paint2);
		

		ll.setBackgroundDrawable((new BitmapDrawable(bg)));
	}
	public void onStartTrackingTouch(SeekBar seekBar) {}
	public void onStopTrackingTouch(SeekBar seekBar) {}

}
