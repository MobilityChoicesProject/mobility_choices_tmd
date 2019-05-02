package at.fhv.transportClassifier.common.configSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Johannes on 14.08.2017.
 */
public class ConfigServiceDefaultCache extends HashMap<String, ConfigSetting> {


    private List<ConfigGroup> configGroups = new ArrayList<>();

    // position jump speed Filter
    public static String PositionJumpSpeedThesholdInKmH = "PositionJumpSpeedThreshold";

    //signal shortage finder filter
    public static String signalShortageThresholdInSeconds = "signalShortageThresholdInSeconds";
    public static String minDurationBetweenCSignalShortageBelow20KmH = "minDurationBetweenCSignalShortageBelow20KmH";
    public static String minDurationBetweenCSignalShortageAbove20KmH = "minDurationBetweenCSignalShortageAbove20KmH";
    public static String signalShortageFinder_speedThreshold = "signalShortageFinder_speedThreshold";
    public static String walkingNonWalkingSpeedThreshold = "walkingNonWalkingSpeedThreshold";


    //segmentation density cluster finder
    public static String distanceThreshold = "distanceThreshold";
    public static String distanceThresholdFactor = "distanceThresholdFactor";
    public static String pointsRadius = "pointsRadius";
    public static String frameSize = "frameSize";
    public static String frameSizeThresholdFactor = "frameSizeThresholdFactor";
    public static String minimalPointsBetweenCluster = "minimalPointsBetweenCluster";
    public static String minimalClusterDuration = "minimalClusterDuration";


    // Walking Splitter
    public static String minWalkingThreshold = "minWalkingThreshold";
    public static String minNonWalkingThreshold = "minNonWalkingThreshold";
    public static String walkingSpeedThrehold = "walkingSpeedThrehold";


    public static String kernelSmoother_sigma = "kernelSmoother_sigma";
    public static String kernelSmoother_calcRangeFactor = "kernelSmoother_calcRangeFactor";


    public static String pp_toShortVehicle_bike = "pp_toShortVehicle_bike";
    public static String pp_toShortVehicle_bus = "pp_toShortVehicle_bus";
    public static String pp_toShortVehicle_car = "pp_toShortVehicle_car";
    public static String pp_toShortVehicle_train = "pp_toShortVehicle_train";

    public static String pp_signalShortage_train_distance = "pp_signalShortage_train_distance";

    // Backgroundgeolocation
    public static final String pp_backgroundGeolocation_iterations = "pp_backgroundGeolocation_iterations";

    // vehicle crossover probabilities (zheng)
    public static final String pp_vehicleCrossoverProb_threshold = "pp_vehicleCrossoverProb_threshold";
    private static final int pp_backgroundGeolocation_defaultIterations = 4;

    // car to...
    public static final String pp_vehicleCrossoverProb_car_car = "pp_vehicleCrossoverProb_car_car";
    public static final String pp_vehicleCrossoverProb_car_bike = "pp_vehicleCrossoverProb_car_bike";
    public static final String pp_vehicleCrossoverProb_car_bus = "pp_vehicleCrossoverProb_car_bus";
    public static final String pp_vehicleCrossoverProb_car_train = "pp_vehicleCrossoverProb_car_train";
    public static final String pp_vehicleCrossoverProb_car_other = "pp_vehicleCrossoverProb_car_other";
    public static final String pp_vehicleCrossoverProb_car_stationary = "pp_vehicleCrossoverProb_car_stationary";

    // bike to...
    public static final String pp_vehicleCrossoverProb_bike_car = "pp_vehicleCrossoverProb_bike_car";
    public static final String pp_vehicleCrossoverProb_bike_bike = "pp_vehicleCrossoverProb_bike_bike";
    public static final String pp_vehicleCrossoverProb_bike_bus = "pp_vehicleCrossoverProb_bike_bus";
    public static final String pp_vehicleCrossoverProb_bike_train = "pp_vehicleCrossoverProb_bike_train";
    public static final String pp_vehicleCrossoverProb_bike_other = "pp_vehicleCrossoverProb_bike_other";
    public static final String pp_vehicleCrossoverProb_bike_stationary = "pp_vehicleCrossoverProb_bike_stationary";

