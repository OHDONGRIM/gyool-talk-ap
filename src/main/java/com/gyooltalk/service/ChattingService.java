package com.gyooltalk.service;

import com.gyooltalk.entity.Attachment;
import com.gyooltalk.entity.Chat;
import com.gyooltalk.entity.Message;
import com.gyooltalk.entity.Participant;
import com.gyooltalk.payload.AttachmentDto;
import com.gyooltalk.payload.ChatDto;
import com.gyooltalk.payload.CreateChattingRequestDto;
import com.gyooltalk.payload.SendMessageDto;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
        List<ChatDto> chats = chatRepository.findByUserId(userId);

        List<ChatDto> validChats = chats.stream()
                .filter(chat -> {
                    int index = chat.getParticipants().indexOf(userId);
                    System.out.println(index+"dddd" + chat.getEntryTime().get(index));
                    return index != -1 && !chat.getEntryTime().get(index).equals("");
                })
                .collect(Collectors.toList());
        log.debug("ChattingService fetchChatroom chatDtos: {}", validChats);

        return ResponseEntity.ok(validChats);
    }

    public ResponseEntity<?> deleteChatroom(DeleteChattingRequestDto  deleteChattingRequestDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();
        Optional<Chat> chat = chatRepository.findByChatId(deleteChattingRequestDto.getId());
        Chat chatToDelete = chat.get();
        int userIndex = chatToDelete.getParticipants().indexOf(userId);

        chatRepository.removeUserFromParticipants(deleteChattingRequestDto.getId(),userIndex);

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

        Long timestamp = Long.parseLong(messageDto.getTimestamp());

        // 타임스탬프를 LocalDateTime으로 변환
        Instant instant = Instant.ofEpochMilli(timestamp);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String formattedTime = localDateTime.format(formatter);


        Message message = Message.builder()
                .id(sequenceGeneratorService.generateSequence(chatId + "_message_sequence"))
                .senderId(messageDto.getSenderId())
                .content(messageDto.getContent())
                .messageType(messageDto.getMessageType())
                .attachments(attachments)
                .timestamp(formattedTime)
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
        // 채팅방 ID로 채팅방을 조회
        Optional<Chat> optionalChat = chatRepository.findById(chatId); // chatId는 long 타입으로 변환

        // 채팅방이 존재하는지 확인
        if (optionalChat.isPresent()) {
            // 채팅방에서 메시지를 가져옴
            Chat chat = optionalChat.get();

            // 채팅방에 있는 메시지만 반환
            return ResponseEntity.ok(chat.getMessages());
        } else {
            // 채팅방을 찾을 수 없으면 예외를 던짐
            return ResponseEntity.status(404).body("채팅방을 찾을 수 없습니다.");
        }
    }
}
