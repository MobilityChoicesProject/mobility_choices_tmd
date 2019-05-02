package at.fhv.xychart;

/**
 * Created by Johannes on 06.02.2017.
 */
public class SimpleAcceleratorData implements  AcceleratorData{


    public SimpleAcceleratorData(DataPointRecords x, DataPointRecords y, DataPointRecords z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    private DataPointRecords x;
    private DataPointRecords y;
    private DataPointRecords z;

    @Override
    public DataPointRecords getX() {

        return x;
    }

    @Override
    public DataPointRecords getY() {
        return y;

    }

    @Override
    public DataPointRecords getZ() {
        return z;

    }
}