    // bus to...
    public static final String pp_vehicleCrossoverProb_bus_car = "pp_vehicleCrossoverProb_bus_car";
    public static final String pp_vehicleCrossoverProb_bus_bike = "pp_vehicleCrossoverProb_bus_bike";
    public static final String pp_vehicleCrossoverProb_bus_bus = "pp_vehicleCrossoverProb_bus_bus";
    public static final String pp_vehicleCrossoverProb_bus_train = "pp_vehicleCrossoverProb_bus_train";
    public static final String pp_vehicleCrossoverProb_bus_other = "pp_vehicleCrossoverProb_bus_other";
    public static final String pp_vehicleCrossoverProb_bus_stationary = "pp_vehicleCrossoverProb_bus_stationary";

    // train to...
    public static final String pp_vehicleCrossoverProb_train_car = "pp_vehicleCrossoverProb_train_car";
    public static final String pp_vehicleCrossoverProb_train_bike = "pp_vehicleCrossoverProb_train_bike";
    public static final String pp_vehicleCrossoverProb_train_bus = "pp_vehicleCrossoverProb_train_bus";
    public static final String pp_vehicleCrossoverProb_train_train = "pp_vehicleCrossoverProb_train_train";
    public static final String pp_vehicleCrossoverProb_train_other = "pp_vehicleCrossoverProb_train_other";
    public static final String pp_vehicleCrossoverProb_train_stationary = "pp_vehicleCrossoverProb_train_stationary";

    // other to...
    public static final String pp_vehicleCrossoverProb_other_car = "pp_vehicleCrossoverProb_other_car";
    public static final String pp_vehicleCrossoverProb_other_bike = "pp_vehicleCrossoverProb_other_bike";
    public static final String pp_vehicleCrossoverProb_other_bus = "pp_vehicleCrossoverProb_other_bus";
    public static final String pp_vehicleCrossoverProb_other_train = "pp_vehicleCrossoverProb_other_train";
    public static final String pp_vehicleCrossoverProb_other_other = "pp_vehicleCrossoverProb_other_other";
    public static final String pp_vehicleCrossoverProb_other_stationary = "pp_vehicleCrossoverProb_other_stationary";

    // stationary to...
    public static final String pp_vehicleCrossoverProb_stationary_car = "pp_vehicleCrossoverProb_stationary_car";
    public static final String pp_vehicleCrossoverProb_stationary_bike = "pp_vehicleCrossoverProb_stationary_bike";
    public static final String pp_vehicleCrossoverProb_stationary_bus = "pp_vehicleCrossoverProb_stationary_bus";
    public static final String pp_vehicleCrossoverProb_stationary_train = "pp_vehicleCrossoverProb_stationary_train";
    public static final String pp_vehicleCrossoverProb_stationary_other = "pp_vehicleCrossoverProb_stationary_other";
    public static final String pp_vehicleCrossoverProb_stationary_stationary = "pp_vehicleCrossoverProb_stationary_stationary";

    private static final double pp_vehicleCrossoverProb_defaultProb = (double) 1 / 5; // 5 = number of possible changes per vehicle
    private static final double pp_vehicleCrossoverProb_defaultProbSameVehicle = 0.0;


    public List<ConfigGroup> getGroups() {
        return new ArrayList<>(configGroups);
    }


