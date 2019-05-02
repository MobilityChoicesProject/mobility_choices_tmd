package at.fhv.transportClassifier.common;

import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johannes on 26.06.2017.
 */
public class TrackingIdNamePairFileReaderHelper {

  public static List<TrackingIdNamePair> load(String path) {

    Gson gson = new Gson();

    List<TrackingIdNamePair> trackingIdNamePairList = null;
    try (FileReader reader = new FileReader(path)) {

      TrackingIdNamePair[] trackingIdNamePairs = gson.fromJson(reader, TrackingIdNamePair[].class);
      trackingIdNamePairList = new ArrayList<>(trackingIdNamePairs.length);
      for (TrackingIdNamePair trackingIdNamePair : trackingIdNamePairs) {
        trackingIdNamePairList.add(trackingIdNamePair);
      }


    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return trackingIdNamePairList;
  }
}