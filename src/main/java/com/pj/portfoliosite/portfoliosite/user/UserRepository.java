package com.pj.portfoliosite.portfoliosite.user;

import com.pj.portfoliosite.portfoliosite.global.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 암호화된 이메일로 사용자 검색
     * 주의: 이메일은 암호화되어 저장되므로 암호화된 값으로 검색해야 함
     */
    Optional<User> findByEmail(String encryptedEmail);

    /**
     * RefreshToken으로 사용자 검색
     */
    Optional<User> findByRefreshToken(String refreshToken);

    /**
     * 암호화된 이메일과 Provider로 사용자 검색
     */
    Optional<User> findByEmailAndProvider(String encryptedEmail, String provider);

    /**
     * Provider와 ProviderId로 사용자 검색
     */
    Optional<User> findByProviderAndProviderId(String provider, String providerId);

    /**
     * 마이그레이션을 위한 모든 사용자 조회
     * 주의: 이 메서드는 JPA 리스너를 우회하여 원본 데이터를 가져옴
     */
    @Query(value = "SELECT * FROM user", nativeQuery = true)
    List<User> findAllForMigration();

    /**
     * ID로 사용자 검색 (기본 메서드)
     */
    Optional<User> findById(Long id);

    boolean existsByNickname(String nickname);
}