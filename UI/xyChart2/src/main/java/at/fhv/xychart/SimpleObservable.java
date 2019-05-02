package at.fhv.xychart;

import java.util.Observable;

/**
 * Created by Johannes on 02.03.2017.
 */
public class SimpleObservable extends Observable {

    @Override
    public void notifyObservers() {
        setChanged();
        super.notifyObservers();

    }

    @Override
    public void notifyObservers(Object arg) {
        setChanged();
        super.notifyObservers(arg);
    }

    @Override
    public synchronized boolean hasChanged() {
        return true;
    }
}
