package com.gyooltalk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @GetMapping("/")
    public String home() {
        return "This is Main!";  // 그냥 문자열을 반환
    }
    @GetMapping("/login")
    public String login() {
        return "This is Login Page!";  // 그냥 문자열을 반환
    }

    @GetMapping("/talk")
    public String talk() {
        return "This is talk Page!";  // 그냥 문자열을 반환
    }
}
