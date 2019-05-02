package at.fhv.xychart;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollBar;

public class SimpleChartController implements ChartController {


    private SimpleObservable observable = new SimpleObservable();

    @Override
    public void addPositionClickObserver(SimpleObserver<Double> observer){
        observable.addObserver(observer);
    }

    @Override
    public void deletePositionClickObserver(SimpleObserver<Double> observer){
        observable.deleteObserver(observer);
    }

    protected void fireEvent(double position){
        observable.notifyObservers(position);
    }

    @FXML
    LineChart lineChart;



    @FXML
    ScrollBar scrollbar;

//    @FXML
//    TextField posTextField;


    @FXML
    Button verticalUpButton;
    @FXML
    Button verticalDownButton;
    @FXML
    Button verticalZoomInButton;
    @FXML
    Button verticalZoomOutButton;
    @FXML
    Button horizontalZoomInButton;
    @FXML
    Button horizontalZoomOutButton;
    @FXML
    Button  horizontalPosButton;


    DataModel dataModel = new DataModel();
    NumberAxis xAxis;
    NumberAxis yAxis;

    @Override
    public void init(){

        dataModel.setData(lineChart.getData());


        verticalUpButton.setOnAction(event -> {
            dataModel.moveYUp();

        });
        verticalDownButton.setOnAction(event -> {
            dataModel.moveYDown();

        });

        verticalZoomInButton.setOnAction(event -> {

            dataModel.zoomYIn();

        });
        verticalZoomOutButton.setOnAction(event -> {
            dataModel.zoomYOut();

        });

        horizontalZoomInButton.setOnAction(event -> {
            dataModel.zoomXIn();
        });

        horizontalZoomOutButton.setOnAction(event -> {
            dataModel.zoomXOut();
        });
        horizontalPosButton.setOnAction(event -> {
            dataModel.jumpToPosition();
        });

//        lineChart.getXAxis().setTickLength(0.2);
        lineChart.setAnimated(false);
        lineChart.setCreateSymbols(false);
        lineChart.setLegendSide(Side.RIGHT);

        lineChart.setOnMouseClicked(event -> {
            double x = event.getX();
            double y = event.getY();

            Point2D parentLocation = lineChart.localToScreen(0, 0);
            double parentX = parentLocation.getX();
            Point2D childPoisiton = xAxis.localToScreen(0, 0);
            double childX = childPoisiton.getX();
            double xOffset = childX-parentX;


            double lowerBound = xAxis.getLowerBound();
            double upperBound = xAxis.getUpperBound();


            x = x-xOffset;
            Number valueForDisplay = xAxis.getValueForDisplay(x);
            double position = valueForDisplay.doubleValue();

            if(position<lowerBound ||position > upperBound){
                return;
            }
            fireEvent(position);

        });

        xAxis=(NumberAxis) lineChart.getXAxis();


        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0);   // minimaler angezeigter Wert
        xAxis.setUpperBound(20);  // maximaler angezeigter Wert

//        xAxis.setTickUnit(0.1);     // space between tickmarks

        xAxis.setMinorTickVisible(true);
        xAxis.setMinorTickCount(10);
//


        yAxis=(NumberAxis) lineChart.getYAxis();

        xAxis.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

                dataModel.updateXWidth(newValue.doubleValue());

            }
        });


        xAxis.lowerBoundProperty().bind(dataModel.xLowerBoundProperty);
        xAxis.upperBoundProperty().bindBidirectional(dataModel.xUpperBoundProperty);
        xAxis.tickUnitProperty().bind(dataModel.tickUnitProperty);
//        xAxis.setTickUnit(1);

        yAxis.setAutoRanging(false);
        yAxis.lowerBoundProperty().bind(dataModel.yLowerBoundProperty);
        yAxis.upperBoundProperty().bind(dataModel.yUpperBoundProperty);
        yAxis.tickUnitProperty().bind(dataModel.yTickUnitProperty);

        yAxis.setMinorTickVisible(false);


        initScrollBar();

//        scrollbar.valueProperty().addListener((observable, oldValue, newValue) -> {
//
//            dataModel.setLowerXPosition(newValue.doubleValue());
//
//        });

        double width =scrollbar.getWidth();
        dataModel.updateXWidth(width);

    }

    private void initScrollBar( ){

        scrollbar.setMin(0);
        scrollbar.valueProperty().bindBidirectional(dataModel.xLowerBoundProperty);
        scrollbar.maxProperty().bind(dataModel.totalValueAmountProperty.subtract(dataModel.visibleValueAmountProperty));
        scrollbar.visibleAmountProperty().bind(dataModel.visibleValueAmountProperty);
        scrollbar.unitIncrementProperty().bind(dataModel.visibleValueAmountProperty.divide(2)); //those at the sides;
        scrollbar.setBlockIncrement(1) ; // those between the sides and the thumb
    }




    private void UpdateXPlot(){
        double width = lineChart.getWidth();
        dataModel.updateXWidth(width);
    }

    @Override
    public void setAccelerationData(AcceleratorData data){
        dataModel.setAcceleratorData(data);
    }

    public void clear(){

    }

    @Override
    public void setPosition(double position){
        dataModel.moveToPosition( position);
    }


}
