package at.fhv.tmd.postProcessing;

import at.fhv.transportdetector.trackingtypes.BackgroundGeolocationActivity;

public class ActivityEvaluationStats {
    private BackgroundGeolocationActivity activity;
    private int count;
    private double sumConfidence;

    private ActivityEvaluationStats() {
    }

    public ActivityEvaluationStats(BackgroundGeolocationActivity activity) {
        this.activity = activity;
    }

    public BackgroundGeolocationActivity getActivity() {
        return activity;
    }

    public int getCount() {
        return count;
    }

    public double getAverageConfidence() {
        return (this.count == 0 ? 0 : this.sumConfidence / this.count) / 100;
    }

    public void setActivity(BackgroundGeolocationActivity activity) {
        this.activity = activity;
    }

    public void increaseCount() {
        this.count++;
    }

    public void addConfidence(double confidence) {
        this.sumConfidence += confidence;
    }
}
