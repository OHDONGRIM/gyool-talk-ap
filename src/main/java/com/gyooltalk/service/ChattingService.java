package com.gyooltalk.service;

import com.gyooltalk.entity.Attachment;
import com.gyooltalk.entity.Chat;
import com.gyooltalk.entity.Message;
import com.gyooltalk.entity.Participant;
import com.gyooltalk.payload.*;
import com.gyooltalk.repository.ChatRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChattingService {


    private final ChatRepository chatRepository;
    private final SequenceGeneratorService sequenceGeneratorService;

    public ResponseEntity<?> createChatting(CreateChattingRequestDto createChattingRequestDto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();
        String friendId = createChattingRequestDto.getFriendId();

        log.debug("ChattingService createChatting userId: {} friendId: {}", userId, friendId);
        Optional<Chat> optionalChat = chatRepository.findByParticipants(userId, friendId);

        if (optionalChat.isPresent()) {
            Chat chat = optionalChat.get();
            log.debug("Existing chat id: {}", chat.getId());
            // 채팅방이 존재하면 로그인 유저의 jointime null check
            Boolean isJointime = chatRepository.existsByChatIdAndUserIdAndJoinTimeNotNull(chat.getId(), userId);
            //jointime이 null 이면
            if(isJointime==null){
                LocalDateTime now = LocalDateTime.now();
                chatRepository.updateJoinTimeByChatIdAndUserId(chat.getId(), userId, now);
            }
            return ResponseEntity.ok(chat.getId());
        } else {
            // 데이터 베이스 구조 변경에 따른 데이터 입력 방법 변경
        List<Participant> participants = new ArrayList<>();
        participants.add(Participant.builder()
                .userId(userId)
                .joinTime(LocalDateTime.now())
                .build());
        participants.add(Participant.builder()
                .userId(friendId)
                .joinTime(LocalDateTime.now())
                .build());

            Chat chat = new Chat();
            chat.setId(sequenceGeneratorService.generateSequence("chat_sequence"));
            chat.setChatroomName("Chat: " + createChattingRequestDto.getUserNickname() + ", " + createChattingRequestDto.getFriendNickname());
            chat.setMessages(new ArrayList<>());
            chat.setParticipants(participants);
            chat = chatRepository.save(chat);
            log.debug("New Chat id: {}", chat.getId());
            return ResponseEntity.ok(chat.getId());
        }
    }

    public ResponseEntity<?> fetchChatroom() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();
        log.debug("ChattingService fetchChatroom userId: {}", userId);
        //userid로 입장시간이 빈값이 아닌 리스트 가져오기
        List<ChatDto> chats = chatRepository.findByUserIdWithNonEmptyJoinTime(userId);

        log.debug("ChattingService fetchChatroom chatDtos: {}", chats);

        return ResponseEntity.ok(chats);
    }

    public ResponseEntity<?> deleteChatroom(DeleteChattingRequestDto deleteChattingRequestDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();

        chatRepository.removeUserFromParticipants(deleteChattingRequestDto.getId(),userId);

        return ResponseEntity.ok("ok");
    }

    public Chat saveMessage(Long chatId, SendMessageDto messageDto) {

        Optional<Chat> optionalChat = chatRepository.findById(chatId);
        List<Attachment> attachments = new ArrayList<>();

        if (messageDto.getAttachments().size() > 0) {
            for (AttachmentDto attachment : messageDto.getAttachments()) {
                Attachment attach = Attachment.builder()
                        .id(attachment.getId())
                        .fileType(attachment.getFileType())
                        .filePath(attachment.getFilePath())
                        .build();
                attachments.add(attach);
            }
        }

       // Long timestamp = Long.parseLong(String.valueOf(messageDto.getTimestamp()));

        // 타임스탬프를 LocalDateTime으로 변환
//        Instant instant = Instant.ofEpochMilli(timestamp);
//        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        LocalDateTime localDateTime =messageDto.getTimestamp();

//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
//        String formattedTime = localDateTime.format(formatter);


        Message message = Message.builder()
                .id(sequenceGeneratorService.generateSequence(chatId + "_message_sequence"))
                .senderId(messageDto.getSenderId())
                .content(messageDto.getContent())
                .messageType(messageDto.getMessageType())
                .attachments(attachments)
                .timestamp(localDateTime)
                .build();

        if (optionalChat.isPresent()) {
            Chat chat = optionalChat.get();
            chat.getMessages().add(message);
            return chatRepository.save(chat);  // MongoDB에 저장
        } else {
            // 채팅방을 찾을 수 없을 때 예외를 던집니다.
            throw new RuntimeException("채팅방을 찾을 수 없습니다.");
        }
    }

    public ResponseEntity<?> fetchMessage(Long chatId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();
        // 채팅방 ID로 채팅방을 조회
        Optional<Chat> optionalChat = chatRepository.findByChatIdAndUserId(chatId,userId); // chatId는 long 타입으로 변환

        // 채팅방이 존재하는지 확인
        if (optionalChat.isPresent()) {
            //로그인한 유저의 join time 이후의 메세지만 가져옴
            Participant participant = optionalChat.get().getParticipants().stream()
                    .filter(p -> p.getUserId().equals(userId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("User not found in chat"));
            LocalDateTime mongoJoinTime = participant.getJoinTime();  // MongoDB에서 가져온 Date 타입

            List<Message> filteredMessages = optionalChat.get().getMessages().stream()
                    .filter(message -> message.getTimestamp().isAfter(mongoJoinTime))  // 메시지가 joinTime 이후인지 확인
                    .collect(Collectors.toList());

            // 채팅방에 있는 메시지만 반환
            return ResponseEntity.ok(filteredMessages);
        } else {
            // 채팅방을 찾을 수 없으면 예외를 던짐
            return ResponseEntity.status(404).body("채팅방을 찾을 수 없습니다.");
        }
    }
}
