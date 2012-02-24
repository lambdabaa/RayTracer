package ray.surface;

import ray.IntersectionRecord;
import ray.Ray;
import ray.math.Point3;
import ray.math.Vector3;

public class Box extends Surface {
	
	/* The corner of the box with the smallest x, y, and z components. */
	protected final Point3 minPt = new Point3();
	public void setMinPt(Point3 minPt) { this.minPt.set(minPt); }
	
	/* The corner of the box with the largest x, y, and z components. */
	protected final Point3 maxPt = new Point3();
	public void setMaxPt(Point3 maxPt) { this.maxPt.set(maxPt); }
	
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
		// You will need to implement the three-slab intersection test
		double txMin = (minPt.x - rayIn.origin.x) / rayIn.direction.x;
		double txMax = (maxPt.x - rayIn.origin.x) / rayIn.direction.x;
		if (txMin > txMax) {
			swap(txMin, txMax);
		}
		
		double tyMin = (minPt.y - rayIn.origin.y) / rayIn.direction.y;
		double tyMax = (maxPt.y - rayIn.origin.y) / rayIn.direction.y;
		if (tyMin > tyMax) {
			swap(tyMin, tyMax);
		}
		
		double tzMin = (minPt.z - rayIn.origin.z) / rayIn.direction.z;
		double tzMax = (maxPt.z - rayIn.origin.z) / rayIn.direction.z;
		if (tzMin > tzMax) {
			swap(tzMin, tzMax);
		}
		
		double mint = Math.max(txMin, Math.max(tyMin, tzMin));
		double maxt = Math.min(txMax, Math.min(tyMax, tzMax));
		if (mint > maxt) {
			return false;
		}
		
		outRecord.t = mint;
		rayIn.end = outRecord.t;
		outRecord.surface = this;
		Vector3 scaledDirection = Vector3.getScaledVector(rayIn.direction, outRecord.t);
		outRecord.location.add(rayIn.origin, scaledDirection);
		return true;
	}
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return "Box " + minPt + " " + maxPt + " " + shader + " end";
	}
	
	private void swap(double a, double b) {
		double tmp = a;
		a = b;
		b = tmp;
	}
}

