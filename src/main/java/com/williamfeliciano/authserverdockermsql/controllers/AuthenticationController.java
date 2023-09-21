package com.williamfeliciano.authserverdockermsql.controllers;

import com.williamfeliciano.authserverdockermsql.dto.JwtAuthenticationResponse;
import com.williamfeliciano.authserverdockermsql.dto.SignInRequest;
import com.williamfeliciano.authserverdockermsql.dto.SignUpRequest;
import com.williamfeliciano.authserverdockermsql.services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public JwtAuthenticationResponse signup(@RequestBody SignUpRequest request) {
        return authenticationService.signup(request);
    }

    @PostMapping("/signin")
    public JwtAuthenticationResponse signin(@RequestBody SignInRequest request) {
        return authenticationService.signin(request);
    }

    @GetMapping("/refresh")
    public JwtAuthenticationResponse refreshToken(HttpServletRequest request){
        return authenticationService.refresh(request);
    }

    @GetMapping("/logout")
    public String logout(){
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(null);
        SecurityContextHolder.setContext(securityContext);
        return "Successfully Logout";
    }
}