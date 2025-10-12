package com.pj.portfoliosite.portfoliosite.teampost.skill;

import com.pj.portfoliosite.portfoliosite.global.entity.Skill;
import com.pj.portfoliosite.portfoliosite.global.entity.TeamPost;
import com.pj.portfoliosite.portfoliosite.global.entity.TeamPostSkill;
import com.pj.portfoliosite.portfoliosite.skill.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamPostSkillService {
    
    private final TeamPostSkillRepository teamPostSkillRepository;
    private final SkillService skillService;
    
    // TeamPost에 스킬들 추가
    public void addSkillsToTeamPost(TeamPost teamPost, List<String> skillNames) {
        for (String skillName : skillNames) {
            Skill skill = skillService.getOrCreateSkill(skillName);
            TeamPostSkill teamPostSkill = new TeamPostSkill(teamPost, skill);
            teamPost.addTeamPostSkill(teamPostSkill);
            teamPostSkillRepository.save(teamPostSkill);
        }
    }
    
    // TeamPost의 스킬들 업데이트
    public void updateTeamPostSkills(TeamPost teamPost, List<String> newSkillNames) {
        // 기존 스킬들 삭제
        teamPostSkillRepository.deleteByTeamPostId(teamPost.getId());
        teamPost.getTeamPostSkills().clear();
        
        // 새로운 스킬들 추가
        addSkillsToTeamPost(teamPost, newSkillNames);
    }
    
    // TeamPost의 모든 스킬 조회
    @Transactional(readOnly = true)
    public List<TeamPostSkill> getTeamPostSkills(Long teamPostId) {
        return teamPostSkillRepository.findByTeamPostId(teamPostId);
    }
    
    // TeamPost의 스킬 이름들 조회
    @Transactional(readOnly = true)
    public List<String> getTeamPostSkillNames(Long teamPostId) {
        return teamPostSkillRepository.findByTeamPostId(teamPostId)
                .stream()
                .map(teamPostSkill -> teamPostSkill.getSkill().getName())
                .toList();
    }
    
    // TeamPost의 ResSkill 리스트 조회 (id와 name 포함)
    @Transactional(readOnly = true)
    public List<com.pj.portfoliosite.portfoliosite.skill.ResSkill> getTeamPostResSkills(Long teamPostId) {
        return teamPostSkillRepository.findByTeamPostId(teamPostId)
                .stream()
                .map(teamPostSkill -> new com.pj.portfoliosite.portfoliosite.skill.ResSkill(
                        teamPostSkill.getSkill().getId(),
                        teamPostSkill.getSkill().getName()
                ))
                .toList();
    }
    
    // TeamPost의 모든 스킬 삭제
    public void deleteAllTeamPostSkills(Long teamPostId) {
        teamPostSkillRepository.deleteByTeamPostId(teamPostId);
    }
}