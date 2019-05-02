package at.fhv.transportClassifier.mainserver.security;

import javax.ejb.Local;

/**
 * Created by Johannes on 26.07.2017.
 */
@Local
public interface SecurityManagerLocal {

  public boolean isAllowed(String token);


  LoginResult loginAndGetToken(String username, char[] password) throws AuthenticationException;

  boolean changePassword(String username, char[] oldPassword, char[] newPassword)
      throws AuthenticationException;

  void logout(String token);
}
