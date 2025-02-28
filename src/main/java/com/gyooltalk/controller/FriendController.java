package com.gyooltalk.controller;

import com.gyooltalk.payload.FriendListDto;
import com.gyooltalk.service.FriendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/friend")
public class FriendController {

    private final FriendService friendService;

    @GetMapping("fetchFriendList")
    public ResponseEntity<List<FriendListDto>> fetchFriendList() {
        log.debug("fetchFriendList");

        return friendService.fetchFriendList();
    }
}
