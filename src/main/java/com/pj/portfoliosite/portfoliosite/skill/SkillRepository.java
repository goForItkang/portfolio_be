package com.pj.portfoliosite.portfoliosite.skill;

import com.pj.portfoliosite.portfoliosite.global.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    // 스킬 이름으로 찾기
    Optional<Skill> findByName(String name);

    // 스킬 이름으로 존재 여부 확인
    boolean existsByName(String name);
    @Query("SELECT s FROM Skill s")
    List<Skill> selectAllSkill();

    // 포트폴리오 ID로 스킬 조회
    @Query("""
        SELECT ps.skill 
        FROM PortfolioSkill ps 
        WHERE ps.portfolio.id = :portfolioId
    """)
    List<Skill> selectByPortfolioId(@Param("portfolioId") Long id);

    List<Skill> selectByProjectId(@Param("projectId") Long id);
}
