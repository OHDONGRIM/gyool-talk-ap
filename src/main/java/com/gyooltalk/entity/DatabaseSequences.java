package com.gyooltalk.entity;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collation = "database_sequences")
public class DatabaseSequences {

    @Id
    private String id; // 각 시퀀스의 이름(ex: "chat_sequence", "message_sequence", "attachment_sequence")

    private Long seq;

}
