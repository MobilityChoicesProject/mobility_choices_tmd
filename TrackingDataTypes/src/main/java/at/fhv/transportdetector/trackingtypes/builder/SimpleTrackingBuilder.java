package at.fhv.transportdetector.trackingtypes.builder;

import at.fhv.transportdetector.trackingtypes.AcceleratorState;
import at.fhv.transportdetector.trackingtypes.DisplayStateChangedEvent;
import at.fhv.transportdetector.trackingtypes.DisplayStateChangedType;
import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.TrackingInfo;
import at.fhv.transportdetector.trackingtypes.TrackingSegmentBag;
import at.fhv.transportdetector.trackingtypes.TransportType;
import at.fhv.transportdetector.trackingtypes.segmenttypes.TrackingSegment;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Johannes on 07.02.2017.
 */
public class SimpleTrackingBuilder {
    protected builderState state = builderState.init;
    protected List<TrackingSegment> trackingSegments = new ArrayList<>();
    protected List<IExtendedGpsPoint> gpsPoints = new ArrayList<>();
    protected List<AcceleratorState> acceleratorStates = new ArrayList<>();
    protected List<DisplayStateChangedEvent> displayStateChangedEvents = new ArrayList<>();
    protected List<TrackingInfo> trackingInfos = new ArrayList<>();
    protected List<TempSegment> tempSegments = new ArrayList<>();
    protected LocalDateTime startTimestamp = null;

    protected Map<Integer,SimpleTrackingSegmentBag> trackingSegmentBagDictionary = new TreeMap<>();
    private LocalDateTime endtimestamp = null;

    protected void checkAddGpsPointState(){
        if(!(state == builderState.init || state == builderState.ready)){
            throw new IllegalStateException("SimpleTrackingBuilder is in a invalid state ('"+state.name()+"') to addGpsPoints");
        }
    }

    protected void checkAddTrackingInfo(){
        if(!(state == builderState.init || state == builderState.ready)){
            throw new IllegalStateException("SimpleTrackingBuilder is in a invalid state ('"+state.name()+"') to addTrackingInfo");
        }
    }
    protected void checkAcceleratorState(){
        if(!(state == builderState.init || state == builderState.ready)){
            throw new IllegalStateException("SimpleTrackingBuilder is in a invalid state ('"+state.name()+"') to addAcceleratorState");
        }
    }
    protected void checkAddDisplayStateChangedEvent(){
        if(!(state == builderState.init || state == builderState.ready)){
            throw new IllegalStateException("SimpleTrackingBuilder is in a invalid state ('"+state.name()+"') to addDisplayStateChangedEvent");
        }
    }

    public void addTrackingSegment(LocalDateTime startTime, LocalDateTime endTime, TransportType transportType,int version){
        tempSegments.add(new TempSegment(startTime,endTime,transportType,version));

    }


    public void addGpsPoint(double latitude, double longitude,Double altitude,Double accuracy,Double speed, LocalDateTime dateTime,LocalDateTime systemSavingTime){
        checkAddGpsPointState();
        onAddGpsPoint(latitude,longitude,altitude,accuracy,speed,dateTime,systemSavingTime);
    }

    public void addTrackingInfo(String trackingInfo, String value){
        checkAddTrackingInfo();
        onAddTrackingInfo(trackingInfo,value);

    }

    public void addAcceleratorState(LocalDateTime time,double xAcceleration,double yAcceleration, double zAcceleration){
        checkAcceleratorState();
        onAddAcceleratorState(time,xAcceleration,yAcceleration,zAcceleration);
    }

    public void addDisplayStateChangedEvent(LocalDateTime time, DisplayStateChangedType displayStateChangedType){
        checkAddDisplayStateChangedEvent();
        onAddDisplayStateChangedEvent(time,displayStateChangedType);
    }

    public void setStartTimestamp(LocalDateTime localDateTime){
        startTimestamp = localDateTime;
        checkForReadyAndChange();
    }

    public void setEndTimestamp(LocalDateTime localDateTime){
         endtimestamp = localDateTime;
        checkForReadyAndChange();
    }

    protected void checkForReadyAndChange(){
        if(state != builderState.init){
            return;
        }

        if(startTimestamp != null &&endtimestamp != null&& gpsPoints.size() != 0){
            changeState(builderState.ready);
        }
    }


    protected void onAddTrackingInfo(String infoName,String infoValue){
        this.trackingInfos.add(new SimpleTrackingInfo(infoName, infoValue));
    }

    protected void onAddGpsPoint( double latitude, double longitude,Double altitude,Double accuracy,Double speed, LocalDateTime sensorTime, LocalDateTime systemTime){
        gpsPoints.add(new SimpleGpsPoint(sensorTime,latitude,longitude,altitude,accuracy,speed,systemTime));
        checkForReadyAndChange();
    }

    protected void onAddAcceleratorState(LocalDateTime time,double xAcceleration,double yAcceleration, double zAcceleration){
        acceleratorStates.add(new SimpleAcceleratorState(time,xAcceleration,yAcceleration,zAcceleration));

    }

