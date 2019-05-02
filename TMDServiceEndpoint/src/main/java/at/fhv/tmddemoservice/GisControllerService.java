package at.fhv.tmddemoservice;

import at.fhv.gis.CurrentUpdateStatus;
import at.fhv.gis.entities.db.GisDataUpdate;
import at.fhv.tmddemoservice.gisConfig.ActionStatus;
import at.fhv.transportClassifier.mainserver.bean.gis.GisDataCreationServiceLocal;
import at.fhv.transportClassifier.mainserver.bean.gis.GisService;
import at.fhv.transportClassifier.mainserver.bean.gis.GisServiceLocal;
import at.fhv.transportClassifier.mainserver.bean.gis.GisUpdateServiceLocal;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.List;
import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/Gis")
public class GisControllerService {



  @EJB
  GisServiceLocal gisServiceLocal;

  private Gson gson = new GsonBuilder().setPrettyPrinting().create();


  @GET
  @Path("/status")
  @Produces(MediaType.APPLICATION_JSON)
  public Response status() {

    List<? extends GisDataUpdate> tenLastStatus = gisServiceLocal.get10LastStatus();
    String json = gson.toJson(tenLastStatus);
    return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(json).build();
  }

  @GET
  @Path("/getCurrentStatus")
  @Produces(MediaType.APPLICATION_JSON)
  public Response currentUpdateStatus() {

    CurrentUpdateStatus currentStatus = gisServiceLocal.getCurrentStatus();
    String json = gson.toJson(currentStatus);
    return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(json).build();
  }


  @GET
  @Path("/start")
  @Produces(MediaType.APPLICATION_JSON)
  public Response start() {
    try{
      gisServiceLocal.start();

    }catch (Exception ex){
      return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON).build();

    }
    ActionStatus status = new ActionStatus("OK");
    return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(status).build();
  }

  @GET
  @Path("/stop")
  @Produces(MediaType.APPLICATION_JSON)
  public Response stop() {
    try{
      gisServiceLocal.cancel();

    }catch (Exception ex){
      return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON).build();

    }
    ActionStatus status = new ActionStatus("OK");
    return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(status).build();
  }

  @GET
  @Path("/resume")
  @Produces(MediaType.APPLICATION_JSON)
  public Response resume() {
    try{
      gisServiceLocal.resume();
    }catch (Exception ex){
      return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON).build();

    }
    ActionStatus status = new ActionStatus("OK");
    return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(status).build();
  }


}
