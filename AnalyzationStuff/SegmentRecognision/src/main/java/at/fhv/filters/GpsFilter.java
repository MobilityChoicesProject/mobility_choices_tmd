package at.fhv.filters;

import at.fhv.tmd.common.IGpsPoint;
import java.util.List;

/**
 * Created by Johannes on 18.04.2017.
 */
public interface GpsFilter {


  List<IGpsPoint> filter(List<IGpsPoint> coordinates);

}
