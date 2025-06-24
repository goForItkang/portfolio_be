package com.pj.portfoliosite.portfoliosite.controller;

import com.pj.portfoliosite.portfoliosite.global.dto.DataResponse;
import com.pj.portfoliosite.portfoliosite.global.dto.LoginRequestDto;
import com.pj.portfoliosite.portfoliosite.global.dto.LoginResponseDto;
import com.pj.portfoliosite.portfoliosite.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public HttpEntity<DataResponse<String>> login(@RequestBody LoginRequestDto requestDto) {
        DataResponse<String> dataResponse = new DataResponse<>();
        String result = "값 확인";
        dataResponse.setData(result);
        dataResponse.setStatus(200);
        return new HttpEntity<>(dataResponse);
    }
}