package se.chalmers.group42.controller;

import java.util.ArrayList;

import se.chalmers.group42.runforlife.Constants;
import se.chalmers.group42.runforlife.R;
import se.chalmers.group42.runforlife.R.drawable;
import se.chalmers.group42.runforlife.R.id;
import se.chalmers.group42.runforlife.R.layout;

import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
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

	private static 		LatLng CURRENT_POSITION = new LatLng(58.705477, 11.990884);
	private GoogleMap map;
	private SupportMapFragment fragment;
	private ArrayList<Location> finalRoute = new ArrayList<Location>();
	private ArrayList<Marker> markers = new ArrayList<Marker>();
	//	private int marks;
	private Boolean directionFinished = false;
	private Marker marker;

	private PolylineOptions routeLine = new PolylineOptions().width(10).color(Color.RED);
	private Polyline			myPolyRoute;
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
					//					map.clear();
					//					mCallback.sendMapLocation(latLng);
					//
					//					MarkerOptions markerOptions = new MarkerOptions();
					//					markerOptions.position(latLng);
					//					markerOptions.title(latLng.latitude + " : "
					//							+ latLng.longitude);
					//					map.addMarker(markerOptions);
					//randomTest();

				}
			});
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
		Marker marker = map.addMarker(new MarkerOptions()
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
		marker = map.addMarker(new MarkerOptions()
		.position(new LatLng(routePoint.getLatitude(), routePoint.getLongitude()))
		.title("Random2").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

		for(int i = 0; i < routeTest.size(); i++){
			marker = map.addMarker(new MarkerOptions()
			.position(new LatLng(routeTest.get(i).getLatitude(), routeTest.get(i).getLongitude()))
			.title("Random: " + i).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
		}


		//		Location random = new Location("");
		//		random.setLatitude(CURRENT_POSITION.latitude);
		//		random.setLongitude(CURRENT_POSITION.longitude);
		//		
		//		double a = Math.random();
		//		double b = Math.random();
		//
		//		double r = 50 / Constants.LAT_LNG_TO_METER;
		//
		//		double w = r * Math.sqrt(a);
		//		double t = 2 * Math.PI * b;
		//		double x = w * Math.cos(t);
		//		double y = w * Math.sin(t);
		//		double xNew = x / Math.cos(random.getLatitude());
		//
		//		Location random2 = new Location("");
		//		random2.setLongitude(xNew + random.getLongitude());
		//		random2.setLatitude(random.getLatitude()+y);
		//
		//		float bearingTo = random.bearingTo(random2);
		//		double addLat;
		//		double addLng;
		//		double distanceFromLocation = 1000/Constants.LAT_LNG_TO_METER; 
		//		addLat = Math.sin(bearingTo)*distanceFromLocation;
		//		addLng = Math.cos(bearingTo)*distanceFromLocation / Math.cos(random.getLatitude());
		//
		//		random2.setLatitude(random2.getLatitude() + addLat);
		//		random2.setLongitude(random2.getLongitude() + addLng);
		//		
		//		marker = map.addMarker(new MarkerOptions()
		//		.position(new LatLng(random2.getLatitude(), random2.getLongitude()))
		//		.title("Random2").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
	}

	public void handleGetDirectionsResult(ArrayList<LatLng> directionPoints) {
		PolylineOptions rectLine = new PolylineOptions().width(10).color(
				Color.BLUE);
		int points = directionPoints.size() - 1;
		// int halfway=0;
		Location distanceHelper = new Location("");
		distanceHelper.setLatitude(directionPoints.get(0).latitude);
		distanceHelper.setLongitude(directionPoints.get(0).longitude);

		for (int i = 0; i < directionPoints.size(); i++) {
			// points ++;
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
		// if (newPolyline != null)
		// {
		// newPolyline.remove();
		// }

//		if (finalRoute.size() == 1) {
//			// soundSource.set(wholeWayLocation);
//			//map.clear();
//			markers.add(map.addMarker(new MarkerOptions()
//			.position(
//					new LatLng(wholeWayLocation.getLatitude(),
//							wholeWayLocation.getLongitude()))
//							.title("First!")
//							.icon(BitmapDescriptorFactory
//									.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))));
//		}

		map.addPolyline(rectLine);

		if (finalRoute.size() == checkpoints) {
			directionFinished = true;
			mCallback.sendFinalRoute(finalRoute, distance);
		}
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

	public void setCheckpoints(int checkpoints) {
		this.checkpoints = checkpoints;
		
	}
}