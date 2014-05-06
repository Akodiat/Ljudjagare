package se.chalmers.group42.runforlife;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * @author Joakim
 *
 *	Handles the case when the headphones are pluggid in or out
 */
public class StatusIconHandler extends BroadcastReceiver {

	private StatusIconEventListener listener;
	public StatusIconHandler(StatusIconEventListener listener, Context context) {
		this.listener = listener;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// android.util.Log.d("StatusIcon", "Recieved broadcast: "+
		// intent.getAction());
		if (intent.getAction() == "android.intent.action.HEADSET_PLUG") {
			String message = "Headset";
			if (intent.getExtras().getInt("state") == 1) {
				message += " was plugged in";
				listener.onHeadphonesIn();
			} else if (intent.getExtras().getInt("state") == 0) {
				message += " was unplugged";
				listener.onHeadphonesOut();
			}
			android.util.Log.d("StatusIcon", message);
		}
		// Broadcast Action: Wired Headset plugged in or unplugged.
		//
		// The intent will have the following extra values:
		//
		// state - 0 for unplugged, 1 for plugged.
		// name - Headset type, human readable string
		// microphone - 1 if headset has a microphone, 0 otherwise
		//
		// Constant Value: "android.intent.action.HEADSET_PLUG"
	}
}