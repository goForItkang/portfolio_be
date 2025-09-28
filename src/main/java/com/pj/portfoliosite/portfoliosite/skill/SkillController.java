package com.pj.portfoliosite.portfoliosite.skill;

import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import com.pj.portfoliosite.portfoliosite.global.entity.Skill;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {
    
    private final SkillService skillService;
    
    // 모든 스킬 조회
    @GetMapping
    public DataResponse<List<Skill>> getAllSkills() {
        try {
            List<Skill> skills = skillService.getAllSkills();
            return new DataResponse<>(200, "스킬 목록 조회 성공", skills);
        } catch (Exception e) {
            return new DataResponse<>(500, "스킬 목록 조회 실패", null);
        }
    }
    
    // 스킬 생성
    @PostMapping
    public DataResponse<Skill> createSkill(@RequestBody String name) {
        try {
            // JSON 파싱 ("React" -> React)
            String skillName = name.replace("\"", "").trim();
            Skill skill = skillService.createSkill(skillName);
            return new DataResponse<>(201, "스킬 생성 성공", skill);
        } catch (IllegalArgumentException e) {
            return new DataResponse<>(400, e.getMessage(), null);
        } catch (Exception e) {
            return new DataResponse<>(500, "스킬 생성 실패: " + e.getMessage(), null);
        }
    }
    
    // 스킬 삭제
    @DeleteMapping("/{id}")
    public DataResponse<String> deleteSkill(@PathVariable Long id) {
        try {
            skillService.deleteSkill(id);
            return new DataResponse<>(200, "스킬 삭제 성공", null);
        } catch (Exception e) {
            return new DataResponse<>(500, "스킬 삭제 실패", null);
        }
    }
}