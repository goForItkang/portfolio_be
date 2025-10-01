package com.pj.portfoliosite.portfoliosite.teampost;

import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import com.pj.portfoliosite.portfoliosite.global.dto.PageDTO;
import com.pj.portfoliosite.portfoliosite.teampost.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TeamPostController {
    private final TeamPostService teamPostService;

    @PostMapping("/teampost")
    @Operation(summary = "팀원 구하기 정식 등록", description = "header에 JWT 토큰 필요")
    public ResponseEntity<DataResponse> createTeamPost(@RequestBody ReqTeamPostDTO reqTeamPostDTO) {
        reqTeamPostDTO.setSaveStatus(false);
        teamPostService.saveTeamPost(reqTeamPostDTO);
        DataResponse dataResponse = new DataResponse();
        dataResponse.setMessage("팀원 구하기 등록 완료");
        return ResponseEntity.ok(dataResponse);
    }

    @PostMapping("/teampost/draft")
    @Operation(summary = "팀원 구하기 임시저장", description = "header에 JWT 토큰 필요")
    public ResponseEntity<DataResponse> saveDraft(@RequestBody ReqTeamPostDTO reqTeamPostDTO) {
        reqTeamPostDTO.setSaveStatus(true);
        teamPostService.saveTeamPost(reqTeamPostDTO);
        DataResponse dataResponse = new DataResponse();
        dataResponse.setMessage("팀원 구하기 임시저장 완료");
        return ResponseEntity.ok(dataResponse);
    }

    @GetMapping("/teamposts")
    @Operation(summary = "팀원 구하기 목록", description = "페이지네이션 적용된 팀원 구하기 목록 - 최신순 12개씩 (비로그인 가능)")
    public ResponseEntity<DataResponse> getTeamPosts(
            @RequestParam(defaultValue = "0") int page) {
        DataResponse dataResponse = new DataResponse();
        // 페이지당 12개로 고정
        PageDTO<ResTeamPostDTO> teamPosts = teamPostService.getTeamPosts(page, 12);
        dataResponse.setData(teamPosts);
        return ResponseEntity.ok(dataResponse);
    }

    @GetMapping("/teamposts/top4")
    @Operation(summary = "메인 페이지용 팀원 구하기 TOP 4", description = "일주일간 좋아요를 많이 받은 팀원 구하기 게시글 4개 (비로그인 가능)")
    public ResponseEntity<DataResponse> getTop4TeamPosts() {
        DataResponse dataResponse = new DataResponse();
        List<ResTeamPostDTO> top4Posts = teamPostService.getTop4TeamPostsByLikes();
        dataResponse.setData(top4Posts);
        dataResponse.setMessage("일주일간 인기 팀원 구하기 게시글 조회 성공");
        return ResponseEntity.ok(dataResponse);
    }

    @GetMapping("/teampost/{id}")
    @Operation(summary = "팀원 구하기 상세", description = "팀원 구하기 상세 정보 (비로그인 가능)")
    public ResponseEntity<DataResponse> getTeamPostDetail(@PathVariable Long id) {
        DataResponse dataResponse = new DataResponse();
        ResTeamPostDetailDTO teamPostDetail = teamPostService.getTeamPostById(id);
        dataResponse.setData(teamPostDetail);
        return ResponseEntity.ok(dataResponse);
    }

    @PutMapping("/teampost/{id}")
    @Operation(summary = "팀원 구하기 수정 (정식 등록)", description = "header에 JWT 토큰 필요")
    public ResponseEntity<DataResponse> updateTeamPost(@PathVariable Long id, @RequestBody ReqTeamPostDTO reqTeamPostDTO) {
        reqTeamPostDTO.setSaveStatus(false);
        teamPostService.updateTeamPost(id, reqTeamPostDTO);
        DataResponse dataResponse = new DataResponse();
        dataResponse.setMessage("팀원 구하기 수정 완료");
        return ResponseEntity.ok(dataResponse);
    }

    @PutMapping("/teampost/{id}/draft")
    @Operation(summary = "팀원 구하기 수정 (임시저장)", description = "header에 JWT 토큰 필요")
    public ResponseEntity<DataResponse> updateTeamPostAsDraft(@PathVariable Long id, @RequestBody ReqTeamPostDTO reqTeamPostDTO) {
        reqTeamPostDTO.setSaveStatus(true);
        teamPostService.updateTeamPost(id, reqTeamPostDTO);
        DataResponse dataResponse = new DataResponse();
        dataResponse.setMessage("팀원 구하기 임시저장 수정 완료");
        return ResponseEntity.ok(dataResponse);
    }

    @DeleteMapping("/teampost/{id}")
    @Operation(summary = "팀원 구하기 삭제", description = "header에 JWT 토큰 필요")
    public ResponseEntity<DataResponse> deleteTeamPost(@PathVariable Long id) {
        teamPostService.deleteTeamPost(id);
        DataResponse dataResponse = new DataResponse();
        dataResponse.setMessage("팀원 구하기 삭제 완료");
        return ResponseEntity.ok(dataResponse);
    }

    @GetMapping("/teampost/{id}/details")
    @Operation(summary = "팀원 구하기 세부정보", description = "좋아요/북마크 상태 정보 (비로그인 가능)")
    public ResponseEntity<DataResponse> getTeamPostDetails(@PathVariable Long id) {
        DataResponse dataResponse = new DataResponse();
        ResTeamPostDetailDTO teamPostDetails = teamPostService.getTeamPostById(id);
        dataResponse.setData(teamPostDetails);
        return ResponseEntity.ok(dataResponse);
    }

    @GetMapping("/user/teamposts/drafts")
    @Operation(summary = "사용자의 임시저장 게시물 목록", description = "JWT 토큰 필요")
    public ResponseEntity<DataResponse> getUserDrafts() {
        List<ResTeamPostDTO> drafts = teamPostService.getUserDrafts();
        DataResponse dataResponse = new DataResponse();
        dataResponse.setData(drafts);
        dataResponse.setMessage("임시저장 게시물 조회 완료");
        return ResponseEntity.ok(dataResponse);
    }

    @PostMapping("/teampost/{id}/publish")
    @Operation(summary = "임시저장 게시물 정식 발행", description = "JWT 토큰 필요")
    public ResponseEntity<DataResponse> publishDraft(@PathVariable Long id) {
        String result = teamPostService.publishDraft(id);
        DataResponse dataResponse = new DataResponse();
        dataResponse.setMessage(result);

        if (result.contains("오류") || result.contains("찾을 수 없습니다") || result.contains("권한이 없습니다")) {
            dataResponse.setStatus(400);
        } else {
            dataResponse.setStatus(200);
        }

        return ResponseEntity.ok(dataResponse);
    }

    @DeleteMapping("/teampost/{id}/draft")
    @Operation(summary = "임시저장 게시물 삭제", description = "JWT 토큰 필요")
    public ResponseEntity<DataResponse> deleteDraft(@PathVariable Long id) {
        String result = teamPostService.deleteDraft(id);
        DataResponse dataResponse = new DataResponse();
        dataResponse.setMessage(result);

        if (result.contains("오류") || result.contains("찾을 수 없습니다") || result.contains("권한이 없습니다")) {
            dataResponse.setStatus(400);
        } else {
            dataResponse.setStatus(200);
        }

        return ResponseEntity.ok(dataResponse);
    }
}