package helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Johannes on 29.07.2017.
 */
public class StringSavingHelper {



  public static void save(String pathStr, byte[] data) throws IOException {

    Path path = Paths.get(pathStr);

    Path parent = path.getParent();

    File file = parent.toFile();
    if(!file.exists()){
      file.mkdirs();
    }

     Files.write(path,data);

  }



}
