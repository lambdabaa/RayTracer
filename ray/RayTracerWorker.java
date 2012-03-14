package ray;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import ray.light.Light;
import ray.math.Color;
import ray.math.Vector3;

public class RayTracerWorker implements Runnable {
  private Camera cam;
  private Scene scene;
  private Ray ray;
  public Queue<Work> todo;
  private Color rayColor;
  private ArrayList<Light> lights;
  private double invWidth;
  private double invHeight;
  
  public RayTracerWorker(Camera cam, Scene scene, Ray ray, Color rayColor, ArrayList<Light> lights, double invWidth, double invHeight) {
    this.cam = cam;
    this.scene = scene;
    this.ray = ray;
    this.rayColor = rayColor;
    this.lights = lights;
    this.invWidth = invWidth;
    this.invHeight = invHeight;
    todo = new LinkedList<Work>();
  }
  
  public RayTracerWorker assign(Work work) {
    todo.add(work);
    return this;
  }

  @Override
  public void run() {
    while (todo.isEmpty()) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
      }
    }
    
    for (Work work : todo) {
      cam.getRay(ray, (work.x + 0.5) * invWidth, (work.y + 0.5) * invHeight);
      shadeRay(rayColor, scene, ray, lights, 1, 1, false);
      work.color.set(rayColor);
      
      // Gamma correct and clamp pixel values
      work.color.gammaCorrect(2.2);
      work.color.clamp(0, 1);
    }
  }
  
  /**
   * This method returns the color along a single ray in outColor.
   *
   * @param outColor output space
   * @param scene the scene
   * @param ray the ray to shade
   */
  public void shadeRay(Color outColor, Scene scene, Ray ray,// Workspace workspace, 
      ArrayList<Light> lights, int depth, double contribution, boolean internal) {
    
    // Reset the output color
    // TODO: change back to 0,0,0; here for ocean color
    outColor.set(0, 0, 0);

    IntersectionRecord eyeRecord = new IntersectionRecord();
    Vector3 toEye = new Vector3();
    if (!scene.intersect(eyeRecord, ray, false)) {
      return;
    }
    
    toEye.sub(scene.camera.viewPoint, eyeRecord.location);
    if (eyeRecord.surface != null) {
      eyeRecord.surface.getShader().shade(outColor, scene, lights, toEye, eyeRecord);
    }
  }

}