    public void initConfigGroups() {

        //position jump speed filter
        ConfigGroup positionJumpSpeedFilter = new ConfigGroup();
        positionJumpSpeedFilter.setName("(Pre Processing) Position Jump Speed Filter");
        positionJumpSpeedFilter.setDescription("The 'Position Jump Speed Filter' removes gps points, which can be reached by their neighbours only with speeds above a given speed threshold.");
        positionJumpSpeedFilter.addConfigSetting(new ConfigSetting(PositionJumpSpeedThesholdInKmH, "Speed Threshold", "Maximal speed in km/h before a point is removed", 300));
        configGroups.add(positionJumpSpeedFilter);


        //kernel smoother
        ConfigGroup kernelSmoother = new ConfigGroup();
        kernelSmoother.setName("(Pre Processing) Kernel Smoother");
        kernelSmoother.setDescription("Uses a gaussian kernel smoother to smooth the gps points. Not all points are used for the smoothing, but only those, which are inside a Range which is calculated by <Sigma> multiplied with <Calc Range Factor>. ");
        kernelSmoother.addConfigSetting(new ConfigSetting(kernelSmoother_sigma, "Sigma", "Sigma value in seconds of the gaussian kernel smoothing algorithm", 5));
        kernelSmoother.addConfigSetting(new ConfigSetting(kernelSmoother_calcRangeFactor, "Calc Range Factor", "Factor which is calculated with sigma, to define a range of gps points which are used to smooth a gps point. A smaller value makes the algorithm faster and but less precise", 4.417));
        configGroups.add(kernelSmoother);


        //signal shortage finder filter
        ConfigGroup signalShortageFinder = new ConfigGroup();
        signalShortageFinder.setName("(Segmentation) Signal Shortage Finder");
        signalShortageFinder.setDescription("The 'Signal Shortage Finder' is part of the segmentation process. It uses 3 phases.\n\n"
                + "In the first phase the algorithm is searching for signal shortages. If two gps points are seperated by a time highter than <signalShortageThreshold>, a signal shortage is found.\n\n "
                + "In phase two signal shortages are melted together, if there is not a long enough segment of gps points between them. The length of this minimal segment is measured in seconds and there are different min durations depending on the speed <speedThreshold> of the points. If the speed is above the <speedThreshold> the <aboveSpeedThresholdDuration> is used otherwise the <belowSpeedThresholdDuration>.\n\n"
                + "In the third phase the signal shortages are divided into stationary and moving signal shortages. If the speed between the two points of a signal shortage is below the <stationarySpeedThreshold>, the signal shortage is classiefied as stationary signal shortage. \n\n  ");

        signalShortageFinder.addConfigSetting(new ConfigSetting(signalShortageThresholdInSeconds, "signalShortageThreshold", "Duration threshold in seconds to detect signal shortages", 30));
        signalShortageFinder.addConfigSetting(new ConfigSetting(minDurationBetweenCSignalShortageBelow20KmH, "belowSpeedThresholdDuration", "This is the minimal duration in seconds a segment between two signal shortages has to have. Otherwise the signal shortages are going to be merged ", 30));
        signalShortageFinder.addConfigSetting(new ConfigSetting(minDurationBetweenCSignalShortageAbove20KmH, "aboveSpeedThresholdDuration", "This is the minimal duration in seconds a segment between two signal shortages has to have. Otherwise the signal shortages are going to be merged", 120));
        signalShortageFinder.addConfigSetting(new ConfigSetting(walkingNonWalkingSpeedThreshold, "speedThreshold", "This speed threshold (int km/h) decides, which speed threshold duration is used", 20));
        signalShortageFinder.addConfigSetting(new ConfigSetting(signalShortageFinder_speedThreshold, "stationarySpeedThreshold", "Minimal speed in km/h to distinguish between stationary signal shortages and moving signal shortages", 0.2));
        configGroups.add(signalShortageFinder);


        //segmentation density cluster finder
        ConfigGroup segmentationDensityClusterFinder = new ConfigGroup();
        segmentationDensityClusterFinder.setName("(Segmentation) Density Cluster Finder");
        segmentationDensityClusterFinder.setDescription("This algorithm consists of 5 parts: \n \n"
                + "1) Each gps point is iterated. For each gps point his <GPS cluster radius> previous points and his <GPS cluster radius> next points are checked. Those points are relevant, to decide if a point is a cluster point. If the maximal distance to one of this points is <Cluster Point Distance Threshold>, this indicates the iterated point is a cluster point. The ratio of the sum of all the points, which indicate a cluster point, and all relevant points, decides if the point is considered a cluster point. If the ratio is above the <Cluster Point Ratio Threshold>, the point is set as a cluster point.\n\n"
                + "2) During this part the cluster points are joined to form a cluster. A cluster starts, if <Cluster Frame Size Threshold> percent of the next <Cluster Frame Size> gps points are set as cluster point. All next gps points are part of the cluster,  until the just given criteria is not valid anymore\n\n"
                + "3) During this part clusters with not at least <Min Points Between Cluster> points between them, are merged together.\n\n"
                + "4) If a cluster is close to the edges of the track and there are not at least <Min Points Between Cluster> points between the start/ending of the track and the cluster, those points become part of the cluster \n\n"
                + "5) During this part all clusters which have a duration shorter than <Min Cluster Duration> are removed");

        segmentationDensityClusterFinder.addConfigSetting(new ConfigSetting(distanceThreshold, "Cluster Point Distance Threshold", "Distance threshold in meter. Is used to tell if a neighbour point indicates that a point is a cluster point. ", 5));
        segmentationDensityClusterFinder.addConfigSetting(new ConfigSetting(distanceThresholdFactor, "Cluster Point Ratio Threshold", "Tells in percent (0-1.0) how many of the neighbour points of a point have to indicate that the point is a cluster point", 0.8));
        segmentationDensityClusterFinder.addConfigSetting(new ConfigSetting(pointsRadius, "GPS cluster radius", "Sets the number of points which are seen as neighbour points and are used to decide if a point is a cluster point", 30));
        segmentationDensityClusterFinder.addConfigSetting(new ConfigSetting(frameSize, "Cluster Frame Size", "Number of points which are used to decide if a cluster started", 30));
        segmentationDensityClusterFinder.addConfigSetting(new ConfigSetting(frameSizeThresholdFactor, "Cluster Frame Size Threshold", "Percentage (0.0-1.0) of points, which have to be cluster points, so a cluster is detected", 0.66));
        segmentationDensityClusterFinder.addConfigSetting(new ConfigSetting(minimalPointsBetweenCluster, "Min Points Between Cluster", "Minimal number of points between two clusters. Otherwise the clusters are merged", 60));
        segmentationDensityClusterFinder.addConfigSetting(new ConfigSetting(minimalClusterDuration, "Min Cluster Duration", "Minimal duration in seconds of a cluster. Otherwise the cluster is removed", 60));
        configGroups.add(segmentationDensityClusterFinder);


        //segmentation Walking Non Walking
        ConfigGroup walkingSplitter = new ConfigGroup();
        walkingSplitter.setName("(Segmentation) Walking|Non-Walking");
        walkingSplitter.setDescription("Part of the segmentation phase. Consists of three parts \n \n"
                + "1) All points are classified as Walking and Non-Walking points depending if their speed is below or above <Walking Speed Threshold> \n \n "
                + "2) All walking segments which have a shorter duration than <Min Walk Duration> are merged with their neighbour non-walking segments to become non-walking segments. \n\n"
                + "3) All non-walking segments which have a shorter duration than <Min Non Walk Duration> are merged with their neighbour walking segments to become walking semgents");
        walkingSplitter.addConfigSetting(new ConfigSetting(walkingSpeedThrehold, "Walking Speed Threshold", "Minimal speed in km/h a gps point needs to be classified as non-walking ", 10));
        walkingSplitter.addConfigSetting(new ConfigSetting(minWalkingThreshold, "Min Walk Duration", "Minimal duration in seconds a walking segment needs. Otherwise the segment is merged with its neighbour segments", 60));
        walkingSplitter.addConfigSetting(new ConfigSetting(minNonWalkingThreshold, "Min Non Walk Duration", "Minimal duration in seconds a non-walking segment needs. Otherwise the non-walking segment is merged with its neighbour segments", 60));
        configGroups.add(walkingSplitter);


        // minimal length post processing
        ConfigGroup minimalLength = new ConfigGroup();
        minimalLength.setName("(Post Processing) Minimal Length");
        minimalLength.setDescription("During this post processing step the length of each segment is checked. If a segment with its corresponding transport type is to short, the segment is merged with its neighbours ");
        minimalLength.addConfigSetting(new ConfigSetting(pp_toShortVehicle_bike, "Min Bike Distance", "Minimal bike distance in km", 0.2));
        minimalLength.addConfigSetting(new ConfigSetting(pp_toShortVehicle_bus, "Min Bus Distance", "Minimal bus distance in km", 0.5));
        minimalLength.addConfigSetting(new ConfigSetting(pp_toShortVehicle_car, "Min Car Distance", "Minimal car distance in km", 0.5));
        minimalLength.addConfigSetting(new ConfigSetting(pp_toShortVehicle_train, "Min Train Distance", "Minimal train distance in km", 1));
        configGroups.add(minimalLength);

        // minimal length post processing
        ConfigGroup ppSignalShortageTrain = new ConfigGroup();
        ppSignalShortageTrain.setName("(Post Processing) Signal Shortage Close Rails");
        ppSignalShortageTrain.setDescription("This post processing step assigns a signal shortage segment a transport type depending on the distances of the edge points of the signal shortage and the the next rails. If "
                + "both distances are below <Rail Distance Threshold>, the segment is classified with 100% train. If one distance is below <Rail Distance Threshold> the segment is classified with 70% train, 20% bus and 10% car. ");
        ppSignalShortageTrain.addConfigSetting(new ConfigSetting(pp_signalShortage_train_distance, "Rail Distance Threshold", "Distance threshold in km", 0.1));
        configGroups.add(ppSignalShortageTrain);

        // Backgroundgeolocation
        ConfigGroup ppBackgroundGeolocation = new ConfigGroup();
        ppBackgroundGeolocation.setName("(Post Processing) Background Geolocation settings");
        ppBackgroundGeolocation.setDescription("The number of iterations, the backgroundGeolocation task should be executed.");
        ppBackgroundGeolocation.addConfigSetting(new ConfigSetting(pp_backgroundGeolocation_iterations, "Iterations", "Defines how often the backgroundGeolocation-task should be executed. More iterations mean a bigger impact of the backgroundGeolocation.", pp_backgroundGeolocation_defaultIterations));
        configGroups.add(ppBackgroundGeolocation);

        // Zheng vehicle crossover probabilities
        ConfigGroup ppCrossoverProbability = new ConfigGroup();
        ppCrossoverProbability.setName("(Post Processing) Vehicle Crossover Probabilities");
        ppCrossoverProbability.setDescription("The probabilities of transport modes of adjacent segments is adjusted if the probability of the most probable transport mode" +
                "of a segment is greater than or equal <Vehicle Crossover Threshold>. The probabilities of each transport mode are adjusted according to the settings below.");
        // threshold
        ppCrossoverProbability.addConfigSetting(new ConfigSetting(pp_vehicleCrossoverProb_threshold, "Vehicle Crossover Threshold", "Probability Threshold at which the Vehicle Crossover Filter becomes active", 0.6));
        // car to...
        ppCrossoverProbability.addConfigSetting(new ConfigSetting(pp_vehicleCrossoverProb_car_car, "Car to Car", "Crossover Probability from Car to Car", pp_vehicleCrossoverProb_defaultProbSameVehicle));
        ppCrossoverProbability.addConfigSetting(new ConfigSetting(pp_vehicleCrossoverProb_car_bike, "Car to Bike", "Crossover Probability from Car to Bike", pp_vehicleCrossoverProb_defaultProb));
        ppCrossoverProbability.addConfigSetting(new ConfigSetting(pp_vehicleCrossoverProb_car_bus, "Car to Bus", "Crossover Probability from Car to Bus", pp_vehicleCrossoverProb_defaultProb));
        ppCrossoverProbability.addConfigSetting(new ConfigSetting(pp_vehicleCrossoverProb_car_train, "Car to Train", "Crossover Probability from Car to Train", pp_vehicleCrossoverProb_defaultProb));
        ppCrossoverProbability.addConfigSetting(new ConfigSetting(pp_vehicleCrossoverProb_car_other, "Car to Other", "Crossover Probability from Car to Other", pp_vehicleCrossoverProb_defaultProb));
        ppCrossoverProbability.addConfigSetting(new ConfigSetting(pp_vehicleCrossoverProb_car_stationary, "Car to Stationary", "Crossover Probability from Car to Stationary", pp_vehicleCrossoverProb_defaultProb));
        // bike to...
        ppCrossoverProbability.addConfigSetting(new ConfigSetting(pp_vehicleCrossoverProb_bike_car, "Bike to Car", "Crossover Probability from Bike to Car", pp_vehicleCrossoverProb_defaultProb));
        ppCrossoverProbability.addConfigSetting(new ConfigSetting(pp_vehicleCrossoverProb_bike_bike, "Bike to Bike", "Crossover Probability from Bike to Bike", pp_vehicleCrossoverProb_defaultProbSameVehicle));
        ppCrossoverProbability.addConfigSetting(new ConfigSetting(pp_vehicleCrossoverProb_bike_bus, "Bike to Bus", "Crossover Probability from Bike to Bus", pp_vehicleCrossoverProb_defaultProb));
        ppCrossoverProbability.addConfigSetting(new ConfigSetting(pp_vehicleCrossoverProb_bike_train, "Bike to Train", "Crossover Probability from Bike to Train", pp_vehicleCrossoverProb_defaultProb));
        ppCrossoverProbability.addConfigSetting(new ConfigSetting(pp_vehicleCrossoverProb_bike_other, "Bike to Other", "Crossover Probability from Bike to Other", pp_vehicleCrossoverProb_defaultProb));
        ppCrossoverProbability.addConfigSetting(new ConfigSetting(pp_vehicleCrossoverProb_bike_stationary, "Bike to Stationary", "Crossover Probability from Bike to Stationary", pp_vehicleCrossoverProb_defaultProb));
        // bus to...
        ppCrossoverProbability.addConfigSetting(new ConfigSetting(pp_vehicleCrossoverProb_bus_car, "Bus to Car", "Crossover Probability from Bus to Car", pp_vehicleCrossoverProb_defaultProb));
        ppCrossoverProbability.addConfigSetting(new ConfigSetting(pp_vehicleCrossoverProb_bus_bike, "Bus to Bike", "Crossover Probability from Bus to Bike", pp_vehicleCrossoverProb_defaultProb));
        ppCrossoverProbability.addConfigSetting(new ConfigSetting(pp_vehicleCrossoverProb_bus_bus, "Bus to Bus", "Crossover Probability from Bus to Bus", pp_vehicleCrossoverProb_defaultProbSameVehicle));
        ppCrossoverProbability.addConfigSetting(new ConfigSetting(pp_vehicleCrossoverProb_bus_train, "Bus to Train", "Crossover Probability from Bus to Train", pp_vehicleCrossoverProb_defaultProb));
        ppCrossoverProbability.addConfigSetting(new ConfigSetting(pp_vehicleCrossoverProb_bus_other, "Bus to Other", "Crossover Probability from Bus to Other", pp_vehicleCrossoverProb_defaultProb));
        ppCrossoverProbability.addConfigSetting(new ConfigSetting(pp_vehicleCrossoverProb_bus_stationary, "Bus to Stationary", "Crossover Probability from Bus to Stationary", pp_vehicleCrossoverProb_defaultProb));
        // train to...
        ppCrossoverProbability.addConfigSetting(new ConfigSetting(pp_vehicleCrossoverProb_train_car, "Train to Car", "Crossover Probability from Train to Car", pp_vehicleCrossoverProb_defaultProb));
        ppCrossoverProbability.addConfigSetting(new ConfigSetting(pp_vehicleCrossoverProb_train_bike, "Train to Bike", "Crossover Probability from Train to Bike", pp_vehicleCrossoverProb_defaultProb));
        ppCrossoverProbability.addConfigSetting(new ConfigSetting(pp_vehicleCrossoverProb_train_bus, "Train to Bus", "Crossover Probability from Train to Bus", pp_vehicleCrossoverProb_defaultProb));
        ppCrossoverProbability.addConfigSetting(new ConfigSetting(pp_vehicleCrossoverProb_train_train, "Train to Train", "Crossover Probability from Train to Train", pp_vehicleCrossoverProb_defaultProbSameVehicle));
        ppCrossoverProbability.addConfigSetting(new ConfigSetting(pp_vehicleCrossoverProb_train_other, "Train to Other", "Crossover Probability from Train to Other", pp_vehicleCrossoverProb_defaultProb));
        ppCrossoverProbability.addConfigSetting(new ConfigSetting(pp_vehicleCrossoverProb_train_stationary, "Train to Stationary", "Crossover Probability from Train to Stationary", pp_vehicleCrossoverProb_defaultProb));
        // other to...
        ppCrossoverProbability.addConfigSetting(new ConfigSetting(pp_vehicleCrossoverProb_other_car, "Other to Car", "Crossover Probability from Other to Car", pp_vehicleCrossoverProb_defaultProb));
        ppCrossoverProbability.addConfigSetting(new ConfigSetting(pp_vehicleCrossoverProb_other_bike, "Other to Bike", "Crossover Probability from Other to Bike", pp_vehicleCrossoverProb_defaultProb));
        ppCrossoverProbability.addConfigSetting(new ConfigSetting(pp_vehicleCrossoverProb_other_bus, "Other to Bus", "Crossover Probability from Other to Bus", pp_vehicleCrossoverProb_defaultProb));
        ppCrossoverProbability.addConfigSetting(new ConfigSetting(pp_vehicleCrossoverProb_other_train, "Other to Train", "Crossover Probability from Other to Train", pp_vehicleCrossoverProb_defaultProb));
        ppCrossoverProbability.addConfigSetting(new ConfigSetting(pp_vehicleCrossoverProb_other_other, "Other to Other", "Crossover Probability from Other to Other", pp_vehicleCrossoverProb_defaultProbSameVehicle));
        ppCrossoverProbability.addConfigSetting(new ConfigSetting(pp_vehicleCrossoverProb_other_stationary, "Other to Stationary", "Crossover Probability from Other to Stationary", pp_vehicleCrossoverProb_defaultProb));
        // stationary to...
        ppCrossoverProbability.addConfigSetting(new ConfigSetting(pp_vehicleCrossoverProb_stationary_car, "Stationary to Car", "Crossover Probability from Stationary to Car", pp_vehicleCrossoverProb_defaultProb));
        ppCrossoverProbability.addConfigSetting(new ConfigSetting(pp_vehicleCrossoverProb_stationary_bike, "Stationary to Bike", "Crossover Probability from Stationary to Bike", pp_vehicleCrossoverProb_defaultProb));
        ppCrossoverProbability.addConfigSetting(new ConfigSetting(pp_vehicleCrossoverProb_stationary_bus, "Stationary to Bus", "Crossover Probability from Stationary to Bus", pp_vehicleCrossoverProb_defaultProb));
        ppCrossoverProbability.addConfigSetting(new ConfigSetting(pp_vehicleCrossoverProb_stationary_train, "Stationary to Train", "Crossover Probability from Stationary to Train", pp_vehicleCrossoverProb_defaultProb));
        ppCrossoverProbability.addConfigSetting(new ConfigSetting(pp_vehicleCrossoverProb_stationary_other, "Stationary to Other", "Crossover Probability from Stationary to Other", pp_vehicleCrossoverProb_defaultProb));
        ppCrossoverProbability.addConfigSetting(new ConfigSetting(pp_vehicleCrossoverProb_stationary_stationary, "Stationary to Stationary", "Crossover Probability from Stationary to Stationary", pp_vehicleCrossoverProb_defaultProbSameVehicle));

        configGroups.add(ppCrossoverProbability);

        for (ConfigGroup configGroup : configGroups) {
            for (ConfigSetting configSetting : configGroup.getConfigSettingList()) {
                put(configSetting.getKey(), configSetting);
            }
        }
    }

