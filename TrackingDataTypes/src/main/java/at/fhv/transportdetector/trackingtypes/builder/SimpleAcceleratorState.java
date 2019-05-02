package at.fhv.transportdetector.trackingtypes.builder;

import at.fhv.transportdetector.trackingtypes.AcceleratorState;
import java.time.LocalDateTime;

/**
 * Created by Johannes on 07.02.2017.
 */
public class SimpleAcceleratorState implements AcceleratorState {
    protected LocalDateTime time;
    protected double xAcceleration;
    protected double yAcceleration;
    protected double zAcceleration;

    public SimpleAcceleratorState() {
    }

    public SimpleAcceleratorState(LocalDateTime time, double xAcceleration, double yAcceleration, double zAcceleration) {
        this.time = time;
        this.xAcceleration = xAcceleration;
        this.yAcceleration = yAcceleration;
        this.zAcceleration = zAcceleration;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public void setxAcceleration(double xAcceleration) {
        this.xAcceleration = xAcceleration;
    }

    public void setyAcceleration(double yAcceleration) {
        this.yAcceleration = yAcceleration;
    }

    public void setzAcceleration(double zAcceleration) {
        this.zAcceleration = zAcceleration;
    }

    @Override
    public LocalDateTime getTime() {
        return time;
    }

    @Override
    public double getXAcceleration() {
        return xAcceleration;
    }

    @Override
    public double getYAcceleration() {
        return yAcceleration;
    }

    @Override
    public double getZAcceleration() {
        return zAcceleration;
    }
}
