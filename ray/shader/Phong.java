package ray.shader;

import java.util.ArrayList;
import java.util.Iterator;

import ray.IntersectionRecord;
import ray.Scene;
import ray.light.Light;
import ray.math.Color;
import ray.math.Vector3;

/**
 * A Phong material. Uses the Modified Blinn-Phong model which is energy
 * preserving and reciprocal.
 *
 * @author ags, modified by DS 2/2012
 */
public class Phong extends Shader {
	
	/** The color of the diffuse reflection. */
	protected final Color diffuseColor = new Color(1, 1, 1);
	public void setDiffuseColor(Color diffuseColor) { this.diffuseColor.set(diffuseColor); }
	
	/** The color of the specular reflection. */
	protected final Color specularColor = new Color(1, 1, 1);
	public void setSpecularColor(Color specularColor) { this.specularColor.set(specularColor); }
	
	/** The exponent controlling the sharpness of the specular reflection. */
	protected double exponent = 1.0;
	public void setExponent(double exponent) { this.exponent = exponent; }
	
	public Phong() { }
	
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
		// Calculate the half vector and normalize
		// Compute the specular scale factor
		// Compute the color value
		
		outColor.set(0, 0, 0);
	}
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		
		return "phong " + diffuseColor + " " + specularColor + " " + exponent + " end";
	}
}