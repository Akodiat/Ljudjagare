package se.chalmers.group42.runforlife;

import android.content.Context;
import android.content.Intent;
import se.chalmers.group42.gameModes.*;

public class ModeController {
	
	/**
	 * Available modes.
	 */
	public static final int COIN_COLLECTOR = 0;
	public static final int TUTORIAL = 1;
	
	private Context context;


	public ModeController(Context context){
		this.context = context;
	}
	
	public void launchMode(int gameModeID) {
		switch (gameModeID) {
		case COIN_COLLECTOR:
			launchCoinCollector();
			break;
		case TUTORIAL:
			launchTutorial();
			break;
		default:
			break;
		}
	}

	/**
	 * Initialize values needed to play 'Quest' mode.
	 */
	private void launchTutorial() {
		Intent runActivityIntent = new Intent(context, TutorialActivity.class);
		context.startActivity(runActivityIntent);
	}

	/**
	 * Initialize values needed to play 'Coin Collector' mode.
	 * @return 
	 */
	private void launchCoinCollector() {
		Intent runActivityIntent = new Intent(context, FreerunActivity.class);
		context.startActivity(runActivityIntent);
	}
}
