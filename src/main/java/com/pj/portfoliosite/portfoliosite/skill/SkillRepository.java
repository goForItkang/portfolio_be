package com.pj.portfoliosite.portfoliosite.skill;

import com.pj.portfoliosite.portfoliosite.global.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    
    // 스킬 이름으로 찾기
    Optional<Skill> findByName(String name);
    
    // 스킬 이름으로 존재 여부 확인
    boolean existsByName(String name);
}