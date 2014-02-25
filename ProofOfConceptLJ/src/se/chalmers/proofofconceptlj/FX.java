package se.chalmers.proofofconceptlj;

public class FX {

	public static final int NOT_PLAYING = -1;
	private int id;
	private int streamID;
	private float leftVolume, rightVolume;
	private float distance;
	private float angle;
	
	/**
	 * Value between 0.5 (half the speed) and 2 (twice the speed).
	 * 1 is normal speed.
	 */
	private float pitch;

	public FX(int id) {
		this.id = id;
		leftVolume = rightVolume = 1f;
		streamID = NOT_PLAYING;
		distance = Constants.MAX_DISTANCE;
		angle = 90; // must be 90 to be heard in front
		pitch = 1;
	}

	public int ID() {
		return id;
	}

	public float leftVolume() {
		return leftVolume;
	}

	public float rightVolume() {
		return rightVolume;
	}

	public void setVolume(float left, float right) {
		leftVolume = left;
		rightVolume = right;
	}
	
	public void setStreamID(int streamID) {
		this.streamID = streamID;
	}
	
	public int streamID() {
		return streamID;
	}
	
	public boolean isPlaying() {
		return !(streamID == NOT_PLAYING);
	}
	
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
	
	public float pitch() {
		return pitch;
	}
	
	public void setDistance(float distance) {
		this.distance = distance; 
	}
	
	public float distance() {
		return distance; 
	}
	
	public void setAngle(float angle) {
		this.angle = angle; 
	}
	
	public float angle() {
		return angle;
	}
 }

