package se.chalmers.group42.runforlife;

public class ModeController {

	public enum Mode {
		COIN_COLLECTOR,
		QUEST,
		MONSTER_HUNT
	}
	
	public void launchMode(Mode mode) {
		switch (mode) {
		case COIN_COLLECTOR:
			launchCoinCollector();
		case QUEST:
			launchQuest();
		case MONSTER_HUNT:
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
	 */
	private void launchCoinCollector() {
		// TODO Auto-generated method stub
		
	}
}
