package ray.surface;

import ray.IntersectionRecord;
import ray.Ray;
import ray.math.Point3;
//import ray.math.Vector3;
import ray.math.Vector3;

public class Cylinder extends Surface {
	
	/** The center of the bottom of the cylinder  x , y ,z components. */
	protected final Point3 center = new Point3();
	public void setCenter(Point3 center) { this.center.set(center); }
	
	/** The radius of the cylinder. */
	protected double radius = 1.0;
	public void setRadius(double radius) { this.radius = radius; }
	
	/** The height of the cylinder. */
	protected double height = 1.0;
	public void setHeight(double height) { this.height = height; }
	
	public Cylinder() { }
	
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
		Vector3 eminusc = new Vector3();
		eminusc.sub(rayIn.origin, center);
		
		double a = rayIn.direction.x * rayIn.direction.x + rayIn.direction.y * rayIn.direction.y;
		double b = 2 * (rayIn.direction.x * eminusc.x + rayIn.direction.y * eminusc.y);
		double c = eminusc.x * eminusc.x + eminusc.y * eminusc.y - radius * radius;
		double discriminant = b * b - 4 * a * c;
		if (discriminant < 0) {
			return false;
		}
		
		outRecord.surface = this;
		Double t1 = rayIn.end = (discriminant == 0 ? -b : -b - Math.sqrt(discriminant)) / (2 * a);
		outRecord.location.add(rayIn.origin, Vector3.getScaledVector(rayIn.direction, t1));
		if (eminusc.z < height && eminusc.z > 0) {
			outRecord.normal.set(new Vector3(outRecord.location.x, outRecord.location.y, 0));
			outRecord.normal.normalize();
		} else {
			t1 = 1000.0;
		}
		
		Double t2 = (height - eminusc.z) / rayIn.direction.z;
		Double t3 = (0 - eminusc.z) / rayIn.direction.z;
		//System.out.println("t1 "+t1+"t2 "+t2+"t3 "+t3);
		outRecord.t = Math.min(t1, Math.min(t2, t3));
		
		if (t2 < t1 & t2 < t3) {
			outRecord.location.add(rayIn.origin, Vector3.getScaledVector(rayIn.direction, t2));
			outRecord.normal.set(new Vector3(0,0,1));
		} else if (t3 < t1 & t3 < t2){
			outRecord.location.add(rayIn.origin, Vector3.getScaledVector(rayIn.direction, t3));	
			outRecord.normal.set(new Vector3(0,0,-1));
		}

		//System.out.println(outRecord.normal);
		return true;
		return false;
	}
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return "Cylinder " + center + " " + radius + " " + height + " "+ shader + " end";
	}
}

