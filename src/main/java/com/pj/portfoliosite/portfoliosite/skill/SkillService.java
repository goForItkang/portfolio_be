package com.pj.portfoliosite.portfoliosite.skill;


import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import com.pj.portfoliosite.portfoliosite.global.entity.Skill;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class SkillService {

    private final SkillRepository skillRepository;

    // 모든 스킬 조회
    @Transactional(readOnly = true)
    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    // 스킬 ID로 조회
    @Transactional(readOnly = true)
    public Optional<Skill> getSkillById(Long id) {
        return skillRepository.findById(id);
    }

    // 스킬 이름으로 조회
    @Transactional(readOnly = true)
    public Optional<Skill> getSkillByName(String name) {
        return skillRepository.findByName(name);
    }

    // 스킬 생성 또는 기존 스킬 반환
    public Skill getOrCreateSkill(String name) {
        return skillRepository.findByName(name)
                .orElseGet(() -> skillRepository.save(new Skill(name)));
    }

    // 스킬 생성
    public Skill createSkill(String name) {
        if (skillRepository.existsByName(name)) {
            throw new IllegalArgumentException("이미 존재하는 스킬입니다: " + name);
        }
        return skillRepository.save(new Skill(name));
    }

    // 스킬 삭제
    public void deleteSkill(Long id) {
        skillRepository.deleteById(id);
    }
    @Transactional
    public DataResponse getSkill() {
        DataResponse response = new DataResponse();
        List<Skill> skills = skillRepository.findAll();
        List<ResSkill> res = new ResSkill().toResSkillList(skills);
        response.setData(res);
        return response;
    }

}
