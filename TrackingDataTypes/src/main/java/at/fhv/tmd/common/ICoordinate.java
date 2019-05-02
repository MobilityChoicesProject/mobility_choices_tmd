package at.fhv.tmd.common;

import java.io.Serializable;

/**
 * Created by Johannes on 20.06.2017.
 */
public interface ICoordinate extends Serializable {

  Double getLatitude();

  Double getLongitude();
}
