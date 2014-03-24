package se.chalmers.group42.runforlife;

public class Constants {

	public static final int FX_01 = 1;
	public static final int FX_02 = 2;
	public static final int LOOP = -1;
	public static final int NOT_LOADED = -42;
	public static final int MSG = 3;
	public static final int MSG_STOP = 0;
	public static final double LAT_LNG_TO_METER = 111300f;
	
	public final static String TAG = "Run For Life";
	/**
	 * Distance to the first generated point you run to.
	 */
	public static final int RUN_DISTANCE = 150;
	
	
	/**
	 * Distance to destination where sound behavior changes.
	 */
	public static final int MAX_DISTANCE = 500;
	/**
	 * Distance to destination where user has reached the destination.
	 */
	public static final int MIN_DISTANCE = 50;

	/**
	 * Minimum pitch rate (0.5 - 1)
	 */
	public static final float MIN_PITCH = 0.5f;

	/**
	 * Accuracy needed for the target to be within reach. 
	 * A value of 10 would be 10 degrees to each side (20).
	 */
	public static final int ACCURACY = 10;
	
	public static final int MAX_DELAY = 1000;
	public static final int MIN_DELAY = 250;
	
	public static final int BEHIND_ANGLE = 40;

}
