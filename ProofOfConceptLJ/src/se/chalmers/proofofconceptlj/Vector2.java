package se.chalmers.proofofconceptlj;
/**
 * Vector class for two-dimensional vectors (double precision)
 * @author joakim
 *
 */
public class Vector2 {
	private double x;
	private double y;
	
	public Vector2(){
		
	}
	
	public Vector2(double x, double y) {
		this.x = x;
		this.y = y;
	}
	/**
	 * Calculates the distance between two vectors
	 * @param a the first vector
	 * @param b the second vector
	 * @return the distance between vector a and b
	 */
	public static double distance(Vector2 a, Vector2 b) {
		return Math.sqrt(
				Math.pow(a.x - b.x, 2) + 
				Math.pow(a.y - b.y, 2));
	}
	/**
	 * Gives a vector at origo (0,0)
	 * @return the vector at origo (0,0)
	 */
	public static Vector2 zero() {
		return new Vector2(0,0);
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}

}
