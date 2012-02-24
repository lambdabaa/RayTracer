package ray.surface;

import ray.IntersectionRecord;
import ray.Ray;
import ray.math.Point3;
//import ray.math.Vector3;

public class Cone extends Surface {
	
	/** The center of the the truncated cone (x,y,z components). */
	protected final Point3 center = new Point3();
	public void setCenter(Point3 center) { this.center.set(center); }

	/** The full opening angle of the cone. */
	protected double theta = 60.0;
	public void setTheta(double theta) { this.theta = theta; }

	/** The height of the cone.
	 * Truncation of the cone occurs at center-height/2 and center+height/2
	 */
	protected double height = 1.0;
	public void setHeight(double height) { this.height = height; }

	public Cone() { }

	/**
	 * Tests this surface for intersection with ray. If an intersection is found
	 * record is filled out with the information about the intersection and the
	 * method returns true. It returns false otherwise and the information in
	 * outRecord is not modified.
	 *
	 * @param outRecord the output IntersectionRecord
	 * @param ray the ray to intersect
	 * @return true if the surface intersects the ray
	 */
	public boolean intersect(IntersectionRecord outRecord, Ray rayIn) {
		// TODO (soon): fill in this function.

		return false;
	}
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return "Cone " + center + " "+ theta + " "+ height + " "+ shader + " end";
	}
}