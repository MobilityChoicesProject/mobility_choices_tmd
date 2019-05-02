package at.fhv.transportClassifier.mainserver.security;

import at.fhv.gis.entities.db.UserEntity;
import at.fhv.transportClassifier.common.transaction.NoTransaction;
import at.fhv.transportClassifier.common.transaction.TransactionException;
import at.fhv.transportClassifier.mainserver.security.login.PasswordAuthentication;
import java.util.List;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by Johannes on 26.07.2017.
 */


@Singleton
public class SecurityManagerBean implements SecurityManagerLocal{

  private static final String defaultuser ="tmd_admin";
  private UserDao userDao;


  // default password XiTm/.497a_Z
  private static final char[] defaultPassword = new char[]{'X','i','T','m','/','.','4','9','7','a','_','Z'};

  private TokenContainer tokenContainer = new TokenContainer();
  private PasswordAuthentication passwordAuthentication = new PasswordAuthentication();


  @PersistenceContext(unitName = "persistence_context_mysql")
  private EntityManager em;

  boolean notIntialized = true;
  void init(){
    userDao = new UserDao();
    userDao.set(em,new NoTransaction());
    String hash = passwordAuthentication.hash(defaultPassword);
    try {
      List<UserEntity> users = userDao.getUsers();
      for (UserEntity user : users) {
        if(user.getUsername().equals(defaultuser)){
          return;
        }
      }

      userDao.saveUser(defaultuser,hash);
    } catch (TransactionException e) {
      throw new RuntimeException("Failed to initialize SecurityManagerBean");
    }


    notIntialized = false;
  }


  @Override
  public boolean isAllowed(String token) {

    if(notIntialized){
      init();
    }

    return tokenContainer.isValidToken(token);
  }

  @Override
  public LoginResult loginAndGetToken(String username, char[] password)
      throws AuthenticationException {

    if(notIntialized){
      init();
    }

    try{
      userDao.set(em,new NoTransaction());
      List<UserEntity> users = userDao.getUsers();

    String hashedUserPwd = null;
    for (UserEntity user : users) {
      if(user.getUsername().equals(username)){
        hashedUserPwd = user.getHashedPwd();
      }
    }
    if(hashedUserPwd == null){
      return new LoginResult(false,null);
    }

    boolean authenticate = passwordAuthentication.authenticate(password, hashedUserPwd);

    if(authenticate){
      String token = tokenContainer.generateOrGetToken(username);
      return new LoginResult(true,token);
    }else{
      return new LoginResult(false,null);
    }

    }catch (TransactionException e){
      throw new AuthenticationException(e);
    }
  }


  @Override
  public boolean changePassword(String username, char[] oldPassword, char[] newPassword)
      throws AuthenticationException {

    if(notIntialized){
      init();
    }

    try{
      List<UserEntity> users = userDao.getUsers();



    String hashedUserPwd = null;
    for (UserEntity user : users) {
      if(user.getUsername().equals(username)){
        hashedUserPwd = user.getHashedPwd();
      }
    }
    if(hashedUserPwd == null){
      return false;
    }

    boolean authenticate = passwordAuthentication.authenticate(oldPassword, hashedUserPwd);
    if(authenticate){
      try{
        String newHasedPassword = passwordAuthentication.hash(newPassword);
        userDao.saveUser(username,newHasedPassword);
      }catch (Exception ex){
        return false;
      }
      return true;
    }else{
      return false;
    }
    }catch (TransactionException e){
      throw new AuthenticationException(e);
    }
  }


  @Override
  public void logout(String token) {

    if(notIntialized){
      init();
    }
    tokenContainer.removeToken(token);
  }


}