    protected void onAddDisplayStateChangedEvent(LocalDateTime time, DisplayStateChangedType displayStateChangedType){
        this.displayStateChangedEvents.add(new SimpleDisplayStateChangedEvent(displayStateChangedType,time));
    }


    protected void changeState(builderState state){
      this.state = state;
    }




    public Tracking build(){
        if(state != builderState.ready ){
            throw new IllegalStateException("SimpleTrackingBuilder is in a invalid state ('"+state.name()+"') to build");
        }

        SimpleTracking tracking = null;

        gpsPoints.sort((o1, o2) -> {
            Duration dur =Duration.between(o2.getSensorTime(),o1.getSensorTime());
            return (int) dur.toMillis();
        });

        acceleratorStates.sort((o1, o2) -> {
            Duration dur =Duration.between(o2.getTime(),o1.getTime());
            return (int) dur.toMillis();
        });

        displayStateChangedEvents.sort((o1, o2) -> {
            Duration dur =Duration.between(o2.getTime(),o1.getTime());
            return (int) dur.toMillis();
        });




        if(displayStateChangedEvents.size() != 0 || acceleratorStates.size() != 0){
            SimpleAllTracking simpleAllTracking  = new SimpleAllTracking();


            for (TempSegment tempSegment : tempSegments) {

                SimpleAllTrackingSegment simpleAllTrackingSegment = new SimpleAllTrackingSegment();
                simpleAllTrackingSegment.setAllAcceleratorStates(acceleratorStates);
                simpleAllTrackingSegment.setAllDisplayStateChangedEvents(displayStateChangedEvents);
                simpleAllTrackingSegment.setAllGpsPoints(gpsPoints);
                setAndAddTrackingSegment(simpleAllTracking, tempSegment, simpleAllTrackingSegment);
            }


            simpleAllTracking.addAcceleratorSates(acceleratorStates);
            simpleAllTracking.addDisplayStateChangeEvents(displayStateChangedEvents);
            tracking = simpleAllTracking;
        }else{
            tracking  = new SimpleTracking();
            for (TempSegment tempSegment : tempSegments) {
                SimpleTrackingSegment simpleTrackingSegment = new SimpleTrackingSegment();
                simpleTrackingSegment.setAllGpsPoints(gpsPoints);
                setAndAddTrackingSegment(tracking, tempSegment, simpleTrackingSegment);

            }
        }

        tracking.addGpsPoints(gpsPoints);
        tracking.addTrackingInfos(trackingInfos);
        tracking.setStartTimestamp(startTimestamp);
        tracking.setEndTimestamp(endtimestamp);

        tracking.getTrackingSegmentBags().sort((o1, o2) -> o2.getVersion() -o1.getVersion());
        for (TrackingSegmentBag trackingSegmentBag : tracking.getTrackingSegmentBags()) {
            trackingSegmentBag.getSegments().sort((o1, o2) -> {
                Duration dur =Duration.between(o2.getStartTime(),o1.getStartTime());
                return (int) dur.toMillis();
            });

        }
        changeState(builderState.builded);





        return tracking;
    }

    private void setAndAddTrackingSegment(SimpleTracking tracking, TempSegment tempSegment, SimpleTrackingSegment simpleAllTrackingSegment) {
        simpleAllTrackingSegment.setStartTime(tempSegment.getStart());
        simpleAllTrackingSegment.setEndTime(tempSegment.getEnd());
        simpleAllTrackingSegment.setTransportType(tempSegment.getTransportType());

        SimpleTrackingSegmentBag trackingSegmentBag;
        if(trackingSegmentBagDictionary.containsKey(tempSegment.getVersion())){
            trackingSegmentBag = trackingSegmentBagDictionary.get(tempSegment.getVersion());
        }else{
            trackingSegmentBag = new SimpleTrackingSegmentBag();
            trackingSegmentBag.setVersion(tempSegment.getVersion());
            trackingSegmentBagDictionary.put(tempSegment.getVersion(),trackingSegmentBag);
            tracking.addTrackingSegmentBag(trackingSegmentBag);
        }
        trackingSegmentBag.addSegment(simpleAllTrackingSegment);
    }


    private enum builderState{
        init,
        ready,
        builded
    }

    private static class TempSegment{
        private LocalDateTime start;
        private LocalDateTime end;
        private TransportType transportType;
        private int version;

        public LocalDateTime getStart() {
            return start;
        }

        public void setStart(LocalDateTime start) {
            this.start = start;
        }

        public LocalDateTime getEnd() {
            return end;
        }

        public void setEnd(LocalDateTime end) {
            this.end = end;
        }

        public TransportType getTransportType() {
            return transportType;
        }

        public void setTransportType(TransportType transportType) {
            this.transportType = transportType;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public TempSegment(LocalDateTime start, LocalDateTime end, TransportType transportType, int version) {
            this.start = start;
            this.end = end;
            this.transportType = transportType;
            this.version = version;
        }
    }
}
