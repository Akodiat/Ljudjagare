package se.chalmers.group42.controller;

import se.chalmers.group42.runforlife.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * A fragment including the info related to the current run.
 */
public class RunFragment extends Fragment {

	private ProgressBar progressBar;
	private int progressStatus = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("Fragment", "Run Fragment created");
		View rootView = inflater.inflate(R.layout.fragment_run, container,
				false);
		return rootView;
	}

	public void updateDisp(long seconds, int distance, double currentSpeed,
			int coins) {
		Time t = new Time();
		t.set(seconds * 1000);
		t.switchTimezone("GMT");

		TextView txDist = (TextView) getView().findViewById(R.id.dist_desc);

		if (distance >= 1000)
			txDist.setText(Math.round((distance / 1000) * 100) / 100.0d + "km");
		else
			txDist.setText(distance + "m");

		double s = seconds;

		double speed = (distance / s) * 3.6;
		double pace;
		if (speed != 0) {
			pace = 60 / speed;
		} else {
			pace = 0;
		}

		speed = Math.round(speed * 100) / 100.0d;
		pace = Math.round(pace * 100) / 100.0d;

		double currSpd = currentSpeed * 3.6;
		double currPace;
		if (currSpd != 0) {
			currPace = 60 / currSpd;
		} else {
			currPace = 0;
		}

		currSpd = Math.round(currSpd * 100) / 100.0d;
		currPace = Math.round(currPace * 100) / 100.0d;

		setTexts(t, speed, currSpd, pace, currPace);

		progress(coins);
	}

	public void progress(int coins) {
		if (coins != progressStatus) {
			progressBar = (ProgressBar) getView().findViewById(
					R.id.progressBar1);
			progressStatus = coins;
			if (progressStatus <= progressBar.getMax()) {
				TextView txProg = (TextView) getView().findViewById(
						R.id.progressText2);
				txProg.setText(progressStatus + "/" + progressBar.getMax()
						+ " Coins");
				progressBar.setProgress(progressStatus);
			}
		}
	}

	public void setMax(int max) {
		progressBar = (ProgressBar) getView().findViewById(R.id.progressBar1);
		progressBar.setMax(max);
		TextView txProg = (TextView) getView().findViewById(R.id.progressText2);
		txProg.setText(progressStatus + "/" + progressBar.getMax() + " Coins");
	}

	public void setTexts(Time time, double speed, double currentSpeed,
			double pace, double currentPace) {
		TextView txTime = (TextView) getView().findViewById(R.id.time_table);
		TextView txAvgSpd = (TextView) getView().findViewById(
				R.id.avg_speed_table);
		TextView txCurrSpd = (TextView) getView().findViewById(
				R.id.curr_speed_table);
		TextView txAvgPace = (TextView) getView().findViewById(
				R.id.avg_pace_table);
		TextView txCurrPace = (TextView) getView().findViewById(
				R.id.curr_pace_table);

		// Set times
		txTime.setText(time.format("%H:%M:%S" + "s"));
		txAvgSpd.setText(speed + "m/s");
		txCurrSpd.setText(currentSpeed + "m/s");
		txAvgPace.setText(pace + "m/s");
		txCurrPace.setText(currentPace + "m/s");

	}
}