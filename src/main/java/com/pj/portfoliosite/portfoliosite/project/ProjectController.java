package com.pj.portfoliosite.portfoliosite.project;

import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import com.pj.portfoliosite.portfoliosite.global.dto.ReqProject;
import com.pj.portfoliosite.portfoliosite.global.dto.ResProjectRecommendDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProjectController {
    private final ProjectService projectService;
    //메인 화면 추천 프로젝트
    @GetMapping("/projects/recommend")
    @Operation(
            summary = "메인 화면에 추천 프로젝트",
            description = "오늘 날짜로 부터 1주일간 가장 많은 좋아요를 얻은 프로젝트 12개 출력"
    )
    public ResponseEntity<DataResponse> recommend() {
        DataResponse dataResponse = new DataResponse();
        List<ResProjectRecommendDto> projectServiceRecommend =projectService.getRecommend();
        return ResponseEntity.ok(
            dataResponse
        );
    }
    // 프로젝트 등록
    @PostMapping("/project")
    @Operation(
            summary = "프로젝트 등록",
            description = "header에 token 값 꼭 넣어 주세요"
    )
    public ResponseEntity<DataResponse> projectUpload(ReqProject reqProject){
        DataResponse dataResponse = new DataResponse();// 성공 무조건 처리
        projectService.projectUpload(reqProject);
        return ResponseEntity.ok(
            dataResponse
        );
    }


}
