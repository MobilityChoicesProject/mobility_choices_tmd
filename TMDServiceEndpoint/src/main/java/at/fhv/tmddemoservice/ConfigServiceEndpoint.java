package at.fhv.tmddemoservice;

import static at.fhv.transportClassifier.common.configSettings.ConfigServiceDefaultCache.*;

import at.fhv.tmddemoservice.configsettings.ConfigChangedStatus;
import at.fhv.tmddemoservice.configsettings.ConfigSettingEntity;
import at.fhv.tmddemoservice.configsettings.ConfigSettingRequest;
import at.fhv.transportClassifier.common.configSettings.ConfigGroup;
import at.fhv.transportClassifier.common.configSettings.ConfigSetting;
import at.fhv.transportClassifier.common.configSettings.ConfigurationExcepction;
import at.fhv.transportClassifier.common.configSettings.NewSettings;
import at.fhv.transportClassifier.mainserver.api.ConfigServerLocal;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * Created by Johannes on 09.08.2017.
 */

@Path("/ConfigService")
public class ConfigServiceEndpoint {



  @EJB
  private ConfigServerLocal configServerLocal;

  Gson gson = new GsonBuilder().setPrettyPrinting().create();

  @GET
  @Path("/currentSettings")
  public Response getConfigSettings(){

    List<ConfigSetting> configSettings = null;
    try {
      configSettings = configServerLocal.getConfigSettings();
    } catch (ConfigurationExcepction configurationExcepction) {
      return Response.status(Status.INTERNAL_SERVER_ERROR).build();
    }


    List<ConfigSettingEntity> configSettingEntities = new ArrayList<>();
    for (ConfigSetting configSetting : configSettings) {
      ConfigSettingEntity configSettingEntity = new ConfigSettingEntity(configSetting.getKey(),configSetting.getValue(),configSetting.getDescription());
      configSettingEntities.add(configSettingEntity);
    }

    String json = gson.toJson(configSettingEntities);
    return Response.status(200).type(MediaType.APPLICATION_JSON).entity(json).build();
  }

  @POST
  @Path("/getAllDefaultSettings")
  public Response getAllDefaultSetting(){

    List<ConfigSetting> configSettings = null;
    try {
      configSettings = configServerLocal.getDeafaultSettings();
    } catch (ConfigurationExcepction configurationExcepction) {
      return Response.status(Status.INTERNAL_SERVER_ERROR).build();
    }

    List<ConfigSettingEntity> configSettingEntities = new ArrayList<>();
    for (ConfigSetting configSetting : configSettings) {
      ConfigSettingEntity configSettingEntity = new ConfigSettingEntity(configSetting.getKey(),configSetting.getValue(),configSetting.getDescription());
      configSettingEntities.add(configSettingEntity);
    }

    String json = gson.toJson(configSettingEntities);
    return Response.status(200).type(MediaType.APPLICATION_JSON).entity(json).build();
  }


  @GET
  @Path("/currentGroups")
  public Response getAllDefaultGroups(){

    List<ConfigGroup> configSettings = null;
    try {
      configSettings = configServerLocal.getConfigGroups();
    } catch (ConfigurationExcepction configurationExcepction) {
      return Response.status(Status.INTERNAL_SERVER_ERROR).build();
    }


    String json = gson.toJson(configSettings);
    return Response.status(200).type(MediaType.APPLICATION_JSON).entity(json).build();

  }


  @POST
  @Path("/updateConfigSettings")
  public Response updateSetting(List<ConfigSettingRequest> configSettingRequestngs ){

    List<NewSettings> newSettings = new ArrayList<>();
    for (ConfigSettingRequest configSettingRequestng : configSettingRequestngs) {
      newSettings.add(new NewSettings(configSettingRequestng.getKey(),configSettingRequestng.getValue()));
    }
    ConfigChangedStatus ConfigChangedStatus = new ConfigChangedStatus("OK");
    try {
      configServerLocal.chanceSettings(newSettings);
    } catch (ConfigurationExcepction configurationExcepction) {
      ConfigChangedStatus.setStatus("Failed");
    }

    String json = gson.toJson(ConfigChangedStatus);
    return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(json).build();

  }


}
