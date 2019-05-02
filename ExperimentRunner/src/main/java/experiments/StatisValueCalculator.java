package experiments;

import at.fhv.transportdetector.trackingtypes.TransportType;
import experiments.classificationEvaluation.ClassificationEvaluationResult;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Johannes on 11.07.2017.
 */
public class StatisValueCalculator {

  DecimalFormat df = new DecimalFormat("#.###");

  public String calculate(List<? extends ClassificationEvaluationResult> results) {

    TransportType[] values = TransportType.values();

    List<String> valuesStr= new ArrayList<>(values.length);
    for (TransportType value : values) {
      if(value == TransportType.WALK){
        continue;
      }
      valuesStr.add(value.name());
    }
    Collections.sort(valuesStr,String.CASE_INSENSITIVE_ORDER);

    int size = results.size();
    List<StatisticValue> statisticValues = new ArrayList<>();
    for (String transportType : valuesStr) {
      statisticValues.add(new StatisticValue(transportType));
    }

    for (ClassificationEvaluationResult result : results) {

      for (StatisticValue  statisticValue: statisticValues) {

        if (statisticValue.getLabel().equals(result.getActual().name()) ) {
          statisticValue.incrementActualClassCounter();
          if (result.getActual() == result.getClassifiedAs()) {
            statisticValue.incrementTruePositives();
          } else {
            statisticValue.incrementFalseNegatives();
          }
        } else {
          if ( statisticValue.getLabel().equals(result.getClassifiedAs().name())) {
            statisticValue.incrementFalsePositives();
          } else {
            statisticValue.incrementTrueNegative();
          }
        }
      }
    }

    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append(padRight("",16));
    stringBuilder.append(padRight("Precision", 12));
    stringBuilder.append(padRight("Recall", 12));
    stringBuilder.append(padRight("F-Measure", 12));
    stringBuilder.append(padRight("class", 12));
    stringBuilder.append(System.lineSeparator());


    double wPrecision = 0;
    double wRecall = 0;
    double wFMeasure = 0;

    for (StatisticValue statisticValue : statisticValues) {
;
      double recall = statisticValue.calcRecall();
      double fMeasure = statisticValue.calcFMeasure();
      double precision = statisticValue.calcPrecision();
      stringBuilder.append(padRight("",16));
      stringBuilder.append(padRight(df.format(precision) + "", 12));
      stringBuilder.append(padRight(df.format(recall) + "", 12));
      stringBuilder.append(padRight(df.format(fMeasure) + "", 12));
      stringBuilder.append(padRight(statisticValue.getLabel(), 12));
      stringBuilder.append(System.lineSeparator());


      double counter = statisticValue.getCounter();
      double weight = counter/size;

      wPrecision += weight*precision;
      wRecall += weight*recall;
      wFMeasure += weight*fMeasure;
    }

    stringBuilder.append(padRight("Weighted Avg.",16));
    stringBuilder.append(padRight(df.format(wPrecision) + "", 12));
    stringBuilder.append(padRight(df.format(wRecall) + "", 12));
    stringBuilder.append(padRight(df.format(wFMeasure) + "", 12));

    return stringBuilder.toString();

  }

  public static String padRight(String s, int n) {
    return String.format("%1$-" + n + "s", s);
  }

}
