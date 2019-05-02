package at.fhv.transportClassifier.analyzation;

import at.fhv.transportdetector.trackingtypes.TransportType;
import java.time.LocalDateTime;

/**
 * Created by Johannes on 12.03.2017.
 */
public class SegmentChange {

    private LocalDateTime time;
    private TransportType before;
    private TransportType after;

    public SegmentChange(LocalDateTime time, TransportType before, TransportType after) {
        this.time = time;
        this.before = before;
        this.after = after;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public TransportType getBefore() {
        return before;
    }

    public void setBefore(TransportType before) {
        this.before = before;
    }

    public TransportType getAfter() {
        return after;
    }

    public void setAfter(TransportType after) {
        this.after = after;
    }
}
