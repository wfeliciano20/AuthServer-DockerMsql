package com.williamfeliciano.authserverdockermsql.services;

import com.williamfeliciano.authserverdockermsql.dto.JwtAuthenticationResponse;
import com.williamfeliciano.authserverdockermsql.dto.SignInRequest;
import com.williamfeliciano.authserverdockermsql.dto.SignUpRequest;
import com.williamfeliciano.authserverdockermsql.models.Role;
import com.williamfeliciano.authserverdockermsql.models.User;
import com.williamfeliciano.authserverdockermsql.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
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
        return JwtAuthenticationResponse.builder().token(jwt).refreshToken(refreshJwt).build();
    }


    public JwtAuthenticationResponse signin(SignInRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
        var jwt = jwtService.generateToken(user);
        var refreshJwt = jwtService.generateRefreshToken(user);
        return JwtAuthenticationResponse.builder().token(jwt).refreshToken(refreshJwt).build();
    }

}