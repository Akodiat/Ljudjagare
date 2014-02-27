package se.chalmers.group42.runforlife;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import at.technikum.mti.fancycoverflow.FancyCoverFlow;
import at.technikum.mti.fancycoverflow.FancyCoverFlowSampleAdapter;

public class MainActivity extends Activity{

	private FancyCoverFlow fancyCoverFlow;
	private ImageButton runButton;
	private Intent runActivityIntent;
	private int coverFlowHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/*
		 * 
		 * 
		 * http://stackoverflow.com/questions/1016896/how-to-get-screen-dimensions
		 */
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		System.out.println("Density= " + getResources().getDisplayMetrics().density);
		int density = (int)getResources().getDisplayMetrics().density;
		int width = size.x;
		int height = size.y;
		System.out.println("Width= " + width);
		System.out.println("Height= " + height);
		//Setting a good coverflow height as 4/10 of the screen height minus the actionbar
		coverFlowHeight = (int)((4.0/10.0)*height) - density*32;

		//Setting up the cover flow
		fancyCoverFlow = (FancyCoverFlow) this.findViewById(R.id.fancyCoverFlow);
		fancyCoverFlow.setAdapter(new FancyCoverFlowSampleAdapter(coverFlowHeight));
		fancyCoverFlow.setUnselectedAlpha(1.0f);
		fancyCoverFlow.setUnselectedSaturation(0.0f);
		fancyCoverFlow.setUnselectedScale(0.5f);
		fancyCoverFlow.setSpacing(0);
		fancyCoverFlow.setMaxRotation(0);
		fancyCoverFlow.setScaleDownGravity(0.2f);
		fancyCoverFlow.setActionDistance(FancyCoverFlow.ACTION_DISTANCE_AUTO);

		//Setting up the run-button
		runButton = (ImageButton) findViewById(R.id.runButton);
		runActivityIntent = new Intent(this, RunActivity.class);
		runButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				System.out.println("Selected " + fancyCoverFlow.getSelectedItemId());
				startActivity(runActivityIntent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}