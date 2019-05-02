package at.fhv.xychart;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

/**
 * Created by Johannes on 29.11.2016.
 */
public class DataModel {

    private double zoomFactor = 1.5;


    public SimpleDoubleProperty xLowerBoundProperty = new SimpleDoubleProperty();
    public SimpleDoubleProperty xUpperBoundProperty = new SimpleDoubleProperty();
    public SimpleDoubleProperty tickUnitProperty = new SimpleDoubleProperty(0.5);
    public SimpleDoubleProperty totalValueAmountProperty = new SimpleDoubleProperty();
    public SimpleDoubleProperty visibleValueAmountProperty = new SimpleDoubleProperty();

    public SimpleDoubleProperty yLowerBoundProperty = new SimpleDoubleProperty(-11.390625);
    public SimpleDoubleProperty yUpperBoundProperty = new SimpleDoubleProperty(11.390625);
    public SimpleDoubleProperty yTickUnitProperty = new SimpleDoubleProperty(2);

    private ObservableList chartData;
    private List<DataPointRecords> dataPointRecordsList = new LinkedList<>();
    private AcceleratorData acceleratorData;
    private Double markerPosition = null;

    private static final int pixelPerTick = 100;
    private double pixelWidth;




    public void setAcceleratorData(AcceleratorData data){
        dataPointRecordsList.add(data.getX());
        dataPointRecordsList.add(data.getY());
        dataPointRecordsList.add(data.getZ());
        double duration =calcDuration(data.getX());
        totalValueAmountProperty.set(duration);
        this.acceleratorData = data;
    }

    private double calcDuration(DataPointRecords dataPointRecords){
        double minValue = dataPointRecords.getLowerBound();
        double maxValue = dataPointRecords.getUpperBound();
        return maxValue-minValue;
    }


    public void updateXWidth(double pixelWidth){
       this.pixelWidth = pixelWidth;
        calculateXAxis();
    }









    protected void calculateXAxis(){

        double lowerXPosition = xLowerBoundProperty.get();
        double numberOfSpacesBetweenTicks = Math.floor(pixelWidth/ pixelPerTick);
        double upperBoundProperty = lowerXPosition + tickUnitProperty.get()*numberOfSpacesBetweenTicks;

        xUpperBoundProperty.set(upperBoundProperty);
        double visibleAmount = upperBoundProperty-lowerXPosition;
        visibleValueAmountProperty.set(visibleAmount);
        updatePlot();
    }

    ArrayList<XYChart.Data<Double,Double>> dataPoints = new ArrayList<>();


    public DataModel(){


        xLowerBoundProperty.addListener((observable, oldValue, newValue) -> {
            calculateXAxis();
        });
//        DataPointsRecordStub dataPointRecords = new DataPointsRecordStub();
//        DataPointsRecordStub dataPointRecords1 = new DataPointsRecordStub();
//        DataPointsRecordStub dataPointRecords2 = new DataPointsRecordStub();


    }

    protected XYChart.Series getData(DataPointRecords dataPointRecords,String name){

        int numberOfSpacesBetweenTicks = (int)  Math.ceil( pixelWidth/ pixelPerTick)*10;

        XYChart.Series series = new XYChart.Series();
        series.setName(name);

        double startPosition =xLowerBoundProperty.get();
        double endPosition = (int) (numberOfSpacesBetweenTicks+ xLowerBoundProperty.get());


        double ticksize = tickUnitProperty.get();
        int tickAmount = (int) ((endPosition-startPosition)/ticksize);

        for( int i = 0; i <= numberOfSpacesBetweenTicks;i++){

            double seconds = startPosition+i*(ticksize/10);
            if(dataPointRecords.getLowerBound()<=seconds && dataPointRecords.getUpperBound()>=seconds){
                Double value =dataPointRecords.getValue(seconds);
                if(value != null){
                    XYChart.Data<Double,Double> doubleDoubleData = new XYChart.Data<>(seconds, value);
                    series.getData().add(doubleDoubleData);
                }else{
                    XYChart.Data<Double,Double> doubleDoubleData = new XYChart.Data<>(seconds, -1000.0);
                    series.getData().add(doubleDoubleData);
                }
            }
        }
        return series;
    }


