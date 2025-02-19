package com.gyooltalk.controller;

import com.gyooltalk.payload.LoginResponseDto;
import com.gyooltalk.payload.UserDto;
import com.gyooltalk.service.LoginService;
import com.gyooltalk.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/")
    public String home() {
        return "This is Main!";  // 그냥 문자열을 반환
    }

//    @PostMapping("/login")
//    public String login(@RequestBody Map<String, Object> params) {
//        log.debug("login params: {}", params);
//
//        String result = null;
//
//        if(params.get("id").equals("test") && params.get("pwd").equals("test")){
//            result="success";
//        }
//        System.out.println("로그인 시도 - ID: " + params.get("id") + ", PWD: " + params.get("pwd"));
//        return result;
//    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody UserDto userDto) {
        log.debug("login userDto: {}", userDto);
        return loginService.login(userDto);
    }

    @GetMapping("/talk")
    public String talk() {
        return "This is talk Page!";  // 그냥 문자열을 반환
    }
}
