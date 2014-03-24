package se.chalmers.group42.runforlife;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A fragment including the map with information of what route has been taken.
 */
public class FinishedMapFragment extends MapFragment {
	private GoogleMap map;
	private SupportMapFragment fragment;
	
	private PolylineOptions routeLine = new PolylineOptions().width(10).color(Color.RED);
	private Polyline			myPolyRoute;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_map, container,
				false);
		Log.i("Fragment", "map Fragment running");
		
		fragment = ((SupportMapFragment) getFragmentManager().findFragmentById(
				R.id.map));
		map = fragment.getMap();
		
		drawRoute(rootView);
		
		return rootView;
	}
	
	public void drawRoute(View view){
		Bundle locs = getArguments();
		double[] latitudes = locs.getDoubleArray("latitudes");
		double[] longitudes = locs.getDoubleArray("longitudes");
		
		double[] coinlat = locs.getDoubleArray("coinlat");
		double[] coinlng = locs.getDoubleArray("coinlng");
		
		//draw path
		for(int i = 0 ; i < latitudes.length; i++){
			LatLng l = new LatLng(latitudes[i],longitudes[i]);
			
			routeLine.add(l);
			myPolyRoute = map.addPolyline(routeLine);
		}
		
		//draw coin
		for(int i = 0 ; i < coinlat.length ; i++){
			LatLng l = new LatLng(coinlat[i],coinlng[i]);
			showCollectedCoin(l);
		}
	}
	
	public void showCollectedCoin(LatLng l){
		Log.d("asd","test: "+l.latitude+":"+l.longitude);
		Marker marker = map.addMarker(new MarkerOptions()
		.position(l)
				.title("Coin")
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_coin)));
	}
	
}