package at.fhv.xychart;

/**
 * Created by Johannes on 30.01.2017.
 */
public interface ChartController {

    void addPositionClickObserver(SimpleObserver<Double> observer);

    void deletePositionClickObserver(SimpleObserver<Double> observer);

    void init();

    void setAccelerationData(AcceleratorData data);

    void setPosition(double position);
}
