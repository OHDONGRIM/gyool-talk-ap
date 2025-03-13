package com.gyooltalk.controller;

import com.gyooltalk.entity.Chat;
import com.gyooltalk.entity.Message;
import com.gyooltalk.payload.ChatDto;
import com.gyooltalk.payload.CreateChattingRequestDto;
import com.gyooltalk.payload.MessageDto;
import com.gyooltalk.payload.SendMessageDto;
import com.gyooltalk.service.ChattingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/chatting")
public class ChattingController {

    private final ChattingService chattingService;
    private final SimpMessagingTemplate messagingTemplate;  // 특정 사용자에게 WebSocket 메시지 전송용


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


    @MessageMapping("/chat/send/{chatId}")
    @SendTo("/subscribe/chat/{chatId}")
    public MessageDto sendMessage(@Payload SendMessageDto messageDto, @DestinationVariable String chatId) {
        log.debug("Received chatId, message: {}, {}", chatId, messageDto.getContent());

        return messageDto;
    }

}
