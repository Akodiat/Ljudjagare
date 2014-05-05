package se.chalmers.group42.gameModes;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.group42.runforlife.Constants;
import se.chalmers.group42.runforlife.FXHandler;
import se.chalmers.group42.runforlife.R;
import sensors.GyroInputHandler;
import sensors.GyroInputListener;
import utils.Vector2;
import android.app.Activity;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class TutorialActivity extends Activity implements GyroInputListener {

	private FXHandler fx;

	public static int MAX_PROGRESS = 1000;

	private float 			orientation;
	private int 			x,y;
	private int				coinX, coinY;
	private	int 			curr100;
	private int 			score = 0;
	private List<Vector2> 	foundCoins;
	private int 			updateCounter = 0;
	//	private DrawableView drawableView;
	private Canvas 			canvas;
	private Paint 			paint1;
	private Paint 			paint2;
	private TextView 		distanceText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tutorial);

		//Providing an up button
		getActionBar().setDisplayHomeAsUpEnabled(true);

		orientation = 0;

		foundCoins = new ArrayList<Vector2>();

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

		curr100 = ((int) (getDistanceToCoin()/100))*100;

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
		fx.update(
				fx.getNavigationFX(), 
				getAngleToCoin());

		if (isCoinFound()) {
			fx.foundCoin();
			increaseScore();
			foundCoins.add(new Vector2(coinX, coinY));
			generateNewCoin();
		}

		if (getDistanceToCoin() - curr100 < 0) {
			int current100 = ((int) (getDistanceToCoin() / 100));
			fx.sayDistance(fx.getSpeech(current100));
			curr100 = current100 * 100;
		}

		else if (!(getDistanceToCoin() - curr100 >= 100)) {
			float delayRatio, newDist = getDistanceToCoin() % 100;
			delayRatio = (float) Math.pow(newDist / 100, 2);


			fx.updateDelay((Constants.MAX_DELAY - Constants.MIN_DELAY)
					* delayRatio + Constants.MIN_DELAY);
		}
		else
			fx.updateDelay(Constants.MAX_DELAY);
	}
	private void increaseScore() {
		score++;
		((TextView) findViewById(R.id.textView_score)).setText(
				"Score: " + score
				);
	}
	private void generateNewCoin() {
		coinX = (int) (MAX_PROGRESS * Math.random());
		coinY = (int) (MAX_PROGRESS * Math.random());
	}
	public int getDistanceToCoin(){
		return (int) Math.sqrt(Math.pow(x-coinX, 2) + Math.pow(y-coinY, 2));
	}
	public float getAngleToCoin(){
		float tempAngle = orientation + (float) Math.toDegrees(
				Math.atan2(	x - coinX, 
						y - coinY)
				);
		// Marcus added this, was problem before
		if(tempAngle > 180){
			tempAngle = tempAngle - 360;
		}else if(tempAngle < -180){
			tempAngle = tempAngle + 360;
		}
		return tempAngle;

	}
	public boolean isCoinFound() {
		return getDistanceToCoin() < Constants.MIN_DISTANCE; // && Math.abs(angle - coinAngle) < 5;
	}

	public void onRunButton(View view) {

		float deltaDistance = 30;

		x += deltaDistance * Math.cos(Math.toRadians(orientation-90));
		y += deltaDistance * Math.sin(Math.toRadians(orientation-90));

		//Checking for edge of the world
		if(x<0) x=0; else if (x>MAX_PROGRESS) x=MAX_PROGRESS;
		if(y<0) y=0; else if (y>MAX_PROGRESS) y=MAX_PROGRESS;

		draw();

		distanceText.setText("Distance: " + getDistanceToCoin() +" m");

	}

	public void onDoneButton(View view) {
		fx.stopLoop();
		finish();
	}

	public void draw(){
		LinearLayout ll = (LinearLayout) findViewById(R.id.drawingLayout);
		Bitmap drawing = Bitmap.createBitmap(
				ll.getWidth() 	> 0 ? ll.getWidth()  : 800, 
						ll.getHeight()	> 0 ? ll.getHeight() : 800, 
								Bitmap.Config.ARGB_8888);
		canvas = new Canvas(drawing);

		Bitmap arrow= BitmapFactory.decodeResource(getResources(), R.drawable.arrow);
		Matrix matrix = new Matrix();
		matrix.postRotate(orientation);

		Bitmap rotatedArrow = Bitmap.createBitmap(arrow , 0, 0, arrow.getWidth(), arrow.getHeight(), matrix, true);
		canvas.drawBitmap(rotatedArrow, 
				x	* canvas.getWidth()/TutorialActivity.MAX_PROGRESS - rotatedArrow.getWidth()/2, 
				y	* canvas.getHeight()/TutorialActivity.MAX_PROGRESS - rotatedArrow.getHeight()/2, 
				null);

		//Paint
		canvas.drawCircle(
				x	* canvas.getWidth()/TutorialActivity.MAX_PROGRESS, 
				y	* canvas.getHeight()/TutorialActivity.MAX_PROGRESS, 
				15, paint1);

		if(isCheating())
			canvas.drawCircle(
					coinX	* canvas.getWidth()/TutorialActivity.MAX_PROGRESS, 
					coinY	* canvas.getHeight()/TutorialActivity.MAX_PROGRESS, 
					30, paint2);


		for (Vector2 coin : foundCoins) {
			Bitmap coinBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.map_coin);
			canvas.drawBitmap(coinBitmap,
					coin.getX()	* canvas.getWidth()/TutorialActivity.MAX_PROGRESS 	- coinBitmap.getWidth()/2, 
					coin.getY()	* canvas.getHeight()/TutorialActivity.MAX_PROGRESS 	- coinBitmap.getHeight()/2,
					null);
		}


		ll.setBackgroundDrawable((new BitmapDrawable(drawing)));
	}
	private boolean isCheating(){
		return ((CheckBox) findViewById(R.id.checkBox_cheating)).isChecked();
	}

	public void onStartTrackingTouch(SeekBar seekBar) {}
	public void onStopTrackingTouch(SeekBar seekBar) {}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
