package at.fhv.tmd.postProcessing;

import at.fhv.context.SegmentContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.UnaryOperator;

/**
 * Created by Johannes on 19.07.2017.
 */
public class PostprocessClassifications implements List<PostProcessClassificationResult>,
    Serializable {


  public static void addPostProcessClassification(SegmentContext segmentContext,PostProcessClassificationResult result){
    PostprocessClassifications postProcessClassificationResults =null;
    boolean b = segmentContext.hasData(SegmentContext.POST_PROCESS_RESULT);
    if (b) {
      postProcessClassificationResults = segmentContext.getData(SegmentContext.POST_PROCESS_RESULT);
    } else {
      postProcessClassificationResults = new PostprocessClassifications();
      segmentContext.addData(SegmentContext.POST_PROCESS_RESULT,postProcessClassificationResults);
    }
    postProcessClassificationResults.add(result);
  }

  private List<PostProcessClassificationResult> results = new ArrayList<>();

  public int size() {
    return results.size();
  }

  public boolean isEmpty() {
    return results.isEmpty();
  }

  public boolean contains(Object o) {
    return results.contains(o);
  }

  public Iterator<PostProcessClassificationResult> iterator() {
    return results.iterator();
  }

  public Object[] toArray() {
    return results.toArray();
  }

  public <T> T[] toArray(T[] a) {
    return results.toArray(a);
  }

  public boolean add(
      PostProcessClassificationResult postProcessClassificationResult) {
    return results.add(postProcessClassificationResult);
  }

  public boolean remove(Object o) {
    return results.remove(o);
  }

  public boolean containsAll(Collection<?> c) {
    return results.containsAll(c);
  }

  public boolean addAll(
      Collection<? extends PostProcessClassificationResult> c) {
    return results.addAll(c);
  }

  public boolean addAll(int index,
      Collection<? extends PostProcessClassificationResult> c) {
    return results.addAll(index, c);
  }

  public boolean removeAll(Collection<?> c) {
    return results.removeAll(c);
  }

  public boolean retainAll(Collection<?> c) {
    return results.retainAll(c);
  }

  public void replaceAll(
      UnaryOperator<PostProcessClassificationResult> operator) {
    results.replaceAll(operator);
  }

  public void sort(
      Comparator<? super PostProcessClassificationResult> c) {
    results.sort(c);
  }

  public void clear() {
    results.clear();
  }

  @Override
  public boolean equals(Object o) {
    return results.equals(o);
  }

  @Override
  public int hashCode() {
    return results.hashCode();
  }

  public PostProcessClassificationResult get(int index) {
    return results.get(index);
  }

  public PostProcessClassificationResult set(int index,
      PostProcessClassificationResult element) {
    return results.set(index, element);
  }

  public void add(int index, PostProcessClassificationResult element) {
    results.add(index, element);
  }

  public PostProcessClassificationResult remove(int index) {
    return results.remove(index);
  }

  public int indexOf(Object o) {
    return results.indexOf(o);
  }

  public int lastIndexOf(Object o) {
    return results.lastIndexOf(o);
  }

  public ListIterator<PostProcessClassificationResult> listIterator() {
    return results.listIterator();
  }

  public ListIterator<PostProcessClassificationResult> listIterator(int index) {
    return results.listIterator(index);
  }

  public List<PostProcessClassificationResult> subList(int fromIndex, int toIndex) {
    return results.subList(fromIndex, toIndex);
  }

  public Spliterator<PostProcessClassificationResult> spliterator() {
    return results.spliterator();
  }
}
