package se.chalmers.group42.controller;

import se.chalmers.group42.runforlife.R;
import se.chalmers.group42.runforlife.R.id;
import se.chalmers.group42.runforlife.R.layout;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.maps.SupportMapFragment;

public class FinishedStatsFragment extends StatsFragment{
	
	private TableLayout table;
	private boolean evenNrOfRows = true;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_stats,
				container, false);
		Log.i("Fragment", "Stats Fragment created");
		table = (TableLayout) rootView.findViewById(R.id.tableLayout);
		
		Bundle stats = getArguments();
		long[] times = stats.getLongArray("times");
		int[] dists = stats.getIntArray("dists");
		
		for(int row = 0 ; row < times.length ; row++){
			updateTableData(dists[row],times[row]);
		}
		
		return rootView;
	}
	
	public void updateTableData(int distance, long seconds){
		Time t = new Time();
		t.set(seconds*1000);
		t.switchTimezone("GMT");
		
		double d = distance;
		double s = seconds;
		double speed = (d / s)*3.6;
		double paceX;
		if(speed != 0){
			paceX = Math.round((60 / speed)*100)/100.0d;
		}else{
			paceX = 0;
		}
		
		TableRow row = new TableRow(this.getActivity());
		TextView event = new TextView(this.getActivity());
		TextView time = new TextView(this.getActivity());
		TextView dist = new TextView(this.getActivity());
		TextView pace = new TextView(this.getActivity());
		event.setText("Coin");
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
}
