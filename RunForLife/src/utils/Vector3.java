package utils;

public class Vector3 {
	public double x,y,z;

	public Vector3(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public void normalize(){
		double lenght = getLength();

		x /= lenght;
		y /= lenght;
		z /= lenght;
	}

	public double getLength(){
		return Math.sqrt(x*x+ y*y + z*z);
	}

	public void multiply(double factor){
		x *= factor;
		y *= factor;
		z *= factor;
	}

	public Vector3 crossProduct(Vector3 other){
		return crossProduct(this, other);
	}

	public static Vector3 crossProduct(Vector3 a, Vector3 b){
		return new Vector3
				(
						a.y * b.z - a.z * b.y,
						a.z * b.x - a.x * b.z,
						a.x * b.y - a.y * b.x
				);
	}

}
