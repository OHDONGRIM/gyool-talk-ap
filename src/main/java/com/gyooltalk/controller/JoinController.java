package com.gyooltalk.controller;


import com.gyooltalk.entity.User;
import com.gyooltalk.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/join")
public class JoinController {

    private final UserService userService;

    @PostMapping("/idCheck")
    public boolean idCheck(@RequestBody Map<String, Object> params) {
        boolean result;

        String id = (String) params.get("id");
        log.info(id+"idCheck");
        result = userService.existsByUserId(id);
        System.out.println(result);
        return result;
    }

//    @PostMapping("/confirmEmail")
//    public boolean confirmEmail(@RequestBody Map<String, Object> params) {
//        boolean result =true;
//        String email = (String) params.get("email");
//        System.out.println("email is: "+email+"<---발송");
//        return result;
//    }
    @PostMapping("/checkConfimNum")
    public boolean checkConfimNum(@RequestBody Map<String, Object> params) {
        boolean result =true;
        String comfrimNum = (String) params.get("comfrimNum");
        System.out.println("comfrimNum is: "+comfrimNum+"<---인증번호");
        return result;
    }

    @PostMapping("/joinUser")
    public boolean joinUser(@RequestBody User params) {


        System.out.println("params is: "+params+"<---params");
        return  userService.insertUser(params);
    }

}
