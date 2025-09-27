package com.pj.portfoliosite.portfoliosite.skill;

import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SkillController {
    private final SkillService skillService;
    @GetMapping("/skills")
    public ResponseEntity<DataResponse> getSkill(){
        return ResponseEntity.ok(skillService.getSkill());
    }
}
