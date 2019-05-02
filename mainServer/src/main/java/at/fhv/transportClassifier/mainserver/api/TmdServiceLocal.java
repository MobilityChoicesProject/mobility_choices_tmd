package at.fhv.transportClassifier.mainserver.api;

import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.processFlow.TmdResult;
import java.util.ArrayList;
import javax.ejb.Local;

/**
 * Created by Johannes on 27.06.2017.
 */
@Local
public interface TmdServiceLocal {

  TmdResult process(ArrayList<IGpsPoint> gpsPointList);



}
