package at.fhv.transportClassifier;

import at.fhv.transportdetector.trackingtypes.Tracking;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by Johannes on 11.03.2017.
 */
public class FhGpsLoggerImporterManager {



    LinkedList<FhGpsLoggerImporter> importers = new LinkedList<>();

    public void addImporter(FhGpsLoggerImporter importer){
        importers.add(importer);
        importers.sort((o1, o2) -> o2.getPriority()-o1.getPriority());
    }

    public Tracking loadTracking(File file) throws IOException {
        for (FhGpsLoggerImporter importer : importers) {

            if (importer.isThisVersion(file)) {
                return importer.createTracking(file);
            }
        }
        throw new IllegalArgumentException("No valid input");
    }


}
