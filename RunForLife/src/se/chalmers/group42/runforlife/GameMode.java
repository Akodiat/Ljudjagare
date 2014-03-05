package se.chalmers.group42.runforlife;

/**
 * Interface for a GameMode, contains methods for communicating with a general
 * game mode that will handle the logic of a game.
 * @author Joakim Johansson
 *
 */
public interface GameMode {
	/**
	 * Returns the score achieved in the game
	 * @return the score achieved in the game
	 */
	public int getScore();
	
	/**
	 * Method to call every time sensor values are updated
	 */
	public void onSensorUpdate(SensorValues sensorValues);

}
