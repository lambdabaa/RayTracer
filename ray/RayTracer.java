package ray;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ray.light.Light;
import ray.math.Color;
import ray.math.Factory;
import ray.math.Vector3;
import ray.math.Vector3Factory;

/**
 * A simple ray tracer.
 *
 * @author ags, modified by DS 2/2012
 */
public class RayTracer {
  private static final float NUMBER_OF_PROGRESS_DOTS = 20;

	public static String testFolderPath;
	
	public static Factory<Vector3> v3factory = new Factory<Vector3>(new Vector3Factory(), new LinkedList());
	
	public static String getTestFolderPath() { return testFolderPath; }
	/**
	 * If filename is a directory, set testFolderPath = fn.
	 * And return a list of all .xml files inside the directory
	 * @param fn Filename or directory
	 * @return fn itself in case fn is a file, or all .xml files inside fn
	 */
	public static final ArrayList<String> getFileLists(String fn) {
		if(fn.endsWith("/"))
			fn.substring(0, fn.length()-1);
		
		File file = new File(fn);
		ArrayList<String> output = new ArrayList<String>();
		if(file.exists()) {
			if(file.isFile()) {
				if(file.getParent() != null)
					testFolderPath = file.getParent() + "/";
				else
					testFolderPath = "";
				output.add(fn);
			} else {
				testFolderPath = fn + "/";
				for(String fl : file.list()) {
					if(fl.endsWith(".xml")) {
						output.add(testFolderPath + fl);
					}
				}	
			}
		} else
			System.out.println("File not found.");

		return output;
	}
	/**
	 * The main method takes all the parameters an assumes they are input files
	 * for the ray tracer. It tries to render each one and write it out to a PNG
	 * file named <input_file>.png.
	 *
	 * @param args
	 */
	public static final void main(String[] args) {
		Parser parser = new Parser();
		for (int ctr = 0; ctr < args.length; ctr++) {
			ArrayList<String> fileLists = getFileLists(args[ctr]);
			
			for (String inputFilename : fileLists) {
				String outputFilename = inputFilename + ".png";
	
				// Parse the input file
				Scene scene = (Scene) parser.parse(inputFilename, Scene.class);
				System.out.printf("Rendering %-25s  ", inputFilename);
				
				// Render the scene
				renderImage(scene);
	
				// Write the image out
				scene.getImage().write(outputFilename);
			}
		}
	}

	/**
	 * The renderImage method renders the entire scene.
	 *
	 * @param scene The scene to be rendered
	 */
	public static void renderImage(Scene scene) {
		// Get the output image
		Image image = scene.getImage();
		Camera cam = scene.getCamera();

		// Set the camera aspect ratio to match output image
		int width = image.getWidth();
		int height = image.getHeight();

		// Timing counters
		long startTime = System.currentTimeMillis();

		Ray ray = new Ray();
		Color pixelColor = new Color(255, 255, 255);
		Color rayColor = new Color();
		ArrayList<Light> lights = scene.getLights();

		int total = height * width;
		int counter = 0;
		int lastShownProgress = 0;
				
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				cam.getRay(ray, (x + 0.5) / width, (y + 0.5) / height);
				shadeRay(rayColor, scene, ray, lights, 1, 1, false);
				pixelColor.set(rayColor);
				
				// Gamma correct and clamp pixel values
				pixelColor.gammaCorrect(2.2);
				pixelColor.clamp(0, 1);
				image.setPixelColor(pixelColor, x, y);
				
				counter++;
				if ((int) (NUMBER_OF_PROGRESS_DOTS * counter / total) != lastShownProgress) {
					lastShownProgress = (int) (NUMBER_OF_PROGRESS_DOTS * counter / total);
					System.out.print(".");
				}
			}
		}

		// Output time
		long totalTime = (System.currentTimeMillis() - startTime);
		System.out.printf(" done in %5.2f seconds.\n", totalTime / 1000.0);

	}

	/**
	 * This method returns the color along a single ray in outColor.
	 *
	 * @param outColor output space
	 * @param scene the scene
	 * @param ray the ray to shade
	 */
	public static void shadeRay(Color outColor, Scene scene, Ray ray,// Workspace workspace, 
			ArrayList<Light> lights, int depth, double contribution, boolean internal) {
		
		// Reset the output color
		outColor.set(0, 0, 0);

		IntersectionRecord eyeRecord = new IntersectionRecord();
		Vector3 toEye = v3factory.get();
		if (!scene.intersect(eyeRecord, ray, false)) {
			return;
		}
		
		toEye.sub(scene.camera.viewPoint, eyeRecord.location);
		if (eyeRecord.surface != null) {
			eyeRecord.surface.getShader().shade(outColor, scene, lights, toEye, eyeRecord);
		}
	}

}
