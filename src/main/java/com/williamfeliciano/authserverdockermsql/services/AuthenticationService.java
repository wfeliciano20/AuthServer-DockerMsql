package com.williamfeliciano.authserverdockermsql.services;

import com.williamfeliciano.authserverdockermsql.dto.JwtAuthenticationResponse;
import com.williamfeliciano.authserverdockermsql.dto.RefreshRequest;
import com.williamfeliciano.authserverdockermsql.dto.SignInRequest;
import com.williamfeliciano.authserverdockermsql.dto.SignUpRequest;
import com.williamfeliciano.authserverdockermsql.models.Role;
import com.williamfeliciano.authserverdockermsql.models.User;
import com.williamfeliciano.authserverdockermsql.repositories.RefreshTokenRepository;
import com.williamfeliciano.authserverdockermsql.repositories.UserRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    public JwtAuthenticationResponse signup(SignUpRequest request) {
        var user = User
                .builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        user = userService.save(user);
        var jwt = jwtService.generateToken(user);
        refreshTokenRepository.findByUserId(user.getId()).ifPresent(refreshTokenRepository::delete);
        var refreshJwt = refreshTokenService.GenerateRefreshToken(user);
        JwtAuthenticationResponse response = JwtAuthenticationResponse.builder().token(jwt).refreshToken(refreshJwt.getToken()).build();
        log.info("signup response, {}", response);
        return response;
    }


    public JwtAuthenticationResponse signin(SignInRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
        var jwt = jwtService.generateToken(user);
        refreshTokenRepository.findByUserId(user.getId()).ifPresent(refreshTokenRepository::delete);
        var refreshJwt = refreshTokenService.GenerateRefreshToken(user);
        log.info("sign in token {}",jwt);
        log.info("sign in refresh {}",refreshJwt);
        var response = new  JwtAuthenticationResponse();
        response.setToken(jwt);
        response.setRefreshToken(refreshJwt.getToken());
        log.info("signIn response, {}", response);
        System.out.println(response);
        return response;
    }

//    public JwtAuthenticationResponse refresh(HttpServletRequest request) {
//        try {
//            String authHeader = request.getHeader("Authorization");
//            String jwt = authHeader.substring(7);
//            String userEmail = jwtService.extractUserName(jwt);
//            var user = userRepository.findByEmail(userEmail)
//                    .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
//            if(jwtService.isTokenValid(jwt,user)){
//                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(),user.getPassword()));
//                var newAccessToken = jwtService.generateToken(user);
//                var newRefreshToken = jwtService.generateRefreshToken(user);
//                return JwtAuthenticationResponse.builder().token(newAccessToken).refreshToken(newRefreshToken).build();
//            }
//        }catch(Exception ex){
//            log.error(ex.getMessage());
//        }
//        return null;
//    }

    public JwtAuthenticationResponse refresh(RefreshRequest request) {
        // Check that the token is valid meaning it is on the db and it's also not expired
        if(refreshTokenService.isTokenValid(request.getToken())){
            // Find the token from the db
          var refreshTokenFromDB =  refreshTokenRepository.findByToken(request.getToken()).get();

          // Generate a new Access token using the dbtoken user
          var accessToken = jwtService.generateToken(refreshTokenFromDB.getUser());
          // delete old refresh token
            refreshTokenRepository.findByToken(refreshTokenFromDB.getToken()).ifPresent(refreshTokenRepository::delete);
          // Generate a new Access Token using the dbtoken user
          var newRefreshToken = refreshTokenService.GenerateRefreshToken(refreshTokenFromDB.getUser());
          return JwtAuthenticationResponse.builder().refreshToken(newRefreshToken.getToken()).token(accessToken).build();
        }else{
            // If we get here we already know the token is in db but is expired thats why I Get
            var invalidToken = refreshTokenRepository.findByToken(request.getToken()).get();
            // We delete the invalid token
            refreshTokenRepository.delete(invalidToken);
            return null;
        }
    }
}