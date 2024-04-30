package com.lcwd.electroic.store.controllers;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.lcwd.electroic.store.dtos.JwtRequest;
import com.lcwd.electroic.store.dtos.JwtResponse;
import com.lcwd.electroic.store.dtos.UserDto;
import com.lcwd.electroic.store.entities.User;
import com.lcwd.electroic.store.exceptions.BadApiRequest;
import com.lcwd.electroic.store.security.JwtHelper;
import com.lcwd.electroic.store.services.UserService;
import io.swagger.annotations.Api;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Api(value = "AuthController",description = "APIs for authentication!!")
public class AuthController {

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private AuthenticationManager manager;
    @Autowired
    private JwtHelper helper ;
    @Autowired
    private UserService userService ;
    private Logger logger = LoggerFactory.getLogger(AuthController.class);
    @GetMapping("/current")
    public ResponseEntity<UserDto> getCurrentUser(Principal principal){
        String name = principal.getName();
        return new ResponseEntity<>(modelMapper.map(userDetailsService.loadUserByUsername(name), UserDto.class), HttpStatus.OK);
    }
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request){
        this.doAuthenticate(request.getEmail(),request.getPassword());
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = this.helper.generateToken(userDetails);
        UserDto userDto = modelMapper.map(userDetails,UserDto.class);
        JwtResponse response = JwtResponse.builder()
                .jwtToken(token)
                .user(userDto)
                .build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    private void doAuthenticate(String email, String password) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email,password);
        try{
            manager.authenticate(authentication);
        }catch(BadCredentialsException e){
            throw new BadApiRequest("Invalid Username or Password");
        }

    }


    //login with google API
//    @PostMapping ("/google")
//    public ResponseEntity<JwtResponse> loginWithGoogle(@RequestBody Map<String, Object> data) throws IOException {
//        //get id token from request
//        String idToken= data.get("idToken").toString();
//
//        NetHttpTransport netHttpTransport = new NetHttpTransport();
//        JacksonFactory jacksonFactory = JacksonFactory.getDefaultInstance();
//        GoogleIdTokenVerifier.Builder verifier = new GoogleIdTokenVerifier.Builder(netHttpTransport,jacksonFactory).setAudience(Collections.singleton(googleClientId));
//        GoogleIdToken googleIdToken = GoogleIdToken.parse(verifier.getJsonFactory(), idToken);
//        GoogleIdToken.Payload payload = googleIdToken.getPayload();
//        logger.info("Payload: {}",payload);
//        String email = payload.getEmail();
//
//        User user = null;
//        user = userService.findUserByEmailOptional(email).orElse(null);
//        if(user == null){
//            //create new user
//            user = this.saveUser(email,data.get("name").toString(),data.get("photoUrl").toString());
//        }
//        ResponseEntity<JwtResponse> jwtResponseResponseEntity = this.login(JwtRequest.builder().email(user.getEmail()).password(newPassword).build());
//        return jwtResponseResponseEntity;
//
//    }

//    private User saveUser(String email, String name, String photoUrl) {
//        UserDto newUser = UserDto.builder()
//                .name(name)
//                .email(email)
//                .password(newPassword)
//                .imageName(photoUrl)
//                .roles(new HashSet<>())
//                .build();
//        UserDto user = userService.createUser(newUser);
//        return this.modelMapper.map(user,User.class);
//    }

}
