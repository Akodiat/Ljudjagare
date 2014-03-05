package se.chalmers.group42.runforlife;

import android.content.Context;
import android.content.Intent;
import se.chalmers.group42.gameModes.CoinCollector;
import se.chalmers.group42.gameModes.GameMode;

public class ModeController {
	private GameMode activeGameMode;
	private Context context;

	public ModeController(Context context){
		this.context = context;
	}
	
	public void launchMode(int gameModeID) {
		switch (gameModeID) {
		case 0:
			launchCoinCollector();
		case 1:
			launchQuest();
		case 2:
			launchMonsterHunt();
		default:
			break;
		}
	}

	/**
	 * Initialize values needed to play 'Monster Hunt' mode.
	 */
	private void launchMonsterHunt() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Initialize values needed to play 'Quest' mode.
	 */
	private void launchQuest() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Initialize values needed to play 'Coin Collector' mode.
	 * @return 
	 */
	private void launchCoinCollector() {
		Intent runActivityIntent = new Intent(context, RunActivity.class);
		context.startActivity(runActivityIntent);
	}
}
