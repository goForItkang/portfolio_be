package com.pj.portfoliosite.portfoliosite.teampost;

import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TeamPostController {
    //팀원 구하기에 있는 메인 페이지 기본값
    @PostMapping("/api/team")
    public HttpEntity<DataResponse<String>> teamProjectIndex(){
        DataResponse<String> dataResponse = new DataResponse<>();
        String result = "값 확인";
        dataResponse.setData(result);
        dataResponse.setStatus(200);
        return new HttpEntity<>(dataResponse);
    }
}
