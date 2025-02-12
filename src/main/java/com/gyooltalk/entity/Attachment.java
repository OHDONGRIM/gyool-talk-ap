package com.gyooltalk.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {

    @Id
    private Long id; // 파일 인덱스, 시퀀스 값으로 관리

    @Field("file_type")
    private int fileType; // 파일 타입

    @Field("file_path")
    private String filePath; // 파일 경로
}