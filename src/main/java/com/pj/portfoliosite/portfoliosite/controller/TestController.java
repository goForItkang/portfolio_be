package com.pj.portfoliosite.portfoliosite.controller;

import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    //ㅔㅌ스트 수정
    @GetMapping("/")
    public HttpEntity<String> index(){
        String response = "Hello World";
        return new HttpEntity<>(response);
    }
}
