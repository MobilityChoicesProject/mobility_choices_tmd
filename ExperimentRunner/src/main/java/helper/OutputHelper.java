package helper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Johannes on 25.06.2017.
 */
public class OutputHelper {


  private OutputHelper(){

  }

  private BufferedWriter writer;

  public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("YYYY_MM_dd HH:mm:ss");
  public static OutputHelper getOutputHelper(String filename) throws IOException {

    String outputFolder = PropertyHelper.getValue("outputFolder");

    File folder = new File(outputFolder);
    folder.mkdirs();

    File file = new File(outputFolder,filename);

    if(file.exists()){
      boolean exists = file.exists();
      if(exists){
        boolean delete = file.delete();
        if(!delete){
          throw new IOException("Could not delete old outputFile:'"+file.getPath()+"'");
        }
      }

    }
    boolean newFile = file.createNewFile();

    BufferedWriter out = new BufferedWriter(new FileWriter(file));

    String nowDateTimeStr = LocalDateTime.now().format(dateTimeFormatter);
    out.write("--------------- -- - "+filename+ " - -- - "+ nowDateTimeStr);
    out.newLine();
    out.write("-----------------------------------------------------------------------");
    out.newLine();

    OutputHelper outputHelper = new OutputHelper();
    outputHelper.writer =out;
    return outputHelper;
  }



  public void write(String text) throws IOException {
    writer.write(text);
    System.out.print(text);
  }

  public void writeLine(String text) throws IOException {
    writer.write(text);
    writer.newLine();
    System.out.print(text);
    System.out.println();
  }


  public void saveAndClose() throws IOException {
    writer.close();
  }





}
