package se.chalmers.group42.runforlife;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//import se.chalmers.proofofconceptlj.Polyline;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A fragment including the map with information of what route has been taken.
 */
public class MapFragment extends Fragment {
	OnHeadlineSelectedListener mCallback;

	// Container Activity must implement this interface
	public interface OnHeadlineSelectedListener {
		public void sendMapLocation(LatLng latlng);
		public void sendFinalRoute(ArrayList<Location> finalRoute);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (OnHeadlineSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnHeadlineSelectedListener");
		}
	}

	private GoogleMap map;
	private SupportMapFragment fragment;
	private ArrayList<Location> finalRoute = new ArrayList<Location>();
	//	private int marks;
	private Boolean directionFinished = false;
	private Marker marker;
	
	private PolylineOptions routeLine = new PolylineOptions().width(10).color(Color.RED);
	private Polyline			myPolyRoute;
	private boolean isConnected = false;

	public MapFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_map, container,
				false);
		
		Log.i("Fragment", "map Fragment running");

		// Show location on map
		fragment = ((SupportMapFragment) getFragmentManager().findFragmentById(
				R.id.map));
		map = fragment.getMap();
		if (map != null) {
			map.setMyLocationEnabled(true);

			map.setOnMapLongClickListener(new OnMapLongClickListener() {
				@Override
				public void onMapLongClick(LatLng latLng) {
					map.clear();
					mCallback.sendMapLocation(latLng);

					MarkerOptions markerOptions = new MarkerOptions();
					markerOptions.position(latLng);
					markerOptions.title(latLng.latitude + " : "
							+ latLng.longitude);
					map.addMarker(markerOptions);
				}
			});
		}

		return rootView;
	}

	public void handleNewCoin(Location newCoinLocation){
		if (marker != null){
			marker.remove();
		}
		marker = map.addMarker(new MarkerOptions()
		.position(
				new LatLng(newCoinLocation.getLatitude(),
						newCoinLocation.getLongitude()))
						.title("Another Coin!")
						.icon(BitmapDescriptorFactory
								.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
	}

	public void handleGetDirectionsResult(ArrayList<LatLng> directionPoints) {
		PolylineOptions rectLine = new PolylineOptions().width(10).color(
				Color.BLUE);
		int points = directionPoints.size() - 1;
		// int halfway=0;
		for (int i = 0; i < directionPoints.size(); i++) {
			// points ++;
			rectLine.add(directionPoints.get(i));
		}

		// --- Is used to make more points on the directions, dont remove
		// without asking Marcus ---
		// halfway = directionPoints.size() / 2;
		// Location halfwayLocation = new Location("");
		// halfwayLocation.setLatitude(directionPoints.get(halfway).latitude);
		// halfwayLocation.setLongitude(directionPoints.get(halfway).longitude);
		// finalRoute.add(halfwayLocation);

		Location wholeWayLocation = new Location("");
		wholeWayLocation.setLatitude(directionPoints.get(points).latitude);
		wholeWayLocation.setLongitude(directionPoints.get(points).longitude);
		finalRoute.add(wholeWayLocation);
		// if (newPolyline != null)
		// {
		// newPolyline.remove();
		// }

		if (finalRoute.size() == 1) {
			// soundSource.set(wholeWayLocation);
			map.clear();
			if (marker != null){
				marker.remove();
			}
			marker = map.addMarker(new MarkerOptions()
			.position(
					new LatLng(wholeWayLocation.getLatitude(),
							wholeWayLocation.getLongitude()))
							.title("First!")
							.icon(BitmapDescriptorFactory
									.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
		}



		map.addPolyline(rectLine);
		// latlngBounds = createLatLngBoundsObject(RANDOM, CURRENT_POSITION);
		// float zoom = 19;
		// map.animateCamera(CameraUpdateFactory.newLatLngBounds(latlngBounds,
		// width, height, 500));

		// Add a marker on the last position in the route.
		// if (marker != null){
		// marker.remove();
		// }



		if (finalRoute.size() == 3) {
			directionFinished = true;
			mCallback.sendFinalRoute(finalRoute);
		}
		// marks++;
		// marker = map.addMarker(new MarkerOptions()
		// .position(new LatLng(directionPoints.get(halfway).latitude,
		// directionPoints.get(halfway).longitude))
		// .title("halfway! " + marks + ",  " +
		// directionPoints.get(halfway).latitude
		// + " " + directionPoints.get(halfway).longitude));

		// marker = map.addMarker(new MarkerOptions()
		// .position(new LatLng(directionPoints.get(points).latitude,
		// directionPoints.get(points).longitude))
		// .title("End of route!  " + marks +",  "+
		// directionPoints.get(points).latitude
		// + " " + directionPoints.get(points).longitude));
		// map.animateCamera(CameraUpdateFactory.zoomOut());

	}
	
	public void drawMyPath(Location location){
		if(isConnected || location.getAccuracy() <= 20){
			LatLng p = new LatLng(location.getLatitude(),location.getLongitude());
			routeLine.add(p);
			myPolyRoute = map.addPolyline(routeLine);
		}else{
			isConnected = true;
		}
	}
}