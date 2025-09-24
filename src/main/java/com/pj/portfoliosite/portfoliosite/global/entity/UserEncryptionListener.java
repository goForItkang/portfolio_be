package com.pj.portfoliosite.portfoliosite.global.entity;

import com.pj.portfoliosite.portfoliosite.util.PersonalDataUtil;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * User 엔티티의 개인정보 자동 암호화/복호화 리스너
 */
@Slf4j
@Component
public class UserEncryptionListener {
    
    private static PersonalDataUtil personalDataUtil;
    
    @Autowired
    public void setPersonalDataUtil(PersonalDataUtil personalDataUtil) {
        UserEncryptionListener.personalDataUtil = personalDataUtil;
    }
    
    /**
     * 저장 전 개인정보 암호화
     */
    @PrePersist
    @PreUpdate
    public void encryptPersonalData(User user) {
        try {
            log.debug("사용자 개인정보 암호화 시작 - ID: {}", user.getId());
            
            // 이메일 암호화 (중복 암호화 방지)
            if (user.getEmail() != null && !personalDataUtil.isAlreadyEncrypted(user.getEmail())) {
                user.setEmail(personalDataUtil.encryptPersonalData(user.getEmail()));
            }
            
            // 이름 암호화
            if (user.getName() != null && !personalDataUtil.isAlreadyEncrypted(user.getName())) {
                user.setName(personalDataUtil.encryptPersonalData(user.getName()));
            }
            
            // 닉네임 암호화
            if (user.getNickname() != null && !personalDataUtil.isAlreadyEncrypted(user.getNickname())) {
                user.setNickname(personalDataUtil.encryptPersonalData(user.getNickname()));
            }
            
            // 직업 암호화
            if (user.getJob() != null && !personalDataUtil.isAlreadyEncrypted(user.getJob())) {
                user.setJob(personalDataUtil.encryptPersonalData(user.getJob()));
            }
            
            // 관심분야1 암호화
            if (user.getInterest() != null && !personalDataUtil.isAlreadyEncrypted(user.getInterest())) {
                user.setInterest(personalDataUtil.encryptPersonalData(user.getInterest()));
            }
            
            // 관심분야2 암호화
            if (user.getInterest2() != null && !personalDataUtil.isAlreadyEncrypted(user.getInterest2())) {
                user.setInterest2(personalDataUtil.encryptPersonalData(user.getInterest2()));
            }
            
            // Git 링크 암호화
            if (user.getGitLink() != null && !personalDataUtil.isAlreadyEncrypted(user.getGitLink())) {
                user.setGitLink(personalDataUtil.encryptPersonalData(user.getGitLink()));
            }
            
            // 자기소개 암호화
            if (user.getIntroduce() != null && !personalDataUtil.isAlreadyEncrypted(user.getIntroduce())) {
                user.setIntroduce(personalDataUtil.encryptPersonalData(user.getIntroduce()));
            }
            
            // 프로필 이미지 URL 암호화
            if (user.getProfile() != null && !personalDataUtil.isAlreadyEncrypted(user.getProfile())) {
                user.setProfile(personalDataUtil.encryptPersonalData(user.getProfile()));
            }
            
            log.debug("사용자 개인정보 암호화 완료 - ID: {}", user.getId());
            
        } catch (Exception e) {
            log.error("사용자 개인정보 암호화 실패 - ID: {}, Error: {}", user.getId(), e.getMessage());
            throw new RuntimeException("개인정보 암호화에 실패했습니다.", e);
        }
    }
    
    /**
     * 조회 후 개인정보 복호화 (개선된 안전한 복호화)
     */
    @PostLoad
    public void decryptPersonalData(User user) {
        try {
            log.debug("사용자 개인정보 복호화 시작 - ID: {}", user.getId());
            
            // 이메일 복호화
            user.setEmail(safeDecrypt(user.getEmail(), "이메일"));
            
            // 이름 복호화
            user.setName(safeDecrypt(user.getName(), "이름"));
            
            // 닉네임 복호화
            user.setNickname(safeDecrypt(user.getNickname(), "닉네임"));
            
            // 직업 복호화
            user.setJob(safeDecrypt(user.getJob(), "직업"));
            
            // 관심분야1 복호화
            user.setInterest(safeDecrypt(user.getInterest(), "관심분야1"));
            
            // 관심분야2 복호화
            user.setInterest2(safeDecrypt(user.getInterest2(), "관심분야2"));
            
            // Git 링크 복호화
            user.setGitLink(safeDecrypt(user.getGitLink(), "Git링크"));
            
            // 자기소개 복호화
            user.setIntroduce(safeDecrypt(user.getIntroduce(), "자기소개"));
            
            // 프로필 이미지 URL 복호화
            user.setProfile(safeDecrypt(user.getProfile(), "프로필"));
            
            log.debug("사용자 개인정보 복호화 완료 - ID: {}", user.getId());
            
        } catch (Exception e) {
            // 복호화 실패 시 로그만 남기고 원본 데이터 유지
            log.warn("사용자 정보 복호화 실패 - ID: {}, Error: {}", user.getId(), e.getMessage());
        }
    }
    
    /**
     * 안전한 복호화 메서드 - 복호화 실패 시 원본 데이터 유지 (개선한 버전)
     */
    private String safeDecrypt(String data, String fieldName) {
        if (data == null || data.trim().isEmpty()) {
            return data;
        }
        
        try {
            // 암호화된 데이터인지 확인 (안전한 방식)
            if (personalDataUtil.isAlreadyEncrypted(data)) {
                try {
                    String decrypted = personalDataUtil.decryptPersonalData(data);
                    log.trace("{} 복호화 성공", fieldName);
                    return decrypted;
                } catch (Exception decryptError) {
                    // 복호화 실패 시 원본 데이터 유지
                    log.debug("{} 복호화 실패, 원본 데이터 유지: {}", fieldName, decryptError.getMessage());
                    return data;
                }
            } else {
                // 이미 평문인 경우 그대로 반환
                log.trace("{} 이미 평문 상태", fieldName);
                return data;
            }
        } catch (Exception e) {
            // 전체적인 실패 시 원본 데이터 유지하고 로그만 기록
            log.debug("{} 처리 실패, 원본 데이터 유지: {}", fieldName, e.getMessage());
            return data;
        }
    }
}
