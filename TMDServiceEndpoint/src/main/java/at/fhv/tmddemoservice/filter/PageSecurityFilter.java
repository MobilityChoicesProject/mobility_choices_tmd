package at.fhv.tmddemoservice.filter;

import at.fhv.transportClassifier.mainserver.security.SecurityManagerLocal;
import java.io.IOException;
import javax.ejb.EJB;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PageSecurityFilter implements Filter {

  FilterConfig filterConfig = null;

  public void init(FilterConfig filterConfig) throws ServletException {
    this.filterConfig = filterConfig;
  }


  @EJB
  SecurityManagerLocal securityManager;

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
      FilterChain filterChain) throws IOException, ServletException {

    boolean chainFilter = true;
    if(servletRequest instanceof HttpServletRequest) {


      HttpServletRequest request = (HttpServletRequest) servletRequest;
      HttpServletResponse response = (HttpServletResponse) servletResponse;
      String requestURI = request.getRequestURI();
      String lCRequestUri = requestURI.toLowerCase();

      if(lCRequestUri.endsWith("login.html")){
        // do nothing
      }else{
        if(lCRequestUri.endsWith(".html")){

          request = (HttpServletRequest) servletRequest;
          requestURI = request.getRequestURI();
          requestURI = requestURI.toLowerCase();

          Cookie[] cookies = request.getCookies();
          String token = null;
          for (Cookie cookie : cookies) {
            boolean tokenValue = cookie.getName().equals("tokenValue");
            if(tokenValue){
              token = cookie.getValue();
            }
          }
          if(token!= null){
            if (!securityManager.isAllowed(token)) {
              String newPah = request.getContextPath() + "/Login.html";
              response.sendRedirect(newPah);
              chainFilter =false;
            }
          }else{
            String newPah = request.getContextPath() + "/Login.html";
            response.sendRedirect(newPah);
            chainFilter =false;
          }

        }

      }


    }
    if(chainFilter){
      filterChain.doFilter(servletRequest,servletResponse);

    }





  }

  @Override
  public void destroy() {

  }


}
