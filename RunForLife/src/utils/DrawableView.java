package utils;

import android.R.color;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class DrawableView extends View{
	private Paint paint;
	
	public DrawableView(Context context) {
		super(context);
		paint = new Paint();
	}
	public DrawableView(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint();
	}
	public DrawableView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		paint = new Paint();
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
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		this.setBackgroundColor(color.background_dark);
		
		paint.setColor(color.holo_blue_bright);
		canvas.drawCircle(x, y, 100, paint);
	}

}
