package com.gyooltalk.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    private Long id;  // 메시지 인덱스, 시퀀스 값으로 관리

    @Field("sender_id")
    private String senderId; // 보낸사람 아이디

    private String content; // 채팅내용

    @Field("message_type")
    private int messageType; // 메시지 타입

    private List<Attachment> attachments; // 파일 리스트

    private Long timestamp; // 보낸 시간
}