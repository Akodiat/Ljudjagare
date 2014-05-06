package se.chalmers.group42.sensors;

public interface OrientationInputListener {
	/**
	 * Called by a OrientationInputHandler when the compass value
	 * is changed.
	 * @param orientation the updated orientation value
	 */
	public void onOrientationChanged(float orientation);
}
