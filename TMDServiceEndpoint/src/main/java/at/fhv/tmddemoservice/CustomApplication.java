package at.fhv.tmddemoservice;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Created by Johannes on 26.07.2017.
 */
public class CustomApplication extends ResourceConfig {
    public CustomApplication() {
        packages("at.fhv.at.fhv.tmddemoservice");
//    register(LoggingFilter.class);
        register(LoggingFeature.class);

        //Register Auth Filter here
        register(AuthenticationFilter.class);
    }
}