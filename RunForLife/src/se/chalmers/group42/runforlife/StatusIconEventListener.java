package se.chalmers.group42.runforlife;

public interface StatusIconEventListener {
	
	public void onGPSConnect();
	
	public void onGPSDisconnect();
	
	public void onSoundOn();
	
	public void onSoundOff();
	
	public void onHeadphonesIn();
	
	public void onHeadphonesOut();
	
}
