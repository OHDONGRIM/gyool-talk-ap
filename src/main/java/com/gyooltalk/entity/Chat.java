package com.gyooltalk.entity;


import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chatrooms")
public class Chat {

    @Id
    private Long id;  // 챗 인덱스, 시퀀스 값으로 관리

    @Field("chatroom_name")
    private String chatroomName; // 채팅방 이름

    private List<Message> messages; // 채팅 리스트
}