    public ConfigServiceDefaultCache() {

        initConfigGroups();

//    // PositionJumpSpeedFilter
//    // The 'position jump speeed filter' removes gps points, which can be reached by their neighbours only with speeds above a given speed threshold.
//    addToCache(PositionJumpSpeedThesholdInKmH,"Maximal speed in km/h before a point is removed",300);
//
//
//    // SignalShortageFinder
//    addToCache(signalShortageThresholdInSeconds,"Duration threshold in seconds to distinguis between signal shortage points and normal points ",30);
//    addToCache(minDurationBetweenCSignalShortageBelow20KmH,"Minimal Duration between signal shortages, which have a speed below 20km/H ('walkingNonWalkingSpeedThreshold'), If duration is below the signal shortages are merged ",30);
//    addToCache(minDurationBetweenCSignalShortageAbove20KmH,"Minimal Duration between signal shortages, which have a speed above 20km/H ('walkingNonWalkingSpeedThreshold'), If duration is below the signal shortages are merged ",120);
//    addToCache(signalShortageFinder_speedThreshold,"Minimal speed in km/h to distinguis between stationary signal shortages and moving signal shortages",0.2);
//    addToCache(walkingNonWalkingSpeedThreshold,"Speed threshold in km/h to distinguis between walking and non-walking signal shortages. This distinguision is only used for the mergin of signal shortages which are to close to each other.",20);
//
//    //DensityCluster
//    addToCache(distanceThreshold,"Distance threshold in meter to distinguis between cluster points and normal points",5);
//    addToCache(distanceThresholdFactor,"This factory deterimnes the minimal proportion of points which are below the distanceThreshold",0.80);
//    addToCache(pointsRadius,"Number of points before and after a point, which are counted checked against the distance threshold",30);
//    addToCache(frameSize,"Frame size in which the amount of density points is counted and then checked against the framesizethresholdfactor",30);
//    addToCache(frameSizeThresholdFactor,"Minimal percentage amount threshold. If the amount of density points inside a frame is higher then the framesizeThesholdFActor, the points are combined to a cluster",0.66);
//    addToCache(minimalPointsBetweenCluster,"Minimal amount of points, which have to be between clusters. Otherwise the clusters are merged together",60);
//    addToCache(minimalClusterDuration,"Minimal Cluster Duration in seconds. After mergin Cluster, etc. All Clusters are checked and those which are shorter than this value are remved. ",60);
//
//    //WalkingNonWalking
//    addToCache(minWalkingThreshold,"Duration Threshold in seconds. Walking segments which are below this threshold are merged together with the surrounding non walking segments",60);
//    addToCache(minNonWalkingThreshold,"Duration Threshold in seconds. Non Walking segments which are below this threshold are merged together with the surrounding  walking segments",60);
//    addToCache(walkingSpeedThrehold,"Speed threshold in km/h. Speeds below are seen as walking, speed above are seen as non walking",10);
//
//    //kernel smoother
//    addToCache(kernelSmoother_sigma,"kernelbandwith in seconds. Used for the gausian kernel smother",5);
//    addToCache(kernelSmoother_calcRangeFactor,"this factor is used to calculate the time frame, which is used to smooth a point. This factor is multiplied with sigma and the result is the radius, which contains the gps points which are used to smooth for a given time",4.417);

    }

    private void addToCache(String key, String description, double value) {
        ConfigSetting configSetting = new ConfigSetting(key, description, value);
        put(key, configSetting);
    }

}
