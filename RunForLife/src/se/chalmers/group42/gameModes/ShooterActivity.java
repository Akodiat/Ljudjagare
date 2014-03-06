package se.chalmers.group42.gameModes;

import se.chalmers.group42.runforlife.FXHandler;
import se.chalmers.group42.runforlife.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ShooterActivity extends Activity {

	private TextView monsterNotification;
	private FXHandler fx;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		monsterNotification = (TextView) findViewById(R.id.monster_notification);
		(fx = new FXHandler()).initSound(this);

	}

	public void monsterSpotted() {
		monsterNotification.setText("RAAWR");
	}
}
