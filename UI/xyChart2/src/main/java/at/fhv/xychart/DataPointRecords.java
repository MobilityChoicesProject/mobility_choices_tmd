package at.fhv.xychart;

/**
 * Created by Johannes on 23.01.2017.
 */
public interface DataPointRecords {


    /**
     * Smallest value on the x scale
     * @return
     */
    double getLowerBound();

    /**
     * Biggest value on the x scale
     * @return
     */
    double getUpperBound();

    /**
     * Number of records contained
     * @return
     */
    double getRecordsCount();

    /**
     * r
     * @param indexInBounds
     * @return the value at the indexInBounds position. If there is no value, the value is interpolated.
     * @exception if the indexInBounds is not in the range of the lowerBound and upperBound
     */
    Double getValue(double indexInBounds);


    /**
     * The stepsize, where each value is positioned on the x scale
     * @return
     */
    double getMinTickSize();


}
