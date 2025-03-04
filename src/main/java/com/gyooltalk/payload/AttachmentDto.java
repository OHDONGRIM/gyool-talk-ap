package com.gyooltalk.payload;

import jakarta.persistence.Id;
import org.springframework.data.mongodb.core.mapping.Field;

public interface AttachmentDto {
    Long getId(); // 파일 인덱스, 시퀀스 값으로 관리
    int getFileType(); // 파일 타입
    String getFilePath(); // 파일 경로
}
