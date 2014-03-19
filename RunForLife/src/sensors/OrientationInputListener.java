package sensors;

public interface OrientationInputListener {
	/**
	 * Called by a OrientationInputHandler when the compass value
	 * is changed.
	 * @param headingAngleOrientation the updated orientation value
	 */
	public void onCompassChanged(float headingAngleOrientation);
}
