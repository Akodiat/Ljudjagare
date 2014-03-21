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
public class FinishedRunFragment extends Fragment{

	private TextView time;

	public FinishedRunFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("Fragment", "Run Finished-Fragment created");
		View rootView = inflater.inflate(R.layout.fragment_finished_run,
				container, false);

		Bundle asd = getArguments();
		if(asd != null){
			setValues(asd.getLong("time"), rootView);
		}
		
		return rootView;
	}
	
	public void setValues(long seconds,View view){
		Time t = new Time();
		t.set(seconds*1000);
		t.switchTimezone("GMT");
		
		TextView txTime = (TextView) view.findViewById(R.id.textViewTime2);
		txTime.setText(t.format("%H:%M:%S"));
	}
}