package com.gyooltalk.controller;

import com.gyooltalk.payload.FriendAddDto;
import com.gyooltalk.payload.UserDto;
import com.gyooltalk.service.UserActiveService;
import com.gyooltalk.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/active")
public class UserActiveController {

    private final UserActiveService userActiveService;
    private final UserService userService;

    @GetMapping("/getFriends")
    public ResponseEntity<?> getFriends() {
        log.debug("getFriends called");
        // 1. SecurityContext에서 현재 인증된 사용자 이름 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.debug("username {}", username);

        // 2. 사용자 이름을 기반으로 친구 목록 조회
        return userActiveService.getFriends(username);
    }
    @PostMapping("/findId")
    public ResponseEntity<?> findId(@RequestBody UserDto userDto) {
        log.debug("findId: {}", userDto);
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByUserEmail(userDto,userId);
    }
    @PostMapping("/addFriend")
    public ResponseEntity<Boolean> addFriend(@RequestBody FriendAddDto friendAddDto) {
        log.debug("friendAddDto: {}", friendAddDto);
        return userActiveService.addFriend(friendAddDto);
    }

    @PostMapping("/upDateNickName")
    public ResponseEntity<?> upDateNickName(@RequestBody UserDto userDto) {
        log.info("reqe: " + userDto);

        return userActiveService.UpdateNickname(userDto);
    }

    @PostMapping("/upload/image")
    public ResponseEntity<String> uploadFile( @RequestParam("uri") String uri, @RequestParam("userId") String userId){

        return userActiveService.uploadImage(uri,userId);
    }
}
