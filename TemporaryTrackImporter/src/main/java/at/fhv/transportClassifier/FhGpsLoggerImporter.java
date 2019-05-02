package at.fhv.transportClassifier;

import at.fhv.transportdetector.trackingtypes.Tracking;
import java.io.File;
import java.io.IOException;

/**
 * Created by Johannes on 11.03.2017.
 */
public interface FhGpsLoggerImporter {
    int getPriority();

    boolean isThisVersion(File file);

    Tracking createTracking(File file) throws IOException;
}
