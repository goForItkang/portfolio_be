package com.pj.portfoliosite.portfoliosite.project;

import com.pj.portfoliosite.portfoliosite.global.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
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
        dataResponse.setData(projectServiceRecommend);

        return ResponseEntity.ok(
            dataResponse
        );
    }
    // 프로젝트 등록
    @PostMapping(value = "/project",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "프로젝트 등록",
            description = "header에 token 값 꼭 넣어 주세요"
    )
    public ResponseEntity<DataResponse> projectUpload(ReqProject reqProject) throws IOException {
        System.out.println("reqProject = " + reqProject);
        DataResponse dataResponse = new DataResponse();// 성공 무조건 처리
        projectService.projectUpload(reqProject);
        return ResponseEntity.ok(
            dataResponse
        );
    }
    // 최신순 프로젝트 목록 (페이지네이션)
    @GetMapping("/projects")
    @Operation(
            summary = "최신순으로 프로젝트 리스트 출력",
            description = "page 처리로 출력 함"
    )
    public ResponseEntity<DataResponse> getProjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ){
        //데이터 출력 방식
        DataResponse dataResponse = new DataResponse();

        projectService.getProjects(page,size);
        PageDTO<ResProjectDto> projectDtoPageDTO =projectService.getProjects(page,size);
        dataResponse.setData(projectDtoPageDTO);
        return ResponseEntity.ok(dataResponse);
    }
    //프로젝트 상세페이지 내용
    @GetMapping("/project")
    @Operation(
            summary = "프로젝트 상세페이지",
            description = "아직 미구현 곧 구현 할꺼에용~"
    )
    public ResponseEntity<DataResponse> getProject(
            @RequestParam Long id
    ){
        DataResponse dataResponse = new DataResponse();
        ResProjectDetailDTO projectDetailDTO = projectService.projectGetById(id);
        dataResponse.setData(projectDetailDTO);
        return ResponseEntity.ok(
                dataResponse
        );
    }
    @DeleteMapping("/projects/{id}")
    public ResponseEntity<DataResponse> deleteProject(
            @PathVariable Long id
    ){
        DataResponse dataResponse = new DataResponse();
        dataResponse.setStatus(200);
        dataResponse.setMessage("삭제가 완료 되었습니다.");
        projectService.delete(id);
        return ResponseEntity.ok(dataResponse);
    }
    @GetMapping("/project/{id}/details")
    @Operation(
            summary = "프로젝트 세부정보(좋아요 및 북마크 상태)",
            description = "id로 세부 정보"
    )
    public ResponseEntity<DataResponse> projectGetDetails(
            @PathVariable Long id
    ){
        DataResponse dataResponse = new DataResponse();
        dataResponse.setStatus(200);
        dataResponse.setData(projectService.projectDetailsById(id));

        return ResponseEntity.ok(dataResponse);
    }
    @PutMapping(value = "/project/{id}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DataResponse> projectUpdate(
            @PathVariable Long id,
            ReqProject reqProject
    ) throws IOException {
        log.info("프로젝트 업데이트 id : " + id);
        log.info("project : {}",reqProject);
        projectService.update(id,reqProject);
        return ResponseEntity.ok(null);
    }
}
