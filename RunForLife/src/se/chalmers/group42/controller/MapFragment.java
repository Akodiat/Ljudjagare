package se.chalmers.group42.controller;

import java.util.ArrayList;

import se.chalmers.group42.runforlife.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
//import se.chalmers.proofofconceptlj.Polyline;

/**
 * A fragment including the map with information of what route has been taken.
 */
public class MapFragment extends Fragment {
	OnHeadlineSelectedListener mCallback;

	// Container Activity must implement this interface
	public interface OnHeadlineSelectedListener {
		public void sendMapLocation(LatLng latlng);
		public void sendFinalRoute(ArrayList<Location> finalRoute, float distance);
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
	private ArrayList<Marker> markers = new ArrayList<Marker>();

	private PolylineOptions routeLine = new PolylineOptions().width(10).color(Color.RED);
	private boolean isConnected = false;
	private float distance = 0;
	private int checkpoints;

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
				}
			});
		}

		SharedPreferences pref = getActivity().getSharedPreferences("MODE", Context.MODE_PRIVATE);
		String mode = pref.getString("application_mode", "");
		if(mode.equals("DISPLAY_MODE")){
			Bundle locs = getArguments();
			if(locs != null){
				rootView.findViewById(R.id.imageView_arrow).setVisibility(View.GONE);
				displayFinishedMap(locs);
			}
		}
		return rootView;
	}

	public void handleNewCoin(Location newCoinLocation){
		if (markers.size() != 0){
			for(int i = 0; i < markers.size();i++ ){
				markers.get(i).remove();
			}
		}

		markers.add(map.addMarker(new MarkerOptions()
		.position(
				new LatLng(newCoinLocation.getLatitude(),
						newCoinLocation.getLongitude()))
						.title("Another Coin!")
						.icon(BitmapDescriptorFactory
								.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))));
	}

	public void showCollectedCoin(Location locationOfCoin){
		map.addMarker(new MarkerOptions()
		.position(new LatLng(locationOfCoin.getLatitude(),
				locationOfCoin.getLongitude()))
				.title("Coin")
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_coin)));
	}
	public void zoomToPosition(Location position){
		CameraUpdate cameraUpdate= CameraUpdateFactory.
				newLatLngZoom(new LatLng(position.getLatitude(), position.getLongitude()), 16);
		if(cameraUpdate!=null){
			map.animateCamera(cameraUpdate);
		}
	}

	public void randomTest(ArrayList<Location> routeTest, Location routePoint){
		map.addMarker(new MarkerOptions()
		.position(new LatLng(routePoint.getLatitude(), routePoint.getLongitude()))
		.title("Random2").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

		for(int i = 0; i < routeTest.size(); i++){
			map.addMarker(new MarkerOptions()
			.position(new LatLng(routeTest.get(i).getLatitude(), routeTest.get(i).getLongitude()))
			.title("Random: " + i).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
		}
	}

	public void handleGetDirectionsResult(ArrayList<LatLng> directionPoints) {
		PolylineOptions rectLine = new PolylineOptions().width(10).color(
				Color.BLUE);
		int points = directionPoints.size() - 1;
		Location distanceHelper = new Location("");
		distanceHelper.setLatitude(directionPoints.get(0).latitude);
		distanceHelper.setLongitude(directionPoints.get(0).longitude);

		for (int i = 0; i < directionPoints.size(); i++) {
			Location distancePoint = new Location("");
			distancePoint.setLatitude(directionPoints.get(i).latitude);
			distancePoint.setLongitude(directionPoints.get(i).longitude);
			distance += distanceHelper.distanceTo(distancePoint);
			distanceHelper = distancePoint;
			rectLine.add(directionPoints.get(i));
		}

		Location wholeWayLocation = new Location("");
		wholeWayLocation.setLatitude(directionPoints.get(points).latitude);
		wholeWayLocation.setLongitude(directionPoints.get(points).longitude);
		finalRoute.add(wholeWayLocation);

		map.addPolyline(rectLine);

		if (finalRoute.size() == checkpoints) {
			mCallback.sendFinalRoute(finalRoute, distance);
		}
	}

	public void drawMyPath(Location location){
		if(isConnected || location.getAccuracy() <= 20){
			LatLng p = new LatLng(location.getLatitude(),location.getLongitude());
			routeLine.add(p);
			map.addPolyline(routeLine);
		}else{
			isConnected = true;
		}
	}

	public void setCheckpoints(int checkpoints) {
		this.checkpoints = checkpoints;	
	}

	public void displayFinishedMap(Bundle locs){

		
		
		map.clear();
		map.setMyLocationEnabled(false);

		double[] latitudes = locs.getDoubleArray("latitudes");
		double[] longitudes = locs.getDoubleArray("longitudes");

		double[] coinlat = locs.getDoubleArray("coinlat");
		double[] coinlng = locs.getDoubleArray("coinlng");

		LatLng l = null;
		//draw path
		for(int i = 0 ; i < latitudes.length; i++){
			l = new LatLng(latitudes[i],longitudes[i]);

			routeLine.add(l);
			map.addPolyline(routeLine);
		}

		if(l != null){
			CameraUpdate cameraUpdate= CameraUpdateFactory.
					newLatLngZoom(l, 16);
			if(cameraUpdate!=null){
				map.animateCamera(cameraUpdate);
			}
		}

		//draw coin
		for(int i = 0 ; i < coinlat.length ; i++){
			l = new LatLng(coinlat[i],coinlng[i]);
			showCollectedCoin(l);
		}
	}
	public void showCollectedCoin(LatLng l){
		map.addMarker(new MarkerOptions()
		.position(l)
		.title("Coin")
		.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_coin)));
	}
}