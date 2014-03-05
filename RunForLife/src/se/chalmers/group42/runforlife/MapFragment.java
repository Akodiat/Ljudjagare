package se.chalmers.group42.runforlife;

//import se.chalmers.proofofconceptlj.R;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A fragment including the map with information of what route has been taken.
 */
public class MapFragment extends Fragment{
	
	private GoogleMap 			map;
	private SupportMapFragment 	fragment;
	public MapFragment() {
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_map,
				container, false);
		
		// Show location on map
		fragment = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map));
		map = fragment.getMap();
		if(map != null){
			map.setMyLocationEnabled(true);
		} //
		
		map.setOnMapLongClickListener(new OnMapLongClickListener() {
			@Override
			public void onMapLongClick(LatLng latLng) {
				map.clear();
				MarkerOptions markerOptions = new MarkerOptions();
				markerOptions.position(latLng);
				markerOptions.title(latLng.latitude + " : " + latLng.longitude);
				map.addMarker(markerOptions);
			}
		});


		
		return rootView;
	}
}