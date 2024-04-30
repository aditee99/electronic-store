package com.lcwd.electroic.store.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    UserDetailsService userDetailsService;
    private Logger logger = LoggerFactory.getLogger(OncePerRequestFilter.class);
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //authorization
        String requestHeader = request.getHeader("Authorization");
        logger.info("Header {}:",requestHeader);
        String username = null;
        String token = null;
        if(requestHeader!=null && requestHeader.startsWith("Bearer")){
            token = requestHeader.substring(7);
            try{
                username = this.jwtHelper.getUsernameFromToken(token);
            }catch(IllegalArgumentException e){
                logger.error("Illegal Argument !");
                e.printStackTrace();
            }catch(ExpiredJwtException e){
                logger.error("Token is expired !");
                e.printStackTrace();
            }catch(MalformedJwtException e){
                logger.error("Invalid Token !");
                e.printStackTrace();
            }catch(Exception e){
                e.printStackTrace();
            }
        }else{
            logger.info("Invalid Header Value");
        }
        //
        if(username!=null && SecurityContextHolder.getContext().getAuthentication() == null) {
            //fetch user detail
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            Boolean validateToken = this.jwtHelper.validateToken(token, userDetails);
            if (validateToken) {
                //set the authentication
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } else {
                logger.info("Validation failed");

            }
        }
            filterChain.doFilter(request,response);



    }
}
