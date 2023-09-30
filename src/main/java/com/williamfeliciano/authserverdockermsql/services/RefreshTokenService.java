package com.williamfeliciano.authserverdockermsql.services;

import com.williamfeliciano.authserverdockermsql.models.RefreshToken;
import com.williamfeliciano.authserverdockermsql.models.User;
import com.williamfeliciano.authserverdockermsql.repositories.RefreshTokenRepository;
import com.williamfeliciano.authserverdockermsql.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;


    private final JwtService jwtService;


    public RefreshToken  GenerateRefreshToken(User user){
        var token = jwtService.generateRefreshToken(user);
        RefreshToken refreshToken = RefreshToken.builder()
                .expirationTime(Instant.now().plusMillis(jwtService.refreshJwtExpirationMs))
                .token(token)
                .user(user)
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    public Boolean isTokenValid(String token){
        var savedToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() ->new RuntimeException("Token not valid. Re authenticate."));
        return jwtService.isTokenValid(savedToken.getToken(),savedToken.getUser());
    }

}
