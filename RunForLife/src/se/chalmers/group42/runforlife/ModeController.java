package se.chalmers.group42.runforlife;

import se.chalmers.group42.gameModes.CoinCollector;
import se.chalmers.group42.gameModes.GameMode;

public class ModeController {
	private GameMode activeGameMode;

	
	public void launchMode(int gameModeID) {
		switch (gameModeID) {
		case 1:
			launchCoinCollector();
		case 2:
			launchQuest();
		case 3:
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
		activeGameMode = new CoinCollector();
	}
	
	public GameMode getActiveGameMode(){
		if (activeGameMode == null)
			throw new NullPointerException("No game mode initialized, launch a game mode before calling getActiveGameMode.");
		else
			return activeGameMode;
	}
}
