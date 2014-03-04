package se.chalmers.group42.runforlife;

public class ModeController {

	public static enum Mode {
		COIN_COLLECTOR,
		QUEST,
		MONSTER_HUNT
	}
	
	public static void launchMode(Mode mode) {
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
	private static void launchMonsterHunt() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Initialize values needed to play 'Quest' mode.
	 */
	private static void launchQuest() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Initialize values needed to play 'Coin Collector' mode.
	 */
	private static void launchCoinCollector() {
		// TODO Auto-generated method stub
		
	}
}
