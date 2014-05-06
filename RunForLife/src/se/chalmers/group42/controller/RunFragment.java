package se.chalmers.group42.controller;

import se.chalmers.group42.runforlife.R;
import android.content.Context;
import android.content.SharedPreferences;
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
	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("Fragment", "Run Fragment created");
		View rootView = inflater.inflate(R.layout.fragment_run, container,
				false);
		view = rootView;
		SharedPreferences pref = getActivity().getSharedPreferences("MODE", Context.MODE_PRIVATE);
		String mode = pref.getString("application_mode", "");
		if(mode.equals("DISPLAY_MODE")){
			Bundle args = getArguments();
			if(args != null){
				int maxCoins = args.getInt("maxCoins");
				setMax(maxCoins);
				setDisplay(args);
				
			}
		}
		return rootView;
	}
	
	public void updateDisp(long seconds, int distance, double currentSpeed,
			int coins) {
		Time t = new Time();
		t.set(seconds * 1000);
		t.switchTimezone("GMT");

		TextView txDist = (TextView) getView().findViewById(R.id.dist_table);

		if (distance >= 1000){
			double d = distance;
			txDist.setText(Math.round((d / 1000) * 100) / 100.0d + "km");
		}else
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
			progressBar = (ProgressBar) view.findViewById(
					R.id.progressBar1);
			progressStatus = coins;
			if (progressStatus <= progressBar.getMax()) {
				TextView txProg = (TextView) view.findViewById(
						R.id.progressText2);
				txProg.setText(progressStatus + "/" + progressBar.getMax()
						+ " Coins");
				progressBar.setProgress(progressStatus);
			}
		}
	}

	public void setMax(int max) {
		progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
		progressBar.setMax(max);
		TextView txProg = (TextView) view.findViewById(R.id.progressText2);
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
		txTime.setText(time.format("%H:%M:%S"));
		txAvgSpd.setText("Average: " + speed + "km/h");
		txCurrSpd.setText(currentSpeed + "km/h");
		txAvgPace.setText("Average: " + pace + "min/km");
		txCurrPace.setText(currentPace + "min/km");
	}
	
	public void setDisplay(Bundle args){
		
		//********REMOVE PACE****
//		view.findViewById(R.id.curr_pace_desc).setVisibility(View.GONE);
		TextView txp = (TextView) view.findViewById(R.id.curr_pace_desc);
		txp.setText("Pace");
		view.findViewById(R.id.avg_pace_table).setVisibility(View.GONE);
		//********REMOVE SPEED****
//		view.findViewById(R.id.curr_speed_desc).setVisibility(View.GONE);
		TextView txs = (TextView) view.findViewById(R.id.curr_speed_desc);
		txs.setText("Speed");
		view.findViewById(R.id.avg_speed_table).setVisibility(View.GONE);
		
		//******SET FINISH**
//		view.findViewById(R.id.finished_run).setVisibility(View.VISIBLE);
//		view.findViewById(R.id.finished_run_desc).setVisibility(View.VISIBLE);
		
		if (args != null) {

			// ************SET TIME!!**************
			Time t = new Time();
			t.set(args.getLong("time") * 1000);
			t.switchTimezone("GMT");

			((TextView) view.findViewById(R.id.time_table)).setText(t
					.format("%H:%M:%S"));

			// *************SET Distance*************
			int distance = args.getInt("distance");

			if (distance >= 1000){
				double d = distance;
				((TextView) view.findViewById(R.id.dist_table)).setText(Math
						.round((d / 1000) * 100) / 100.0d + "km");
			}else
				((TextView) view.findViewById(R.id.dist_table))
						.setText(distance + "m");

			// *************SET SPEED/pace***************
			double speed = args.getDouble("speed");
			speed = Math.round(speed * 100) / 100.0d;
			((TextView) view.findViewById(R.id.curr_speed_table)).setText(speed
					+ "km/h");

			double pace;
			if (speed != 0)
				pace = Math.round((60 / speed) * 100) / 100.0d;
			else
				pace = 0;

			((TextView) view.findViewById(R.id.curr_pace_table)).setText(pace
					+ "min/km");

			// *******************SET PROGRESS********************
			int coins = args.getInt("nrCoins");
			progress(coins);
		}
	}
}
