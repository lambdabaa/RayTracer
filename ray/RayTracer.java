package ray;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ray.light.Light;
import ray.math.Color;
import ray.math.Vector3;

/**
 * A simple ray tracer.
 *
 * @author ags, modified by DS 2/2012
 */
public class RayTracer {
	public static String testFolderPath;
	
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

		ArrayList<Light> lights = scene.getLights();
		double invHeight = 1.0 / height;
		double invWidth = 1.0 / width;
		
		System.out.println("Initializing worker threads...");
		List<RayTracerWorker> workers = new LinkedList<RayTracerWorker>();
		List<Thread> threads = new LinkedList<Thread>();
		for (int i = 0; i < 32; i++) {
		  RayTracerWorker worker = new RayTracerWorker(cam, scene, new Ray(), new Color(), lights, invWidth, invHeight);
		  Thread thread = new Thread(worker);
		  workers.add(worker);
		  threads.add(thread);
		}
		
		System.out.println("Distributing work between workers...");
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
			  workers.get((x + y) % workers.size()).todo.add(new Work(x, y, new Color(255, 255, 255)));
			}
		}
		
		System.out.println("Starting workers...");
		for (Thread thread : threads) {
		  thread.start();
		}
		
		System.out.println("Waiting on workers...");
		for (Thread thread : threads) {
		  try {
        thread.join();
        thread = null;
      } catch (InterruptedException e) {
      }
		}
		
		System.out.println("Coloring image...");
		for (RayTracerWorker worker : workers) {
		  for (Work work : worker.todo) {
		    image.setPixelColor(work.color, (int) work.x, (int) work.y);
		  }
		}

		// Output time
		long totalTime = (System.currentTimeMillis() - startTime);
		System.out.printf(" done in %5.2f seconds.\n", totalTime / 1000.0);
	}

}
