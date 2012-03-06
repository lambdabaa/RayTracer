package ray.surface;

import ray.IntersectionRecord;
import ray.Ray;
import ray.math.Point3;
//import ray.math.Vector3;

public class Cone extends Surface {
	
	/** The center of the the truncated cone. */
    protected final Point3 center = new Point3();
    public void setCenter(Point3 center) { this.center.set(center); }
    
    /** The z-location of the tip of the cone. */
    protected double tipz = 0.0;
    public void setTipz(double tipz) { this.tipz = tipz; }
    
    /** The radius of the cone in the plane z = center.z. */
    protected double radius = 1.0;
    public void setRadius(double radius) { this.radius = radius; }

    /** The height of the cone.
     *  Truncation of the cone occurs at center.z - height/2 and center.z + height/2
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
        return "Cone " + center + " "+ radius + " "+ height + " "+ tipz + " "+ shader + " end";
	}
}