package at.fhv.tmd.segmentClassification.classifier;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.converters.Loader;

/**
 * Created by Johannes on 21.06.2017.
 */
public class ArfFileWrapper {


  private List<String> attributes = new ArrayList<>();
  private String labelName;
  private List<String> labelValues;

  private List<List<String>> data = new ArrayList<>();

  public void addStringAttribute(String name){
    name = name +"    "+"STRING ";
    attributes.add(name);
  }

  public void addNumericAttribute(String name){
    name = name+"  "+"NUMERIC ";
    attributes.add(name);
  }

  public void setLabelAttribute(String name, List<String> types){
    this.labelName= name;
    this.labelValues = types;
  }

  public void addData(List<String> dataRow){
    data.add(dataRow);
  }

  public Instances getDataSet(String name) {

    String arfFile = getFileAsText(name);

    InputStream stream = new ByteArrayInputStream(arfFile.getBytes(StandardCharsets.UTF_8));

    Instances dataSet = null;
    try {
      DataSource dataSource = new DataSource(stream);
      Loader loader = dataSource.getLoader();
      dataSet = loader.getDataSet();

    }catch (Exception ex){
      throw new ArfFileException("There was an exception during the creation of the arfFile",ex);
    }
    dataSet.setClassIndex(dataSet.numAttributes()-1);
    return dataSet;
  }



  public String getFileAsText(String fileName){
    String ls = System.lineSeparator();

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("@RELATION " + fileName + ls);

    for (String attribute : attributes) {
      stringBuilder
          .append("@ATTRIBUTE " + attribute +ls);
    }
    stringBuilder.append("@ATTRIBUTE " + labelName + "        {");

    labelValues.sort((o1, o2) -> o1.charAt(0) -o2.charAt(0));
    int size = labelValues.size();
    int index = 0;
    for (String labelValue : labelValues) {
      stringBuilder.append(labelValue);
      index++;

      if (index < size) {
        stringBuilder.append(", ");
      }
    }
    stringBuilder.append("}"+ls);
    stringBuilder.append(ls);
    stringBuilder.append("@data"+ls);



    for (List<String> dataRow : data) {
      size = dataRow.size();
      index = 0;
      for (String attributeValue : dataRow) {
        stringBuilder.append(attributeValue);
        index++;

        if (index < size) {
          stringBuilder.append(", ");
        }

      }
      stringBuilder.append(ls);

    }


    String arfFile = stringBuilder.toString();
    return arfFile;
  }


}
