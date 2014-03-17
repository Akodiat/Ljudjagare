package se.chalmers.proofofconceptlj;

public class Speech {
	private int id;
	private boolean hasBeenPlayed;
	
	/**
	 * The previous speech is shared among all objects.
	 */
	private static Speech previous;

	public Speech(int id) {
		this.id = id;
		hasBeenPlayed = false;
		previous = null;
	}

	public int ID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	public boolean hasBeenPlayed() {
		return hasBeenPlayed;
	}

	public void setPlayed() {
		hasBeenPlayed = true;
	}

	public void setPlayable() {
		hasBeenPlayed = false;
	}

	public static void setPrevious(Speech speech) {
		previous = speech;
	}

	public static Speech previous() {
		return previous;
	}
}
