package at.fhv.tmddemoservice;


import at.fhv.transportClassifier.mainserver.security.SecurityManagerLocal;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * Created by Johannes on 26.07.2017.
 */
@Provider
public class AuthenticationFilter implements ContainerRequestFilter {

  private static final String AUTHORIZATION_PROPERTY = "Authorization";
  private static final String AUTHENTICATION_SCHEME = "Token";

  private static final Response ACCESS_DENIED = Response.status(Response.Status.UNAUTHORIZED)
      .entity("You cannot access this resource").build();
  private static final Response ACCESS_FORBIDDEN = Response.status(Response.Status.FORBIDDEN)
      .entity("Access blocked for all users !!").build();


  @EJB
  SecurityManagerLocal securityManager;

  @Context
  private ResourceInfo resourceInfo;

  @Override
  public void filter(ContainerRequestContext containerRequestContext) throws IOException {


    Method method = resourceInfo.getResourceMethod();
    //Access allowed for all

    Annotation[] annotations = method.getAnnotations();

    if( ! method.isAnnotationPresent(PermitAll.class))
    {

//      final MultivaluedMap<String, String> headers = containerRequestContext.getHeaders();

      Map<String, Cookie> cookies = containerRequestContext.getCookies();
      boolean tokenValue = cookies.containsKey("tokenValue");
      if(tokenValue){
        Cookie cookie = cookies.get("tokenValue");
        String tokenStr = cookie.getValue();

        boolean allowed = isAllowed(tokenStr);
        if(!allowed){
          containerRequestContext.abortWith(ACCESS_DENIED);
        }

      }else{
        containerRequestContext.abortWith(ACCESS_DENIED);
      }

    }
  }

  private boolean isAllowed(String token) {
    return securityManager.isAllowed(token);
  }

}
