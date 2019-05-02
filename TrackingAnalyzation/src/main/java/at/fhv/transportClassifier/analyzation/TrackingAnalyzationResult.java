package at.fhv.transportClassifier.analyzation;

import at.fhv.transportdetector.trackingtypes.BoundingBox;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Johannes on 09.03.2017.
 */
public class TrackingAnalyzationResult {
    public String phoneId;
    public String phoneType;
    public long startimestamp;
    public Duration durationStartAndGpsSensorStart;
    public Duration durationEndAndGpsSensorEnd;
    public Duration durationStartAndAcSensorStart;
    public Duration durationEndAndAcSensorEnd;
    public Duration durationStartAndGpsSystemStart;
    public Duration durationEndAndGpsSystemEnd;
    public Duration totalNoAcStateData;
    public double totalNoAcStateDataPercentage;
    public Set<LocalDateTime> noAccAtSegmentChange = new TreeSet<>();
    public Set<LocalDateTime> noGpsAtSegmentChange = new TreeSet<>();
    public List<SegmentChange> gpsSegmentChanges = new ArrayList<>();

    public List<SegmentChange> acSegmentChanges = new ArrayList<>();
    public BoundingBox boundingbox;
    public int trackingVersion
        ;
}
