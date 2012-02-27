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
		double txMin, txMax, tyMin, tyMax, tzMin, tzMax;
		
		double tx1 = (minPt.x - rayIn.origin.x) / rayIn.direction.x;
		double tx2 = (maxPt.x - rayIn.origin.x) / rayIn.direction.x;
		txMin = Math.min(tx1,tx2);
		txMax = Math.max(tx1,tx2);
		
		double ty1 = (minPt.y - rayIn.origin.y) / rayIn.direction.y;
		double ty2 = (maxPt.y - rayIn.origin.y) / rayIn.direction.y;
		tyMin = Math.min(ty1,ty2);
		tyMax = Math.max(ty1,ty2);
		
		double tz1 = (minPt.z - rayIn.origin.z) / rayIn.direction.z;
		double tz2 = (maxPt.z - rayIn.origin.z) / rayIn.direction.z;
		tzMin = Math.min(tz1,tz2);
		tzMax = Math.max(tz1,tz2);
		
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
		//TODO: set normal
		//Note to Gareth: the direction for the normal may not be correct for +/-
		if (outRecord.location.x == maxPt.x) outRecord.normal.set(new Vector3(1,0,0));
		if (outRecord.location.x == minPt.x) outRecord.normal.set(new Vector3(-1,0,0));
		if (outRecord.location.y == maxPt.y) outRecord.normal.set(new Vector3(0,1,0));
		if (outRecord.location.y == minPt.y) outRecord.normal.set(new Vector3(0,-1,0));
		if (outRecord.location.z == maxPt.z) outRecord.normal.set(new Vector3(0,0,1));
		if (outRecord.location.z == minPt.z) outRecord.normal.set(new Vector3(0,0,-1));
		return true;
	}
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return "Box " + minPt + " " + maxPt + " " + shader + " end";
	}

}

