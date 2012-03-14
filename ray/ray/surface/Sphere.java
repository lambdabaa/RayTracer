package ray.surface;

import ray.IntersectionRecord;
import ray.Ray;
import ray.math.Point3;
import ray.math.Vector3;

/**
 * Represents a sphere as a center and a radius.
 *
 * @author ags
 */
public class Sphere extends Surface {
	
	/** The center of the sphere. */
	protected final Point3 center = new Point3();
	public void setCenter(Point3 center) { this.center.set(center); }
	
	/** The radius of the sphere. */
	protected double radius = 1.0;
	public void setRadius(double radius) { this.radius = radius; }
	
	public Sphere() { }
	
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
	  Vector3 eminusc = new Vector3();
		eminusc.sub(rayIn.origin, center);
		
		double a = rayIn.direction.dot(rayIn.direction);
		double b = 2 * rayIn.direction.dot(eminusc);
		double c = eminusc.dot(eminusc) - radius * radius;
		double discriminant = b * b - 4 * a * c;
		if (discriminant < 0) {
			return false;
		}
		
		eminusc = null;
		
		double t = (discriminant == 0 ? -b : -b - Math.sqrt(discriminant)) / (2 * a);
		if (t > rayIn.end || t < rayIn.start) {
		  return false;
		}
		
		outRecord.t = rayIn.end = t;
		outRecord.surface = this;
		outRecord.location.add(rayIn.origin, Vector3.getScaledVector(rayIn.direction, outRecord.t));
		outRecord.normal.sub(outRecord.location, center); // TODO(garethaye): Complain more about lib
		outRecord.normal.normalize();
		return true;
	}
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return "sphere " + center + " " + radius + " " + shader + " end";
	}

}