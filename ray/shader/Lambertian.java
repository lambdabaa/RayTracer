package ray.shader;

import java.util.ArrayList;

import ray.IntersectionRecord;
import ray.RayTracer;
import ray.Scene;
import ray.light.Light;
import ray.math.Color;
import ray.math.Vector3;

/**
 * A Lambertian material scatters light equally in all directions. BRDF value is
 * a constant
 *
 * @author ags, modified by DS 2/2012
 */
public class Lambertian extends Shader {
	
	/** The color of the surface. */
	protected final Color diffuseColor = new Color(1, 1, 1);
	public void setDiffuseColor(Color inDiffuseColor) { diffuseColor.set(inDiffuseColor); }
	
	public Lambertian() { }
	
	/**
	 * Calculate the BRDF value for this material at the intersection described in record.
	 * Returns the BRDF color in outColor.
	 * @param outColor Space for the output color
	 * @param scene The scene
	 * @param lights The lights
	 * @param toEye Vector pointing towards the eye
	 * @param record The intersection record, which hold the location, normal, etc.
	 */
	public void shade(Color outColor, Scene scene, ArrayList<Light> lights, Vector3 toEye, 
			IntersectionRecord record) {
		outColor.set(0, 0, 0);
		Vector3 l = RayTracer.v3factory.get();
		
		for (Light light : lights) {
		  
			if (!isShadowed(scene, light, record)) {
				l.sub(light.position, record.location);
				l.normalize();
				
	      double x = Math.max(0, record.normal.dot(l));
				outColor.r += diffuseColor.r * light.intensity.r * x;
				outColor.g += diffuseColor.g * light.intensity.g * x;
				outColor.b += diffuseColor.b * light.intensity.b * x;
			}
		}
		
		RayTracer.v3factory.recycle(l);
		outColor.clamp(0, 1);
	}
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		
		return "Lambertian: " + diffuseColor;
	}
	
}