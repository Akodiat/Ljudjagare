package se.chalmers.group42.runforlife;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * A fragment including the info related to the current run.
 */
public class RunFragment extends Fragment{

	private ProgressBar progressBar;
	private int progressStatus = 0;

	public RunFragment() {	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("Fragment", "Run Fragment created");
		View rootView = inflater.inflate(R.layout.fragment_run,
				container, false);
		return rootView;
	}

	public void updateDisp(long seconds,int distance, double currentSpeed, int coins){
		Time t = new Time();
		t.set(seconds*1000);
		t.switchTimezone("GMT");

		if(distance >= 1000){
			double d = Math.round((distance/1000)*100)/100.0d;
			
			TextView txDist = (TextView) getView().findViewById(R.id.textViewDist2);
			txDist.setText(""+d);
			
			TextView txkm = (TextView) getView().findViewById(R.id.textViewDist3);
			txkm.setText("km");
		}else{
			int d = distance;
			
			TextView txDist = (TextView) getView().findViewById(R.id.textViewDist2);
			txDist.setText(""+d);
			
			TextView txkm = (TextView) getView().findViewById(R.id.textViewDist3);
			txkm.setText("m");
		}

		double s = seconds;

		double speed = (distance / s)*3.6;
		double pace;
		if(speed != 0){
			pace = 60 / speed;
		}else{
			pace = 0;
		}

		speed = Math.round(speed*100)/100.0d;
		pace = Math.round(pace*100)/100.0d;

		double currSpd = currentSpeed*3.6;
		double currPace;
		if(currSpd != 0){
			currPace = 60 / currSpd;
		}else{
			currPace = 0;
		}

		currSpd = Math.round(currSpd*100)/100.0d;
		currPace = Math.round(currPace*100)/100.0d;

		setTexts(t, speed, currSpd, pace, currPace);

		progress(coins);
	}

	public void progress(int coins){
		if(coins != progressStatus){
			progressBar = (ProgressBar) getView().findViewById(R.id.progressBar1);
			progressStatus = coins;
			if(progressStatus <= progressBar.getMax()){
				TextView txProg = (TextView) getView().findViewById(R.id.progressText2);
				txProg.setText(progressStatus+"/"+progressBar.getMax()+" Coins");
				progressBar.setProgress(progressStatus);
			}
		}
	}

	public void setMax(int max){
		progressBar = (ProgressBar) getView().findViewById(R.id.progressBar1);
		progressBar.setMax(max);
	}

	public void setTexts(Time time, double speed, double currentSpeed, double pace,double currentPace){
		TextView txTime = (TextView) getView().findViewById(R.id.textViewTime2);
		txTime.setText(time.format("%H:%M:%S"));


		TextView txAvgSpd = (TextView) getView().findViewById(R.id.textViewSpeedAverage2);
		txAvgSpd.setText(""+speed);

		TextView txSpd = (TextView) getView().findViewById(R.id.textViewSpeed2);
		txSpd.setText(""+currentSpeed);

		TextView txPace = (TextView) getView().findViewById(R.id.textViewPaceAverage2);
		txPace.setText(""+pace);
		TextView txCurrPace = (TextView) getView().findViewById(R.id.textViewPace2);
		txCurrPace.setText(""+currentPace);

	}
}