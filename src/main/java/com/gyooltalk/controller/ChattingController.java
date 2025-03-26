package com.gyooltalk.controller;

import com.gyooltalk.entity.Chat;
import com.gyooltalk.entity.Message;
import com.gyooltalk.payload.*;
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
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping("/deleteChatting")
    public ResponseEntity<?> deleteChatroom(@RequestBody DeleteChattingRequestDto deleteChattingRequestDto) {
        log.debug("deleteChatting");
        return chattingService.deleteChatroom(deleteChattingRequestDto);
    }

    @MessageMapping("/chat/send/{chatId}")
    @SendTo("/subscribe/chat/{chatId}")
    public MessageDto sendMessage(@Payload SendMessageDto messageDto, @DestinationVariable Long chatId ) {
        log.debug("Received chatId, message: {}, {}", chatId, messageDto.getContent());

        chattingService.saveMessage(chatId,messageDto);
        return messageDto;
    }

    @PostMapping("/fetchMessage")
    public ResponseEntity<?> fetchMessage(@RequestParam Long chatId) {
        log.debug("fetchMessage ==> chatId: {}", chatId);
        return chattingService.fetchMessage(chatId);
    }

    @PostMapping("/uploadAttachment")
    public ResponseEntity<String> uploadAttachment(
            @RequestParam(value ="file", required=false) MultipartFile file, // 파일을 MultipartFile로 받음
            @RequestParam("chatId") String chatId) {
        log.debug("uploadAttachment ==> chatId: {}, {}", chatId, file);
        return chattingService.uploadAttachment(file, chatId);
    }

}
