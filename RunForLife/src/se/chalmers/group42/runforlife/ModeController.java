package se.chalmers.group42.runforlife;

import se.chalmers.group42.gameModes.CoinCollector;
import se.chalmers.group42.gameModes.GameMode;

public class ModeController {
	private GameMode activeGameMode;
	
	public static final int COIN_COLLECTOR = 0;
	public static final int QUEST = 1;
	public static final int SHOOTER = 2;

	
	public void launchMode(int gameModeID) {
		switch (gameModeID) {
		case 0:
			launchCoinCollector();
		case 1:
			launchQuest();
		case 2:
			launchShooter();
		default:
			break;
		}
	}

	/**
	 * Initialize values needed to play 'Shooter' mode.
	 */
	private void launchShooter() {
		
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
		//START CoinCollector activity here.
	}
	
	public GameMode getActiveGameMode(){
		if (activeGameMode == null)
			throw new NullPointerException("No game mode initialized, launch a game mode before calling getActiveGameMode.");
		else
			return activeGameMode;
	}
}
