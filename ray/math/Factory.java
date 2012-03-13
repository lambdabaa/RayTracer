package ray.math;

import java.util.List;

public class Factory<T> {
  private Subfactory<T> sub;
  private List<T> list;
  
  public Factory(Subfactory<T> sub, List<T> list) {
    this.sub = sub;
    this.list = list;
  }
  
  public T get() {
    if (list.isEmpty()) {
      list.add(sub.create());
    }
    
    return list.remove(0);
  }
  
  public void recycle(T t) {
    list.add(t);
  }
}
