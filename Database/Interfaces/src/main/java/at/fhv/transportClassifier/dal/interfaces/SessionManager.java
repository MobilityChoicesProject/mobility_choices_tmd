package at.fhv.transportClassifier.dal.interfaces;

/**
 * Created by Johannes on 13.02.2017.
 */
public interface SessionManager {

    void startLongTransaction();
    void commitLongTransaction();
    void rollbackLongTransaction();

}
