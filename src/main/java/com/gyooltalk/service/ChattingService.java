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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChattingService {

    @Value("${chat.file.upload-dir}")
    private String uploadDir;

    private final ChatRepository chatRepository;
    private final SequenceGeneratorService sequenceGeneratorService;

    public ResponseEntity<?> createChatting(CreateChattingRequestDto createChattingRequestDto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();
        String friendId = createChattingRequestDto.getFriendId();

        log.debug("ChattingService createChatting userId: {} friendId: {}", userId, friendId);
        Optional<Chat> optionalChat = chatRepository.findByParticipants(userId, friendId);
        long currentTimeMillis = System.currentTimeMillis();

        if (optionalChat.isPresent()) {
            Chat chat = optionalChat.get();
            log.debug("Existing chat id: {}", chat.getId());
            // 채팅방이 존재하면 로그인 유저의 jointime null check
            Optional<Chat>  exitUserCheck = chatRepository.existsByChatIdAndUserIdAndJoinTimeNotNull(chat.getId(), userId);
            //jointime이 null 이면
            if(exitUserCheck.isEmpty()){
                chatRepository.updateJoinTimeByChatIdAndUserId(chat.getId(), userId, currentTimeMillis);
            }
            return ResponseEntity.ok(chat.getId());
        } else {
            // 데이터 베이스 구조 변경에 따른 데이터 입력 방법 변경


            List<Participant> participants = new ArrayList<>();
            participants.add(Participant.builder()
                    .userId(userId)
                    .joinTime(currentTimeMillis)
                    .build());
            participants.add(Participant.builder()
                    .userId(friendId)
                    .joinTime(currentTimeMillis)
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

        Long timestamp = Long.parseLong(String.valueOf(messageDto.getTimestamp()));

//         타임스탬프를 LocalDateTime으로 변환
//        Instant instant = Instant.ofEpochMilli(timestamp);
//        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());


//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
//        String formattedTime = localDateTime.format(formatter);


        Message message = Message.builder()
                .id(sequenceGeneratorService.generateSequence(chatId + "_message_sequence"))
                .senderId(messageDto.getSenderId())
                .content(messageDto.getContent())
                .messageType(messageDto.getMessageType())
                .attachments(attachments)
                .timestamp(timestamp)
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
            Long mongoJoinTime = participant.getJoinTime();  // MongoDB에서 가져온 Date 타입

            List<Message> filteredMessages = optionalChat.get().getMessages().stream()
                    .filter(message -> message.getTimestamp() > mongoJoinTime)// 메시지가 joinTime 이후인지 확인
                    .collect(Collectors.toList());
            System.out.println(filteredMessages);
            // 채팅방에 있는 메시지만 반환
            return ResponseEntity.ok(filteredMessages);
        } else {
            // 채팅방을 찾을 수 없으면 예외를 던짐
            return ResponseEntity.status(404).body("채팅방을 찾을 수 없습니다.");
        }
    }

    public ResponseEntity<String> uploadAttachment(MultipartFile file, String chatId) {
        log.debug("ChattingService uploadAttachment chatId: {}, {}", chatId, file);
        try {
            // 파일이 비었는지 확인
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("파일이 없습니다.");
            }

            // 파일 크기 제한
            long maxFileSize = 5 * 1024 * 1024; // 5MB
            if (file.getSize() > maxFileSize) {
                return ResponseEntity.badRequest().body("파일 크기가 너무 큽니다. (최대 5MB)");
            }

            // 지원하는 파일 확장자인지 확인 (이미지 & PDF만 허용 예시)
            String fileName = file.getOriginalFilename();
            log.debug("ChattingService uploadAttachment fileName: {}", fileName);
            if (fileName != null) {
                String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif", "pdf", "txt");
                if (!allowedExtensions.contains(extension)) {
                    return ResponseEntity.badRequest().body("허용되지 않은 파일 형식입니다.");
                }
            }

            Date now = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            //String fileName =  sdf.format(now)+".jpg";

            // 4️⃣ 파일 저장 처리 (예: 로컬 서버에 저장)
            String filePath = uploadDir + fileName;
            File destFile = new File(filePath);

            // 디렉토리 생성
            destFile.getParentFile().mkdirs();

            // 파일 저장
            file.transferTo(destFile);

            return ResponseEntity.ok("파일 업로드 성공: " + filePath);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)

                    .body("파일 업로드 중 오류 발생: " + e.getMessage());
        }
    }
}
