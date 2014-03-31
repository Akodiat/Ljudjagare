package sensors;

import android.content.Context;
import android.location.Location;

public class OrientationAndGPSFusion implements OrientationInputListener, GPSInputListener{
	
	private OrientationInputListener listener;

	private float 	avgOrientaitonDiff;
	private int		numberOfMeasurements;
	private float 	GPSBearing;
	private float 	deviceOrientation;

	public OrientationAndGPSFusion(OrientationInputListener listener, Context context) {
		//Setting up Sensor input
		new GPSInputHandler(this, context);
		new OrientationInputHandler(this, context);
		
		avgOrientaitonDiff = 0;
		numberOfMeasurements = 0;
		
		GPSBearing = 0;
		deviceOrientation = 0;
		
		this.listener = listener;
	}
	
	@Override
	public void onGPSConnect() {};
	@Override
	public void onGPSDisconnect() {}

	@Override
	public void onLocationChanged(Location location) {
		GPSBearing = location.getBearing();
		//calcAvgOrientaitonDiff();
	}

	@Override
	public void onCompassChanged(float headingAngleOrientation) {
		deviceOrientation = headingAngleOrientation;
		calcAvgOrientaitonDiff();
		
		listener.onCompassChanged(deviceOrientation + avgOrientaitonDiff);
	}
	
	private void calcAvgOrientaitonDiff(){
		float currDiff = GPSBearing - deviceOrientation;
		
		avgOrientaitonDiff = ((avgOrientaitonDiff * numberOfMeasurements) + currDiff) /
								(++numberOfMeasurements);
		
		android.util.Log.d("GPSORIENTFUSION", 
				"\tnum: "+ numberOfMeasurements + 
				"\tGPS: "+ GPSBearing +
				"\tcalComp: "+ (deviceOrientation + avgOrientaitonDiff));
	}

}
