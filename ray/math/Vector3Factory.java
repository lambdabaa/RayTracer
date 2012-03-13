package ray.math;

public class Vector3Factory implements Subfactory<Vector3> {

  @Override
  public Vector3 create() {
    return new Vector3();
  }

}
