package com.pj.portfoliosite.portfoliosite.skill;

import com.pj.portfoliosite.portfoliosite.global.entity.Skill;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResSkill {
    private Long id;
    private String name;

    public ResSkill(Skill skill) {
        this.id = skill.getId();
        this.name = skill.getName();
    }
    public List<ResSkill> toResSkillList(List<Skill> skills) {
        if(skills == null || skills.isEmpty()){
            return null;
        }
        return skills.stream().map(ResSkill::new).toList();
    }
}
