package at.fhv.transportClassifier.mainserver.security;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Johannes on 26.07.2017.
 */
public class TokenContainer {

  SessionIdentifierGenerator sessionIdentifierGenerator = new SessionIdentifierGenerator();

  List<TokenInfo> tokens = new LinkedList<>();
  Duration maxtime  = Duration.ofMinutes(15);

  public boolean isValidToken(String token){
    LocalDateTime now = LocalDateTime.now();

    Iterator<TokenInfo> iterator = tokens.iterator();
    while (iterator.hasNext()) {
      TokenInfo tokenInfo = iterator.next();
      LocalDateTime time = tokenInfo.getTime();
      Duration between = Duration.between(time, now);

      if(maxtime.compareTo(between)<0){
        iterator.remove();
        continue;
      }

      if(tokenInfo.getToken().equals(token)){
        tokenInfo.updateLastTimeUsed();
        return true;

      }
    }
    return false;
  }


  public void removeToken(String token){
    Iterator<TokenInfo> iterator = tokens.iterator();
    while (iterator.hasNext()) {
      TokenInfo next = iterator.next();
      if(next.getToken().equals(token)){
        iterator.remove();
      }
    }
  }

  public String generateOrGetToken(String hashedPassword) {

    for (TokenInfo token : tokens) {
      if (token.getHashedPassword().equals(hashedPassword)) {
        return token.getToken();
      }
    }

    String token = sessionIdentifierGenerator.nextSessionId();
    tokens.add(new TokenInfo(LocalDateTime.now(), token,hashedPassword));
    return token;
  }

  public static class TokenInfo {
    LocalDateTime time;
    String token;
    private String hashedPassword;

    public LocalDateTime getTime() {
      return time;
    }

    public String getToken() {
      return token;
    }

    public TokenInfo(LocalDateTime time, String token,String hashedPassword) {
      this.time = time;
      this.token = token;
      this.hashedPassword =hashedPassword;
    }

    public void updateLastTimeUsed(){
      time = LocalDateTime.now();
    }



    public String getHashedPassword() {
      return hashedPassword;
    }
  }


  public final class SessionIdentifierGenerator {
    private SecureRandom random = new SecureRandom();

    public String nextSessionId() {
      return new BigInteger(130, random).toString(32);
    }
  }


}
