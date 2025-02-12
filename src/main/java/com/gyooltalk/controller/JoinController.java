package com.gyooltalk.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/join")
public class JoinController {
    @PostMapping("/idCheck")
    public boolean idCheck(@RequestBody Map<String, Object> params) {
        boolean result;

        if(params.get("id").equals("test")){
            result= false;
        }else{
            result= true;
        }
        System.out.println("id is: "+result);
        return result;
    }

    @PostMapping("/confirmEmail")
    public boolean confirmEmail(@RequestBody Map<String, Object> params) {
        boolean result =true;
        String email = (String) params.get("email");
        System.out.println("email is: "+email+"<---발송");
        return result;
    } 
    @PostMapping("/checkConfimNum")
    public boolean checkConfimNum(@RequestBody Map<String, Object> params) {
        boolean result =true;
        String comfrimNum = (String) params.get("comfrimNum");
        System.out.println("comfrimNum is: "+comfrimNum+"<---인증번호");
        return result;
    }

    @PostMapping("/joinUser")
    public boolean joinUser(@RequestBody Map<String, Object> params) {
        boolean result =true;

        System.out.println("params is: "+params+"<---params");
        return true;
    }

}
