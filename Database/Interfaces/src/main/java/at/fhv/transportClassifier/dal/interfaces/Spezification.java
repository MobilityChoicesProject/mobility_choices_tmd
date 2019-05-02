package at.fhv.transportClassifier.dal.interfaces;

/**
 * Created by Johannes on 15.02.2017.
 */
public interface Spezification<T> {

    boolean isReady();
    boolean isSatiesfiedBy(T entity);
}
