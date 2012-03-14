package ray.surface;

import java.util.Arrays;

import ray.IntersectionRecord;
import ray.Ray;
import ray.math.Point3;
import ray.math.Vector3;

public class Cylinder extends Surface {
	
	/** The center of the cylinder. */
	protected final Point3 center = new Point3();
	public void setCenter(Point3 center) { this.center.set(center); }
	
	/** The radius of the cylinder. */
	protected double radius = 1.0;
	public void setRadius(double radius) { this.radius = radius; }
	
	/** The height of the cylinder in the z-direction.
	 *  The cylinder's extent in z is center.z +/- height/2 */
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
	  Vector3 eminusc = new Vector3();
		eminusc.sub(rayIn.origin, center);
		
		double a = Math.pow(rayIn.direction.x, 2) + Math.pow(rayIn.direction.y, 2);
		double b = 2 * (rayIn.direction.x * eminusc.x + rayIn.direction.y * eminusc.y);
		double c = Math.pow(eminusc.x, 2) + Math.pow(eminusc.y, 2) - Math.pow(radius, 2);
		double discriminant = b * b - 4 * a * c;
		if (discriminant < 0) {
			return false;
		}
		
		double t1 = Math.min((-b + Math.sqrt(discriminant)) / (2 * a), (-b - Math.sqrt(discriminant)) / (2 * a));
    double t2 = (height / 2.0 - eminusc.z) / rayIn.direction.z;
    double t3 = (-height / 2.0 - eminusc.z) / rayIn.direction.z;
    
    // We'll iterate through the values in sorted order so we find closest intersection first
    double[] tarr = {t1, t2, t3};
    Arrays.sort(tarr);
    
    Double t = null;                 // The lowest intersection we find
    for (double x : tarr) {
      IntersectionRecord tmp = new IntersectionRecord();
      tmp.location.add(rayIn.origin, Vector3.getScaledVector(rayIn.direction, x));
      
      if (x == t1) {
        if (Math.abs(tmp.location.z - center.z) < height / 2.0) {
          outRecord.normal.set(
              tmp.location.x - center.x, 
              tmp.location.y - center.y,
              0);
          outRecord.normal.normalize();
          t = x;
          break;
        }
      } else {
        if (Math.pow(tmp.location.x - center.x, 2)
            + Math.pow(tmp.location.y - center.y, 2) 
            - Math.pow(radius, 2) <= 0) {
          if (x == t2) {
            outRecord.normal.set(0, 0, 1);
          } else if (x == t3) {
            outRecord.normal.set(0, 0, -1);
          }
          
          t = x;
          break;
        }
      }
    }
    
    if (t == null || t > rayIn.end || t < rayIn.start) {
      return false;
    }
		rayIn.end = t;
        outRecord.surface = this;
		outRecord.t = t;
		outRecord.location.add(rayIn.origin, Vector3.getScaledVector(rayIn.direction, t));
		return true;
	}
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return "Cylinder " + center + " " + radius + " " + height + " "+ shader + " end";
	}
}

