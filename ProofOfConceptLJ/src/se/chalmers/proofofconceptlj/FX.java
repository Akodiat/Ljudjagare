package se.chalmers.proofofconceptlj;

public class FX {

	public static final int NOT_PLAYING = -1;
	private int id;
	private int streamID;
	private float leftVolume, rightVolume;

	public FX(int id) {
		this.id = id;
		leftVolume = rightVolume = 1f;
		streamID = NOT_PLAYING;
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
}
