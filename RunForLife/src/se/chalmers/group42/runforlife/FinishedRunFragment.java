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
public class FinishedRunFragment extends Fragment {

	private TextView time;

	public FinishedRunFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("Fragment", "Run Finished-Fragment created");
		View rootView = inflater.inflate(R.layout.fragment_finished_run,
				container, false);

		setValues(rootView);

		return rootView;
	}

	public void setValues(View view) {
		Bundle args = getArguments();
		if (args != null) {
			// ************SET TIME!!**************
			Time t = new Time();
			t.set(args.getLong("time") * 1000);
			t.switchTimezone("GMT");

			TextView txTime = (TextView) view.findViewById(R.id.textViewTime2);
			txTime.setText(t.format("%H:%M:%S"));

			// *************SET Distance*************
			int dist = args.getInt("distance");
			TextView txDist = (TextView) view.findViewById(R.id.textViewDist2);
			txDist.setText("" + dist);

			// *************SET SPEED/pace***************
			double speed = args.getDouble("speed");
			speed = Math.round(speed * 100) / 100.0d;
			TextView txSpeed = (TextView) view
					.findViewById(R.id.textViewSpeedAverage2);
			txSpeed.setText("" + speed);

			double pace;
			if(speed != 0){
				pace = Math.round((60 / speed) * 100) / 100.0d;
			}else{
				pace = 0;
			}
			TextView txPace = (TextView) view
					.findViewById(R.id.textViewPaceAverage2);
			txPace.setText("" + pace);

		}
	}
}