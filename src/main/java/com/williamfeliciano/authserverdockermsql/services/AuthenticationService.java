package com.williamfeliciano.authserverdockermsql.services;

import com.williamfeliciano.authserverdockermsql.dto.JwtAuthenticationResponse;
import com.williamfeliciano.authserverdockermsql.dto.SignInRequest;
import com.williamfeliciano.authserverdockermsql.dto.SignUpRequest;
import com.williamfeliciano.authserverdockermsql.models.Role;
import com.williamfeliciano.authserverdockermsql.models.User;
import com.williamfeliciano.authserverdockermsql.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
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
        var refreshJwt = jwtService.generateRefreshToken(user);
        JwtAuthenticationResponse response = JwtAuthenticationResponse.builder().token(jwt).refreshToken(refreshJwt).build();
        log.info("signup response, {}", response);
        return response;
    }


    public JwtAuthenticationResponse signin(SignInRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
        var jwt = jwtService.generateToken(user);
        var refreshJwt = jwtService.generateRefreshToken(user);
        log.info("sign in token {}",jwt);
        log.info("sign in refresh {}",refreshJwt);
        JwtAuthenticationResponse response =JwtAuthenticationResponse.builder().token(jwt).refreshToken(refreshJwt).build();
        log.info("signIn response, {}", response);
        System.out.println(response);
        return response;
    }

    public JwtAuthenticationResponse refresh(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            String jwt = authHeader.substring(7);
            String userEmail = jwtService.extractUserName(jwt);
            var user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
            if(jwtService.isTokenValid(jwt,user)){
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(),user.getPassword()));
                var newAccessToken = jwtService.generateToken(user);
                var newRefreshToken = jwtService.generateRefreshToken(user);
                return JwtAuthenticationResponse.builder().token(newAccessToken).refreshToken(newRefreshToken).build();
            }
        }catch(Exception ex){
            log.error(ex.getMessage());
        }
        return null;
    }
}