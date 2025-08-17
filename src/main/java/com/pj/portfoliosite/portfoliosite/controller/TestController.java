package com.pj.portfoliosite.portfoliosite.controller;

import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/api")
    public HttpEntity<String> index() {
        String response = "Hello World";
        return new HttpEntity<>(response);
    }

}
