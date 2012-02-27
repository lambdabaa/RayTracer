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
		// TODO: fill in this function.
		//compute ray-sphere intersection
		Vector3 e_minus_c = new Vector3();
		e_minus_c.sub(rayIn.origin, center);
		double a = rayIn.direction.dot(rayIn.direction);
		double b = 2 * rayIn.direction.dot(e_minus_c);
		double c = e_minus_c.dot(e_minus_c) - radius * radius;
		double discriminant = b * b - 4 * a * c;
		if (discriminant < 0) {
			return false;
		}
	
		if (discriminant == 0) {
			outRecord.t = (-b + Math.sqrt(discriminant))/(2 * a);
		} else {
			outRecord.t = Math.min((-b + Math.sqrt(discriminant)) / (2 * a),
					(-b - Math.sqrt(discriminant)) / (2 * a));
		}
		
		rayIn.end = outRecord.t;
		outRecord.surface = this;
		Vector3 scaledDirection = Vector3.getScaledVector(rayIn.direction, outRecord.t);
		outRecord.location.add(rayIn.origin, scaledDirection);
		// TODO(gareth + daisy): outRecord.normal
		Vector3 normal = new Vector3();
		normal.sub(outRecord.location, center);
		normal.normalize();
		outRecord.normal.set(normal);
		return true;
	}
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return "sphere " + center + " " + radius + " " + shader + " end";
	}

}