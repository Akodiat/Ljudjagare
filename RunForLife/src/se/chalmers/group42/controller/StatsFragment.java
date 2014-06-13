package se.chalmers.group42.controller;

import se.chalmers.group42.runforlife.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * A fragment including statistical information of the current run.
 */
public class StatsFragment extends Fragment{
	
	private TableLayout table;
	private boolean evenNrOfRows = true;
	
	public StatsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_stats,
				container, false);
		Log.i("Fragment", "Stats Fragment created");
		table = (TableLayout) rootView.findViewById(R.id.tableLayout);
		
		SharedPreferences pref = getActivity().getSharedPreferences("MODE", Context.MODE_PRIVATE);
		String mode = pref.getString("application_mode", "");
		if(mode.equals("DISPLAY_MODE")){
			Bundle stats = getArguments();
			if(stats != null){
				displayFinishedStats(stats);
			}
		}
		return rootView;
	}
	
	//TODO Fixa s� att varannan rad blir gr� med hj�lp av om det inl�sta 
	//elementet har udda eller j�mn plats
	public void updateTableData(int distance, long seconds, String EventText){
		Time t = new Time();
		t.set(seconds*1000);
		t.switchTimezone("GMT");
		
		double d = distance;
		double s = seconds;
		double speed = (d / s)*3.6;
		double paceX = 60 / speed;
		paceX = Math.round(paceX*100)/100.0d;
		
		TableRow row = new TableRow(this.getActivity());
		TextView event = new TextView(this.getActivity());
		TextView time = new TextView(this.getActivity());
		TextView dist = new TextView(this.getActivity());
		TextView pace = new TextView(this.getActivity());
		event.setText(EventText);
		time.setText(t.format("%H:%M:%S"));
		dist.setText(distance+"m");
		pace.setText(paceX+"min/km");
		//Set row color
		if(evenNrOfRows){
			row.setBackgroundColor(Color.LTGRAY);
		}
		evenNrOfRows=!evenNrOfRows;
		
		row.addView(event);
		row.addView(time);
		row.addView(dist);
		row.addView(pace);
		table.addView(row);
	}
	
	public void displayFinishedStats(Bundle stats){
		
		//clear
		
		
		long[] times = stats.getLongArray("times");
		int[] dists = stats.getIntArray("dists");
		
		for(int row = 0 ; row < times.length ; row++){
			updateTableData(dists[row],times[row], "Coin"); //TODO: Do something more general than "Coin"
		}
	}
}