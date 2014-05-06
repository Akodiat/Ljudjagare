package se.chalmers.group42.runforlife;


/**
 * @author Linus
 *
 *	Keep track of the speeches, for example "100 meters".
 */
public class Speech {

	private int id;
	private boolean isPlayable;
	private static Speech previous;
	
	public Speech(int id) {
		this.id = id;
		isPlayable = true;
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
		isPlayable = false;
	}
	
	public void setPlayable() {
		isPlayable = true;
	}
	
	public boolean isPlayable() {
		return isPlayable;
	}
}
