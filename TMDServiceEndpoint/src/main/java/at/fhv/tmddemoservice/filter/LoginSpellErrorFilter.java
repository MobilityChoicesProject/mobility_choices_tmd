package at.fhv.tmddemoservice.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginSpellErrorFilter implements Filter {

  FilterConfig filterConfig = null;
  private static Logger logger = LoggerFactory.getLogger(LoginSpellErrorFilter.class);


  public void init(FilterConfig filterConfig) throws ServletException {
    this.filterConfig = filterConfig;
  }



  public void destroy() {
  }

  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
      throws IOException, ServletException {


    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;
    String requestURI = request.getRequestURI();

    boolean chainFilters = true;
      if (requestURI.endsWith("/login.html")) {
        String newURI = requestURI.replace("/login.html", "/Login.html");
        response.sendRedirect(newURI);
        chainFilters = false;
      }

      if(chainFilters){
        filterChain.doFilter(request,response);
      }

  }

}