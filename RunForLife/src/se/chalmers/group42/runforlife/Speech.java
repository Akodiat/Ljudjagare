package se.chalmers.group42.runforlife;

public class Speech {

	private int id;
	private boolean hasBeenPlayed;
	private static Speech previous;
	
	public Speech(int id) {
		this.id = id;
		hasBeenPlayed = false;
		previous = null;
	}
	
	public int id() {
		return id;
	}
	
	public static Speech previous() {
		return previous;
	}
	
	public static void setPrevious(Speech speech) {
		previous = speech;
	}
	
	public void setPlayed() {
		hasBeenPlayed = true;
	}
	
	public void setPlayable() {
		hasBeenPlayed = false;
	}
	
	public boolean isPlayable() {
		return !hasBeenPlayed;
	}
}
