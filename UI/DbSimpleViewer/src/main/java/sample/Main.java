package sample;

import at.fhv.transportClassifier.dal.HibernateSessionMananger;
import at.fhv.transportClassifier.dal.HibernateUtil;
import at.fhv.transportClassifier.dal.daos.HibernateLeightweightAccelerationValueDao;
import at.fhv.transportClassifier.dal.daos.HibernateLeightweightTrackingDao;
import at.fhv.transportClassifier.dal.databaseRepositories.HibernateBoundingBoxRepository;
import at.fhv.transportClassifier.dal.databaseRepositories.HibernateDatapointsRepository;
import at.fhv.transportClassifier.dal.databaseRepositories.HibernateTrackingInfoRepository;
import at.fhv.transportClassifier.dal.databaseRepositories.HibernateTrackingRepository;
import at.fhv.transportClassifier.dal.databaseRepositories.HibernateTrackingSegmentBagRepository;
import at.fhv.transportClassifier.dal.databaseRepositories.HibernateTrackingSegmentRepository;
import at.fhv.transportClassifier.dal.databaseRepositories.HibernateTrackingSegmentTypeRepository;
import at.fhv.transportClassifier.dal.databaseRepositories.TrackingInfoTypeRepository;
import at.fhv.transportClassifier.dal.interfaces.LeightweightAccelerationValueDao;
import at.fhv.transportClassifier.dal.interfaces.LeightweightTrackingDao;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.hibernate.Session;

public class Main extends Application {

    private Session session;
    LeightweightTrackingDao leightweightTrackingDao;
    private HibernateTrackingRepository hibernateTrackingRepository;


    @Override
    public void start(Stage primaryStage) throws Exception{

        initHibernate();
        MouseUiController mouseUiController = mouseType -> {primaryStage.getScene().setCursor(mouseType);};
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                    "/MainView.fxml"
                ));

        Parent root = loader.load();
        MainController controller = loader.getController();
        controller.injectDependencies(leightweightTrackingDao,mouseUiController,hibernateTrackingRepository);
        primaryStage.setTitle("Viewer");
        primaryStage.setScene(new Scene(root, 1000, 700));
        primaryStage.show();
        controller.init();



    }


    private void initHibernate(){

        org.hibernate.SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        session = sessionFactory.openSession();
        HibernateSessionMananger hibernateSessionMananger = new HibernateSessionMananger(session);
        LeightweightAccelerationValueDao leightweightAccelerationValueDao = new HibernateLeightweightAccelerationValueDao(hibernateSessionMananger);

        TrackingInfoTypeRepository trackingInfoTypeRepository = new TrackingInfoTypeRepository(hibernateSessionMananger);
        HibernateTrackingInfoRepository trackingInfoRepository = new HibernateTrackingInfoRepository(hibernateSessionMananger,trackingInfoTypeRepository);
        HibernateDatapointsRepository hibernateDatapointsRepository = new HibernateDatapointsRepository(hibernateSessionMananger);
        HibernateTrackingSegmentTypeRepository hibernateTrackingSegmentTypeRepository = new HibernateTrackingSegmentTypeRepository(hibernateSessionMananger);
        HibernateBoundingBoxRepository hibernateBoundingBoxRepository = new HibernateBoundingBoxRepository(hibernateSessionMananger);
        HibernateTrackingSegmentRepository hibernateTrackingSegmentRepository = new HibernateTrackingSegmentRepository(hibernateSessionMananger,hibernateBoundingBoxRepository,hibernateTrackingSegmentTypeRepository);
        HibernateTrackingSegmentBagRepository hibernateTrackingSegmentBagRepository = new HibernateTrackingSegmentBagRepository(hibernateSessionMananger,hibernateTrackingSegmentRepository);
         hibernateTrackingRepository = new HibernateTrackingRepository(hibernateSessionMananger,trackingInfoRepository,hibernateTrackingSegmentBagRepository,hibernateBoundingBoxRepository,hibernateDatapointsRepository);

        leightweightTrackingDao = new HibernateLeightweightTrackingDao(hibernateSessionMananger,leightweightAccelerationValueDao,hibernateTrackingRepository);

    }

    public static void main(String[] args) {
        launch(args);
    }
}
