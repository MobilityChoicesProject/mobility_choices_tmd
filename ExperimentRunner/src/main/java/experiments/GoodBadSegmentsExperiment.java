package experiments;

import at.fhv.filters.SameSequelTransportModeMergeFilter;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.TrackingSegmentBag;
import at.fhv.transportdetector.trackingtypes.segmenttypes.TrackingSegment;
import helper.ManualyEditedHelper;
import java.util.Iterator;

/**
 * Created by Johannes on 22.06.2017.
 */
public class GoodBadSegmentsExperiment {




  public void execute(Iterator<Tracking> trackingIterator){


    int goodTrackings= 0;
    int trackings=0;

    int goodBusSegment = 0;
    int goodTrainSegment= 0;
    int goodCarSegment= 0;
    int goodWalkingSegment= 0;
    int goodBikeSegment= 0;
    int goodOtherSegment= 0;

    int busSegment= 0;
    int trainSegment= 0;
    int carSegment= 0;
    int walkingSegment= 0;
    int bikeSegment= 0;
    int otherSegment= 0;

    SameSequelTransportModeMergeFilter sameSequelTransportModeMergeFilter = new SameSequelTransportModeMergeFilter();

    while (trackingIterator.hasNext()) {

      trackings++;
      Tracking tracking = trackingIterator.next();
      tracking=sameSequelTransportModeMergeFilter.filter(tracking);

      TrackingSegmentBag trackingSegmentBagVersion0 = tracking.getTrackingSegmentBagWithVersion(0);

      for (TrackingSegment trackingSegment : trackingSegmentBagVersion0.getSegments()) {

        switch (trackingSegment.getTransportType()){
          case BUS:{
            busSegment++;
            break;
          }
          case CAR:{
            carSegment++;
            break;
          }
          case BIKE:{
            bikeSegment++;
            break;
          }case WALK:{
            walkingSegment++;
            break;
          }case OTHER:{
            otherSegment++;
            break;
          }case TRAIN:{
            trainSegment++;
            break;
          }
        }

      }


      if (ManualyEditedHelper.isManuallyEdited(tracking.getTrackingInfos())) {
        goodTrackings++;

        for (TrackingSegment trackingSegment : trackingSegmentBagVersion0.getSegments()) {

          switch (trackingSegment.getTransportType()){
            case BUS:{
              goodBusSegment++;
              break;
            }
            case CAR:{
              goodCarSegment++;
              break;
            }
            case BIKE:{
              goodBikeSegment++;
              break;
            }case WALK:{
              goodWalkingSegment++;
              break;
            }case OTHER:{
              goodOtherSegment++;
              break;
            }case TRAIN:{
              goodTrainSegment++;
              break;
            }
          }

        }
      }


    }

    System.out.println("good tracking: "+goodTrackings);
    System.out.println("trackings: "+trackings);

    System.out.println("good bus segment: "+goodBusSegment);
    System.out.println("good car segment: "+goodCarSegment);
    System.out.println("good walking segment: "+goodWalkingSegment);
    System.out.println("good other segment: "+goodOtherSegment);
    System.out.println("good train segment: "+goodTrainSegment);
    System.out.println("good bike segment: "+goodBikeSegment);

    System.out.println("bus segment: "+busSegment);
    System.out.println("car segment: "+carSegment);
    System.out.println("walking segment: "+walkingSegment);
    System.out.println("other segment: "+otherSegment);
    System.out.println("train segment: "+trainSegment);
    System.out.println("bike segment: "+bikeSegment);


  }






}
