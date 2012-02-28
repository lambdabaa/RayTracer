package ray.shader;

import java.util.ArrayList;
import java.util.Iterator;

import ray.IntersectionRecord;
import ray.Scene;
import ray.light.Light;
import ray.math.Color;
import ray.math.Vector3;
import ray.surface.Surface;

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
		// TODO: fill in this function.
		// Hint: 
		//   1. Add contribution to the final pixel from each light source. 
		//   2. See how to use isShadowed().
		
		// Get the light direction
		// Compute the color value
		outColor.set(0, 0, 0);
		
		for (Light light : lights) {
				Vector3 l = new Vector3();
				l.sub(light.position, record.location);
				l.normalize();
				outColor.r = diffuseColor.r * light.intensity.r * record.normal.dot(l);
				outColor.g = diffuseColor.g * light.intensity.g * record.normal.dot(l);
				outColor.b = diffuseColor.b * light.intensity.b * record.normal.dot(l);
		}
	}
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		
		return "Lambertian: " + diffuseColor;
	}
	
}