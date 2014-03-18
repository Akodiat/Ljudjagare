package se.chalmers.group42.runforlife;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * A fragment including statistical information of the current run.
 */
public class StatsFragment extends Fragment{
	
	private TableLayout table;
	
	public StatsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_stats,
				container, false);
		Log.i("Fragment", "Stats Fragment running");
		table = (TableLayout) rootView.findViewById(R.id.tableLayout);
		return rootView;
	}
	
	//TODO Fixa så att varannan rad blir grå med hjälp av om det inlästa 
	//elementet har udda eller jämn plats
	public void updateTableData(int distance, long seconds){
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
		event.setText("Coin");
		time.setText(t.format("%H:%M:%S"));
		dist.setText(distance+"m");
		pace.setText(paceX+"min/km");
		row.addView(event);
		row.addView(time);
		row.addView(dist);
		row.addView(pace);
		table.addView(row);
	}
}