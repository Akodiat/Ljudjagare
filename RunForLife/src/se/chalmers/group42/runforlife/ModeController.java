package se.chalmers.group42.runforlife;

import android.content.Context;
import android.content.Intent;
import se.chalmers.group42.gameModes.CoinCollectorActivity;
import se.chalmers.group42.gameModes.GameMode;
import se.chalmers.group42.gameModes.ShooterActivity;

public class ModeController {
	private GameMode activeGameMode;
	
	/**
	 * Available modes.
	 */
	public static final int COIN_COLLECTOR = 0;
	public static final int QUEST = 1;
	public static final int SHOOTER = 2;
	
	private Context context;


	public ModeController(Context context){
		this.context = context;
	}
	
	public void launchMode(int gameModeID) {
		switch (gameModeID) {
		case COIN_COLLECTOR:
			launchCoinCollector();
		case QUEST:
			launchQuest();
		case SHOOTER:
			launchShooter();
		default:
			break;
		}
	}

	/**
	 * Initialize values needed to play 'Shooter' mode.
	 */
	private void launchShooter() {
		Intent runActivityIntent = new Intent(context, ShooterActivity.class);
		context.startActivity(runActivityIntent);
	}

	/**
	 * Initialize values needed to play 'Quest' mode.
	 */
	private void launchQuest() {
		// TODO
	}

	/**
	 * Initialize values needed to play 'Coin Collector' mode.
	 * @return 
	 */
	private void launchCoinCollector() {
		Intent runActivityIntent = new Intent(context, CoinCollectorActivity.class);
		context.startActivity(runActivityIntent);
	}
}
