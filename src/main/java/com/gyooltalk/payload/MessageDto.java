package com.gyooltalk.payload;

import java.util.List;

public interface MessageDto {
    Long getId();  // 메시지 인덱스, 시퀀스 값으로 관리
    String getSenderId(); // 보낸사람 아이디
    String getContent(); // 채팅내용
    int getMessageType(); // 메시지 타입
    List<AttachmentDto> getAttachments(); // 파일 리스트
    String getTimestamp(); // 보낸 시간
}
