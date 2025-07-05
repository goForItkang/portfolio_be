package com.pj.portfoliosite.portfoliosite.global.repository;

import com.pj.portfoliosite.portfoliosite.global.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer>{
}
