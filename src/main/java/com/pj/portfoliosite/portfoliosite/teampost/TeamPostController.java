package com.pj.portfoliosite.portfoliosite.teampost;

import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import com.pj.portfoliosite.portfoliosite.global.dto.ReqPostWriteDTO;
import com.pj.portfoliosite.portfoliosite.global.dto.ResTeamPostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TeamPostController {
    private final TeamPostService teamPostService;
    //팀원 구하기에 있는 메인 페이지 기본값
    @GetMapping("/api/team")
    public HttpEntity<DataResponse<ResTeamPostDto>> teamProjectIndex(
            @RequestParam String category,
            Pageable pageable
    ){
        System.out.println("category: " + category);
        List<ResTeamPostDto> teamPostDtoList = teamPostService.getTeamPostIndex();

        return null;
    }
    //팀원 구하기 글쓰기
    //작성자 받아와서 수정해야함 **
    @PostMapping("/api/team/write")
    public HttpEntity<DataResponse<String>> teamPostWrite(@RequestBody ReqPostWriteDTO reqPostWriteDTO){
        System.out.println(reqPostWriteDTO);

        teamPostService.teamPostWrite(reqPostWriteDTO);

        DataResponse<String> dataResponse = new DataResponse<>();
        dataResponse.setData("글쓰기가 성공적으로 작성되었습니다!");
        return new HttpEntity<>(dataResponse);
    }

}
