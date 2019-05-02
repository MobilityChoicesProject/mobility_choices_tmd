package at.fhv.transportClassifier.segmentsplitting;

import at.fhv.tmd.smoothing.CoordinateInterpolator;
import at.fhv.transportClassifier.common.configSettings.ConfigService;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Johannes on 21.07.2017.
 */
public interface SegmentationService {

  void updateConfigService(ConfigService configService);

  List<Segment> splitIntoSegments(CoordinateInterpolator coordinateInterpolator,
      LocalDateTime startTime, LocalDateTime endTime);
}
