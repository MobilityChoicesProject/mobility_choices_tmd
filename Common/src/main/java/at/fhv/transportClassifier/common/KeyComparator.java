package at.fhv.transportClassifier.common;

/**
 * Created by Johannes on 13.08.2017.
 */
public interface KeyComparator<T, TKey> {

  int compare(T item, TKey key);


}
