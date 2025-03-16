package com.gyooltalk.service;

import com.gyooltalk.entity.Attachment;
import com.gyooltalk.entity.Chat;
import com.gyooltalk.entity.Message;
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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
            Chat chat = new Chat();
            chat.setId(sequenceGeneratorService.generateSequence("chat_sequence"));
            chat.setChatroomName("Chat: " + userId + ", " + friendId);
            chat.setMessages(new ArrayList<>());
            chat.setParticipants(Arrays.asList(userId, friendId));
            chat = chatRepository.save(chat);
            log.debug("New Chat id: {}", chat.getId());
            return ResponseEntity.ok(chat.getId());
        }
    }

    public ResponseEntity<?> fetchChatroom() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();
        log.debug("ChattingService fetchChatroom userId: {}", userId);
        List<ChatDto> chatDtos = chatRepository.findByUserId(userId);
        log.debug("ChattingService fetchChatroom chatDtos: {}", chatDtos);

        return ResponseEntity.ok(chatDtos);
    }

    public Chat saveMessage(Long chatId, SendMessageDto messageDto) {

        Optional<Chat> optionalChat = chatRepository.findById(chatId);
        List<Attachment> attachments = new ArrayList<>();

        if(messageDto.getAttachments().size() > 0){
            for(AttachmentDto attachment : messageDto.getAttachments()){
                Attachment attach = Attachment.builder()
                        .id(attachment.getId())
                        .fileType(attachment.getFileType())
                        .filePath(attachment.getFilePath())
                        .build();
                attachments.add(attach);
            }
        }
        ZonedDateTime utcDateTime = ZonedDateTime.parse(messageDto.getTimestamp(), DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime localDateTime = utcDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        Message message = Message.builder()
                .id(sequenceGeneratorService.generateSequence(messageDto.getId()+"_message_sequence"))
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
}
