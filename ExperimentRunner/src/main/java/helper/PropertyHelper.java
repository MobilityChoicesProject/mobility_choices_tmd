package helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Johannes on 21.06.2017.
 */
public class PropertyHelper {


  static Properties properties =null;

  public static String  getValue(String key){
    if(properties == null){
      initProperties();
    }
    return  properties.getProperty(key);
  }

  public static double getDouble(String key){
    String value = getValue(key);
    return Double.parseDouble(value);
  }

  public static void setProperties(String path){

  }

  private static void initProperties() {
    properties= new Properties();

    InputStream in = PropertyHelper.class.getClassLoader().getResourceAsStream(
        "settings.properties");
    try {
      properties.load(in);
      in.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }


  public static int getInt(String key) {
    String value = getValue(key);
    return Integer.parseInt(value);

  }
}
