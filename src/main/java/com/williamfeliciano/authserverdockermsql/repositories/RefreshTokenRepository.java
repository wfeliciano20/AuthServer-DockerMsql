package com.williamfeliciano.authserverdockermsql.repositories;

import com.williamfeliciano.authserverdockermsql.models.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUserId(Long userId);
    boolean existsByToken(String token);
}
