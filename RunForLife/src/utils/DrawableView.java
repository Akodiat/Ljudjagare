package utils;

import se.chalmers.group42.gameModes.TutorialActivity;
import android.R.color;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

public class DrawableView extends SurfaceView{
	private Paint paint;
	private int w,h;
	
	public DrawableView(Context context) {
		super(context);
		init();
		setWillNotDraw(false);
	}
	public DrawableView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		setWillNotDraw(false);
	}
	public DrawableView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
		setWillNotDraw(false);
	}
	public void init(){
		paint = new Paint();
		paint.setColor(color.holo_blue_bright);
		this.setVisibility(VISIBLE);
		this.invalidate();
	}

	private int x = 50;
	private int y = 50;
	
	public void setXCoord(int x){
		this.x = x;
	}
	
	public void setYCoord(int y){
		this.y = y;
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		this.w = w;
		this.h = h;
		
		Log.d("TUTORIAL", "View w: "+w+"\th:"+h);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
//		this.setBackgroundColor(color.);
		//Log.d("TUTORIAL", "Circle at x: "+x+"\ty:"+y);
		canvas.drawCircle(
				x	* w/TutorialActivity.MAX_PROGRESS, 
				y	* h/TutorialActivity.MAX_PROGRESS, 
				100, paint);
		canvas.drawRect(10, 7, 15, 12, paint);
	}

}
