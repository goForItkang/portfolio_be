package com.pj.portfoliosite.portfoliosite.controller;

import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class TestController {
    // testController
    @GetMapping("/test")
    public HttpEntity<String> test(){
        String response = "Hello World!!";
        return new HttpEntity<>(response);
    }
    @GetMapping("/test2")
    public HttpEntity<String> test2(){
        String msg = "project_CICD_test";
        return new HttpEntity<>(msg);
    }

}
