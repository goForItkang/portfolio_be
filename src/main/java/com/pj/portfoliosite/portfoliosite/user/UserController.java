package com.pj.portfoliosite.portfoliosite.user;

import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import com.pj.portfoliosite.portfoliosite.global.dto.LoginRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public HttpEntity<DataResponse<String>> login(@RequestBody LoginRequestDto requestDto) {
        DataResponse<String> dataResponse = new DataResponse<>();
        String result = "값 확인";
        dataResponse.setData(result);
        dataResponse.setStatus(200);
        return new HttpEntity<>(dataResponse);
    }
}