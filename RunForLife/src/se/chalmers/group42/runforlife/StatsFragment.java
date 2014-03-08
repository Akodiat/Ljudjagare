package se.chalmers.group42.runforlife;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
		System.out.println("Stats Fragment running");
		table = (TableLayout) rootView.findViewById(R.id.tableLayout);
		return rootView;
	}
	
	//TODO Fixa så att varannan rad blir grå med hjälp av om det inlästa 
	//elementet har udda eller jämn plats
	public void updateTableData(){
		TableRow row = new TableRow(this.getActivity());
		TextView event = new TextView(this.getActivity());
		TextView time = new TextView(this.getActivity());
		TextView dist = new TextView(this.getActivity());
		TextView pace = new TextView(this.getActivity());
		event.setText("Coin");
		time.setText("01:09");
		dist.setText("500 m");
		pace.setText("4:30 km/min");
		row.addView(event);
		row.addView(time);
		row.addView(dist);
		row.addView(pace);
		table.addView(row);
	}
}