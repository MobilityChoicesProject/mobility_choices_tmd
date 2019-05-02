package helper;

import java.util.Iterator;

/**
 * Created by Johannes on 21.06.2017.
 */
public class ThreadSafeIterator<T> implements Iterator<T> {

  private Iterator<T> iterator;

  public ThreadSafeIterator(Iterator<T> iterator){
    this.iterator = iterator;
  }



  @Override
  public synchronized  boolean hasNext() {

    return iterator.hasNext();
  }

  @Override
  public synchronized T next() {
    return iterator.next();
  }
}
