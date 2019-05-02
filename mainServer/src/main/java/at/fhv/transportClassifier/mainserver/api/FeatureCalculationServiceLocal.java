package at.fhv.transportClassifier.mainserver.api;

import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.smoothing.CoordinateInterpolator;
import at.fhv.transportClassifier.common.transaction.TransactionException;
import at.fhv.transportdetector.trackingtypes.features.FeatureResult;
import java.util.List;
import javax.ejb.Local;

/**
 * Created by Johannes on 13.06.2017.
 */
@Local
public interface FeatureCalculationServiceLocal {




  List<FeatureResult> calculateFeature(List<IGpsPoint> interpolatedCoordinatesExact,
      CoordinateInterpolator coordinateInterpolator)
      throws TransactionException;
}
