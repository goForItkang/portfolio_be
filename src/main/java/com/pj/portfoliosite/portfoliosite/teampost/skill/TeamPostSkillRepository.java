package com.pj.portfoliosite.portfoliosite.teampost.skill;

import com.pj.portfoliosite.portfoliosite.global.entity.TeamPostSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamPostSkillRepository extends JpaRepository<TeamPostSkill, Long> {
    
    // 특정 TeamPost의 모든 스킬 조회
    @Query("SELECT tps FROM TeamPostSkill tps WHERE tps.teamPost.id = :teamPostId")
    List<TeamPostSkill> findByTeamPostId(@Param("teamPostId") Long teamPostId);
    
    // 특정 Skill의 모든 TeamPost 조회
    @Query("SELECT tps FROM TeamPostSkill tps WHERE tps.skill.id = :skillId")
    List<TeamPostSkill> findBySkillId(@Param("skillId") Long skillId);
    
    // TeamPost와 Skill로 TeamPostSkill 찾기
    @Query("SELECT tps FROM TeamPostSkill tps WHERE tps.teamPost.id = :teamPostId AND tps.skill.id = :skillId")
    TeamPostSkill findByTeamPostIdAndSkillId(@Param("teamPostId") Long teamPostId, @Param("skillId") Long skillId);
    
    // 특정 TeamPost의 스킬들 삭제
    @Modifying
    @Query("DELETE FROM TeamPostSkill tps WHERE tps.teamPost.id = :teamPostId")
    void deleteByTeamPostId(@Param("teamPostId") Long teamPostId);
}