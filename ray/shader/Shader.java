package ray.shader;

import java.util.ArrayList;

import ray.IntersectionRecord;
import ray.Ray;
import ray.Scene;
import ray.light.Light;
import ray.math.Color;
import ray.math.Vector3;

/**
 * This interface specifies what is necessary for an object to be a material.
 * The shade method is pretty obvious - a material should know how to "color"
 * itself.  The copy method is needed so that a deep copy may be performed by
 * Surface objects, which all have a generic reference to a material.
 * @author ags, modified by DS 2/2012
 */
public abstract class Shader {
	
	/**
	 * The material given to all surfaces unless another is specified.
	 */
	public static final Shader DEFAULT_MATERIAL = new Lambertian();
	
	/**
	 * Calculate the BRDF value for this material at the intersection described in record.
	 * Returns the BRDF color in outColor.
	 * @param outColor Space for the output color
	 * @param scene The scene
	 * @param lights The lights
	 * @param toEye Vector pointing towards the eye
	 * @param record The intersection record, which hold the location, normal, etc.
	 */
	public abstract void shade(Color outColor, Scene scene, ArrayList<Light> lights, Vector3 toEye, 
			IntersectionRecord record);
	
	/**
	 * Utility method to compute shadows.
	 */
	protected boolean isShadowed(Scene scene, Light light, IntersectionRecord record) {
		// TODO (soon): fill in this function
		Vector3 shadowDirection = new Vector3();
		shadowDirection.sub(light.position, record.location);
		double distance = shadowDirection.length();
		shadowDirection.normalize();
		Ray shadowRay = new Ray(record.location, shadowDirection);
		shadowRay.start = Ray.EPSILON;
		shadowRay.end = distance - Ray.EPSILON;
		return scene.getAnyIntersection(shadowRay);
	}
}