package sensors;

public interface GyroInputListener {
	/**
	 * Gives the change of angle (in radians!) since last update
	 * @param deltaAngle the angle (in radians!)
	 */
	public void onNewDeltaAngle(float deltaAngle);
}
