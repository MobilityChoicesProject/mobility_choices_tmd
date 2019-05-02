package experiments.classificationEvaluation;

import at.fhv.transportdetector.trackingtypes.TransportType;

/**
 * Created by Johannes on 26.07.2017.
 */
public class MarginThresholdCounter {


  private int totalcar;
  private int totalbus;
  private int totaltrain;
  private int totalbike;
  private int totalother;
  private int totalwalking;


  // actual can be lower than total, because actual only counts those, who are valid for the condition
  private int actualcar;
  private int actualbus;
  private int actualtrain;
  private int actualbike;
  private int actualother;
  private int actualwalking;

  private int predictedCar;
  private int predictedBus;
  private int predictedTrain;
  private int predictedBike;
  private int predictedOther;
  private int predictedWalking;

  public void incrementCorrect(TransportType transportType){
    switch (transportType){
      case WALK:{
        predictedWalking++;
        break;
      }
      case CAR:
        predictedCar++;
        break;

      case BIKE:{
        predictedBike++;
        break;
      }

      case BUS:
        predictedBus++;
        break;

      case TRAIN:
        predictedTrain++;
        break;

      case OTHER:
        predictedOther++;
        break;
    }
  }

  public void incrementActual(TransportType transportType){
    switch (transportType){
      case WALK:{
        actualwalking++;
        break;
      }
      case CAR:
        actualcar++;
        break;

      case BIKE:{
        actualbike++;
        break;
      }

      case BUS:
        actualbus++;
        break;

      case TRAIN:
        actualtrain++;
        break;

      case OTHER:
        actualother++;
        break;
    }
  }

  public void incrementTotal(TransportType transportType){
    switch (transportType){
      case WALK:{
        totalwalking++;
        break;
      }
      case CAR:
        totalcar++;
        break;

      case BIKE:{
        totalbike++;
        break;
      }

      case BUS:
        totalbus++;
        break;

      case TRAIN:
        totaltrain++;
        break;

      case OTHER:
        totalother++;
        break;
    }
  }

  public int getActual(TransportType transportType){

    if(transportType != null){
      switch (transportType) {
        case CAR:
          return actualcar;
        case BIKE:
          return actualbike;
        case BUS:
          return actualbus;
        case TRAIN:
          return actualtrain;
        case WALK:
          return actualwalking;
        case OTHER:
          return actualother;
      }
    }
    return actualcar+actualbike+actualbus+actualtrain+actualwalking+actualother;

  }

  public double getAccuracy(TransportType transportType){

    if(transportType != null){
      switch (transportType){
        case WALK:{
          return predictedWalking/(1.0*actualwalking);
        }
        case CAR:
          return predictedCar/(1.0*actualcar);
        case BIKE:{
          return predictedBike/(1.0*actualbike);
        }
        case BUS:
          return predictedBus/(1.0*actualbus);
        case TRAIN:
          return predictedTrain/(1.0*actualtrain);
        case OTHER:
          return predictedOther/(1.0*actualother);
      }
    }

    int predictedSum  = predictedWalking+predictedCar+predictedBike+predictedBus+predictedTrain+predictedOther;
    int actualSum = actualwalking+actualcar+actualbike+actualbus+actualtrain+actualother;

    return (predictedSum)/(1.0*actualSum);
  }

  public double getActualPercentageOfTotal(TransportType transportType){
    if(transportType != null){
      switch (transportType){
        case WALK:{
          return (1.0*actualwalking)/totalwalking;
        }
        case CAR:
          return  (1.0*actualcar)/totalcar;
        case BIKE:{
          return  (1.0*actualbike)/totalbike;
        }
        case BUS:
          return (1.0*actualbus)/totalbus;
        case TRAIN:
          return  (1.0*actualtrain)/totaltrain;
        case OTHER:
          return  (1.0*actualother)/totalother;
      }
    }
    int totalSum  = totalwalking+totalcar+totalbike+totalbus+totaltrain+totalother;
    int actualSum = actualwalking+actualcar+actualbike+actualbus+actualtrain+actualother;

    return (actualSum)/(1.0*totalSum);
  }


  public double getTotalAccuracy(){
    return getAccuracy(null);
  }




}
