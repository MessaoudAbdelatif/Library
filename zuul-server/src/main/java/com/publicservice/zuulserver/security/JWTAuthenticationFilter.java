package com.publicservice.zuulserver.security;

import com.publicservice.zuulserver.configuration.ApplicationPropertiesConfiguration;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {


  private AuthenticationManager authenticationManager;


  public JWTAuthenticationFilter(
      AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException {

    String username = request.getParameter("username");
    String password = request.getParameter("password");

    return authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(username, password));
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authResult) throws IOException, ServletException {
    User user = (User) authResult.getPrincipal();
    List<String> roles = new ArrayList<>();
    authResult.getAuthorities().forEach(a -> {
      roles.add(a.getAuthority());
    });
    String jwt = Jwts.builder()
        .setIssuer(request.getRequestURI())
        .setSubject(user.getUsername())
        .claim("roles", roles.toArray(new String[roles.size()]))
        .setExpiration(
            new Date(System.currentTimeMillis() + ApplicationPropertiesConfiguration.EXPIRATION
            ))
        .signWith(SignatureAlgorithm.HS512, ApplicationPropertiesConfiguration.SECRET)
        .compact();
    System.out.println(jwt);
    // ADD COOKIES ##################################################
    Cookie cookie = new Cookie("JWTtoken", jwt);
    cookie.setSecure(false);
    cookie.setHttpOnly(true);
    // 12 days about 999999
    cookie.setMaxAge(999999);
    cookie.setDomain("localhost");
    cookie.setPath("/");
    response.addCookie(cookie);
    // REDIRECT ##################################################
    response.sendRedirect("/Books");
  }
}