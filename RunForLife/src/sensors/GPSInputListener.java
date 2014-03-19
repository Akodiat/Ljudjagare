package sensors;

import android.location.Location;

public interface GPSInputListener {
	
	public void onGPSConnect();
	
	public void onGPSDisconnect();
	
	public void onLocationChanged(Location location);
}
