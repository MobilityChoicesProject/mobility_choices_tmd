package experiments.classificationEvaluation;

import at.fhv.transportdetector.trackingtypes.TransportType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Johannes on 26.07.2017.
 */
public class ConfusionMatrix {

  private String[] classes;


  public void initClasses(String[] classes) {
    this.classes = classes;
  }

  public void initClasses(TransportType[] classes) {
    String[] arrayList = new String[classes.length];
    for (int i = 0; i < classes.length; i++) {
      arrayList[i] = classes[i].name();
    }
    this.classes = arrayList;
  }

  public void sortAndInit(List<TransportType> classes) {
    List<String> arrayList = new ArrayList<>();

    for (TransportType transportType:classes) {
      arrayList.add(transportType.name());
    }

    Collections.sort(arrayList, String.CASE_INSENSITIVE_ORDER);

    String[] array = new String[classes.size()];
    for (int i = 0; i < classes.size(); i++) {
      array[i] = arrayList.get(i);
    }
    this.classes = array;
  }

  public String calculate(List<? extends ClassificationEvaluationResult> classificationEvaluationResults) {

    int length = classes.length;
    int[][] values = new int[length][length];

    int maxValue = 0;

    for (ClassificationEvaluationResult classificationEvaluationResult : classificationEvaluationResults) {

      int row = getRow(classificationEvaluationResult.getActual().name());
      int column = getRow(classificationEvaluationResult.getClassifiedAs().name());

      int value = values[row][column];
      value++;
      values[row][column] = value;

      if (value > maxValue) {
        maxValue = value;
      }

    }

    int maxLength = (maxValue + "").length();
    int padding = maxLength + 2;

    StringBuilder confusionMatrix = new StringBuilder();

    char letterIndex = 'a';
    for (String aClass : classes) {

      String s = padLeft(letterIndex + "", padding);
      confusionMatrix.append(s);
      letterIndex++;
    }
    confusionMatrix.append("   <-- classified as" + System.lineSeparator());

    letterIndex = 'a';
    for (int row = 0; row < classes.length; row++) {
      for (int column = 0; column < classes.length; column++) {
        int value = values[row][column];
        confusionMatrix.append(padLeft(value + "", padding));
      }

      confusionMatrix.append(" |   ");
      confusionMatrix.append(letterIndex + " = ");
      String aClass = classes[row];
      confusionMatrix.append(aClass + System.lineSeparator());
      letterIndex++;
    }

    return confusionMatrix.toString();


  }

  public static String padLeft(String s, int n) {
    return String.format("%1$" + n + "s", s);
  }

  public int getRow(String classStr) {
    int index = 0;
    for (String aClass : classes) {
      if (aClass.equals(classStr)) {
        return index;
      }
      index++;
    }
    throw new IllegalArgumentException("Class not available:"+classStr);
  }


}
