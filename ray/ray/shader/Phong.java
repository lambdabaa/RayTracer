package ray.shader;

import java.util.ArrayList;

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
		outColor.set(0, 0, 0);
		Vector3 l = new Vector3();
		
		for (Light light : lights) {
			if (!isShadowed(scene, light, record)) {
				l.sub(light.position, record.location);
				l.normalize();
				toEye.normalize();
				
				Vector3 h = new Vector3();
				h.add(l,toEye);
				h.normalize();
				
				double x = Math.max(0, record.normal.dot(l));
				double y = Math.pow(Math.max(0, record.normal.dot(h)), exponent);
				outColor.r += light.intensity.r * (diffuseColor.r * x + specularColor.r * y);
				outColor.g += light.intensity.g * (diffuseColor.g * x + specularColor.g * y);
				outColor.b += light.intensity.b * (diffuseColor.b * x + specularColor.b * y);
			}
		}
		
		outColor.clamp(0, 1);
	}
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		
		return "phong " + diffuseColor + " " + specularColor + " " + exponent + " end";
	}
}