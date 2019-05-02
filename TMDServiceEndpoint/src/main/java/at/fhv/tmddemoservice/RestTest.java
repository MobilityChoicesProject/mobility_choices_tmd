package at.fhv.tmddemoservice;

import java.time.format.DateTimeFormatter;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/Test")
public class RestTest {

private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.S'Z'");



  @GET
  @Path("/getStuff")
  @Produces(MediaType.TEXT_PLAIN)
  public Response classify(){

      return Response.status(200).build();

  }




}
