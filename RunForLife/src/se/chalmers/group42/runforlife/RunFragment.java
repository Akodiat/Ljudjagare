package se.chalmers.group42.runforlife;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A fragment including the info related to the current run.
 */
public class RunFragment extends Fragment{
	
	private TextView time;
	
	public RunFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("Fragment", "Run Fragment created");
		View rootView = inflater.inflate(R.layout.fragment_run,
				container, false);
		return rootView;
	}
	public void setTime(long seconds,int distance, double currentSpeed){
		Time t = new Time();
		t.set(seconds*1000);
		t.switchTimezone("GMT");
		
		double d = distance;
		double s = seconds;
		
		double speed = (d / s)*3.6;
		speed = Math.round(speed*100)/100.0d;
		
		double pace = 60 / speed;
		pace = Math.round(pace*100)/100.0d;
		
		double currSpd = currentSpeed*3.6;
		currSpd = Math.round(currSpd*100)/100.0d;
		
		double currPace = 60 / currSpd;
		currPace = Math.round(currPace*100)/100.0d;
		
		TextView txTime = (TextView) getView().findViewById(R.id.textViewTime2);
		txTime.setText(t.format("%H:%M:%S"));
		
		
		TextView txDist = (TextView) getView().findViewById(R.id.textViewDist2);
		txDist.setText(""+distance);
			
		if(distance > 0){
			TextView txAvgSpd = (TextView) getView().findViewById(R.id.textViewSpeedAverage2);
			txAvgSpd.setText(""+speed);

			TextView txSpd = (TextView) getView().findViewById(R.id.textViewSpeed2);
			txSpd.setText(""+currSpd);

			TextView txPace = (TextView) getView().findViewById(R.id.textViewPaceAverage2);
			txPace.setText(""+pace);
			if(currSpd == 0){
				TextView txCurrPace = (TextView) getView().findViewById(R.id.textViewPace2);
				txCurrPace.setText("0.0");
			}else{
				TextView txCurrPace = (TextView) getView().findViewById(R.id.textViewPace2);
				txCurrPace.setText(""+currPace);
			}
		}
	}
}