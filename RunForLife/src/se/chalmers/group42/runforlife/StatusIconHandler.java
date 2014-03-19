package se.chalmers.group42.runforlife;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;


public class StatusIconHandler extends BroadcastReceiver{

	private StatusIconEventListener  	listener;
	private Context						context;
	
	public StatusIconHandler(StatusIconEventListener listener, Context context){
		this.listener 	= listener;
		this.context	= context;
		
		//Inspired by http://stackoverflow.com/questions/6896746/android-is-there-a-broadcast-action-for-volume-changes/7017516#7017516
		VolumeChangeObserver volObserver = new VolumeChangeObserver(new Handler());
		context.getContentResolver().registerContentObserver( 
		    android.provider.Settings.System.CONTENT_URI, true, 
		    volObserver);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {

		//android.util.Log.d("StatusIcon", "Recieved broadcast: "+ intent.getAction());
		if(intent.getAction() == "android.intent.action.HEADSET_PLUG"){
			String message = "Headset";
			if (intent.getExtras().getInt("state") == 1) {
				message += " was plugged in";
				listener.onHeadphonesIn();
			}
			else if (intent.getExtras().getInt("state") == 0) {
				message += " was unplugged";
				listener.onHeadphonesOut();
			}
			android.util.Log.d("StatusIcon", message);
		}
//		Broadcast Action: Wired Headset plugged in or unplugged.
//		
//		The intent will have the following extra values:
//		
//		    state - 0 for unplugged, 1 for plugged.
//		    name - Headset type, human readable string
//		    microphone - 1 if headset has a microphone, 0 otherwise
//		
//		Constant Value: "android.intent.action.HEADSET_PLUG"
			
	}

	private class VolumeChangeObserver extends ContentObserver{

		public VolumeChangeObserver(Handler handler) {
			super(handler);
		}

		 @Override
		   public void onChange(boolean selfChange) {
		      super.onChange(selfChange);
		      
		      int currentVolume  = ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).getStreamVolume(AudioManager.STREAM_MUSIC);
		      
		      android.util.Log.d("StatusIcon", "Something changed. Was it the volume? Volume: "+currentVolume);
		      
		      if(currentVolume == 0){
		    	  listener.onSoundOff();
		      }
		      else
		    	  listener.onSoundOn();
		     
		   }
		
	}
}
