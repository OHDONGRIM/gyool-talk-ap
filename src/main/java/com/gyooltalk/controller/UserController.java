package com.gyooltalk.controller;

import com.gyooltalk.payload.FriendAddDto;
import com.gyooltalk.payload.UserDto;
import com.gyooltalk.service.MailService;
import com.gyooltalk.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {


    private final UserService userService;
    private final MailService mailService;


    @PostMapping("/confirmEmail")
    public ResponseEntity<String> confirmEmail(@RequestBody UserDto userDto) {

        if (userService.findByUserEmail(userDto) == null && userDto.getUserId() != null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 이메일을 찾을 수 없습니다.");
        }

        try {
            mailService.sendMail(userDto);
            return ResponseEntity.ok("인증 메일이 발송되었습니다.");
        } catch (Exception e) {
            log.error("메일 전송 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("메일 전송 중 오류 발생");
        }
    }

    @PostMapping("/checkAuthNum")
    public ResponseEntity<Boolean> checkAuthNum(@RequestBody Map<String, Object> params) {
        String email = (String) params.get("email");
        String authNum = (String) params.get("authNum");

        log.debug("email:{},authNum:{}", email, authNum);

        boolean result =  mailService.checkAuthNum(email,authNum);
        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/findId")
    public ResponseEntity<?> findId(@RequestBody UserDto userDto) {
        log.debug("findId: {}", userDto);
        return userService.findByUserEmail(userDto);
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<Boolean> resetPassword(@RequestBody UserDto userDto) {
        log.debug("findId: {}", userDto);
        return userService.resetPassword(userDto);
    }

    @PostMapping("/addFriend")
    public ResponseEntity<Boolean> addFriend(@RequestBody FriendAddDto friendAddDto) {
        log.debug("friendAddDto: {}", friendAddDto);
        return userService.addFriend(friendAddDto);
    }

    @PostMapping("/upDateNickName")
    public ResponseEntity<?> upDateNickName(@RequestBody UserDto userDto) {
        log.info("reqe: " + userDto);

        return userService.UpdateNickname(userDto);
    }

//    @PostMapping("/upload/image")
//    public ResponseEntity<Boolean> uploadFile(@RequestParam("uri") String uri, @RequestParam("userId") String userId){
//
//
//        return userService.uploadImage(uri,userId);
//    }

}

