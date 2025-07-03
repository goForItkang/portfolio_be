package com.pj.portfoliosite.portfoliosite.teampost;

import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import com.pj.portfoliosite.portfoliosite.global.dto.ReqPostWriteDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TeamPostController {
    private final TeamPostService teamPostService;
    //팀원 구하기에 있는 메인 페이지 기본값
    @GetMapping("/api/team")
    public HttpEntity<DataResponse<String>> teamProjectIndex(){
        DataResponse<String> dataResponse = new DataResponse<>();
        String result = "값 확인";
        dataResponse.setData(result);
        dataResponse.setStatus(200);
        return new HttpEntity<>(dataResponse);
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
