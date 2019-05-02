package at.fhv.tmd.processFlow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * Created by Johannes on 21.07.2017.
 */
public class TmdResult implements List<TmdSegment> {

  private List<TmdSegment> tmdSegments = new ArrayList<>();

  @Override
  public int size() {
    return tmdSegments.size();
  }

  @Override
  public boolean isEmpty() {
    return tmdSegments.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return tmdSegments.contains(o);
  }

  @Override
  public Iterator<TmdSegment> iterator() {
    return tmdSegments.iterator();
  }

  @Override
  public Object[] toArray() {
    return tmdSegments.toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return tmdSegments.toArray(a);
  }

  @Override
  public boolean add(TmdSegment tmdSegment) {
    return tmdSegments.add(tmdSegment);
  }

  @Override
  public boolean remove(Object o) {
    return tmdSegments.remove(o);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return tmdSegments.containsAll(c);
  }

  @Override
  public boolean addAll(Collection<? extends TmdSegment> c) {
    return tmdSegments.addAll(c);
  }

  @Override
  public boolean addAll(int index, Collection<? extends TmdSegment> c) {
    return tmdSegments.addAll(index, c);
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    return tmdSegments.removeAll(c);
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return tmdSegments.retainAll(c);
  }

  @Override
  public void replaceAll(UnaryOperator<TmdSegment> operator) {
    tmdSegments.replaceAll(operator);
  }

  @Override
  public void sort(Comparator<? super TmdSegment> c) {
    tmdSegments.sort(c);
  }

  @Override
  public void clear() {
    tmdSegments.clear();
  }

  @Override
  public boolean equals(Object o) {
    return tmdSegments.equals(o);
  }

  @Override
  public int hashCode() {
    return tmdSegments.hashCode();
  }

  @Override
  public TmdSegment get(int index) {
    return tmdSegments.get(index);
  }

  @Override
  public TmdSegment set(int index, TmdSegment element) {
    return tmdSegments.set(index, element);
  }

  @Override
  public void add(int index, TmdSegment element) {
    tmdSegments.add(index, element);
  }

  @Override
  public TmdSegment remove(int index) {
    return tmdSegments.remove(index);
  }

  @Override
  public int indexOf(Object o) {
    return tmdSegments.indexOf(o);
  }

  @Override
  public int lastIndexOf(Object o) {
    return tmdSegments.lastIndexOf(o);
  }

  @Override
  public ListIterator<TmdSegment> listIterator() {
    return tmdSegments.listIterator();
  }

  @Override
  public ListIterator<TmdSegment> listIterator(int index) {
    return tmdSegments.listIterator(index);
  }

  @Override
  public List<TmdSegment> subList(int fromIndex, int toIndex) {
    return tmdSegments.subList(fromIndex, toIndex);
  }

  @Override
  public Spliterator<TmdSegment> spliterator() {
    return tmdSegments.spliterator();
  }

  @Override
  public boolean removeIf(Predicate<? super TmdSegment> filter) {
    return tmdSegments.removeIf(filter);
  }

  @Override
  public Stream<TmdSegment> stream() {
    return tmdSegments.stream();
  }

  @Override
  public Stream<TmdSegment> parallelStream() {
    return tmdSegments.parallelStream();
  }

  @Override
  public void forEach(Consumer<? super TmdSegment> action) {
    tmdSegments.forEach(action);
  }
}
