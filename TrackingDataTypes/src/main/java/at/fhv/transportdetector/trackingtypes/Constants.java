package at.fhv.transportdetector.trackingtypes;

/**
 * Created by Johannes on 14.02.2017.
 */
public class Constants {

    public static final String FH_GPS_LOGGER_VERSION ="FILE_VERSION";
    public static final String FH_GPS_LOGGER_VERSION_0 ="0";
    public static final String FH_GPS_LOGGER_VERSION_1 ="1";
    public static final String FH_GPS_LOGGER_VERSION_2 ="2"; // added the system time to the gps sensor event
    public static final String FH_GPS_LOGGER_VERSION_4 ="4";

    public static final String FILENAME = "File_Name";
    public static final String SCHEFFKNECHT_SEGMENT = "SCHEFFKNECHT_SEGMENT";
    public static final String PHONE_ID = "PHONE_ID";
    public static final String PHONE_TYPE = "PHONE_TYPE";



    public static final String ORIGIN = "ORIGIN";
    public static final String ORIGIN_FHGPSLOGGER = "FHGPSLOGGER";
    public static final String ORIGIN_SCHEFFKECHT = "SCHEFFKNECHT";
    public static final String ORIGIN_MobiTracker = "MOBI_TRACKER";
    public static final String ORIGIN_MobilityChoices = "MOBILITY_CHOICES";

    public static final String ORIGIN_SCHEFFKNECHT_MYTRACKS = "SCHEFFKNECHT_MYTRACKS";

    public static final String ORIGIN_SCHEFFKNECHT_GPX = "SCHEFFKNECHT_GPX";
    public static String ManualyEdited = "manuallyEdited";
    public static String dataFolder = "dataFolder";
    public static String testSet = "testSet.json";
    public static String trainingSet = "trainingSet.json";

    public static String trainingSetWithIds = "trainingSetWithIds";
    public static String testSetWithIds = "testSetWithIds";
    public static String trainingSetWithoutIds = "trainingSetWithoutIds";
    public static String testSetWithoutIds = "testSetWithoutIds";
    public static String trainingSetWithIds_withoutOther = "trainingSetWithIds_withoutOther";
    public static String testSetWithIds_withoutOther = "testSetWithIds_withoutOther";
    public static String trainingSetWithoutIds_withoutOther = "trainingSetWithoutIds_withoutOther";
    public static String testSetWithoutIds_withoutOther = "testSetWithoutIds_withoutOther";


    public static String validTrackingIdNamePairFileName = "validTrackingIdNamePair.json";


    public static String dataFolder_cache ="dataFolder_cache";
    public static String trackingExportFolder ="exportFolder";

    public static final String dataFolder_tmdOverlapping="dataFolder_tmdOverlapping";

}
