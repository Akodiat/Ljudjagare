package se.chalmers.group42.runforlife;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
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
		View rootView = inflater.inflate(R.layout.fragment_run,
				container, false);
		return rootView;
	}
	public void setTime(long seconds){
		Time t = new Time();
		t.set(seconds*1000);
		t.switchTimezone("GMT");
		
		TextView textView = (TextView) getView().findViewById(R.id.textViewTime2);
		textView.setText(t.format("%H:%M:%S"));
	}
}