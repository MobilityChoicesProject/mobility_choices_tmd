package at.fhv.tmddemoservice.user;

import at.fhv.transportClassifier.mainserver.security.AuthenticationException;
import at.fhv.transportClassifier.mainserver.security.LoginResult;
import at.fhv.transportClassifier.mainserver.security.SecurityManagerLocal;
import com.google.gson.Gson;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * Created by Johannes on 26.07.2017.
 */

@Path("/User")
public class UserService {

    private Gson gson = new Gson();


    @EJB
    SecurityManagerLocal securityManager;


    @PermitAll
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response Login(String json) {

        LoginRequest loginRequest = gson.fromJson(json, LoginRequest.class);

        char[] password = loginRequest.getPassword();
        LoginResult loginResult = null;
        try {
            loginResult = securityManager
                    .loginAndGetToken(loginRequest.getUsername(), loginRequest.getPassword());
        } catch (AuthenticationException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        if (loginResult.isSuccesfull()) {
            LoginResponse loginResponse = new LoginResponse("ok", loginResult.getToken());
            return Response.status(200).type(MediaType.APPLICATION_JSON).entity(loginResponse).build();
        } else {
            LoginResponse loginResponse = new LoginResponse("login_failed", loginResult.getToken());
            return Response.status(Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON).entity(loginResponse).build();
        }
    }

    @PermitAll
    @POST
    @Path("/logout")
    @Consumes(MediaType.APPLICATION_JSON)
    public void Login(LogoutRequest logoutRequest) {
        securityManager.logout(logoutRequest.getToken());
    }


    @POST
    @Path("/changePassword")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changePassword(ChangePasswordRequest passwordRequest) {

        boolean passwordChanged = false;
        try {
            passwordChanged = securityManager
                    .changePassword("tmd_admin", passwordRequest.getOldPassword(),
                            passwordRequest.getNewPassword());
        } catch (AuthenticationException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        if (passwordChanged) {
            ChangePasswordResponse changePasswordResponse = new ChangePasswordResponse("ok");
            return Response.status(200).type(MediaType.APPLICATION_JSON).entity(changePasswordResponse).build();
        } else {
            ChangePasswordResponse changePasswordResponse = new ChangePasswordResponse("failed");
            return Response.status(Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(changePasswordResponse).build();
        }


    }


    @PermitAll
    @POST
    @Path("/isValidToken")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response isValidToken(TokenCheckRequest token) {

        TokenCheckResponse tokenCheckResponse = new TokenCheckResponse();
        tokenCheckResponse.setTokenIsValid(securityManager.isAllowed(token.getTokenToCheck()));

        String json = gson.toJson(tokenCheckResponse);
        return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(json).build();
    }


}
