package com.pj.portfoliosite.portfoliosite.skill;

import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import com.pj.portfoliosite.portfoliosite.global.entity.Skill;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    public DataResponse getSkill() {
        DataResponse response = new DataResponse();
        List<Skill> skill=skillRepository.selectAllSkill();
        response.setData(skill);
        return response;

    }
}
