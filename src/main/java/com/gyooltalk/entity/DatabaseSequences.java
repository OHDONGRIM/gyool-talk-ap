package com.gyooltalk.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "database_sequences")
public class DatabaseSequences {

    @Id
    private String id; // 각 시퀀스의 이름(ex: "chat_sequence", "message_sequence", "attachment_sequence")

    private long seq;

}
