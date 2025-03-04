package com.gyooltalk.controller;

import com.gyooltalk.payload.CreateChattingRequestDto;
import com.gyooltalk.service.ChattingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/chatting")
public class ChattingController {

    private final ChattingService chattingService;

    @PostMapping("/createChatting")
    public ResponseEntity<?> createChatting(@RequestBody CreateChattingRequestDto createChattingRequestDto) {
        log.debug("createChatting createChattingRequestDto: {}", createChattingRequestDto);

        return chattingService.createChatting(createChattingRequestDto);
    }

    @GetMapping("/fetchChatroom")
    public ResponseEntity<?> fetchChatroom() {
        log.debug("fetchChatroom");
        return chattingService.fetchChatroom();
    }
}
