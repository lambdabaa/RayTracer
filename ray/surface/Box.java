package ray.surface;

import ray.IntersectionRecord;
import ray.Ray;
import ray.RayTracer;
import ray.math.Point3;
import ray.math.Vector3;

public class Box extends Surface {
	private final static Double EPSILON = 0.000001;
	
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
		double txMin, txMax, tyMin, tyMax, tzMin, tzMax;
		
		double invx = 1 / rayIn.direction.x;
		double tx1 = (minPt.x - rayIn.origin.x) * invx;
		double tx2 = (maxPt.x - rayIn.origin.x) * invx;
		txMin = Math.min(tx1, tx2);
		txMax = Math.max(tx1, tx2);
		
		double invy = 1 / rayIn.direction.y;
		double ty1 = (minPt.y - rayIn.origin.y) * invy;
		double ty2 = (maxPt.y - rayIn.origin.y) * invy;
		tyMin = Math.min(ty1, ty2);
		tyMax = Math.max(ty1, ty2);
		
		double invz = 1 / rayIn.direction.z;
		double tz1 = (minPt.z - rayIn.origin.z) * invz;
		double tz2 = (maxPt.z - rayIn.origin.z) * invz;
		tzMin = Math.min(tz1, tz2);
		tzMax = Math.max(tz1,tz2);
		
		double mint = Math.max(txMin, Math.max(tyMin, tzMin));
		double maxt = Math.min(txMax, Math.min(tyMax, tzMax));
		if (mint > maxt || mint > rayIn.end || mint < rayIn.start) {
			return false;
		}
		
		outRecord.t = mint;
		outRecord.surface = this;
		Vector3 scaledDirection = Vector3.getScaledVector(rayIn.direction, outRecord.t);
		outRecord.location.add(rayIn.origin, scaledDirection);
		if (Math.abs(outRecord.location.x - maxPt.x) <= EPSILON) {
			outRecord.normal.set(1, 0, 0);
		} else if (Math.abs(outRecord.location.x - minPt.x) <= EPSILON) {
			outRecord.normal.set(-1, 0, 0);
		} else if (Math.abs(outRecord.location.y - maxPt.y) <= EPSILON) {
			outRecord.normal.set(0, 1, 0);
		} else if (Math.abs(outRecord.location.y - minPt.y) <= EPSILON) { 
			outRecord.normal.set(0, -1, 0);
		} else if (Math.abs(outRecord.location.z - maxPt.z) <= EPSILON) {
			outRecord.normal.set(0, 0, 1);
		} else if (Math.abs(outRecord.location.z - minPt.z) <= EPSILON) {
			outRecord.normal.set(0, 0, -1);
		}
		
		return true;
	}
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return "Box " + minPt + " " + maxPt + " " + shader + " end";
	}

}
