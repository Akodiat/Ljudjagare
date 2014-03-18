package se.chalmers.group42.runforlife;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StatusIconHandler extends BroadcastReceiver{

	private StatusIconEventListener  listener;
	
	public StatusIconHandler(StatusIconEventListener listener){
		this.listener = listener;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {

		android.util.Log.d("Status", "Recieved broadcast: "+ intent.getAction());
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

}
