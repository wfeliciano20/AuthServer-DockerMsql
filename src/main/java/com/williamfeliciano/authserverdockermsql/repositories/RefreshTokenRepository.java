package com.williamfeliciano.authserverdockermsql.repositories;

import com.williamfeliciano.authserverdockermsql.models.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {

}
