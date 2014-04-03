package se.chalmers.group42.runforlife;

import org.pielot.openal.Source;

public class FX {

	private Source source, forwardSource, behindSource;
	private float distance;
	private boolean isPlaying;

	/**
	 * Angle to source from player.
	 */
	private float angle;

	/**
	 * Value between 0.5 (half the speed) and 2 (twice the speed). 1 is normal
	 * speed.
	 */
	private float pitch;

	public FX(Source forwardSource, Source behindSource) {
		this.source = forwardSource;
		this.forwardSource = forwardSource;
		this.behindSource = behindSource;
		this.distance = Constants.MAX_DISTANCE;
		this.angle = 0;
		this.pitch = 1; // original pitch at first

		source.setPosition(0, 0, -10); // set position in front on user
		source.setRolloffFactor(0); // no roll-off
	}

	public Source source() {
		return source;
	}

	public void setPitch(float pitch) {
		source.setPitch(this.pitch = pitch);
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

		if (Math.abs(angle) > Constants.FRONT_ANGLE && Math.abs(angle) <= 180) {
			source = behindSource;
		} else
			source = forwardSource;
	}

	public float angle() {
		return angle;
	}

	/**
	 * Play sound once without looping.
	 */
	public void play() {
		source.play(false);
		isPlaying = true;
	}

	public void stop() {
		source.stop();
		isPlaying = false;
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public boolean isBehindUser() {
		return Math.abs(angle) > 90;
	}

	public void setVolume(float volume) {
		forwardSource.setGain(volume);
		behindSource.setGain(volume);
	}
}