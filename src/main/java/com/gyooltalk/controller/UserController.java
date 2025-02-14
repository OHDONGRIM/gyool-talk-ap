package com.gyooltalk.controller;

import com.gyooltalk.service.MailService;
import com.gyooltalk.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final MailService mailService;


    @PostMapping("/confirmEmail")
    public boolean confirmEmail(@RequestBody Map<String, Object> params) {
        boolean result =true;
        String email = (String) params.get("email");
        try {
            mailService.sendMail(email);
        }catch(Exception e) {
            result = false;
            e.printStackTrace();
        }
        return result;
    }

    @PostMapping("/checkAuthNum")
    public boolean checkAuthNum(@RequestBody Map<String, Object> params) {
        String email = (String) params.get("email");
        String authNum = (String) params.get("authNum");

        log.debug("email:{},authNum:{}", email, authNum);

        return mailService.checkAuthNum(email,authNum);
    }

    @PostMapping("/findId")
    public String findId(@RequestBody Map<String, Object> params) {
        log.debug("findId: {}", params);
        String email = (String) params.get("email");
        return userService.findByUserEmail(email);
    }


}