    protected void updatePlot(){
        if(acceleratorData == null){
            return;
        }
        List<XYChart.Series> seriesList  = new LinkedList<>();

        DataPointRecords x = acceleratorData.getX();
        seriesList.add(getData(x,"X"));
        DataPointRecords y = acceleratorData.getY();
        seriesList.add(getData(y,"Y"));
        DataPointRecords z = acceleratorData.getZ();
        seriesList.add(getData(z,"Z"));

        if(markerPosition !=null){
            XYChart.Series series = GetLine(markerPosition, yUpperBoundProperty.get(), yLowerBoundProperty.get());
            seriesList.add(series);
        }

        chartData.removeAll(chartData);
        chartData.addAll(seriesList);
    }


    protected XYChart.Series GetLine(double pos, double minValue,double maxValue){
        XYChart.Series series = new XYChart.Series();
        series.setName("marker");
        XYChart.Data<Double,Double> doubleDoubleData = new XYChart.Data<Double,Double>(pos, maxValue);
        series.getData().add(doubleDoubleData);
        XYChart.Data<Double,Double> doubleDoubleData1 = new XYChart.Data<Double,Double>(pos, minValue);
        series.getData().add(doubleDoubleData1);
        return series;
    }


    public void moveYUp(){
        moveY(yTickUnitProperty.get());
    }
    public void moveYDown(){
        moveY(-yTickUnitProperty.get());
        updatePlot();

    }

    public void moveY(double value){
        yUpperBoundProperty.set(yUpperBoundProperty.get()+value);
        yLowerBoundProperty.set(yLowerBoundProperty.get()+value);
        updatePlot();


    }

    protected void zoomYRelative(double factor){
        double upperbound = yUpperBoundProperty.get();
        double lowerBound = yLowerBoundProperty.get();
        double distance =Math.abs(upperbound-lowerBound);
        double halfDistance = distance/2;
        double middle = lowerBound+halfDistance;

        double zoomedDistance = halfDistance*factor;

        double tickUnit = zoomedDistance *2/ 5;
        yTickUnitProperty.set(tickUnit);

        yLowerBoundProperty.set(middle-zoomedDistance);
        yUpperBoundProperty.set(middle+zoomedDistance);
        updatePlot();

    }

    public void zoomYOut() {
        zoomYRelative(zoomFactor);
    }

    public void zoomYIn() {
        zoomYRelative(1/zoomFactor);

    }

    public void zoomXOut() {

        double tickUnit = tickUnitProperty.get();
        if(tickUnit==0.1){
            tickUnitProperty.set(0.2);
        }else if(tickUnit==0.2){
            tickUnitProperty.set(0.5);
        } else if(tickUnit==0.5){
            tickUnitProperty.set(1);
        } else if(tickUnit==1){
            tickUnitProperty.set(2);
        } else if(tickUnit==2){
        tickUnitProperty.set(5);
         } else if(tickUnit==5){
            tickUnitProperty.set(10);
        }

        calculateXAxis();

    }
    public void zoomXIn() {

        double tickUnit = tickUnitProperty.get();
        if(tickUnit==0.1){
            tickUnitProperty.set(0.1);
        }else if(tickUnit==0.2){
            tickUnitProperty.set(0.1);
        } else if(tickUnit==0.5){
            tickUnitProperty.set(0.2);
        } else if(tickUnit==1){
            tickUnitProperty.set(0.5);
        } else if(tickUnit==2){
            tickUnitProperty.set(1);
        } else if(tickUnit==5){
            tickUnitProperty.set(2);
        } else if(tickUnit==10){
            tickUnitProperty.set(5);
        }
        calculateXAxis();

    }

    public void jumpToPosition(){
        if(markerPosition != null){
            jumpToPosition(markerPosition);

        }
    }

    private void jumpToPosition(double position){
        double lowerBound = xLowerBoundProperty.get();
        double upperBound = xUpperBoundProperty.get();

        double visibleRangeWidth = upperBound - lowerBound;
        double halfVisibleRangeWidth = visibleRangeWidth/2;

        double lowerBoundPosition = position - halfVisibleRangeWidth;
        double upperBoundPosition = position + halfVisibleRangeWidth;

        if(lowerBoundPosition < 0){
            upperBoundPosition= upperBoundPosition -lowerBoundPosition;
            lowerBoundPosition=0;
        }
        xLowerBoundProperty.set(lowerBoundPosition);
        xUpperBoundProperty.set(upperBoundPosition);
    }

    public void removePosition(){
        markerPosition=null;
        updatePlot();
    }

    public void moveToPosition(double position) {
        jumpToPosition(position);
        markerPosition = position;
        updatePlot();
    }

    public void setData(ObservableList data) {
        chartData = data;
    }
}
