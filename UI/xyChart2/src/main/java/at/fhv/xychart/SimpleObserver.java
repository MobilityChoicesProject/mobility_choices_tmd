package at.fhv.xychart;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Johannes on 02.03.2017.
 */
public abstract class SimpleObserver<T> implements Observer {


    @Override
    public void update(Observable o, Object arg) {
        onUpdate(o,(T)arg);
    }

    public abstract void onUpdate(Observable o,T arg);

}
