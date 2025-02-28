package com.gyooltalk.controller;

import com.gyooltalk.service.UserActiveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/active")
public class UserActiveController {

    private final UserActiveService userActiveService;

    @GetMapping("/getFriends")
    public ResponseEntity<?> getFriends() {
        log.debug("getFriends called");
        // 1. SecurityContext에서 현재 인증된 사용자 이름 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.debug("username {}", username);

        // 2. 사용자 이름을 기반으로 친구 목록 조회
        return userActiveService.getFriends(username);
    }

}
